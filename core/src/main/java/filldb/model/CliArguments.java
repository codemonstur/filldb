package filldb.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

/**
 * The {@code CliArguments} class.
 */
@CliCommand(name="filldb", description = "FillDB is a tool for inserting test data into arbitrary MariaDB databases.")
public final class CliArguments {

    /**
     * The {@code jdbcUrl} field.
     */
    @CliOption(name = 'c', longName = "connect", isMandatory = true, description = "The JDBC url to connect to")
    public String jdbcUrl;
    /**
     * The {@code numQueries} field.
     */
    @CliOption(name = 'n', longName = "number-of-queries", defaultValue = "10", description = "The number if INSERT queries to execute on each table")
    public int numQueries;
    /**
     * The {@code useCommentPattern} field.
     */
    @CliOption(name = 'r', longName = "regular-expression", description = "Whether to check the comment on a column for a regular expression, if one exists this expression is used to generate data")
    public boolean useCommentPattern;
    /**
     * The {@code username} field.
     */
    @CliOption(name = 'u', longName = "user", defaultValue = "root", description = "The username to use when connecting to the DB")
    public String username;
    /**
     * The {@code password} field.
     */
    @CliOption(name = 'p', longName = "password", defaultValue = "", description = "The password to use when connecting to the DB")
    public String password;
    /**
     * The {@code clearDbFirst} field.
     */
    @CliOption(name = 'd', longName = "delete-all-rows", description = "Whether to delete the data in the DB before inserting")
    public boolean clearDbFirst;
    /**
     * The {@code ignoreErrors} field.
     */
    @CliOption(name = 'i', longName = "ignore-errors", description = "Whether to stop when errors occur or continue trying")
    public boolean ignoreErrors;
    /**
     * The {@code reportQueries} field.
     */
    @CliOption(name = 'q', longName = "print-queries", description = "Whether to print INSERT queries for the generated data")
    public boolean reportQueries;
    /**
     * The {@code skipTablesWithData} field.
     */
    @CliOption(name = 's', longName = "skip-filled-tables", description = "Whether to insert data in tables that already have data")
    public boolean skipTablesWithData;
    /**
     * The {@code allowHumor} field.
     */
    @CliOption(longName = "allow-humor", description = "Various generators are humorous, use this flag to see the data")
    public boolean allowHumor;
    /**
     * The {@code allowRemote} field.
     */
    @CliOption(longName = "allow-remote", description = "Various generators retrieve data from the internet, use this flag to allow this")
    public boolean allowRemote;
    /**
     * The {@code allowNSFW} field.
     */
    @CliOption(longName = "allow-nsfw", description = "One generator is NSFW, this flag turns it on")
    public boolean allowNSFW;
    /**
     * The {@code help} field.
     */
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Display this help")
    public boolean help;


    /**
     * Creates a new instance.
     */
    public CliArguments() {
    }
}
