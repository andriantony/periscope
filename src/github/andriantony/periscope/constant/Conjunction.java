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

import github.andriantony.periscope.type.Expression;

/**
 * The conjunction operator used to chain one {@link Expression} to the next.
 * This setting will not take effect in the last expression entry in its array.
 * 
 * @author Andriantony
 */
public enum Conjunction {
    
    /**
     * A SQL "AND" operator.
     * Used to specify that this element in a group must be true.
     */
    AND("AND"),
    
    /**
     * A SQL "OR" operator.
     * Used to specify at least one of the elements is true.
     */
    OR("OR");
    
    private final String text;

    Conjunction(final String string) {
        this.text = string;
    }

    @Override
    public String toString() {
        return this.text;
    }
    
}
