package github.andriantony.periscope.constant;

/**
 * An enumeration representing SQL aggregate functions.
 * 
 * @author Andriantony
 */
public enum Function {
    
    /**
     * Represents the COUNT() function.
     * Used to return the number of rows that matches a specified criterion.
     */
    COUNT("COUNT"),
    
    /**
     * Represents the SUM() function.
     * Used to return the sum of specific column.
     */
    SUM("SUM"),
    
    /**
     * Represents the AVG() function.
     * Used to return the average value of specific column.
     */
    AVG("AVG"),
    
    /**
     * Represents the MAX() function.
     * Used to return the maximum value in a column.
     */
    MAX("MAX"),
    
    /**
     * Represents the MIN() function.
     * Used to return the minimum value in a column.
     */
    MIN("MIN");
    
    private final String text;

    Function(final String string) {
        this.text = string;
    }

    @Override
    public String toString() {
        return this.text;
    }
    
}
