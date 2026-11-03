package filldb.core;

import filldb.model.Column;
import filldb.model.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public enum Schema {;

    private static final String SELECT_DATABASE = "SELECT DATABASE();";
    private static final String SELECT_TABLES = "SHOW TABLES;";
    private static final String SELECT_COLUMN_NAMES =
        "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='%s' AND TABLE_NAME='%s';";
    private static final String SELECT_FOREIGN_KEYS =
        "SELECT \n" +
        "  COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME\n" +
        "FROM\n" +
        "  INFORMATION_SCHEMA.KEY_COLUMN_USAGE\n" +
        "WHERE\n" +
        "  REFERENCED_TABLE_SCHEMA = '%s' AND\n" +
        "  TABLE_NAME = '%s';";
    private static final String SELECT_IDS = "SELECT %s FROM %s;";

    public static List<Table> downloadSchema(final Connection connection, final boolean skipTablesWithData) throws SQLException {
        final List<Table> tables = new ArrayList<>();

        final String dbname = getDatabaseName(connection);

        final Map<String, Table> tableMap = new HashMap<>();
        final List<String> tableNames = listTableNames(connection);
        for (final var tableName : tableNames) {
            final Table table = new Table(tableName, listColumns(connection, dbname, tableName));
            if (skipTablesWithData) table.isFilled = hasData(connection, table);
            tables.add(table);
            tableMap.put(table.name, table);
        }

        for (final var table : tables) {
            extractForeignKeys(connection, dbname, table, tableMap);
        }

        return tables;
    }

    private static boolean hasData(final Connection connection, final Table table) throws SQLException {
        try (final var statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(format(SELECT_IDS, "*", table.name));
            return resultSet.next();
        }
    }


    private static String getDatabaseName(final Connection connection) throws SQLException {
        try (final var dbName = connection.createStatement().executeQuery(SELECT_DATABASE)) {
            if (dbName.next()) return dbName.getString(1);
            throw new SQLException("No database selected");
        }
    }

    public static List<String> listTableNames(final Connection connection) throws SQLException {
        final List<String> names = new ArrayList<>();

        try (final var tables = connection.createStatement().executeQuery(SELECT_TABLES)) {
            while (tables.next()) {
                names.add(tables.getString(1));
            }
        }

        return names;
    }

    private static List<Column> listColumns(final Connection connection, final String dbName,
                                            final String tableName) throws SQLException {
        final List<Column> names = new ArrayList<>();

        try (final var columns = connection.createStatement().executeQuery(format(SELECT_COLUMN_NAMES, dbName, tableName))) {
            while (columns.next()) {
                names.add(resultSetRowToColumn(columns));
            }
        }

        return names;
    }

    private static Column resultSetRowToColumn(final ResultSet columns) throws SQLException {
        final String columnName = columns.getString("COLUMN_NAME");
        final boolean isNullable = isNullableColumn(columns.getString("IS_NULLABLE"));
        final boolean isUnique = isUnique(columns.getString("COLUMN_KEY"));
        final boolean isAutoIncrementing = isExtraAutoIncrementing(columns.getString("EXTRA"));
        final boolean isPrimaryKey = isPrimaryKey(columns.getString("COLUMN_KEY"));
        final String dataType = columns.getString("DATA_TYPE");
        final Integer characterMaxLength = toMaxCharacterLength(columns.getString("CHARACTER_MAXIMUM_LENGTH"));
        final String pattern = columns.getString("COLUMN_COMMENT");

        return new Column(isAutoIncrementing, isPrimaryKey, isNullable, isUnique, dataType, characterMaxLength, columnName, pattern);
    }

    private static boolean isPrimaryKey(final String columnKey) {
        return columnKey.contains("PRI");
    }

    private static boolean isUnique(final String columnKey) {
        return columnKey.contains("UNI");
    }

    private static Integer toMaxCharacterLength(final String value) throws SQLException {
        if (value == null || value.isEmpty()) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new SQLException("Unparsable number in character max length: " + value);
        }
    }

    private static boolean isExtraAutoIncrementing(final String extra) {
        if (extra == null || extra.isEmpty()) return false;
        return extra.contains("auto_increment");
    }

    private static boolean isNullableColumn(final String value) throws SQLException {
        if ("NO".equals(value)) return false;
        if ("YES".equals(value)) return true;
        throw new SQLException("Unrecognized nullable value: " + value);
    }

    private static void extractForeignKeys(final Connection connection, final String dbName, final Table table,
                                           final Map<String, Table> map) throws SQLException {
        try (final var columns = connection.createStatement().executeQuery(format(SELECT_FOREIGN_KEYS, dbName, table.name))) {
            while (columns.next()) {
                final Table referencedTable = getReferencedTable(columns, map);
                final Column referencedColumn = getReferencedColumn(columns, referencedTable);
                final String columnName = columns.getString("COLUMN_NAME");

                table.addForeignKey(columnName, referencedTable, referencedColumn);
            }
        }
    }

    private static Table getReferencedTable(final ResultSet columns, final Map<String, Table> map) throws SQLException {
        final String referencedTableName = columns.getString("REFERENCED_TABLE_NAME");
        final Table referencedTable = map.get(referencedTableName);
        if (referencedTable == null) throw new SQLException("Referenced table " + referencedTableName + " does not exist in the database");
        return referencedTable;
    }

    private static Column getReferencedColumn(final ResultSet columns, final Table table) throws SQLException {
        final String referencedColumnName = columns.getString("REFERENCED_COLUMN_NAME");
        final Column referencedColumn = table.findColumn(referencedColumnName, null);
        if (referencedColumn == null) throw new SQLException("Referenced column " + referencedColumnName + " does not exist in table " + table.name);
        return referencedColumn;
    }

    public static List<Long> selectIds(final Connection connection, final Table table, final Column column) throws SQLException {
        final List<Long> ids = new ArrayList<>();
        try (final var columns = connection.createStatement().executeQuery(format(SELECT_IDS, column.name, table.name))) {
            while (columns.next()) {
                ids.add(Long.parseLong(columns.getString(1)));
            }
        }
        return ids;
    }
}
