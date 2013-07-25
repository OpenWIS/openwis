/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.discard;

import org.openwis.metadataportal.services.request.dto.common.TypeRequestDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DiscardRequestDTO {

    private String requestID;

    private TypeRequestDTO typeRequest;

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

    /**
     * Gets the typeRequest.
     * @return the typeRequest.
     */
    public TypeRequestDTO getTypeRequest() {
        return typeRequest;
    }

    /**
     * Sets the typeRequest.
     * @param typeRequest the typeRequest to set.
     */
    public void setTypeRequest(TypeRequestDTO typeRequest) {
        this.typeRequest = typeRequest;
    }
}
