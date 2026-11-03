package filldb;

import bobthebuildtool.pojos.buildfile.Project;
import filldb.error.NoSuchGenerator;
import filldb.model.CliArguments;
import jcli.errors.InvalidCommandLine;

import java.sql.SQLException;
import java.util.Map;

import static filldb.FillDb.clearDatabase;
import static filldb.FillDb.fillDatabase;
import static jcli.CliParserBuilder.newCliParser;

public enum BobPlugin {;

    public static void installPlugin(final Project project) {
        project.addCommand("filldb", "Runs filldb with the given arguments", BobPlugin::runFillDB);
    }

    private static int runFillDB(final Project project, final Map<String, String> env, final String[] args)
            throws InvalidCommandLine, SQLException, NoSuchGenerator {
        final CliArguments arguments = newCliParser(CliArguments::new)
            .onErrorPrintHelpAndExit()
            .onHelpPrintHelpAndExit()
            .parse(args);

        if (arguments.clearDbFirst) clearDatabase(arguments);
        fillDatabase(arguments);
        return 0;
    }

}
