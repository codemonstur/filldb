package filldb.core;

import com.mifmif.common.regex.Generex;
import filldb.error.NoSuchGenerator;
import filldb.generators.LorumIpsumGenerator;
import filldb.generators.ValueGenerator;
import filldb.generators.ValueSetter;
import filldb.model.CliArguments;
import filldb.model.Column;
import filldb.model.ForeignKey;
import filldb.model.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static filldb.core.Schema.selectIds;
import static filldb.core.Util.randomItemFrom;
import static filldb.generators.DataGenerators.newDataGenerators;
import static filldb.generators.LorumIpsumGenerator.newLorumIpsumGenerators;
import static filldb.generators.TypeGenerators.newTypeGenerators;
import static filldb.generators.ValueGenerator.detectGenerator;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

public enum Generate {;

    public static List<String> fillDatabase(final Connection connection, final List<Table> tables
            , final CliArguments arguments) throws SQLException, NoSuchGenerator {

        final List<String> queries = new ArrayList<>(tables.size()*arguments.numQueries);

        final Map<String, ValueGenerator> typeGenerators = newTypeGenerators();
        final Map<String, ValueGenerator> dataGenerators =
                newDataGenerators(arguments.allowHumor, arguments.allowRemote, arguments.allowNSFW);
        final Map<String, LorumIpsumGenerator> ipsumGenerators = newLorumIpsumGenerators();


        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            for (final Table table : tables) {
                if (table.isFilled) continue;
                allDone = false;
                if (!table.isFillable()) continue;

                selectGenerators(connection, dataGenerators, typeGenerators, ipsumGenerators, table, arguments.useCommentPattern);
                queries.addAll(fillTable(connection, table, arguments.numQueries, arguments.ignoreErrors));
                table.isFilled = true;
            }
        }

        return queries;
    }

    private static void selectGenerators(final Connection connection, final Map<String, ValueGenerator> dataGenerators
            , final Map<String, ValueGenerator> typeGenerators, final Map<String, LorumIpsumGenerator> ipsumGenerators
            , final Table table, final boolean usePatterns)
            throws SQLException, NoSuchGenerator {
        for (final Column column : table.columns) {
            if (column.isAutoIncrementing) continue;
            column.valueSetter = selectGenerator(connection, dataGenerators, typeGenerators, ipsumGenerators, column, table, usePatterns);
        }
    }

    private static List<String> fillTable(final Connection connection, final Table table, final int numQueries
            , final boolean ignoreErrors) throws SQLException {

        final List<String> columnList = new ArrayList<>();
        final List<String> columnValues = new ArrayList<>();
        for (final Column column : table.columns) {
            if (column.isAutoIncrementing) continue;

            columnList.add("`"+column.name+"`");
            columnValues.add("?");
        }

        final String insertQuery = format("INSERT INTO %s (%s) VALUES (%s);",
                table.name, join(",", columnList), join(",", columnValues));

        final List<String> queries = new ArrayList<>();
        for (int i = 0; i < numQueries; i++) {
            try {
                final PreparedStatement statement = connection.prepareStatement(insertQuery);
                int j = 0;
                for (final Column column : table.columns) {
                    if (column.isAutoIncrementing) continue;

                    column.valueSetter.setValue(j+1, statement);
                    j++;
                }
                statement.execute();
            } catch (SQLException e) {
                if (!ignoreErrors) throw e;
                System.err.println("[ERROR] Failed to execute query");
                System.err.println("[ERROR] Query: " + insertQuery);
                System.err.println("[ERROR] Error: " + e.getMessage());
            }
            queries.add(insertQuery);
        }

        return queries;
    }

    private static ValueSetter selectGenerator(final Connection connection, final Map<String, ValueGenerator> dataGenerators
            , final Map<String, ValueGenerator> typeGenerators, final Map<String, LorumIpsumGenerator> ipsumGenerators
            , final Column column, final Table table, final boolean usePatterns)
            throws SQLException, NoSuchGenerator {

        if (column.isForeignKey()) return newForeignKeySetter(connection, column.fk);

        if (usePatterns && column.hasPattern()) return newPatternSetter(dataGenerators, typeGenerators, ipsumGenerators, column);

        final var dataGenerator = detectGenerator(dataGenerators, column, null);
        if (dataGenerator != null) return dataGenerator.newValueSetter(column);

        final var typeGenerator = detectGenerator(typeGenerators, column, null);
        if (typeGenerator != null) return typeGenerator.newValueSetter(column);

        throw new SQLException("Could not find a generator for column '" + column.name +
                "' in table '" + table.name + "' with type '" + column.dataType + "'");
    }

    private static ValueSetter newForeignKeySetter(final Connection connection, final ForeignKey fk) throws SQLException {
        final List<Long> values = selectIds(connection, fk.table, fk.column);
        if (values.isEmpty()) throw new SQLException("No values were created for foreign key");
        return (index, statement) -> statement.setLong(index, randomItemFrom(values));
    }

    private static final String
        FILLDB_PATTERN = "filldb pattern ",
        FILLDB_ENUM = "filldb enum ",
        FILLDB_GENERATOR = "filldb generator ",
        FILLDB_IPSUM = "filldb ipsum ";

    private static ValueSetter newPatternSetter(final Map<String, ValueGenerator> dataGenerators
            , final Map<String, ValueGenerator> typeGenerators, final Map<String, LorumIpsumGenerator> ipsumGenerators
            , final Column column) throws NoSuchGenerator {
        if (column.pattern.startsWith(FILLDB_PATTERN)) {
            final Generex generex = new Generex(column.pattern.substring(FILLDB_PATTERN.length()));
            return (index, statement) -> statement.setString(index, generex.random());
        }
        if (column.pattern.startsWith(FILLDB_ENUM)) {
            final List<String> enumItems = Arrays.asList(column.pattern.substring(FILLDB_ENUM.length()).split(","));
            return (index, statement) -> statement.setString(index, randomItemFrom(enumItems));
        }
        if (column.pattern.startsWith(FILLDB_GENERATOR)) {
            final String name = column.pattern.substring(FILLDB_GENERATOR.length());
            final ValueGenerator typeGenerator = typeGenerators.get(name);
            if (typeGenerator != null && typeGenerator.canGenerateFor(column)) {
                return typeGenerator.newValueSetter(column);
            }
            final ValueGenerator dataGenerator = dataGenerators.get(name);
            if (dataGenerator != null && dataGenerator.canGenerateFor(column)) {
                return dataGenerator.newValueSetter(column);
            }
        }
        if (column.pattern.startsWith(FILLDB_IPSUM)) {
            final String name = column.pattern.substring(FILLDB_GENERATOR.length());
            final LorumIpsumGenerator ipsum = ipsumGenerators.get(name);
            if (ipsum != null) {
                return (index, statement) -> {
                    try {
                        statement.setString(index, ipsum.getIpsum());
                    } catch (Exception e) {
                        statement.setString(index, e.getMessage());
                    }
                };
            }
        }
        throw new NoSuchGenerator(column.pattern);
    }

    public static List<String> generateInsertQueries(final Connection connection, final List<Table> tables
            , final CliArguments arguments) throws SQLException {

        final List<String> queries = new ArrayList<>(tables.size()*arguments.numQueries);

        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            for (final Table table : tables) {
                if (table.isFilled) continue;
                allDone = false;
                if (!table.isFillable()) continue;

                queries.addAll(insertQueriesFor(connection, table));
                table.isFilled = true;
            }
        }

        return queries;
    }

    private static List<String> insertQueriesFor(final Connection connection, final Table table) throws SQLException {
        final List<String> queries = new ArrayList<>();

        final List<String> columnList = new ArrayList<>();
        final List<String> columnValues = new ArrayList<>();
        final List<Boolean> columnShouldQuote = new ArrayList<>();
        for (final Column column : table.columns) {
            columnList.add(column.name);
            columnValues.add("%s");
            columnShouldQuote.add(shouldQuoteDataType(column.dataType));
        }

        final String columns = columnList.stream().map(s -> "`"+s+"`").collect(joining(","));
        final String insertQuery = format("INSERT INTO `%s` (%s) VALUES (%s);", table.name, columns,
                join(",", columnValues));

        try (final var statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(format("SELECT * FROM `%s`", table.name));

            final String[] values = new String[columnList.size()];
            while (resultSet.next()) {
                for (int i = 0; i < columnList.size(); i++) {
                    final String value = resultSet.getString(columnList.get(i));
                    values[i] = columnShouldQuote.get(i) ? toQuotedString(value): value;
                }
                queries.add(String.format(insertQuery, values));
            }

        }

        return queries;
    }

    private static Boolean shouldQuoteDataType(final String dataType) {
        if ("int".equals(dataType)) return false;
        if ("bigint".equals(dataType)) return false;
        if ("bit".equals(dataType)) return false;

        return true;
    }

    private static String toQuotedString(final String input) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\'');

        for (final char c : input.toCharArray()) {
            if (c == '\\' || c =='\'') builder.append('\\');
            builder.append(c);
        }

        builder.append('\'');
        return builder.toString();
    }
}
