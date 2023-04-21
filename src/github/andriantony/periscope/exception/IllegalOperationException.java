package github.andriantony.periscope.exception;

import github.andriantony.periscope.DatabaseEngine;
import github.andriantony.periscope.constant.WritePermission;

/**
 * An exception that is thrown when an illegal write operation is attempted on a model.
 * <p>
 * This exception is thrown when the {@link DatabaseEngine} can not find matching {@link WritePermission} in the model's write permission group.
 * </p>
 * 
 * @author Andriantony
 */
public final class IllegalOperationException extends Exception {
    
    /**
     * Constructs a new instance of this exception with the specified message.
     * 
     * @param message the error message
     */
    public IllegalOperationException(String message) {
        super(message);
    }
    
}
