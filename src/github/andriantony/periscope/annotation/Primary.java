package github.andriantony.periscope.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify that a class field is a SQL primary key.
 * Note that this annotation must be used with the {@link Column} annotation.
 * 
 * @author Andriantony
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Primary {
    
}
