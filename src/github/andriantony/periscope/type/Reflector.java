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
import github.andriantony.periscope.annotation.Reference;
import github.andriantony.periscope.annotation.Table;
import github.andriantony.periscope.constant.Relation;
import github.andriantony.periscope.exception.NoAnnotationException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Andriantony
 */
public final class Reflector {

    private final _Verificator verificator = new _Verificator();

    public String getTableName(Class<?> table) throws NoAnnotationException {
        verificator.verifyTableAnnotation(table);
        return table.getAnnotation(Table.class).name();
    }

    public LinkedHashMap<String, ColumnDefinition> getColumnMap(Class<?> table) throws NoAnnotationException {
        LinkedHashMap<String, ColumnDefinition> columnMap = new LinkedHashMap<>();

        for (Field field : table.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                columnMap.put(field.getAnnotation(Column.class).name(), new ColumnDefinition(field));
            }
        }

        return columnMap;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T parse(Class<?> table, LinkedHashMap<String, ColumnDefinition> columnMap, String[] columns, ResultSet rs) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Object result = Class.forName(table.getTypeName()).newInstance();
        
        if (columns.length > 0) {
            for (String column : columns) {
                columnMap.get(column).getField().set(result, rs.getObject(column));
            }
        } else {
            for (Map.Entry<String, ColumnDefinition> columnDefEntry : columnMap.entrySet()) {
                columnDefEntry.getValue().getField().set(result, rs.getObject(columnDefEntry.getKey()));
            }
        }
        
        return (T) result;
    }
    
    public _FieldReference[] getReferences(Class<?> table, TableReference[] tableReferences, LinkedHashMap<String, ColumnDefinition> columnMap) {
        List<_FieldReference> fieldReferences = new ArrayList<>();
        
        for (Field field : table.getDeclaredFields()) {
            if (field.isAnnotationPresent(Reference.class)) {
                Reference reference = field.getAnnotation(Reference.class);
                String referenceName = reference.name();
                
                TableReference tableReference = Arrays.stream(tableReferences).filter(tblRef -> tblRef.getName().equals(referenceName)).findFirst().orElse(null);
                
                if (tableReference != null) {
                    Field sourceField = columnMap.get(reference.source()).getField();
                    Class<?> targetClass = reference.target();
                    Relation relation = reference.relation();
                    
                    Expression[] baseExpression = new Expression[] {new Expression(reference.refer(), null)};
                    Expression[] finalExpression = Stream.of(baseExpression, tableReference.getModifier().getExpressions()).flatMap(Stream::of).toArray(Expression[]::new);
                    
                    tableReference.getModifier().express(finalExpression);
                    field.setAccessible(true);
                    
                    fieldReferences.add(new _FieldReference(targetClass, sourceField, field, tableReference.getModifier(), relation));
                }
            }
        }
        
        return fieldReferences.toArray(new _FieldReference[0]);
    }

}
