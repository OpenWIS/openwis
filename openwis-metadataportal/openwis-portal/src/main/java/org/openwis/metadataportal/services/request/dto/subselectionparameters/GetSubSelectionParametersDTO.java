/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.subselectionparameters;

import org.openwis.metadataportal.services.common.json.SimpleMetadataDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GetSubSelectionParametersDTO extends SimpleMetadataDTO {

    private boolean subscription;

    /**
     * Gets the isSubscription.
     * @return the isSubscription.
     */
    public boolean isSubscription() {
        return subscription;
    }

    /**
     * Sets the isSubscription.
     * @param isSubscription the isSubscription to set.
     */
    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

}
