package filldb.error;

/**
 * The {@code NoSuchGenerator} class.
 */
public final class NoSuchGenerator extends Exception {
    /**
     * Creates a new instance.
     *
     * @param pattern the pattern
     */
    public NoSuchGenerator(final String pattern) {
        super("Could not find a generator for: " + pattern);
    }
}
