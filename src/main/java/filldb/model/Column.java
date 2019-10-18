package filldb.model;

import filldb.generators.ValueSetter;

public final class Column {

    public final boolean isAutoIncrementing;
    public final boolean isPrimaryKey;
    public final boolean isNullable;
    public final boolean isUnique;
    public final String dataType;
    public final Integer characterMaxLength;
    public final String name;
    public final String pattern;

    public ForeignKey fk;
    public ValueSetter valueSetter;

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

    public boolean isForeignKey() {
        return fk != null;
    }

    public boolean hasPattern() {
        return pattern != null && !pattern.isBlank();
    }

}
