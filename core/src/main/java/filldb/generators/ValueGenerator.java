package filldb.generators;

import filldb.model.Column;

import java.util.Map;
import java.util.function.Function;

/**
 * The {@code ValueGenerator} interface.
 */
public interface ValueGenerator {
    /**
     * Returns the result of {@code name}.
     *
     * @return the result
     */
    String name();
    /**
     * Returns the result of {@code canGenerateFor}.
     *
     * @param column the column
     * @return whether the condition holds
     */
    boolean canGenerateFor(Column column);
    /**
     * Returns the result of {@code newValueSetter}.
     *
     * @param column the column
     * @return the result
     */
    ValueSetter newValueSetter(Column column);

    /**
     * Returns the result of {@code detectGenerator}.
     *
     * @param list the list
     * @param column the column
     * @param defaultValue the default value
     * @return the result
     */
    static ValueGenerator detectGenerator(final Map<String, ValueGenerator> list, final Column column, final ValueGenerator defaultValue) {
        for (final ValueGenerator generator : list.values()) {
            if (generator.canGenerateFor(column))
                return generator;
        }
        return defaultValue;
    }

    /**
     * Returns the result of {@code newValueGenerator}.
     *
     * @param name the name
     * @param canGenerateFor the can generate for
     * @param valueSetter the value setter
     * @return the result
     */
    static ValueGenerator newValueGenerator(final String name, final Function<Column, Boolean> canGenerateFor, final Function<Column, ValueSetter> valueSetter) {
        return new ValueGenerator() {
            public String name() { return name; }
            public boolean canGenerateFor(Column column) {
                return canGenerateFor.apply(column);
            }
            public ValueSetter newValueSetter(Column column) {
                return valueSetter.apply(column);
            }
        };
    }
}

