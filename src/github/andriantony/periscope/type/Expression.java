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
    private Object value;
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
    
    public void setValue(Object value) {
        this.value = value;
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
