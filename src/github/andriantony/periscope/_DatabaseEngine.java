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

import github.andriantony.periscope.constant.Function;
import github.andriantony.periscope.constant.SqlEngine;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.type.ColumnDefinition;
import github.andriantony.periscope.type.Expression;
import github.andriantony.periscope.type.TableReference;
import github.andriantony.periscope.type.Modifier;
import github.andriantony.periscope.type.Reflector;
import github.andriantony.periscope.type.Sort;
import github.andriantony.periscope.type._FieldReference;
import github.andriantony.periscope.type._Verificator;
import github.andriantony.periscope.util.QueryBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Andriantony
 */
public final class _DatabaseEngine {
    
    private final Connection connection;
    private final Reflector reflector;
    private final _Verificator verificator;
    private final QueryBuilder builder;
    
    public _DatabaseEngine(Connection connection) throws SQLException {
        this.connection = connection;
        this.reflector = new Reflector();
        this.verificator = new _Verificator();
        this.builder = new QueryBuilder(getConnectionEngine(connection));
    }
    
    private SqlEngine getConnectionEngine(Connection connection) throws SQLException {
        String product = connection.getMetaData().getDatabaseProductName().toLowerCase();
        
        if (product.contains("microsoft sql server"))
            return SqlEngine.SQL_SERVER;
        else if (product.contains("mysql"))
            return SqlEngine.MYSQL;
        else
            return SqlEngine.UNKNOWN;
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
                while (rs.next()) {
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
    
}
