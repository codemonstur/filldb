package filldb;

import filldb.error.NoSuchGenerator;
import filldb.model.CliArguments;
import jcli.errors.InvalidCommandLine;

import java.sql.SQLException;

import static filldb.FillDb.clearDatabase;
import static filldb.FillDb.fillDatabase;
import static jcli.CliParserBuilder.newCliParser;

/**
 * The {@code Main} enum.
 */
public enum Main {;

    /**
     * Runs the application.
     *
     * @param args the command-line arguments
     * @throws SQLException if the operation fails
     * @throws InvalidCommandLine if the operation fails
     * @throws NoSuchGenerator if the operation fails
     */
    public static void main(final String... args) throws SQLException, InvalidCommandLine, NoSuchGenerator {
        final CliArguments arguments = newCliParser(CliArguments::new)
            .onErrorPrintHelpAndExit()
            .onHelpPrintHelpAndExit()
            .parse(args);

        if (arguments.clearDbFirst) clearDatabase(arguments);
        fillDatabase(arguments);
    }

}
