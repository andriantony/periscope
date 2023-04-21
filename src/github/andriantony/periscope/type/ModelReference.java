package github.andriantony.periscope.type;

import github.andriantony.periscope.annotation.Reference;

/**
 * A model reference instance that is used to include data from related tables.
 * Only used by selection queries.
 * 
 * @author Andriantony
 */
public final class ModelReference {

    private final String name;
    private final Model model;

    /**
     * Creates a new reference instance.
     * 
     * @param name The name of reference as defined in the {@link Reference#name()} property
     * @param model The target model used to determine the appropriate return type
     */
    public ModelReference(String name, Model model) {
        this.name = name;
        this.model = model;
    }

    /**
     * Returns the reference name.
     * 
     * @return the reference name
     */
    public String getName() {
        return this.name;
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
