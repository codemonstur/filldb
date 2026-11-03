package filldb.model;

public final class ForeignKey {

    public final Table table;
    public final Column column;

    public ForeignKey(final Table table, final Column column) {
        this.table = table;
        this.column = column;
    }

}
