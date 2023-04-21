package github.andriantony.periscope.type;

import github.andriantony.periscope.constant.Conjunction;
import github.andriantony.periscope.constant.Operator;
import java.sql.PreparedStatement;

/**
 * An expression used for SQL queries.
 * It comprises of a key which represents the column name, and a value to compare against.
 * 
 * @author Andriantony
 */
public class Expression {

    private final String key;
    private final Object value;
    private final Operator operator;
    private final Conjunction conjunction;

    /**
     * Creates am new expression with default operator ({@link Operator#EQUAL}) and conjunction ({@link Conjunction#AND}).
     * 
     * @param key The column name of the expression
     * @param value The value to compare against
     */
    public Expression(String key, Object value) {
        this.key = key;
        this.value = value;
        this.operator = Operator.EQUAL;
        this.conjunction = Conjunction.AND;
    }

    /**
     * Creates am new expression with default conjunction ({@link Conjunction#AND}).
     * 
     * @param key The column name of the expression
     * @param value The value to compare against
     * @param operator The comparison operator to use
     */
    public Expression(String key, Object value, Operator operator) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.conjunction = Conjunction.AND;
    }

    /**
     * Creates am new expression.
     * 
     * @param key The column name of the expression
     * @param value The value to compare against
     * @param operator The comparison operator to use
     * @param conjunction The conjunction operator to use to combine this expression with next expression
     */
    public Expression(String key, Object value, Operator operator, Conjunction conjunction) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.conjunction = conjunction;
    }

    /**
     * Returns the column name of this expression.
     * 
     * @return the column name of this expression
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Returns the value of this expression.
     * 
     * @return the value of this expression
     */
    public Object getValue() {
        return this.value;
    }
    
    /**
     * Returns the comparison operator of this expression.
     * 
     * @return the comparison operator of this expression
     */
    public Operator getOperator() {
        return this.operator;
    }

    /**
     * Returns the conjunction operator of this expression.
     * 
     * @return the conjunction operator of this expression
     */
    public Conjunction getConjunction() {
        return this.conjunction;
    }

    /**
     * Returns a formatted string for {@link PreparedStatement}.
     * 
     * @return a {@link PreparedStatement} compatible string
     */
    @Override
    public String toString() {
        return this.key + " " + this.operator + " ?";
    }

}
