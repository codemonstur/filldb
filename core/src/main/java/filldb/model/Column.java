package filldb.model;

import filldb.generators.ValueSetter;

/**
 * The {@code Column} class.
 */
public final class Column {

    /**
     * The {@code isAutoIncrementing} field.
     */
    public final boolean isAutoIncrementing;
    /**
     * The {@code isPrimaryKey} field.
     */
    public final boolean isPrimaryKey;
    /**
     * The {@code isNullable} field.
     */
    public final boolean isNullable;
    /**
     * The {@code isUnique} field.
     */
    public final boolean isUnique;
    /**
     * The {@code dataType} field.
     */
    public final String dataType;
    /**
     * The {@code characterMaxLength} field.
     */
    public final Integer characterMaxLength;
    /**
     * The {@code name} field.
     */
    public final String name;
    /**
     * The {@code pattern} field.
     */
    public final String pattern;

    /**
     * The {@code fk} field.
     */
    public ForeignKey fk;
    /**
     * The {@code valueSetter} field.
     */
    public ValueSetter valueSetter;

    /**
     * Creates a new instance.
     *
     * @param isAutoIncrementing the is auto incrementing
     * @param isPrimaryKey the is primary key
     * @param isNullable the is nullable
     * @param isUnique the is unique
     * @param dataType the data type
     * @param characterMaxLength the character max length
     * @param name the name
     * @param pattern the pattern
     */
    public Column(final boolean isAutoIncrementing, final boolean isPrimaryKey, boolean isNullable,
                  final boolean isUnique, final String dataType, final Integer characterMaxLength,
                  final String name, final String pattern) {
        this.isAutoIncrementing = isAutoIncrementing;
        this.isPrimaryKey = isPrimaryKey;
        this.isNullable = isNullable;
        this.isUnique = isUnique;
        this.dataType = dataType;
        this.characterMaxLength = characterMaxLength;
        this.name = name;
        this.pattern = pattern;
    }

    /**
     * Returns whether the foreign key condition holds.
     *
     * @return whether the condition holds
     */
    public boolean isForeignKey() {
        return fk != null;
    }

    /**
     * Returns whether the pattern condition holds.
     *
     * @return whether the condition holds
     */
    public boolean hasPattern() {
        return pattern != null && pattern.startsWith("filldb");
    }

}
