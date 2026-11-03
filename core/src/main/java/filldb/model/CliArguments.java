package filldb.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;

@CliCommand(name="filldb", description = "FillDB is a tool for inserting test data into arbitrary MariaDB databases.")
public final class CliArguments {

    @CliOption(name = 'c', longName = "connect", isMandatory = true, description = "The JDBC url to connect to")
    public String jdbcUrl;
    @CliOption(name = 'n', longName = "number-of-queries", defaultValue = "10", description = "The number if INSERT queries to execute on each table")
    public int numQueries;
    @CliOption(name = 'r', longName = "regular-expression", description = "Whether to check the comment on a column for a regular expression, if one exists this expression is used to generate data")
    public boolean useCommentPattern;
    @CliOption(name = 'u', longName = "user", defaultValue = "root", description = "The username to use when connecting to the DB")
    public String username;
    @CliOption(name = 'p', longName = "password", defaultValue = "", description = "The password to use when connecting to the DB")
    public String password;
    @CliOption(name = 'd', longName = "delete-all-rows", description = "Whether to delete the data in the DB before inserting")
    public boolean clearDbFirst;
    @CliOption(name = 'i', longName = "ignore-errors", description = "Whether to stop when errors occur or continue trying")
    public boolean ignoreErrors;
    @CliOption(name = 'q', longName = "print-queries", description = "Whether to print INSERT queries for the generated data")
    public boolean reportQueries;
    @CliOption(name = 's', longName = "skip-filled-tables", description = "Whether to insert data in tables that already have data")
    public boolean skipTablesWithData;
    @CliOption(longName = "allow-humor", description = "Various generators are humorous, use this flag to see the data")
    public boolean allowHumor;
    @CliOption(longName = "allow-remote", description = "Various generators retrieve data from the internet, use this flag to allow this")
    public boolean allowRemote;
    @CliOption(longName = "allow-nsfw", description = "One generator is NSFW, this flag turns it on")
    public boolean allowNSFW;
    @CliOption(name = 'h', longName = "help", isHelp = true, description = "Display this help")
    public boolean help;

}
