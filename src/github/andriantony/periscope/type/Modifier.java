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

/**
 *
 * @author Andriantony
 */
public final class Modifier {

    private String[] columns = new String[0];
    private Expression[] expressions = new Expression[0];
    private TableReference[] references = new TableReference[0];
    private Sort[] sorts = new Sort[0];

    public Modifier mark(String... columns) {
        this.columns = columns;
        return this;
    }

    public Modifier express(Expression... expressions) {
        this.expressions = expressions;
        return this;
    }

    public Modifier include(TableReference... modelReferences) {
        this.references = modelReferences;
        return this;
    }

    public Modifier sort(Sort... sorts) {
        this.sorts = sorts;
        return this;
    }

    public String[] getColumns() {
        return columns;
    }

    public Expression[] getExpressions() {
        return expressions;
    }

    public TableReference[] getReferences() {
        return references;
    }

    public Sort[] getSorts() {
        return sorts;
    }

}
