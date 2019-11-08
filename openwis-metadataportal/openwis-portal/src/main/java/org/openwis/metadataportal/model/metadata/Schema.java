/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

/**
 * A schema. <P>
 * Explanation goes here. <P>
 */
public class Schema implements Comparable<Schema> {

    private String name;

    /**
     * Default constructor.
     * Builds a Template.
     */
    public Schema() {
        super();
    }

    /**
     * Default constructor.
     * Builds a Template.
     * @param id
     * @param name
     * @param displayOrder
     */
    public Schema(String name) {
        super();
        this.name = name;
    }

    /**
     * Gets the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

 
    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Schema o) {
        return getName().compareTo(o.getName());
    }
}
