/*
 * The MIT License
 *
 * Copyright 2023 Andriantony.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
     * This element specifies whether this column's value is required to be unique.
     * This element is false by default.
     * 
     * @return the field's requirement to hold unique value
     */
    public boolean unique() default false;
    
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
