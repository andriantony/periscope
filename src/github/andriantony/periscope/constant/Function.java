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
 * An enumeration representing SQL aggregate functions.
 * 
 * @author Andriantony
 */
public enum Function {
    
    /**
     * Represents the COUNT() function.
     * Used to return the number of rows that matches a specified criterion.
     */
    COUNT("COUNT"),
    
    /**
     * Represents the SUM() function.
     * Used to return the sum of specific column.
     */
    SUM("SUM"),
    
    /**
     * Represents the AVG() function.
     * Used to return the average value of specific column.
     */
    AVG("AVG"),
    
    /**
     * Represents the MAX() function.
     * Used to return the maximum value in a column.
     */
    MAX("MAX"),
    
    /**
     * Represents the MIN() function.
     * Used to return the minimum value in a column.
     */
    MIN("MIN");
    
    private final String text;

    Function(final String string) {
        this.text = string;
    }

    @Override
    public String toString() {
        return this.text;
    }
    
}
