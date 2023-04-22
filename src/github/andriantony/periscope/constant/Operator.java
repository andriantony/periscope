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
package github.andriantony.periscope.constant;

/**
 * An enumeration of SQL comparison operators.
 * 
 * @author Andriantony
 */
public enum Operator {

    /**
     * Represents the SQL "=" operator.
     */
    EQUAL("="),
    
    /**
     * Represents the SQL "!=" operator.
     */
    NOT_EQUAL("!="),
    
    /**
     * Represents the SQL "&lt;&gt;" operator.
     * Provides the same effect as {@link Operator#NOT_EQUAL} for compatibility with older SQL standards.
     */
    NOT_EQUAL_ANSI("<>"),
    
    /**
     * Represents the SQL "&gt;" operator.
     */
    MORE(">"),
    
    /**
     * Represents the SQL "&lt;" operator.
     */
    LESS("<"),
    
    /**
     * Represents the SQL "&gt;=" operator.
     */
    EQUAL_OR_MORE(">="),
    
    /**
     * Represents the SQL "&lt;=" operator.
     */
    EQUAL_OR_LESS("<="),
    
    /**
     * Represents the SQL "IS" clause.
     */
    IS("IS"),
    
    /**
     * Represents the SQL "IS NOT" clause.
     */
    IS_NOT("IS NOT"),
    
    /**
     * Represents the SQL "LIKE" clause.
     */
    LIKE("LIKE"),
    
    /**
     * Represents the SQL "NOT LIKE" clause.
     */
    NOT_LIKE("NOT LIKE");

    private final String text;

    Operator(final String string) {
        this.text = string;
    }

    /**
     * Gets the operator symbol.
     * 
     * @return string representative of operator symbol
     */
    @Override
    public String toString() {
        return this.text;
    }

}
