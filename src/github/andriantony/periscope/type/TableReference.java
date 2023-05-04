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

import github.andriantony.periscope.annotation.Reference;

/**
 * A model reference instance that is used to include data from related tables.
 * Only used by selection queries.
 * 
 * @author Andriantony
 */
public final class TableReference {

    private final String name;
    private final Modifier modifier;
    private final Model model;

    /**
     * Creates a new reference instance.
     * 
     * @param name The name of reference as defined in the {@link Reference#name()} property
     * @param model The target model used to determine the appropriate return type
     */
    public TableReference(String name, Model model) {
        this.name = name;
        this.modifier = new Modifier();
        this.model = model;
    }
    
    public TableReference(String name, Modifier modifier) {
        this.name = name;
        this.modifier = modifier;
        this.model = new Model();
    }
    
    public TableReference(String name) {
        this.name = name;
        this.modifier = new Modifier();
        this.model = new Model();
    }

    /**
     * Returns the reference name.
     * 
     * @return the reference name
     */
    public String getName() {
        return this.name;
    }
    
    public Modifier getModifier() {
        return this.modifier;
    }

    /**
     * Returns the model of target table.
     * 
     * @return the model of target table
     */
    public Model getModel() {
        return this.model;
    }

}
