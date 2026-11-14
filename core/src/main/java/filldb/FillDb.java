package filldb;

import filldb.core.Generate;
import filldb.error.NoSuchGenerator;
import filldb.model.CliArguments;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static filldb.core.DB.*;
import static filldb.core.Schema.downloadSchema;
import static filldb.core.Schema.listTableNames;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

/**
 * The {@code FillDb} enum.
 */
public enum FillDb {;

    /**
     * Performs the {@code clearDatabase} operation.
     *
     * @param arguments the arguments
     * @throws SQLException if the operation fails
     */
    public static void clearDatabase(final CliArguments arguments) throws SQLException {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            executeQueries(connection, toDeleteFromQueries(listTableNames(connection)));
        }
    }

    /**
     * Performs the {@code fillDatabase} operation.
     *
     * @param arguments the arguments
     * @throws SQLException if the operation fails
     * @throws NoSuchGenerator if the operation fails
     */
    public static void fillDatabase(final CliArguments arguments) throws SQLException, NoSuchGenerator {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            final List<String> queries = Generate.fillDatabase(connection,
                    downloadSchema(connection, arguments.skipTablesWithData), arguments);
            if (arguments.reportQueries) System.out.println(join("\n", queries));
        }
    }

    /**
     * Returns the result of {@code listInsertQueries}.
     *
     * @param arguments the arguments
     * @return the result
     * @throws SQLException if the operation fails
     */
    public static List<String> listInsertQueries(final CliArguments arguments) throws SQLException {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            return Generate.generateInsertQueries(connection,
                downloadSchema(connection, false), arguments);
        }
    }

    /**
     * Returns the result of {@code listDeleteQueries}.
     *
     * @param arguments the arguments
     * @return the result
     * @throws SQLException if the operation fails
     */
    public static List<String> listDeleteQueries(final CliArguments arguments) throws SQLException {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            return toDeleteFromQueries(listTableNames(connection));
        }
    }

    private static List<String> toDeleteFromQueries(final List<String> tables) {
        return tables.stream().map(table -> "DELETE FROM `" + table + "`;").collect(toList());
    }

}
