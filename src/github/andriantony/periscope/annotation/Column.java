package github.andriantony.periscope.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.PreparedStatement;

/**
 * This annotation is used to specify that a class field maps to a column in a SQL table.
 * The field using this annotation must be an {@link Object} or its derivatives while also compatible with JDBC's {@link PreparedStatement}.
 * 
 * @author Andriantony
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    
    /**
     * The name of the column in a SQL table.
     * This element is required to specify the column name in query results and assigning the value to its field.
     * 
     * @return the name of the column
     */
    public String name();
    
    /**
     * This element specifies the field's ability to contain null value in SQL operations.
     * The user can still assign null to the field even if this element is set to false, although it will not be accepted during SQL write operations.
     * This element is true by default.
     * 
     * @return the field's ability to contain null value
     */
    public boolean nullable() default true;
    
    /**
     * The maximum length of a value that can be contained by the column during SQL operations.
     * While the user can assign value of higher length than the specified maximum, it will not be accepted during SQL write operations.
     * Set this element's value higher than -1 to enable length checking for its associated field.
     * This element is set to -1 by default, which allows for unlimited value length.
     * 
     * @return the maximum value length this column can contain
     */
    public int length() default -1;
    
    /**
     * The number of decimal places.
     * This element is only used in used only by value of decimal types.
     * 
     * @return the number of decimal paces in a decimal value
     */
    public int scale() default 0;
    
}
