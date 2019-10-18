package filldb.generators;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ValueSetter {

    void setValue(int index, PreparedStatement statement) throws SQLException;

}
