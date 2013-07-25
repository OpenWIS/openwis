/**
 * 
 */
package org.openwis.metadataportal.services.datapolicy.dto;

import java.util.HashSet;
import java.util.Set;

import org.openwis.metadataportal.model.datapolicy.DataPolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPoliciesDTO {

    private Set<DataPolicy> dataPolicies;

    /**
     * Gets the names.
     * @return the names.
     */
    public Set<DataPolicy> getDataPolicies() {
        if (dataPolicies == null) {
            dataPolicies = new HashSet<DataPolicy>();
        }
        return dataPolicies;
    }

    /**
     * Sets the names.
     * @param names the names to set.
     */
    public void setDataPolicies(Set<DataPolicy> dataPolicies) {
        this.dataPolicies = dataPolicies;
    }
}
