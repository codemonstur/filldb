package filldb.model;

/**
 * The {@code Value} class.
 *
 * @param <T> the type parameter
 */
public final class Value<T> {
    /**
     * The {@code value} field.
     */
    public T value;

    /**
     * Creates a new instance.
     *
     * @param value the value
     */
    public Value(final T value) {
        this.value = value;
    }
}
