package github.andriantony.periscope.constant;

/**
 * An enumeration of SQL comparison operators.
 * 
 * @author Andriantony
 */
public enum Operator {

    /**
     * Represents the SQL "=" operator.
     */
    EQUAL("="),
    
    /**
     * Represents the SQL "!=" operator.
     */
    NOT_EQUAL("!="),
    
    /**
     * Represents the SQL "&lt;&gt;" operator.
     * Provides the same effect as {@link Operator#NOT_EQUAL} for compatibility with older SQL standards.
     */
    NOT_EQUAL_ANSI("<>"),
    
    /**
     * Represents the SQL "&gt;" operator.
     */
    MORE(">"),
    
    /**
     * Represents the SQL "&lt;" operator.
     */
    LESS("<"),
    
    /**
     * Represents the SQL "&gt;=" operator.
     */
    EQUAL_OR_MORE(">="),
    
    /**
     * Represents the SQL "&lt;=" operator.
     */
    EQUAL_OR_LESS("<="),
    
    /**
     * Represents the SQL "IS" clause.
     */
    IS("IS"),
    
    /**
     * Represents the SQL "IS NOT" clause.
     */
    IS_NOT("IS NOT"),
    
    /**
     * Represents the SQL "LIKE" clause.
     */
    LIKE("LIKE"),
    
    /**
     * Represents the SQL "NOT LIKE" clause.
     */
    NOT_LIKE("NOT LIKE");

    private final String text;

    Operator(final String string) {
        this.text = string;
    }

    /**
     * Gets the operator symbol.
     * 
     * @return string representative of operator symbol
     */
    @Override
    public String toString() {
        return this.text;
    }

}
