/**
 * 
 */
package org.openwis.metadataportal.services.metainfo.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.dataservice.ProductMetadata;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetaInfoDTO {
    
    /**
     * Comment for <code>productMetadata</code>
     */
    private List<ProductMetadata> productsMetadata;
    
    /**
     * Comment for <code>dataPolicies</code>
     */
    private List<DataPolicy> dataPolicies;

    /**
     * Gets the productsMetadata.
     * @return the productsMetadata.
     */
    public List<ProductMetadata> getProductsMetadata() {
        if (productsMetadata == null) {
            productsMetadata = new ArrayList<ProductMetadata>();
        }
        return productsMetadata;
    }

    /**
     * Sets the productsMetadata.
     * @param productsMetadata the productsMetadata to set.
     */
    public void setProductsMetadata(List<ProductMetadata> productsMetadata) {
        this.productsMetadata = productsMetadata;
    }

    /**
     * Gets the dataPolicies.
     * @return the dataPolicies.
     */
    public List<DataPolicy> getDataPolicies() {
        if (dataPolicies == null) {
            dataPolicies = new ArrayList<DataPolicy>();
        }
        return dataPolicies;
    }

    /**
     * Sets the dataPolicies.
     * @param dataPolicies the dataPolicies to set.
     */
    public void setDataPolicies(List<DataPolicy> dataPolicies) {
        this.dataPolicies = dataPolicies;
    }

}
