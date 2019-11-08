/**
 * 
 */
package org.openwis.metadataportal.model.datapolicy;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Operation {

    /**
     * The id of the operation.
     */
    private Integer id;

    /**
     * The name of the operation.
     */
    private String name;

    /**
     * Default constructor.
     * Builds a Operation.
     */
    public Operation() {
        super();
    }

    /**
     * Default constructor.
     * Builds a Operation.
     * @param name
     */
    public Operation(String name) {
        super();
        this.name = name;
    }

    /**
     * Default constructor.
     * Builds a Operation.
     * @param id
     * @param name
     */
    public Operation(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the id.
     * @return the id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id to set.
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Operation)) {
            return false;
        }
        Operation other = (Operation) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
