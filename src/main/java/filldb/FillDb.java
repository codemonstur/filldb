package filldb;

import filldb.core.Generate;
import filldb.model.CliArguments;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static filldb.core.DB.*;
import static filldb.core.Schema.downloadSchema;
import static filldb.core.Schema.listTableNames;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

public enum FillDb {;

    public static void clearDatabase(final CliArguments arguments) throws SQLException {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            final List<String> tables = listTableNames(connection);
            final List<String> queries = tables.stream().map(table -> "DELETE FROM " + table + ";").collect(toList());

            executeQueries(connection, queries);
        }
    }

    public static void fillDatabase(final CliArguments arguments) throws SQLException {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            final List<String> queries = Generate.fillDatabase(connection,
                    downloadSchema(connection, arguments.skipTablesWithData), arguments);
            if (arguments.reportQueries) System.out.println(join("\n", queries));
        }
    }

    public static List<String> generateInsertQueries(final CliArguments arguments) throws SQLException {
        try (final Connection connection = connect(arguments.jdbcUrl, toDbDriverProperties(arguments))) {
            return Generate.generateInsertQueries(connection,
                downloadSchema(connection, false), arguments);
        }
    }
}
