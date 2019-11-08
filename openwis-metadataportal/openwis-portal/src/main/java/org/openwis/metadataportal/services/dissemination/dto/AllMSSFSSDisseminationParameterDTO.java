/**
 * 
 */
package org.openwis.metadataportal.services.dissemination.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class AllMSSFSSDisseminationParameterDTO {

    /**
     * True if dissemination is authorized for user, false otherwise.
     */
    private boolean authorized;

    /**
     * A list of MSS/FSS Channels.
     */
    private List<String> mssFssChannels;

    /**
     * Gets the authorized.
     * @return the authorized.
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Sets the authorized.
     * @param authorized the authorized to set.
     */
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    /**
     * Gets the mssFssChannels.
     * @return the mssFssChannels.
     */
    public List<String> getMssFssChannels() {
        if (mssFssChannels == null) {
            mssFssChannels = new ArrayList<String>();
        }
        return mssFssChannels;
    }

    /**
     * Sets the mssFssChannels.
     * @param mssFssChannels the mssFssChannels to set.
     */
    public void setMssFssChannels(List<String> mssFssChannels) {
        this.mssFssChannels = mssFssChannels;
    }
}
