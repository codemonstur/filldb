package filldb;

import bobthebuildtool.pojos.buildfile.Project;
import filldb.error.NoSuchGenerator;
import jcli.errors.InvalidCommandLine;

import java.sql.SQLException;
import java.util.Map;

public enum BobPlugin {;

    public static void installPlugin(final Project project) {
        project.addCommand("filldb", "Runs filldb with the given arguments", BobPlugin::runFillDB);
    }

    private static int runFillDB(final Project project, final Map<String, String> env, final String[] args)
            throws InvalidCommandLine, SQLException, NoSuchGenerator {
        Main.main(args);
        return 0;
    }

}
