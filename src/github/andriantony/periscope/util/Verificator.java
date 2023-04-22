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
package github.andriantony.periscope.util;

import github.andriantony.periscope.annotation.Column;
import github.andriantony.periscope.annotation.Table;
import github.andriantony.periscope.constant.WritePermission;
import github.andriantony.periscope.exception.IllegalOperationException;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.exception.NotNullableException;
import github.andriantony.periscope.exception.OverLimitException;
import github.andriantony.periscope.exception.UniqueFieldViolationException;
import github.andriantony.periscope.type.Model;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A class used to verify various rules specified to a table.
 * 
 * @author Andriantony
 */
public class Verificator {

    /**
     * Verify whether the {@link Model} derivative class have a {@link Table}
     * annotation.
     *
     * @param model The derived table class derived from {@link Model}
     * @throws NoAnnotationException Thrown when the {@code model} does not
     * contain the {@link Table} annotation
     */
    public void verifyTableAnnotation(Model model) throws NoAnnotationException {
        Class<?> modelClass = model.getClass();

        if (!modelClass.isAnnotationPresent(Table.class)) {
            throw new NoAnnotationException("Class " + modelClass.getSimpleName() + " does not have the Table annotation");
        }
    }

    /**
     * Verify whether the field can contain null value.
     *
     * @param model The derived table class derived from {@link Model}
     * @param field The {@link Field} from its model object
     * @throws IllegalAccessException if there is an error accessing the field value
     * @throws NotNullableException if the field is marked as not nullable and contains a null value
     */
    public void verifyNullability(Model model, Field field) throws IllegalAccessException, NotNullableException {
        if (!field.getAnnotation(Column.class).nullable() && field.get(model) == null) {
            throw new NotNullableException("Field " + field.getName() + " contains null value");
        }
    }

    /**
     * Verify whether the value in a field is within its configured limit.
     * If the maximum length is not specified (i.e. it is -1), then no verification will be performed.
     * 
     * @param model The derived table class derived from {@link Model}
     * @param field The {@link Field} from its model object
     * @throws OverLimitException if the length of the field value exceeds the maximum length allowed
     * @throws IllegalAccessException if the specified field is inaccessible (i.e. not public)
     */
    public void verifyLength(Model model, Field field) throws OverLimitException, IllegalAccessException {
        int maxLength = field.getAnnotation(Column.class).length();

        if (maxLength > -1) {
            Object val = field.get(model);
            int length = val != null ? val.toString().length() : 0;

            if (length > maxLength) {
                throw new OverLimitException("The value length of field " + field.getName() + " is " + length + ", which is larger than its configured limit of " + maxLength);
            }
        }
    }

    /**
     * Verify whether the model's {@link Table} annotation contains a permission in its {@link Table#writePermissions()} array.
     * 
     * @param model The derived table class derived from {@link Model}
     * @param permission The permission to check
     * @throws NoAnnotationException if the model or its corresponding table is not annotated with the required annotations
     * @throws IllegalOperationException if the table does not have the specified write permission
     */
    public void verifyPermission(Model model, WritePermission permission) throws NoAnnotationException, IllegalOperationException {
        verifyTableAnnotation(model);

        Class<?> modelClass = model.getClass();
        WritePermission[] permissions = modelClass.getAnnotation(Table.class).writePermissions();

        if (!Arrays.asList(permissions).contains(permission)) {
            throw new IllegalOperationException("Table " + modelClass.getSimpleName() + " does not have the " + permission + " permission");
        }
    }
    
    /**
     * Verify whether two models have conflicting unique field values.
     * Will throw an exception if both of them have different primary key values.
     * 
     * @param sourceModel The model object to compare against the uniqueRow object
     * @param uniqueRow The model object to compare against the sourceModel object
     * @param primaryField The primary key field that uniquely identifies the model
     * @param uniqueField The field to retrieve the name of in an exception
     * @throws IllegalAccessException if there was an error accessing the primaryField or uniqueField
     * @throws UniqueFieldViolationException if the primaryField value of the sourceModel object doesn't match with the primaryField value of the uniqueRow object
     */
    public void verifyUniqueness(Model sourceModel, Model uniqueRow, Field primaryField, Field uniqueField) throws IllegalAccessException, UniqueFieldViolationException {
        if (uniqueRow != null) {
            if (!primaryField.get(sourceModel).equals(primaryField.get(uniqueRow)))
                throw new UniqueFieldViolationException("The unique value " + uniqueField.get(uniqueRow) + " already exists");
        }
    }

}
