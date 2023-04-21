package github.andriantony.periscope.exception;

import github.andriantony.periscope.DatabaseEngine;
import github.andriantony.periscope.annotation.Column;

/**
 * An exception that is thrown when {@link DatabaseEngine} can not find a {@link Column} from user-provided name.
 * 
 * @author Andriantony
 */
public class NoSuchColumnException extends Exception {
    
    /**
     * Constructs a new instance of this exception with the specified message.
     * 
     * @param message the error message
     */
    public NoSuchColumnException(String message) {
        super(message);
    }
    
}
