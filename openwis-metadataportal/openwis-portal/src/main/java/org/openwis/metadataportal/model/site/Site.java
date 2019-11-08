/**
 * 
 */
package org.openwis.metadataportal.model.site;

/**
 * A POJO that describes a category in GeoNetwork. <P>
 * Explanation goes here. <P>
 * 
 */
public class Site {

    /**
     * The id of the category.
     */
    private String id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * Default constructor.
     * Builds a Category.
     */
    public Site() {
        super();
    }

    /**
     * Default constructor.
     * Builds a Category.
     * @param id
     * @param name
     */
    public Site(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the id.
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id to set.
     */
    public void setId(String id) {
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
        if (!(obj instanceof Site)) {
            return false;
        }
        Site other = (Site) obj;
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
