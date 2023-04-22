package github.andriantony.periscope.type;

import github.andriantony.periscope.constant.SortDirection;

/**
 * Represents an ORDER BY directive in SQL queries.
 * 
 * @author Andriantony
 */
public final class Sort {

    protected final String column;
    protected final SortDirection direction;

    /**
     * Creates a new instance with provided name.
     * The sort direction is defaulted to {@link SortDirection#ASC}.
     * 
     * @param column The name of the column to sort by
     */
    public Sort(String column) {
        this.column = column;
        this.direction = SortDirection.ASC;
    }

    /**
     * Creates a new instance with provided name and sorting direction.
     * 
     * @param column The name of the column to sort by
     * @param direction The sorting direction
     */
    public Sort(String column, SortDirection direction) {
        this.column = column;
        this.direction = direction;
    }
    
    /**
     * Returns the name of the column to sort by.
     * 
     * @return the name of the column to sort by
     */
    public String getColumn() {
        return this.column;
    }
    
    /**
     * Returns the sorting direction.
     * 
     * @return the sorting direction
     */
    public SortDirection getDirection() {
        return this.direction;
    }

}
