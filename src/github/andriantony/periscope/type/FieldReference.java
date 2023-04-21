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
