package filldb;

import filldb.model.CliArguments;
import jcli.errors.InvalidCommandLine;

import java.sql.SQLException;

import static filldb.FillDb.clearDatabase;
import static filldb.FillDb.fillDatabase;
import static jcli.CliParserBuilder.newCliParser;

public enum Main {;

    public static void main(final String... args) throws SQLException, InvalidCommandLine {
        final CliArguments arguments = newCliParser(CliArguments::new)
            .name("filldb")
            .onErrorPrintHelpAndExit()
            .onHelpPrintHelpAndExit()
            .parse(args);

        if (arguments.clearDbFirst) clearDatabase(arguments);
        fillDatabase(arguments);
    }

}
