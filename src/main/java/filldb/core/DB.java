package filldb.core;

import filldb.model.CliArguments;
import org.mariadb.jdbc.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public enum DB {;

    public static Properties toDbDriverProperties(final CliArguments arguments) {
        final Properties props = new Properties();
        props.setProperty("user", arguments.username);
        props.setProperty("password", arguments.password);
        return props;
    }

    public static Connection connect(final String jdbcUrl, final Properties properties) throws SQLException {
        final Driver driver = new Driver();
        return driver.connect(jdbcUrl, properties);
    }

    public static void executeQueries(final Connection connection, final List<String> queries) throws SQLException {
        for (final var query : queries) {
            try (final var statement = connection.createStatement()) {
                statement.executeUpdate(query);
            }
        }
    }

}
