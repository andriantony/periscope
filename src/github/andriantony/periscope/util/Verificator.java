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

import github.andriantony.periscope.annotation.Table;
import github.andriantony.periscope.constant.WritePermission;
import github.andriantony.periscope.exception.IllegalOperationException;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.exception.NotNullableException;
import github.andriantony.periscope.exception.OverLimitException;
import github.andriantony.periscope.exception.UniqueFieldViolationException;
import github.andriantony.periscope.type.ColumnDefinition;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Andriantony
 */
public final class Verificator {
    
    public void verifyTableAnnotation(Class<?> table) throws NoAnnotationException {
        if (!table.isAnnotationPresent(Table.class)) {
            throw new NoAnnotationException("Class " + table.getSimpleName() + " does not have the Table annotation");
        }
    }
    
    public void verifyPermission(Class<?> table, WritePermission permission) throws NoAnnotationException, IllegalOperationException {
        verifyTableAnnotation(table);
        
        WritePermission[] permissions = table.getAnnotation(Table.class).writePermissions();
        if (!Arrays.asList(permissions).contains(permission)) {
            throw new IllegalOperationException("Table " + table.getSimpleName() + " does not have the " + permission + " permission");
        }
    }
    
    public void verifyNonNullableInsertion(LinkedHashMap<String, ColumnDefinition> columnMap, LinkedHashMap<String, ColumnDefinition> allColumns) throws NotNullableException {
        for (Map.Entry<String, ColumnDefinition> entry : allColumns.entrySet()) {
            if (!columnMap.containsKey(entry.getKey())) {
                ColumnDefinition column = entry.getValue();

                if (!column.getColumn().nullable()) {
                    if (column.getPrimary() != null) {
                        if (!column.getPrimary().auto()) {
                            throw new NotNullableException("Non-nullable column " + column.getColumn().name() + " is not marked for insertion");
                        }
                    } else {
                        throw new NotNullableException("Non-nullable column " + column.getColumn().name() + " is not marked for insertion");
                    }
                }
            }
        }
    }
    
    public void verifyNullability(Object entity, LinkedHashMap<String, ColumnDefinition> columnMap) throws NotNullableException, IllegalAccessException {
        for (Map.Entry<String, ColumnDefinition> entry : columnMap.entrySet()) {
            ColumnDefinition column = entry.getValue();

            if (!column.getColumn().nullable() && column.getField().get(entity) == null) {
                if (column.getPrimary() != null) {
                    if (!column.getPrimary().auto()) {
                        throw new NotNullableException("Field " + column.getField().getName() + " contains null value");
                    }
                } else {
                    throw new NotNullableException("Field " + column.getField().getName() + " contains null value");
                }
            }
        }
    }
    
    public void verifyLength(Object entity, LinkedHashMap<String, ColumnDefinition> columnMap) throws OverLimitException, IllegalAccessException {
        for (Map.Entry<String, ColumnDefinition> entry : columnMap.entrySet()) {
            ColumnDefinition column = entry.getValue();
            int maxLength = column.getColumn().length();

            if (maxLength > -1) {
                Object val = column.getField().get(entity);
                int length = val != null ? val.toString().length() : 0;

                if (length > maxLength) {
                    throw new OverLimitException("The value length of field " + column.getField().getName() + " is " + length + ", which is larger than its configured limit of " + maxLength);
                }
            }
        }
    }
    
    public void verifyUniqueness(Object sourceEntity, Object uniqueRow, Field primaryField, Field uniqueField) throws IllegalAccessException, UniqueFieldViolationException {
        if (uniqueRow != null) {
            if (!primaryField.get(sourceEntity).equals(primaryField.get(uniqueRow))) {
                throw new UniqueFieldViolationException("The unique value " + uniqueField.get(uniqueRow) + " already exists");
            }
        }
    }
    
}
