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
package github.andriantony.periscope;

import github.andriantony.periscope.annotation.Column;
import github.andriantony.periscope.constant.Function;
import github.andriantony.periscope.constant.WritePermission;
import github.andriantony.periscope.exception.IllegalOperationException;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.exception.NoSuchColumnException;
import github.andriantony.periscope.exception.NotNullableException;
import github.andriantony.periscope.exception.OverLimitException;
import github.andriantony.periscope.exception.UniqueFieldViolationException;
import github.andriantony.periscope.type.Expression;
import github.andriantony.periscope.type.FieldReference;
import github.andriantony.periscope.type.Model;
import github.andriantony.periscope.type.ModelReference;
import github.andriantony.periscope.type.Sort;
import github.andriantony.periscope.util.ModelReflector;
import github.andriantony.periscope.util.QueryBuilder;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import jdk.xml.internal.XMLSecurityManager.Limit;

/**
 * The DatabaseEngine class provides a set of methods for interacting with a
 * relational database It encapsulates the connection to the database, a
 * ModelReflector instance for retrieving and manipulating metadata of Model
 * classes, and a QueryBuilder instance for constructing SQL queries. .
 *
 * @author Andriantony
 */
public final class DatabaseEngine {

    private final Connection connection;
    private final ModelReflector reflector;
    private final QueryBuilder builder;

    /**
     * Creates a new instance using the active {@link Connection}.
     *
     * @param connection The active SQL connection
     */
    public DatabaseEngine(Connection connection) {
        this.connection = connection;
        this.reflector = new ModelReflector();
        this.builder = new QueryBuilder();
    }

    /**
     * Retrieves a list of records from a database table corresponding to a
     * given model.
     *
     * @param <T> The dynamic type of returned object extending {@link Model}
     * @param model An instance of a subclass of {@link Model} that represents
     * the database table to query.
     * @return A List of instances of type T, where T is a subclass of Model
     * that corresponds to the input model.
     * @throws NoAnnotationException if the Model parameter does not have the
     * necessary annotations
     * @throws SQLException if there is an error executing the SQL query
     * @throws ClassNotFoundException if the specified class cannot be found
     * @throws IllegalAccessException if access to the declared field is denied
     * @throws InstantiationException if there is an issue while creating a new
     * instance of a class
     * @throws NoSuchColumnException if the specified column name does not exist
     * in the model
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> List<T> list(Model model) throws NoAnnotationException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchColumnException {
        List<Model> results = new ArrayList<>();

        String tableName = reflector.getName(model);
        String[] columns = model.getMarkedColumns();
        Expression[] expressions = model.getExpressions();
        Sort[] sorts = model.getSorts();
        ModelReference[] includedModels = model.getIncludes();

        builder.reset();
        builder.select(tableName, columns).where(expressions).orderBy(sorts);

        try (PreparedStatement statement = connection.prepareCall(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    results.add(reflector.parse(model, columns, rs));
                }
            }
        }

        if (includedModels.length > 0) {
            FieldReference[] refs = reflector.getReferences(model, includedModels);

            for (Model result : results) {
                for (FieldReference ref : refs) {
                    Object refValue = ref.getSourceField().get(result);
                    ref.getTargetModel().getExpressions()[0] = new Expression(ref.getTargetColumn(), refValue);

                    switch (ref.getRelation()) {
                        case TO_MANY:
                            ref.getTargetField().set(result, list(ref.getTargetModel()));
                            break;
                        case TO_ONE:
                            ref.getTargetField().set(result, get(ref.getTargetModel()));
                            break;
                    }
                }
            }
        }

        return (List<T>) results;
    }

    /**
     * Retrieves a single record from the database that matches the given model
     * object.It calls the
     * {@link DatabaseEngine#list(com.bizmann.periscope.type.Model)} and returns
     * the first object in the list.
     *
     * @param <T> The dynamic type of returned object extending {@link Model}
     * @param model The Retrieves a single record from the database that matches
     * the given model object
     * @return the first record as a model object, or null if the resulting
     * array is empty
     * @throws NoAnnotationException if the Model parameter does not have the
     * necessary annotations
     * @throws SQLException if there is an error executing the SQL query
     * @throws ClassNotFoundException if the specified class cannot be found
     * @throws IllegalAccessException if access to the declared field is denied
     * @throws InstantiationException if there is an issue while creating a new
     * instance of a class
     * @throws NoSuchColumnException if the specified column name does not exist
     * in the model
     */
    public <T extends Model> T get(Model model) throws NoAnnotationException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchColumnException {
        List<T> results = list(model);

        return !results.isEmpty() ? results.get(0) : null;
    }
    
    /**
     * Executes an SQL aggregate function on the specified column of the provided model, based on the specified function.
     * 
     * @param <T> The expected return type of the function
     * @param model The model to execute the function on
     * @param function The aggregate function to execute
     * @return the result of the executed function
     * @throws SQLException if there is an error executing the SQL query
     * @throws NoAnnotationException  if the model class or column does not have the required annotation
     */
    @SuppressWarnings("unchecked")
    public <T> T function(Model model, Function function) throws SQLException, NoAnnotationException {
        Object result = null;
        
        String tableName = reflector.getName(model);
        String[] columns = model.getMarkedColumns();
        Expression[] expressions = model.getExpressions();
        
        if (function != null) {
            builder.reset();
            builder.function(tableName, columns, function).where(expressions);
            
            try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
                for (int i = 0; i < expressions.length; i++)
                    statement.setObject(i + 1, expressions[i].getValue());
                
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next())
                        result = rs.getObject(1);
                }
            }
        }
        
        return (T) result;
    }

    /**
     * Inserts a new row into the database table for the given model instance.
     *
     * @param model The Retrieves a single record from the database that matches
     * @return The model instance to insert
     * @throws NoAnnotationException if the given {@code model} class or one of
     * its fields is missing a required annotation
     * @throws IllegalAccessException if access to a field is denied
     * @throws SQLException if an error occurs while accessing the database
     * @throws ClassNotFoundException if the class name could not be found
     * @throws InstantiationException if the dynamic object can not be instantiated
     * @throws NoSuchColumnException if the provided model does not have the column with specified name
     * @throws UniqueFieldViolationException if one of the unique values already exists in the database
     * @throws NotNullableException if a field marked with the
     * {@link Column#nullable()} false annotation is null
     * @throws OverLimitException if a field marked with the {@link Limit}
     * annotation exceeds its specified limit
     * @throws IllegalOperationException if the model does not have the
     * {@link WritePermission#INSERT} permission
     */
    public Integer insert(Model model) throws NoAnnotationException, IllegalAccessException, SQLException, NotNullableException, OverLimitException, IllegalOperationException, NoSuchColumnException, ClassNotFoundException, InstantiationException, UniqueFieldViolationException {
        reflector.verifyPermission(model, WritePermission.INSERT);

        Integer result = null;

        String tableName = reflector.getName(model);
        String[] columns = model.getMarkedColumns();
        Expression[] expressions = reflector.getNonPrimaryExpressions(model, columns);
        
        for (Expression uniqueExpression : reflector.getUniqueExpressions(model, expressions)) {
           Field uniqueField = reflector.getColumnByName(model, uniqueExpression.getKey());
           uniqueField.setAccessible(true);
           
           Model caller = (Model) Class.forName(model.getClass().getTypeName()).newInstance();
           caller.express(uniqueExpression);
           
           Model uniqueResult = get(caller);
           if (uniqueResult != null)
               throw new UniqueFieldViolationException("The unique value " + uniqueField.get(model) + " already exists");
        }

        builder.reset();
        builder.insert(tableName, expressions);

        try (PreparedStatement statement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        }

        return result;
    }

    /**
     * Updates the data in the database for the specified model instance. The
     * update is performed based on the primary key value of the model, unless a
     * custom expression is specified via
     * {@link Model#express(com.bizmann.periscope.type.Expression...)}. Only
     * columns that have been marked with the {@link Column} annotation will be
     * updated, and any non-nullable columns that are not provided a value will
     * throw a {@link NotNullableException}. Additionally, any column values
     * that exceed their maximum length as specified in their {@link Column}
     * annotation will throw an {@link OverLimitException}.
     *
     * @param model The model instance to update
     * @throws IllegalAccessException if the access to a field is illegal
     * @throws SQLException if there is an error processing SQL queries
     * @throws NoAnnotationException if the model class or one of its fields is
     * not annotated with the required annotation
     * @throws NotNullableException if a non-nullable column has a null value
     * @throws OverLimitException if a value exceeds the column size limit
     * @throws ClassNotFoundException if the class name could not be found
     * @throws InstantiationException if the dynamic object can not be instantiated
     * @throws NoSuchColumnException if the provided model does not have the column with specified name
     * @throws UniqueFieldViolationException if one of the unique values already exists in the database
     * @throws IllegalOperationException if the model does not have the
     * {@link WritePermission#UPDATE} permission
     */
    public void update(Model model) throws IllegalAccessException, SQLException, NoAnnotationException, NotNullableException, OverLimitException, IllegalOperationException, NoSuchColumnException, ClassNotFoundException, InstantiationException, UniqueFieldViolationException {
        reflector.verifyPermission(model, WritePermission.UPDATE);

        String tableName = reflector.getName(model);
        String[] columns = model.getMarkedColumns();
        Expression[] keyExpressions = model.getExpressions().length > 0 ? model.getExpressions() : new Expression[]{reflector.getPrimaryExpression(model)};
        Expression[] valueExpressions = reflector.getNonPrimaryExpressions(model, columns);
        
        for (Expression uniqueExpression : reflector.getUniqueExpressions(model, valueExpressions)) {
           Field primaryField = reflector.getPrimaryColumn(model);
           Field uniqueField = reflector.getColumnByName(model, uniqueExpression.getKey());
           
           primaryField.setAccessible(true);
           uniqueField.setAccessible(true);
           
           Model caller = (Model) Class.forName(model.getClass().getTypeName()).newInstance();
           caller.express(uniqueExpression);
           
           Model uniqueResult = get(caller);
           reflector.verifyUniqueness(model, uniqueResult, primaryField, uniqueField);
        }

        builder.reset();
        builder.update(tableName, valueExpressions).where(keyExpressions);

        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < valueExpressions.length; i++) {
                statement.setObject(i + 1, valueExpressions[i].getValue());
            }

            for (int i = 0; i < keyExpressions.length; i++) {
                statement.setObject(i + valueExpressions.length + 1, keyExpressions[i].getValue());
            }

            statement.executeUpdate();
        }
    }

    /**
     * Deletes a row from the table in the database corresponding to the given
     * model instance. This method executes a DELETE SQL statement with the
     * specified expressions, or if none are provided, uses the primary key
     * expression.
     *
     * @param model The model instance to delete from the database
     * @throws NoAnnotationException if the given {@code model} class or one of
     * its fields is missing a required annotation
     * @throws IllegalAccessException if the access to a field is illegal
     * @throws SQLException if there is an error processing SQL queries
     * @throws IllegalOperationException if the model does not have the
     * {@link WritePermission#DELETE} permission
     */
    public void delete(Model model) throws NoAnnotationException, IllegalAccessException, SQLException, IllegalOperationException {
        reflector.verifyPermission(model, WritePermission.DELETE);

        String tableName = reflector.getName(model);
        Expression[] expressions = model.getExpressions().length > 0 ? model.getExpressions() : new Expression[]{reflector.getPrimaryExpression(model)};

        builder.reset();
        builder.delete(tableName).where(expressions);

        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            statement.executeUpdate();
        }
    }
    
    /**
     * Returns the JDBC connection used by this engine.
     * Useful when the user needs to perform raw SQL queries.
     * 
     * @return the JDBC connection this engine is currently using.
     */
    public Connection getConnection() {
        return this.connection;
    }

}
