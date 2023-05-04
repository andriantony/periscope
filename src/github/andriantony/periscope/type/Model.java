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

import github.andriantony.periscope.DatabaseEngine;

/**
 * The base table definition required for {@link DatabaseEngine} processing.
 * 
 * @author Andriantony
 */
public class Model {

    protected String[] markedColumns = new String[0];
    protected Expression[] expressions = new Expression[0];
    protected TableReference[] includes = new TableReference[0];
    protected Sort[] sorts = new Sort[0];

    /**
     * Marks certain columns for processing.
     * 
     * @param columns name of columns to be processed
     * @return self for method chaining
     */
    public Model mark(String... columns) {
        this.markedColumns = columns;
        return this;
    }

    /**
     * Set list of expressions for processing.
     * 
     * @param expressions array of expressions to be processed
     * @return self for method chaining
     */
    public Model express(Expression... expressions) {
        this.expressions = expressions;
        return this;
    }

    /**
     * Set reference to include.
     * Only used on selection queries.
     * 
     * @param includes the reference model to be included
     * @return self for method chaining
     */
    public Model include(TableReference... includes) {
        this.includes = includes;
        return this;
    }
    
    /**
     * Sets list of sort conditions.
     * Only used on selection queries.
     * 
     * @param sorts The list of sort directives
     * @return self for method chaining
     */
    public Model sort(Sort... sorts) {
        this.sorts = sorts;
        return this;
    }

    /**
     * Returns marked column names for processing.
     * 
     * @return marked column names for processing
     */
    public String[] getMarkedColumns() {
        return this.markedColumns;
    }

    /**
     * Returns the list of expressions associated with this table instance.
     * 
     * @return the list of expressions associated with this table instance
     */
    public Expression[] getExpressions() {
        return this.expressions;
    }

    /**
     * Returns the list of reference inclusion.
     * 
     * @return the list of reference inclusion
     */
    public TableReference[] getIncludes() {
        return this.includes;
    }
    
    /**
     * Returns the list of sort directives.
     * 
     * @return the list of sort directives
     */
    public Sort[] getSorts() {
        return this.sorts;
    }
    
    /**
     * Clears all column markings and expressions as well as its list of reference inclusions.
     */
    public void reset() {
        this.markedColumns = new String[0];
        this.expressions = new Expression[0];
        this.includes = new TableReference[0];
        this.sorts = new Sort[0];
    }

}
