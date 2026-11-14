package filldb.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Table} class.
 */
public final class Table {

    private final List<Table> parents = new ArrayList<>();

    /**
     * The {@code name} field.
     */
    public final String name;
    /**
     * The {@code columns} field.
     */
    public final List<Column> columns;

    /**
     * The {@code isFilled} field.
     */
    public boolean isFilled = false;

    /**
     * Creates a new instance.
     *
     * @param name the name
     * @param columns the columns
     */
    public Table(final String name, final List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     * Returns whether the fillable condition holds.
     *
     * @return whether the condition holds
     */
    public boolean isFillable() {
        for (final Table parent : parents) {
            if (!parent.isFilled) return false;
        }
        return true;
    }

    /**
     * Performs the {@code addForeignKey} operation.
     *
     * @param columnName the column name
     * @param referencedTable the referenced table
     * @param referencedColumn the referenced column
     */
    public void addForeignKey(final String columnName, final Table referencedTable, final Column referencedColumn) {
        for (final Column column : columns) {
            if (column.name.equals(columnName)) {
                column.fk = new ForeignKey(referencedTable, referencedColumn);
                parents.add(referencedTable);
                return;
            }
        }
        throw new IllegalArgumentException("Foreign key on column that doesn't exist: " + columnName);
    }

    /**
     * Returns the result of {@code findColumn}.
     *
     * @param columnName the column name
     * @param defaultValue the default value
     * @return the result
     */
    public Column findColumn(final String columnName, final Column defaultValue) {
        for (final var column : columns) {
            if (columnName.equals(column.name))
                return column;
        }
        return defaultValue;
    }
}
