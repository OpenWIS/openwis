/**
 * 
 */
package org.openwis.metadataportal.model.category;

/**
 * A POJO that describes a category in GeoNetwork. <P>
 * Explanation goes here. <P>
 * 
 */
public class Category {

    /**
     * The id of the category.
     */
    private Integer id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * Default constructor.
     * Builds a Category.
     */
    public Category() {
        super();
    }

    /**
     * Default constructor.
     * Builds a Category.
     * @param id
     * @param name
     */
    public Category(Integer id, String name) {
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
        if (!(obj instanceof Category)) {
            return false;
        }
        Category other = (Category) obj;
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
