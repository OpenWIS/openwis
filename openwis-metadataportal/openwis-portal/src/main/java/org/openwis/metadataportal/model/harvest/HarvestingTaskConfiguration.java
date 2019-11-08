/**
 * 
 */
package org.openwis.metadataportal.model.harvest;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvestingTaskConfiguration {

    private String key;

    private String value;

    /**
     * Default constructor.
     * Builds a HarvesterConfiguration.
     */
    public HarvestingTaskConfiguration() {
        super();
    }

    /**
     * Default constructor.
     * Builds a HarvesterConfiguration.
     * @param key
     * @param value
     */
    public HarvestingTaskConfiguration(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key.
     * @return the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     * @param key the key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value.
     * @return the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value the value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        if (!(obj instanceof HarvestingTaskConfiguration)) {
            return false;
        }
        HarvestingTaskConfiguration other = (HarvestingTaskConfiguration) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }
}
