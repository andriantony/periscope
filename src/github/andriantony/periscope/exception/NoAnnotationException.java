package github.andriantony.periscope.exception;

import github.andriantony.periscope.DatabaseEngine;

/**
 * An exception that is thrown when {@link DatabaseEngine} can not find certain required annotations on its models.
 * 
 * @author Andriantony
 */
public class NoAnnotationException extends Exception {
    
    /**
     * Constructs a new instance of this exception with the specified message.
     * 
     * @param message the error message
     */
    public NoAnnotationException(String message) {
        super(message);
    }
    
}
