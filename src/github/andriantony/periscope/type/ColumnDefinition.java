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
package github.andriantony.periscope.type;

import github.andriantony.periscope.annotation.Column;
import github.andriantony.periscope.annotation.Primary;
import github.andriantony.periscope.exception.NoAnnotationException;
import java.lang.reflect.Field;

/**
 * This class specifies a table columns with all of its attributes.
 * 
 * @author Andriantony
 */
public final class ColumnDefinition {

    private final Field field;
    private final Column column;
    private final boolean isPrimary;

    /**
     * Creates a new instance using the given field.
     * 
     * @param field The field to assign to this instance
     * @throws NoAnnotationException if the field is not annotated with Column annotation
     */
    public ColumnDefinition(Field field) throws NoAnnotationException {
        if (field.isAnnotationPresent(Column.class)) {
            this.field = field;
            this.field.setAccessible(true);

            this.column = field.getAnnotation(Column.class);
            this.isPrimary = field.isAnnotationPresent(Primary.class);
        } else {
            throw new NoAnnotationException("Field " + field.getName() + " does not have toe column annotation.");
        }
    }

    /**
     * Return this instance's column field.
     * 
     * @return this instance's column field
     */
    public Field getField() {
        return field;
    }

    /**
     * Return this instance's column annotation.
     * 
     * @return this instance's column annotation
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Check whether this column is a primary key.
     * 
     * @return a boolean that checks whether this column is a primary key
     */
    public boolean IsPrimary() {
        return isPrimary;
    }

}
