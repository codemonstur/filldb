package filldb.core;

import com.mifmif.common.regex.Generex;
import filldb.generators.ValueGenerator;
import filldb.generators.ValueSetter;
import filldb.model.CliArguments;
import filldb.model.Column;
import filldb.model.ForeignKey;
import filldb.model.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static filldb.core.Schema.selectIds;
import static filldb.core.Util.randomItemFrom;
import static filldb.generators.DataGenerators.newDataGenerators;
import static filldb.generators.TypeGenerators.newTypeGenerators;
import static filldb.generators.ValueGenerator.detectGenerator;
import static java.lang.String.format;
import static java.lang.String.join;

public enum Generate {;

    public static List<String> fillDatabase(final Connection connection, final List<Table> tables
            , final CliArguments arguments) throws SQLException {

        final List<String> queries = new ArrayList<>(tables.size()*arguments.numQueries);

        final List<ValueGenerator> typeGenerators = newTypeGenerators();
        final List<ValueGenerator> dataGenerators =
                newDataGenerators(arguments.allowHumor, arguments.allowRemote, arguments.allowNSFW);

        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            for (final Table table : tables) {
                if (table.isFilled) continue;
                allDone = false;
                if (!table.isFillable()) continue;

                selectGenerators(connection, dataGenerators, typeGenerators, table, arguments.useCommentPattern);
                queries.addAll(fillTable(connection, table, arguments.numQueries, arguments.ignoreErrors));
                table.isFilled = true;
            }
        }

        return queries;
    }

    private static void selectGenerators(final Connection connection, final List<ValueGenerator> dataGenerators
            , final List<ValueGenerator> typeGenerators, final Table table, final boolean usePatterns) throws SQLException {
        for (final Column column : table.columns) {
            if (column.isAutoIncrementing) continue;
            column.valueSetter = selectGenerator(connection, dataGenerators, typeGenerators, column, table, usePatterns);
        }
    }

    private static List<String> fillTable(final Connection connection, final Table table, final int numQueries
            , final boolean ignoreErrors) throws SQLException {

        final List<String> queries = new ArrayList<>();

        for (int i = 0; i < numQueries; i++) {
            final List<String> columnList = new ArrayList<>();
            final List<String> columnValues = new ArrayList<>();
            for (final Column column : table.columns) {
                if (column.isAutoIncrementing) continue;

                columnList.add(column.name);
                columnValues.add("?");
            }

            final String insertQuery = format("INSERT INTO %s (%s) VALUES (%s);",
                    table.name, join(",", columnList), join(",", columnValues));

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

    private static ValueSetter selectGenerator(final Connection connection, final List<ValueGenerator> dataGenerators
            , final List<ValueGenerator> typeGenerators, final Column column, final Table table, final boolean usePatterns) throws SQLException {

        if (column.isForeignKey()) {
            System.out.println("Column '" + column.name + "' in '" + table.name + "' bound to foreign key setter");
            return newForeignKeySetter(connection, column.fk);
        }
        if (usePatterns && column.hasPattern()) {
            System.out.println("Column '" + column.name + "' in '" + table.name + "' bound to pattern setter");
            return newPatternSetter(column);
        }

        final var dataGenerator = detectGenerator(dataGenerators, column, null);
        if (dataGenerator != null) {
            System.out.println("Column '" + column.name + "' in '" + table.name + "' bound to " + dataGenerator.name() + " setter");
            return dataGenerator.newValueSetter(column);
        }
        final var typeGenerator = detectGenerator(typeGenerators, column, null);
        if (typeGenerator != null) {
            System.out.println("Column '" + column.name + "' in '" + table.name + "' bound to " + typeGenerator.name() + " setter");
            return typeGenerator.newValueSetter(column);
        }

        throw new SQLException("Could not find a generator for column '" + column.name +
                "' in table '" + table.name + "' with type '" + column.dataType + "'");
    }

    private static ValueSetter newForeignKeySetter(final Connection connection, final ForeignKey fk) throws SQLException {
        final List<Long> values = selectIds(connection, fk.table, fk.column);
        if (values.isEmpty()) throw new SQLException("No values were created for foreign key");
        return (index, statement) -> statement.setLong(index, randomItemFrom(values));
    }

    private static ValueSetter newPatternSetter(final Column column) {
        final Generex generex = new Generex(column.pattern);
        return (index, statement) -> statement.setString(index, generex.random());
    }
}
