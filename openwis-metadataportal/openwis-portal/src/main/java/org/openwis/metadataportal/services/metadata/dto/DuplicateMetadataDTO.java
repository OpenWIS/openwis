/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;



/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DuplicateMetadataDTO {

    /**
     * Comment for <code>toURN</code>
     */
    private String toURN;

    /**
     * Comment for <code>fromURN</code>
     */
    private String fromURN;
    
    /**
     * Gets the toURN.
     * @return the toURN.
     */
    public String getToURN() {
        return toURN;
    }

    /**
     * Sets the toURN.
     * @param toURN the toURN to set.
     */
    public void setToURN(String toURN) {
        this.toURN = toURN;
    }

    /**
     * Gets the fromURN.
     * @return the fromURN.
     */
    public String getFromURN() {
        return fromURN;
    }

    /**
     * Sets the fromURN.
     * @param fromURN the fromURN to set.
     */
    public void setFromURN(String fromURN) {
        this.fromURN = fromURN;
    }
}
