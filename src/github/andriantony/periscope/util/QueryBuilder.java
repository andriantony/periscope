package github.andriantony.periscope.util;

import github.andriantony.periscope.constant.Function;
import github.andriantony.periscope.type.Expression;
import java.sql.PreparedStatement;

/**
 * A class used to dynamically generate SQL queries.
 * 
 * @author Andriantony
 */
public final class QueryBuilder {

    private final StringBuilder query = new StringBuilder();

    /**
     * Appends a SELECT statement with columns based on provided array.
     * Will select "*" (all columns) instead if the provided array is empty.
     * 
     * @param tableName The name of the table to select from
     * @param columns The columns to select
     * @return this instance for further processing
     */
    public QueryBuilder select(String tableName, String[] columns) {
        this.query.append("SELECT ");

        if (columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                this.query.append(wrap(columns[i]));
                this.query.append(i + 1 < columns.length ? ", " : " ");
            }
        } else {
            this.query.append("* ");
        }

        this.query.append("FROM ").append(wrap(tableName)).append(' ');

        return this;
    }
    
    /**
     * Appends a aggregate function statement.
     * 
     * @param tableName The name of the table to select from
     * @param columns The list of column names to pass to the function
     * @param function The function to execute
     * @return this instance for further processing
     */
    public QueryBuilder function(String tableName, String[] columns, Function function) {
        this.query.append("SELECT ").append(function);
        
        if (columns.length > 0) {
            this.query.append("(");
            
            for (int i = 0; i < columns.length; i++) {
                this.query.append(wrap(columns[i]));
                this.query.append(i + 1 < columns.length ? ", " : "");
            }
            
            this.query.append(") ");
        } else {
            this.query.append("(*) ");
        }
        
        this.query.append("FROM ").append(wrap(tableName)).append(' ');
        
        return this;
    }
    
    /**
     * Appends a WHERE clause to the query based on provided expressions.
     * Will do nothing if the provided array is empty.
     * 
     * @param expressions The expressions to use in the WHERE clause
     * @return this instance for further processing
     */
    public QueryBuilder where(Expression[] expressions) {
        if (expressions.length > 0)
            this.query.append("WHERE ");
        
        for (int i = 0; i < expressions.length; i++) {
            this.query.append(wrap(expressions[i].getKey())).append(' ').append(expressions[i].getOperator()).append(" ?");
            this.query.append(i + 1 < expressions.length ? (' ' + expressions[i].getConjunction().toString() + ' ') : ' ');
        }
        
        return this;
    }
    
    /**
     * Appends an INSERT statement to the query based on provided expression array.
     * 
     * @param tableName The name of the table to insert into
     * @param expressions Contains the column needed for insertion
     * @return this instance for further processing
     */
    public QueryBuilder insert(String tableName, Expression[] expressions) {
        this.query.append("INSERT INTO ").append(tableName).append(" (");
        
        for (int i = 0; i < expressions.length; i++) {
            this.query.append(wrap(expressions[i].getKey()));
            this.query.append(i + 1 < expressions.length ? ", " : "");
        }
        
        this.query.append(") VALUES (");
        
        for (int i = 0; i < expressions.length; i++) {
            this.query.append('?');
            this.query.append(i + 1 < expressions.length ? ", " : "");
        }
        
        this.query.append(") ");
        
        return this;
    }
    
    /**
     * Appends an UPDATE statement to the query based on provided expression array.
     * 
     * @param tableName The table name to perform update to
     * @param expressions The expressions needed to change the value
     * @return this instance for further processing
     */
    public QueryBuilder update(String tableName, Expression[] expressions) {
        this.query.append("UPDATE ").append(tableName).append(" SET ");
        
        for (int i = 0; i < expressions.length; i++) {
            this.query.append(wrap(expressions[i].getKey())).append(" = ?");
            this.query.append(i + 1 < expressions.length ? ", " : " ");
        }
        
        return this;
    }
    
    /**
     * Appends a DELETE statement to the query.
     * 
     * @param tableName The name of the table to perform delete to
     * @return this instance for further processing
     */
    public QueryBuilder delete(String tableName) {
        this.query.append("DELETE FROM ").append(tableName).append(' ');
        
        return this;
    }

    private String wrap(String text) {
        return '"' + text + '"';
    }
    
    /**
     * Clear this instance's content.
     */
    public void reset() {
        this.query.setLength(0);
    }

    /**
     * Returns a full SQL query ready for use in {@link PreparedStatement}.
     * 
     * @return a {@link PreparedStatement} compatible SQL query string
     */
    @Override
    public String toString() {
        return query.toString().replace("  ", " ").trim();
    }

}
