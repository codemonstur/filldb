package filldb.core;

import org.mariadb.jdbc.Driver;
import filldb.model.CliArguments;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * The {@code DB} enum.
 */
public enum DB {;

    /**
     * Returns the result of {@code toDbDriverProperties}.
     *
     * @param arguments the arguments
     * @return the result
     */
    public static Properties toDbDriverProperties(final CliArguments arguments) {
        final Properties props = new Properties();
        props.setProperty("user", arguments.username);
        props.setProperty("password", arguments.password);
        return props;
    }

    /**
     * Returns the result of {@code connect}.
     *
     * @param jdbcUrl the jdbc url
     * @param properties the properties
     * @return the result
     * @throws SQLException if the operation fails
     */
    public static Connection connect(final String jdbcUrl, final Properties properties) throws SQLException {
        return new Driver().connect(jdbcUrl, properties);
    }

    /**
     * Performs the {@code executeQueries} operation.
     *
     * @param connection the connection
     * @param queries the queries
     * @throws SQLException if the operation fails
     */
    public static void executeQueries(final Connection connection, final List<String> queries) throws SQLException {
        for (final var query : queries) {
            if (query.isEmpty()) continue;

            try (final var statement = connection.createStatement()) {
                statement.executeUpdate(query);
            }
        }
    }

}
