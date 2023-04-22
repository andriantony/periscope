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
import github.andriantony.periscope.constant.Relation;
import java.lang.reflect.Field;

/**
 * A reference object that binds a field in the source table to its related table.
 * Primarily used by the {@link DatabaseEngine} to specify a table to table relation.
 * 
 * @author Andriantony
 */
public final class FieldReference {

    private final Model targetModel;
    private final Field sourceField;
    private final Field targetField;
    private final String targetColumn;
    private final Relation relation;

    /**
     * Creates a new table relation reference.
     * 
     * @param targetModel The target model used to determine the return type
     * @param sourceFiield The {@link Field} of the source table
     * @param targetField The {@link Field} of the target table
     * @param targetColumn The column name of the target table
     * @param relation The relation between the source table and target table
     */
    public FieldReference(Model targetModel, Field sourceFiield, Field targetField, String targetColumn, Relation relation) {
        this.targetModel = targetModel;
        this.sourceField = sourceFiield;
        this.targetField = targetField;
        this.targetColumn = targetColumn;
        this.relation = relation;
    }

    /**
     * Returns the target model.
     * 
     * @return the target model
     */
    public Model getTargetModel() {
        return this.targetModel;
    }

    /**
     * Returns the source field.
     * 
     * @return the source field
     */
    public Field getSourceField() {
        return this.sourceField;
    }

    /**
     * Returns the target field.
     * 
     * @return the target field
     */
    public Field getTargetField() {
        return this.targetField;
    }

    /**
     * Returns the column name of target table.
     * 
     * @return the column name of target table
     */
    public String getTargetColumn() {
        return this.targetColumn;
    }

    /**
     * Returns the relation between the source table and the target table.
     * 
     * @return the relation between the source table and the target table
     */
    public Relation getRelation() {
        return this.relation;
    }

}
