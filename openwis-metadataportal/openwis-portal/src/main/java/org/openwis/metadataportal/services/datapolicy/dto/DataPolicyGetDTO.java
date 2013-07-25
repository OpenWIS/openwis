/**
 * 
 */
package org.openwis.metadataportal.services.datapolicy.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.Operation;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicyGetDTO {

    private Collection<Operation> operations;

    private DataPolicy dataPolicy;

    /**
     * Gets the operations.
     * @return the operations.
     */
    public Collection<Operation> getOperations() {
        if (operations == null) {
            operations = new ArrayList<Operation>();
        }
        return operations;
    }

    /**
     * Sets the operations.
     * @param operations the operations to set.
     */
    public void setOperations(Collection<Operation> operations) {
        this.operations = operations;
    }

    /**
     * Gets the dataPolicy.
     * @return the dataPolicy.
     */
    public DataPolicy getDataPolicy() {
        return dataPolicy;
    }

    /**
     * Sets the dataPolicy.
     * @param dataPolicy the dataPolicy to set.
     */
    public void setDataPolicy(DataPolicy dataPolicy) {
        this.dataPolicy = dataPolicy;
    }
}
