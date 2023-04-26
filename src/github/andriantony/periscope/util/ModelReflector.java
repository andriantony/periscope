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
import github.andriantony.periscope.exception.NotNullableException;
import github.andriantony.periscope.exception.OverLimitException;
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
     * @throws NoAnnotationException if the specified model class is not annotated with the {@link Table} annotation
     */
    public String getName(Model model) throws NoAnnotationException {
        verificator.verifyTableAnnotation(model);
        return model.getClass().getAnnotation(Table.class).name();
    }

    /**
     * Parses the specified model object from the given ResultSet rs, using the
     * specified columns.
     *
     * @param <T> the dynamic type based on {@link Model}
     * @param model The Model derivative object to parse
     * @param columns The columns to retrieve from the ResultSet
     * @param rs The ResultSet which contains the retrievable data
     * @return the new model instance containing retrieved data
     * @throws ClassNotFoundException If the {@link Model} class cannot be found
     * @throws SQLException if a database access error occurs or if the ResultSet object is not valid
     * @throws IllegalAccessException If the Model class or its properties cannot be accessed
     * @throws InstantiationException If the Model class cannot be instantiated
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> T parse(Model model, String[] columns, ResultSet rs) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        Object result = Class.forName(model.getClass().getTypeName()).newInstance();
        LinkedHashMap<String, Field> fields = new LinkedHashMap<>();

        for (Field field : result.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class)) {
                fields.put(field.getAnnotation(Column.class).name(), field);
            }
        }

        if (columns.length > 0) {
            for (String column : columns) {
                fields.get(column).set(result, rs.getObject(column));
            }
        } else {
            for (Map.Entry<String, Field> field : fields.entrySet()) {
                field.getValue().set(result, rs.getObject(field.getKey()));
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
     * @throws IllegalAccessException If the Model class or its properties cannot be accessed
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
     * Returns an array of Expression objects representing the non-primary
     * columns of the given Model object, optionally limited to the specified
     * columns. Each Expression object contains the column name and the
     * corresponding value of the column in the Model object.
     *
     * @param model The Model object to retrieve the non-primary columns from
     * @param columns An array of column names to limit the selection to, or an
     * empty array to select all non-primary columns
     * @return an array of Expression objects representing the selected
     * non-primary columns
     * @throws IllegalAccessException if access to the specified field is denied
     * @throws NotNullableException if a non-nullable field in the Model instance has a null value
     * @throws OverLimitException if a field value in the Model instance exceeds its defined length limit
     */
    public Expression[] getNonPrimaryExpressions(Model model, String[] columns) throws IllegalAccessException, NotNullableException, OverLimitException {
        List<Expression> expressions = new ArrayList<>();

        if (columns.length > 0) {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Primary.class) && Arrays.stream(columns).anyMatch(field.getAnnotation(Column.class).name()::equals)) {
                    verificator.verifyNullability(model, field);
                    verificator.verifyLength(model, field);

                    expressions.add(new Expression(field.getAnnotation(Column.class).name(), field.get(model)));
                }

            }
        } else {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Primary.class)) {
                    verificator.verifyNullability(model, field);
                    verificator.verifyLength(model, field);

                    expressions.add(new Expression(field.getAnnotation(Column.class).name(), field.get(model)));
                }
            }
        }

        return expressions.toArray(new Expression[0]);
    }

    /**
     * Retrieves the primary key field from the given model.
     * 
     * @param model The model to retrieve the field from
     * @return the field object of the primary key column
     * @throws NoSuchColumnException if the {@link Model} class does not have a primary key column
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
     * @throws NoSuchColumnException if the {@link Model} class does not have a column with the given name
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
     * Returns an array of {@link FieldReference} objects for all fields in the given model class that have the
     * {@link Reference} annotation and for which a corresponding {@link ModelReference} object is present in the given
     * array of included models.
     * 
     * @param model The model instance for which to retrieve the field references
     * @param includes An array of included models that may be referenced by the fields in the given model instance
     * @return an array of filtered {@link FieldReference} object
     * @throws NoSuchColumnException if the {@link Model} class does not have a column with the given name
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

    /**
     * Returns an array of expression from unique columns.
     * Unique columns with null value are not included.
     * 
     * @param model The model to extract the fields from
     * @param fieldExpressions The list of field expressions
     * @return an array of expression from unique columns
     */
    public Expression[] getUniqueExpressions(Model model, Expression[] fieldExpressions) {
        List<Expression> result = new ArrayList<>();
        
        for (Field field : model.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).unique()) {
                Expression expression = Arrays.stream(fieldExpressions).filter(expr -> expr.getKey().equals(field.getAnnotation(Column.class).name())).findFirst().orElse(null);
                if (expression != null && expression.getValue() != null)
                    result.add(expression);
            }
        }
        
        return result.toArray(new Expression[0]);
    }

}
