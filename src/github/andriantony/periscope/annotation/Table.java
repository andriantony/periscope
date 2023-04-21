package github.andriantony.periscope.annotation;

import github.andriantony.periscope.constant.WritePermission;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify that a class is mapped to a SQL table.
 * 
 * @author Andriantony
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    
    /**
     * The name of mapped SQL table.
     * The specified name is used as the target table name in SQL query generation.
     * 
     * @return the name of the mapped SQL table
     */
    public String name();
    
    /**
     * An array of allowed write operations for this table.
     * By default, this array includes all write permissions.
     * This array can be modified to change the scope of operations allowed on this table.
     * 
     * @return the array of write permissions
     */
    public WritePermission[] writePermissions() default { WritePermission.INSERT, WritePermission.UPDATE, WritePermission.DELETE };
    
}
