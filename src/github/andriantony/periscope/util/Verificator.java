package github.andriantony.periscope.util;

import github.andriantony.periscope.annotation.Column;
import github.andriantony.periscope.annotation.Table;
import github.andriantony.periscope.constant.WritePermission;
import github.andriantony.periscope.exception.IllegalOperationException;
import github.andriantony.periscope.exception.NoAnnotationException;
import github.andriantony.periscope.exception.NotNullableException;
import github.andriantony.periscope.exception.OverLimitException;
import github.andriantony.periscope.type.Model;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A class used to verify various rules specified to a table.
 * 
 * @author Andriantony
 */
public class Verificator {

    /**
     * Verify whether the {@link Model} derivative class have a {@link Table}
     * annotation.
     *
     * @param model The derived table class derived from {@link Model}
     * @throws NoAnnotationException Thrown when the {@code model} does not
     * contain the {@link Table} annotation
     */
    public void verifyTableAnnotation(Model model) throws NoAnnotationException {
        Class<?> modelClass = model.getClass();

        if (!modelClass.isAnnotationPresent(Table.class)) {
            throw new NoAnnotationException("Class " + modelClass.getSimpleName() + " does not have the Table annotation");
        }
    }

    /**
     * Verify whether the field can contain null value.
     *
     * @param model The derived table class derived from {@link Model}
     * @param field The {@link Field} from its model object
     * @throws IllegalAccessException if there is an error accessing the field value
     * @throws NotNullableException if the field is marked as not nullable and contains a null value
     */
    public void verifyNullability(Model model, Field field) throws IllegalAccessException, NotNullableException {
        if (!field.getAnnotation(Column.class).nullable() && field.get(model) == null) {
            throw new NotNullableException("Field " + field.getName() + " contains null value");
        }
    }

    /**
     * Verify whether the value in a field is within its configured limit.
     * If the maximum length is not specified (i.e. it is -1), then no verification will be performed.
     * 
     * @param model The derived table class derived from {@link Model}
     * @param field The {@link Field} from its model object
     * @throws OverLimitException if the length of the field value exceeds the maximum length allowed
     * @throws IllegalAccessException if the specified field is inaccessible (i.e. not public)
     */
    public void verifyLength(Model model, Field field) throws OverLimitException, IllegalAccessException {
        int maxLength = field.getAnnotation(Column.class).length();

        if (maxLength > -1) {
            Object val = field.get(model);
            int length = val != null ? val.toString().length() : 0;

            if (length > maxLength) {
                throw new OverLimitException("The value length of field " + field.getName() + " is " + length + ", which is larger than its configured limit of " + maxLength);
            }
        }
    }

    /**
     * Verify whether the model's {@link Table} annotation contains a permission in its {@link Table#writePermissions()} array.
     * 
     * @param model The derived table class derived from {@link Model}
     * @param permission The permission to check
     * @throws NoAnnotationException if the model or its corresponding table is not annotated with the required annotations
     * @throws IllegalOperationException if the table does not have the specified write permission
     */
    public void verifyPermission(Model model, WritePermission permission) throws NoAnnotationException, IllegalOperationException {
        verifyTableAnnotation(model);

        Class<?> modelClass = model.getClass();
        WritePermission[] permissions = modelClass.getAnnotation(Table.class).writePermissions();

        if (!Arrays.asList(permissions).contains(permission)) {
            throw new IllegalOperationException("Table " + modelClass.getSimpleName() + " does not have the " + permission + " permission");
        }
    }

}
