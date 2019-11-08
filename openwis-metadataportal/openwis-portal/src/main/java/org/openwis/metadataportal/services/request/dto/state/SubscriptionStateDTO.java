/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.state;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SubscriptionStateDTO {

    private String requestID;

    private TypeSubscriptionStateSet typeStateSet;

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
     * Gets the typeStateSet.
     * @return the typeStateSet.
     */
    public TypeSubscriptionStateSet getTypeStateSet() {
        return typeStateSet;
    }

    /**
     * Sets the typeStateSet.
     * @param typeStateSet the typeStateSet to set.
     */
    public void setTypeStateSet(TypeSubscriptionStateSet typeStateSet) {
        this.typeStateSet = typeStateSet;
    }

}
