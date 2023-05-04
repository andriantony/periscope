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
import github.andriantony.periscope.constant.SqlEngine;
import github.andriantony.periscope.constant.WritePermission;
import github.andriantony.periscope.exception.IllegalOperationException;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.exception.NoSuchColumnException;
import github.andriantony.periscope.exception.NotNullableException;
import github.andriantony.periscope.exception.OverLimitException;
import github.andriantony.periscope.exception.UniqueFieldViolationException;
import github.andriantony.periscope.type.ColumnDefinition;
import github.andriantony.periscope.type.Expression;
import github.andriantony.periscope.type.TableReference;
import github.andriantony.periscope.type.Modifier;
import github.andriantony.periscope.type.Reflector;
import github.andriantony.periscope.type.Sort;
import github.andriantony.periscope.type._FieldReference;
import github.andriantony.periscope.type._Verificator;
import github.andriantony.periscope.util.QueryBuilder;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andriantony
 */
public final class DatabaseEngine {

    private final Connection connection;
    private final Reflector reflector;
    private final _Verificator verificator;
    private final QueryBuilder builder;

    public DatabaseEngine(Connection connection) throws SQLException {
        this.connection = connection;
        this.reflector = new Reflector();
        this.verificator = new _Verificator();
        this.builder = new QueryBuilder(getConnectionEngine(connection));
    }

    private SqlEngine getConnectionEngine(Connection connection) throws SQLException {
        String product = connection.getMetaData().getDatabaseProductName().toLowerCase();

        if (product.contains("microsoft sql server")) {
            return SqlEngine.SQL_SERVER;
        } else if (product.contains("mysql")) {
            return SqlEngine.MYSQL;
        } else {
            return SqlEngine.UNKNOWN;
        }
    }

    public <T> List<T> list(Class<?> table) throws SQLException, NoAnnotationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        return list(table, new Modifier());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<?> table, Modifier modifier) throws SQLException, NoAnnotationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<Object> results = new ArrayList<>();

        String tableName = reflector.getTableName(table);
        String[] columns = modifier.getColumns();
        Expression[] expressions = modifier.getExpressions();
        Sort[] sorts = modifier.getSorts();
        TableReference[] tableReferences = modifier.getReferences();
        LinkedHashMap<String, ColumnDefinition> columnMap = reflector.getColumnMap(table);

        builder.reset();
        builder.select(tableName, columns).where(expressions).orderBy(sorts);

        try (PreparedStatement statement = this.connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    results.add(reflector.parse(table, columnMap, columns, rs));
                }
            }
        }

        if (tableReferences.length > 0) {
            _FieldReference[] fieldReferences = reflector.getReferences(table, tableReferences, columnMap);

            for (Object result : results) {
                for (_FieldReference fieldReference : fieldReferences) {
                    Object refValue = fieldReference.getSourceField().get(result);
                    fieldReference.getModifier().getExpressions()[0].setValue(refValue);

                    switch (fieldReference.getRelation()) {
                        case TO_MANY:
                            fieldReference.getTargetField().set(result, list(fieldReference.getTargetClass(), fieldReference.getModifier()));
                            break;
                        case TO_ONE:
                            fieldReference.getTargetField().set(result, get(fieldReference.getTargetClass(), fieldReference.getModifier()));
                            break;
                    }
                }
            }
        }

        return (List<T>) results;
    }

    public <T> T get(Class<?> table) throws SQLException, NoAnnotationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        return get(table, new Modifier());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> table, Modifier modifier) throws SQLException, NoAnnotationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object result = null;

        String tableName = reflector.getTableName(table);
        String[] columns = modifier.getColumns();
        Expression[] expressions = modifier.getExpressions();
        Sort[] sorts = modifier.getSorts();
        TableReference[] tableReferences = modifier.getReferences();
        LinkedHashMap<String, ColumnDefinition> columnMap = reflector.getColumnMap(table);

        builder.reset();
        builder.select(tableName, columns).where(expressions).orderBy(sorts);

        try (PreparedStatement statement = this.connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = reflector.parse(table, columnMap, columns, rs);
                }
            }
        }

        if (tableReferences.length > 0) {
            _FieldReference[] fieldReferences = reflector.getReferences(table, tableReferences, columnMap);

            if (result != null) {
                for (_FieldReference fieldReference : fieldReferences) {
                    Object refValue = fieldReference.getSourceField().get(result);
                    fieldReference.getModifier().getExpressions()[0].setValue(refValue);

                    switch (fieldReference.getRelation()) {
                        case TO_MANY:
                            fieldReference.getTargetField().set(result, list(fieldReference.getTargetClass(), fieldReference.getModifier()));
                            break;
                        case TO_ONE:
                            fieldReference.getTargetField().set(result, get(fieldReference.getTargetClass(), fieldReference.getModifier()));
                            break;
                    }
                }
            }
        }

        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public <T> T function(Class<?> table, Modifier modifier, Function function) throws SQLException, NoAnnotationException {
        Object result = null;

        String tableName = reflector.getTableName(table);
        String[] columns = modifier.getColumns();
        Expression[] expressions = modifier.getExpressions();

        if (function != null) {
            builder.reset();
            builder.function(tableName, columns, function).where(expressions);

            try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
                for (int i = 0; i < expressions.length; i++) {
                    statement.setObject(i + 1, expressions[i].getValue());
                }

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getObject(1);
                    }
                }
            }
        }

        return (T) result;
    }

    public Integer insert(Object entity) throws NoAnnotationException, IllegalOperationException, NotNullableException, IllegalAccessException, OverLimitException, SQLException, ClassNotFoundException, InstantiationException, UniqueFieldViolationException, NoSuchColumnException {
        return insert(entity, new Modifier());
    }

    public Integer insert(Object entity, Modifier modifier) throws NoAnnotationException, IllegalOperationException, NotNullableException, IllegalAccessException, OverLimitException, SQLException, ClassNotFoundException, InstantiationException, UniqueFieldViolationException, NoSuchColumnException {
        Class<?> table = entity.getClass();
        verificator.verifyPermission(table, WritePermission.INSERT);

        Integer result = null;

        String tableName = reflector.getTableName(table);
        String[] columns = modifier.getColumns();
        LinkedHashMap<String, ColumnDefinition> columnMap = reflector.getColumnMap(table, columns);

        verificator.verifyNonNullableInsertion(columnMap, reflector.getColumnMap(table));
        verificator.verifyNullability(entity, columnMap);
        verificator.verifyLength(entity, columnMap);

        for (Map.Entry<String, ColumnDefinition> entry : reflector.getUniqueMap(columnMap).entrySet()) {
            Object unique = get(table, new Modifier().express(new Expression(entry.getKey(), entry.getValue().getField().get(entity))));

            if (unique != null) {
                throw new UniqueFieldViolationException("The unique value " + entry.getValue().getField().get(entity) + " already exists");
            }
        }

        String[] insertedColumns = reflector.toColumnArray(columnMap);

        if (columns.length > 0 && columns.length != insertedColumns.length) {
            throw new NoSuchColumnException("Insertion column length mismatch");
        }

        builder.reset();
        builder.insert(tableName, insertedColumns);

        try (PreparedStatement statement = this.connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;

            for (Map.Entry<String, ColumnDefinition> entry : columnMap.entrySet()) {
                statement.setObject(index++, entry.getValue().getField().get(entity));
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

    public void update(Object entity) throws NoAnnotationException, IllegalOperationException, NotNullableException, IllegalAccessException, OverLimitException, SQLException, ClassNotFoundException, InstantiationException, UniqueFieldViolationException, NoSuchColumnException {
        update(entity, new Modifier());
    }

    public void update(Object entity, Modifier modifier) throws NoAnnotationException, IllegalOperationException, NotNullableException, IllegalAccessException, OverLimitException, SQLException, ClassNotFoundException, InstantiationException, UniqueFieldViolationException, NoSuchColumnException {
        Class<?> table = entity.getClass();
        verificator.verifyPermission(table, WritePermission.UPDATE);

        String tableName = reflector.getTableName(table);
        String[] columns = modifier.getColumns();
        Expression[] keyExpressions = modifier.getExpressions().length > 0 ? modifier.getExpressions() : new Expression[]{reflector.getPrimaryExpression(entity)};
        LinkedHashMap<String, ColumnDefinition> columnMap = reflector.getColumnMap(table, columns);

        verificator.verifyNullability(entity, columnMap);
        verificator.verifyLength(entity, columnMap);

        Field primaryField = reflector.getPrimaryColumn(table);

        for (Map.Entry<String, ColumnDefinition> entry : reflector.getUniqueMap(columnMap).entrySet()) {
            ColumnDefinition column = entry.getValue();
            Field uniqueField = column.getField();

            List<Object> uniques = list(table, new Modifier().express(new Expression(entry.getKey(), column.getField().get(entity))));

            for (Object unique : uniques) {
                verificator.verifyUniqueness(entity, unique, primaryField, uniqueField);
            }
        }

        builder.reset();
        builder.update(tableName, reflector.toColumnArray(columnMap)).where(keyExpressions);

        try (PreparedStatement statement = this.connection.prepareStatement(builder.toString())) {
            int index = 1;

            for (Map.Entry<String, ColumnDefinition> entry : columnMap.entrySet()) {
                statement.setObject(index++, entry.getValue().getField().get(entity));
            }

            for (int i = 0; i < keyExpressions.length; i++) {
                statement.setObject(i + columnMap.size() + 1, keyExpressions[i].getValue());
            }

            statement.executeUpdate();
        }
    }
    
    public void delete(Class<?> table, Object primaryKey) throws NoAnnotationException, IllegalOperationException, SQLException, NoSuchColumnException {
        verificator.verifyPermission(table, WritePermission.DELETE);
        
        String tableName = reflector.getTableName(table);
        Expression[] expressions = new Expression[] { new Expression(reflector.getPrimaryColumn(table).getAnnotation(Column.class).name(), primaryKey) };
        
        builder.reset();
        builder.delete(tableName).where(expressions);
        
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            statement.executeUpdate();
        }
    }
    
    public void delete(Class<?> table, Modifier modifier) throws NoAnnotationException, IllegalOperationException, SQLException, NoSuchColumnException {
        verificator.verifyPermission(table, WritePermission.DELETE);
        
        String tableName = reflector.getTableName(table);
        Expression[] expressions = modifier.getExpressions();
        
        builder.reset();
        builder.delete(tableName).where(expressions);
        
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            statement.executeUpdate();
        }
    }
    
    public void delete(Object entity) throws NoSuchColumnException, IllegalAccessException, SQLException, NoAnnotationException, IllegalOperationException {
        Class<?> table = entity.getClass();
        
        verificator.verifyPermission(table, WritePermission.DELETE);
        
        String tableName = reflector.getTableName(table);
        Field primaryField = reflector.getPrimaryColumn(table);
        Expression[] expressions = new Expression[] { new Expression(primaryField.getAnnotation(Column.class).name(), primaryField.get(entity)) };
        
        builder.reset();
        builder.delete(tableName).where(expressions);
        
        try (PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            for (int i = 0; i < expressions.length; i++) {
                statement.setObject(i + 1, expressions[i].getValue());
            }

            statement.executeUpdate();
        }
    }

}
