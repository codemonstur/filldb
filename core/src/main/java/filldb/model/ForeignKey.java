package filldb.model;

/**
 * The {@code ForeignKey} class.
 */
public final class ForeignKey {

    /**
     * The {@code table} field.
     */
    public final Table table;
    /**
     * The {@code column} field.
     */
    public final Column column;

    /**
     * Creates a new instance.
     *
     * @param table the table
     * @param column the column
     */
    public ForeignKey(final Table table, final Column column) {
        this.table = table;
        this.column = column;
    }

}
