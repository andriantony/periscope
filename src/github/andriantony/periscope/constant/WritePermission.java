package github.andriantony.periscope.constant;

import github.andriantony.periscope.DatabaseEngine;
import github.andriantony.periscope.annotation.Table;

/**
 * An enumeration of permissions for managing database write operations.
 * This enumeration must be used in the {@link Table} annotation.
 * 
 * @author Andriantony
 */
public enum WritePermission {

    /**
     * Allows the {@link DatabaseEngine#insert(com.bizmann.periscope.type.Model)} method to be called on this table.
     */
    INSERT("INESRT"),
    
    /**
     * Allows the {@link DatabaseEngine#update(com.bizmann.periscope.type.Model)} method to be called on this table.
     */
    UPDATE("UPDATE"),
    
    /**
     * Allows the {@link DatabaseEngine#delete(com.bizmann.periscope.type.Model)} method to be called on this table.
     */
    DELETE("DELETE");

    public final String operation;

    WritePermission(final String operation) {
        this.operation = operation;
    }

    /**
     * Returns the string representation of the operation.
     * 
     * @return the string representation of the operation
     */
    @Override
    public String toString() {
        return this.operation;
    }

}
