/**
 * 
 */
package org.openwis.metadataportal.services.mock;

import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.RecurrentScale;
import org.openwis.dataservice.RecurrentUpdateFrequency;

/**
 * @author BAILLAGOU
 *
 */
public class MockProductMetadata {
	
	public static ProductMetadata getMock(String urn, int index) {
		ProductMetadata productMetadata = new ProductMetadata();
        productMetadata.setUrn(urn+index);
        productMetadata.setDataPolicy("DP"+index );
        productMetadata.setFed(false);
        productMetadata.setFncPattern("FNC pattern "+index);
        productMetadata.setGtsCategory("GTS Category "+index);
        productMetadata.setIngested(true);
        productMetadata.setLocalDataSource("localDataSource "+index);
        productMetadata.setOriginator("originator "+index);
        productMetadata.setOverridenDataPolicy(" over DP "+index);
        productMetadata.setOverridenFncPattern("over FNC "+index);
        productMetadata.setOverridenPriority(1);
        productMetadata.setPriority(0);
        productMetadata.setProcess("import");
        productMetadata.setTitle("this is a title "+index);
        RecurrentUpdateFrequency recurrentUpdateFrequency = new RecurrentUpdateFrequency();
        recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.DAY);
        recurrentUpdateFrequency.setRecurrentPeriod(1);
        productMetadata.setUpdateFrequency(recurrentUpdateFrequency);
        
        return productMetadata;
	}

}
