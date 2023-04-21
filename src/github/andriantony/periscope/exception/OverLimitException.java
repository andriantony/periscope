package github.andriantony.periscope.exception;

import github.andriantony.periscope.annotation.Column;

/**
 * An exception that is thrown when a write operation was performed on a column
 * that holds more value length than what's specified in its {@link Column#length()} property.
 * 
 * @author Andriantony
 */
public final class OverLimitException extends Exception {
    
    /**
     * Constructs a new instance of this exception with the specified message.
     * 
     * @param message the error message
     */
    public OverLimitException(String message) {
        super(message);
    }
    
}
