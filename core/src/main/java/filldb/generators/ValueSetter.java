package filldb.generators;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The {@code ValueSetter} interface.
 */
public interface ValueSetter {

    /**
     * Performs the {@code setValue} operation.
     *
     * @param index the new value
     * @param statement the new value
     * @throws SQLException if the operation fails
     */
    void setValue(int index, PreparedStatement statement) throws SQLException;

}
