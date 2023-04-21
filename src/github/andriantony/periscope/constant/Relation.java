package github.andriantony.periscope.constant;

import github.andriantony.periscope.annotation.Reference;
import github.andriantony.periscope.type.Model;
import java.util.List;

/**
 * The relation between one table and its references.
 * 
 * @author Andriantony
 */
public enum Relation {
    
    /**
     * Represents an one-to-one relation.
     * The {@link Reference} annotation using this must decorate a {@link Model} derivative class.
     */
    TO_ONE,
    
    /**
     * Represents an one-to-many relation.
     * The {@link Reference} annotation using this must decorate a {@link List} of {@link Model} derivative classes.
     */
    TO_MANY
    
}
