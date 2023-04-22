package github.andriantony.periscope.constant;

/**
 * The SortDirection enum represents the sorting direction for SQL queries.
 * It can be either ascending (ASC) or descending (DESC).
 *
 * @author Andriantony
 */
public enum SortDirection {

    /**
     * Represents ascending sort order.
     */
    ASC("ASC"),
    
    /**
     * Represents descending sort order.
     */
    DESC("DESC");

    private final String text;

    SortDirection(final String string) {
        this.text = string;
    }

    @Override
    public String toString() {
        return this.text;
    }

}
