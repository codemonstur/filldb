package filldb.generators;

import filldb.model.Column;

import java.util.Map;
import java.util.function.Function;

public interface ValueGenerator {
    String name();
    boolean canGenerateFor(Column column);
    ValueSetter newValueSetter(Column column);

    static ValueGenerator detectGenerator(final Map<String, ValueGenerator> list, final Column column, final ValueGenerator defaultValue) {
        for (final ValueGenerator generator : list.values()) {
            if (generator.canGenerateFor(column))
                return generator;
        }
        return defaultValue;
    }

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

