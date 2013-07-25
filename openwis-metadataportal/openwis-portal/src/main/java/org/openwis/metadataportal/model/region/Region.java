/**
 * 
 */
package org.openwis.metadataportal.model.region;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Region {

    /**
     * The id of the region.
     */
    private Integer id;

    /**
     * The name of the region.
     */
    private String name;

    private CardinalExtent extent;

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
     * Gets the extent.
     * @return the extent.
     */
    public CardinalExtent getExtent() {
        return extent;
    }

    /**
     * Sets the extent.
     * @param extent the extent to set.
     */
    public void setExtent(CardinalExtent extent) {
        this.extent = extent;
    }
}
