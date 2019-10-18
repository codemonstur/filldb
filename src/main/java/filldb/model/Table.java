package filldb.model;

import java.util.ArrayList;
import java.util.List;

public final class Table {

    private final List<Table> parents = new ArrayList<>();

    public final String name;
    public final List<Column> columns;

    public boolean isFilled = false;

    public Table(final String name, final List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public boolean isFillable() {
        for (final Table parent : parents) {
            if (!parent.isFilled) return false;
        }
        return true;
    }

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

    public Column findColumn(final String columnName, final Column defaultValue) {
        for (final var column : columns) {
            if (columnName.equals(column.name))
                return column;
        }
        return defaultValue;
    }
}
