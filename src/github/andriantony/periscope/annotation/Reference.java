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
package github.andriantony.periscope.annotation;

import github.andriantony.periscope.constant.Relation;
import github.andriantony.periscope.type.Model;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * This annotation specify a class field that is a reference model to another table.
 * 
 * @author Andriantony
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reference {
    
    /**
     * The user-specified name of the reference.
     * It is used to differentiate between multiple reference fields with the same model type.
     * 
     * @return the name of the reference
     */
    public String name();
    
    /**
     * The target model class type.
     * This element needs a {@link Model} derivative class.
     * It is used to determine the returning model type in SQL results.
     * 
     * @return the class of target model
     */
    public Class<? extends Model> target();
    
    /**
     * The name of source column in one's own table. Must match one of the names defined in the {@link Column} annotation.
     * 
     * @return the name of source table's column
     */
    public String source();
    
    /**
     * The name of column in referenced table. Must match one of the names defined in the {@link Column} annotation.
     * 
     * @return the name of target table's column
     */
    public String refer();
    
    /**
     * Specifies whether this relation is one-to-one or one-to-many.
     * Use {@link Relation#TO_MANY} to map {@link List} and {@link Relation#TO_ONE} to map {@link Model} derived objects.
     * 
     * @return the relation mode between two tables
     */
    public Relation relation();
    
}
