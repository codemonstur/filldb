package filldb.model;

import jcli.CliOption;

public final class CliArguments {

    @CliOption(name = 'c', longName = "connect", isMandatory = true)
    public String jdbcUrl;
    @CliOption(name = 'n', longName = "number-of-queries", defaultValue = "10")
    public int numQueries;
    @CliOption(name = 'r', longName = "regular-expression")
    public boolean useCommentPattern;
    @CliOption(name = 'u', longName = "user", defaultValue = "root")
    public String username;
    @CliOption(name = 'p', longName = "password", defaultValue = "")
    public String password;
    @CliOption(name = 'd', longName = "delete-all-rows")
    public boolean clearDbFirst;
    @CliOption(name = 'i', longName = "ignore-errors")
    public boolean ignoreErrors;
    @CliOption(name = 'q', longName = "print-queries")
    public boolean reportQueries;
    @CliOption(name = 's', longName = "skip-filled-tables")
    public boolean skipTablesWithData;
    @CliOption(longName = "allow-humor")
    public boolean allowHumor;
    @CliOption(longName = "allow-remote")
    public boolean allowRemote;
    @CliOption(longName = "allow-nsfw")
    public boolean allowNSFW;
    @CliOption(name = 'h', longName = "help", isHelp = true)
    public boolean help;

}
