package filldb.generators;

import com.mifmif.common.regex.Generex;
import com.mifmif.common.regex.util.Iterator;
import filldb.model.Column;
import filldb.model.Value;

import java.util.Map;
import java.util.function.Function;

import static filldb.core.Constants.LORUM_IPSUM;
import static filldb.core.Util.isTrue;
import static filldb.core.Util.randomInt;
import static filldb.generators.ValueGenerator.newValueGenerator;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Map.entry;

public enum TypeGenerators {;

    public static Map<String, ValueGenerator> newTypeGenerators() {
        return Map.ofEntries
            ( entry("bigint", newBigIntGenerator())
            , entry("bit", newBitGenerator())
            , entry("int", newIntGenerator())
            , entry("varchar", newVarCharGenerator())
            , entry("text", newTextGenerator())
            , entry("decimal", newDecimalGenerator())
            );
    }

    public static ValueGenerator newTextGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> "text".equals(column.dataType);
        return newValueGenerator("Fallback text", canGenerateFor, column ->
            (index, statement) -> statement.setString(index, LORUM_IPSUM));
    }

    public static ValueGenerator newVarCharGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> "varchar".equals(column.dataType);

        return newValueGenerator("Fallback varchar", canGenerateFor, column -> {
            final int minLength = column.characterMaxLength > 5 ? column.characterMaxLength - 5 : column.characterMaxLength;
            final int maxLength = column.characterMaxLength;
            final Generex generator = new Generex(format("[a-zA-Z]{%d,%d}", minLength, maxLength));
            if (!column.isUnique) {
                return (index, statement) -> statement.setString(index, generator.random());
            } else {
                final Iterator it = generator.iterator();
                return (index, statement) -> {
                    if (!it.hasNext())
                        throw new IllegalArgumentException("Not possible to generate additional values for column " + column.name + " with type " + column.dataType);
                    statement.setString(index, it.next());
                };
            }
        });
    }

    public static ValueGenerator newIntGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> "int".equals(column.dataType);
        return newValueGenerator("Fallback int", canGenerateFor, column -> {
            if (!column.isUnique) {
                return (index, statement) -> statement.setInt(index, (int) (random() * Integer.MAX_VALUE));
            } else {
                final var value = new Value<>(0);
                return (index, statement) -> {
                    value.value++;
                    statement.setInt(index, value.value);
                };
            }
        });
    }

    public static ValueGenerator newBigIntGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> "bigint".equals(column.dataType);
        return newValueGenerator("Fallback bigint", canGenerateFor, column -> {
            if (!column.isUnique) {
                return (index, statement) -> statement.setLong(index, (long) (random() * Long.MAX_VALUE));
            } else {
                final var value = new Value<>(0L);
                return (index, statement) -> {
                    value.value++;
                    statement.setLong(index, value.value);
                };
            }
        });
    }

    public static ValueGenerator newBitGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> "bit".equals(column.dataType);
        return newValueGenerator("Fallback bit", canGenerateFor, column ->
            (index, statement) -> statement.setBoolean(index, isTrue(round(random()))));
    }

    public static ValueGenerator newDecimalGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> "decimal".equals(column.dataType);
        return newValueGenerator("Fallback decimal", canGenerateFor, column ->
            (index, statement) -> statement.setInt(index, randomInt(50)));
    }
}
