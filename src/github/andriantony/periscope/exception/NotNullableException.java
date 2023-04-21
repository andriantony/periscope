package github.andriantony.periscope.exception;
/**
 * An exception that is thrown when a write attempt is made on a non-nullable column that contains null value.
 * 
 * @author Andriantony
 */
public class NotNullableException extends Exception {
    
    /**
     * Constructs a new instance of this exception with the specified message.
     * 
     * @param message the error message
     */
    public NotNullableException(String message) {
        super(message);
    }
    
}
