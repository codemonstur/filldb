package filldb.error;

public final class NoSuchGenerator extends Exception {
    public NoSuchGenerator(final String pattern) {
        super("Could not find a generator for: " + pattern);
    }
}
