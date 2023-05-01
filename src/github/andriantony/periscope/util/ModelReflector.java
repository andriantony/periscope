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
import github.andriantony.periscope.annotation.Primary;
import github.andriantony.periscope.annotation.Reference;
import github.andriantony.periscope.annotation.Table;
import github.andriantony.periscope.constant.Relation;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.exception.NoSuchColumnException;
import github.andriantony.periscope.type.ColumnDefinition;
import github.andriantony.periscope.type.Expression;
import github.andriantony.periscope.type.FieldReference;
import github.andriantony.periscope.type.Model;
import github.andriantony.periscope.type.ModelReference;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class provides reflection methods to access and manipulate table
 * objects.
 *
 * @author Andriantony
 */
public final class ModelReflector {

    private final Verificator verificator = new Verificator();

    /**
     * Returns the name of the table for a given Model object
     *
     * @param model The model derivative object
     * @return the name of table associated with provided model
     * @throws NoAnnotationException if the specified model class is not
     * annotated with the {@link Table} annotation
     */
    public String getName(Model model) throws NoAnnotationException {
        verificator.verifyTableAnnotation(model);
        return model.getClass().getAnnotation(Table.class).name();
    }

    /**
     * Returns the linked hashmap containing columns from a given model.
     *
     * @param model The model to extract columns from
     * @return the list of mapped columns
     * @throws NoAnnotationException if the column is not annotated
     */
    public LinkedHashMap<String, ColumnDefinition> getColumnMap(Model model) throws NoAnnotationException {
        LinkedHashMap<String, ColumnDefinition> columnMap = new LinkedHashMap<>();

        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                columnMap.put(field.getAnnotation(Column.class).name(), new ColumnDefinition(field));
            }
        }

        return columnMap;
    }

    /**
     * Creates a map containing all columns from a model filtered by the given filter.
     * 
     * @param model The model to extract columns from
     * @param columns The list of column names
     * @return the list of mapped columns
     * @throws NoAnnotationException if the column is not annotated
     */
    public LinkedHashMap<String, ColumnDefinition> getColumnMap(Model model, String[] columns) throws NoAnnotationException {
        LinkedHashMap<String, ColumnDefinition> columnMap = new LinkedHashMap<>();
        Supplier<Stream<String>> streamSupplier = () -> Arrays.stream(columns);

        if (columns.length > 0) {
            for (Field field : model.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class) && streamSupplier.get().filter(col -> col.equals(field.getAnnotation(Column.class).name())).findFirst().isPresent()) {
                    field.setAccessible(true);
                    columnMap.put(field.getAnnotation(Column.class).name(), new ColumnDefinition(field));
                }
            }
        } else {
            for (Field field : model.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    if (!field.isAnnotationPresent(Primary.class)) {
                        field.setAccessible(true);
                        columnMap.put(field.getAnnotation(Column.class).name(), new ColumnDefinition(field));
                    } else {
                        if (!field.getAnnotation(Primary.class).auto()) {
                            field.setAccessible(true);
                            columnMap.put(field.getAnnotation(Column.class).name(), new ColumnDefinition(field));
                        }
                    }
                }
            }
        }

        return columnMap;
    }
    
    /**
     * Returns filtered map containing only columns with unique attribute.
     * 
     * @param columnMap The map to get columns from
     * @return filtered map containing only columns with unique attribute
     */
    public LinkedHashMap<String, ColumnDefinition> getUniqueMap(LinkedHashMap<String, ColumnDefinition> columnMap) {
        LinkedHashMap<String, ColumnDefinition> uniqueMap = new LinkedHashMap<>();
        
        for (Map.Entry<String, ColumnDefinition> entry : columnMap.entrySet())
            if (entry.getValue().getColumn().unique())
                uniqueMap.put(entry.getKey(), entry.getValue());
        
        return uniqueMap;
    }

    /**
     * Converts a linked hashmap of columns into an array containing column names.
     * 
     * @param columnMap The map to extract column names from
     * @return an array containing column names
     */
    public String[] toColumnArray(LinkedHashMap<String, ColumnDefinition> columnMap) {
        List<String> columnList = new ArrayList<>();
        
        for (Map.Entry<String, ColumnDefinition> entry : columnMap.entrySet())
            columnList.add(entry.getKey());
        
        return columnList.toArray(new String[0]);
    }
    
    /**
     * Parses the specified model object from the given ResultSet rs, using the
     * specified columns.
     *
     * @param <T> the dynamic type based on {@link Model}
     * @param model The Model derivative object to parse
     * @param columnMap The list of retrieved column maps
     * @param columns The columns to retrieve from the ResultSet
     * @param rs The ResultSet which contains the retrievable data
     * @return the new model instance containing retrieved data
     * @throws ClassNotFoundException If the {@link Model} class cannot be found
     * @throws SQLException if a database access error occurs or if the
     * ResultSet object is not valid
     * @throws IllegalAccessException If the Model class or its properties
     * cannot be accessed
     * @throws InstantiationException If the Model class cannot be instantiated
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> T parse(Model model, LinkedHashMap<String, ColumnDefinition> columnMap, String[] columns, ResultSet rs) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Object result = Class.forName(model.getClass().getTypeName()).newInstance();

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

    /**
     * Returns the expression retrieved from the primary key column of a given
     * model.
     *
     * @param model The model to retrieve the primary expression from
     * @return expression containing value of primary key
     * @throws IllegalAccessException If the Model class or its properties
     * cannot be accessed
     */
    public Expression getPrimaryExpression(Model model) throws IllegalAccessException {
        Expression expression = null;

        for (Field field : model.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Primary.class)) {
                expression = new Expression(field.getAnnotation(Column.class).name(), field.get(model));
            }
        }

        return expression;
    }

    /**
     * Retrieves the primary key field from the given model.
     *
     * @param model The model to retrieve the field from
     * @return the field object of the primary key column
     * @throws NoSuchColumnException if the {@link Model} class does not have a
     * primary key column
     */
    public Field getPrimaryColumn(Model model) throws NoSuchColumnException {
        Field column = null;

        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Primary.class)) {
                column = field;
            }
        }

        if (column != null) {
            return column;
        } else {
            throw new NoSuchColumnException("This model does not have a primary key column");
        }
    }

    /**
     * Retrieves the field that corresponds to the given column name.
     *
     * @param model The model to retrieve the field from
     * @param name The name of the column to retrieve the field for
     * @return field object for the column with the given name
     * @throws NoSuchColumnException if the {@link Model} class does not have a
     * column with the given name
     */
    public Field getColumnByName(Model model, String name) throws NoSuchColumnException {
        Field column = null;

        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).name().equals(name)) {
                column = field;
            }
        }

        if (column != null) {
            return column;
        } else {
            throw new NoSuchColumnException("This model does not have a column with such name");
        }
    }

    /**
     * Returns an array of {@link FieldReference} objects for all fields in the
     * given model class that have the {@link Reference} annotation and for
     * which a corresponding {@link ModelReference} object is present in the
     * given array of included models.
     *
     * @param model The model instance for which to retrieve the field
     * references
     * @param includes An array of included models that may be referenced by the
     * fields in the given model instance
     * @return an array of filtered {@link FieldReference} object
     * @throws NoSuchColumnException if the {@link Model} class does not have a
     * column with the given name
     */
    public FieldReference[] getReferences(Model model, ModelReference[] includes) throws NoSuchColumnException {
        List<FieldReference> fieldReferences = new ArrayList<>();
        Class targetClass = model.getClass();

        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Reference.class)) {
                String fName = field.getAnnotation(Reference.class).name();
                Class fClass = field.getAnnotation(Reference.class).target();
                ModelReference ref = Arrays.stream(includes).filter(include -> fName.equals(include.getName()) && include.getModel().getClass() == fClass).findFirst().orElse(null);

                if (ref != null) {
                    Reference reference = field.getAnnotation(Reference.class);

                    Field sourceField = getColumnByName(model, reference.source());
                    Model targetModel = ref.getModel();
                    String targetColumn = reference.refer();
                    Relation relation = reference.relation();

                    Expression[] baseExpression = new Expression[]{new Expression(targetColumn, null)};
                    Expression[] finalExpressions = Stream.of(baseExpression, targetModel.getExpressions()).flatMap(Stream::of).toArray(Expression[]::new);
                    targetModel.express(finalExpressions);

                    sourceField.setAccessible(true);
                    field.setAccessible(true);

                    fieldReferences.add(new FieldReference(targetModel, sourceField, field, targetColumn, relation));
                }
            }
        }

        return fieldReferences.toArray(new FieldReference[0]);
    }
    
}
