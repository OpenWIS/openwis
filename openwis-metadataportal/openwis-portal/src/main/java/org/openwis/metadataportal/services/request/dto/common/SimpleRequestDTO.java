/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.common;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SimpleRequestDTO {
    private String requestID;

    /**
     * Default constructor.
     * Builds a SimpleRequestDTO.
     * @param requestID
     */
    public SimpleRequestDTO(String requestID) {
        super();
        this.requestID = requestID;
    }

    /**
     * Gets the requestID.
     * @return the requestID.
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the requestID.
     * @param requestID the requestID to set.
     */
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

}
