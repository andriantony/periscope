package github.andriantony.periscope.constant;

import github.andriantony.periscope.type.Expression;

/**
 * The conjunction operator used to chain one {@link Expression} to the next.
 * This setting will not take effect in the last expression entry in its array.
 * 
 * @author Andriantony
 */
public enum Conjunction {
    
    /**
     * A SQL "AND" operator.
     * Used to specify that this element in a group must be true.
     */
    AND("AND"),
    
    /**
     * A SQL "OR" operator.
     * Used to specify at least one of the elements is true.
     */
    OR("OR");
    
    private final String text;

    Conjunction(final String string) {
        this.text = string;
    }

    @Override
    public String toString() {
        return this.text;
    }
    
}
