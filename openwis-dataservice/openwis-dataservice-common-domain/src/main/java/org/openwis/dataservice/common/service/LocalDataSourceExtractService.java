/**
 *
 */
package org.openwis.dataservice.common.service;

import java.util.List;

import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.entity.request.Parameter;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public interface LocalDataSourceExtractService {

   /**
    * Description goes here.
    *
    * @param parameters the parameters
    * @param metadataURN the metadata urn
    * @param processedRequestId the processed request id
    * @param localDataSource the local data source
    * @param uri the uri
    * @param productId product id to extract (may be null)
    * @return the message status
    */
   public MessageStatus extract(List<Parameter> parameters, String metadataURN,
         Long processedRequestId, String localDataSource, String uri, String productId);

   /**
    * Description goes here.
    *
    * @param ldsName the local datasource name
    * @param timestampUTC the timestamp UTC
    * @return the metadata urns
    */
   public List<String> getAvailability(String ldsName, String timestampUTC);

   /**
    * Gets the all local data source reference.
    *
    * @return the all local data source reference
    */
   public List<String> getAllLocalDataSourceRef();

   /**
    * Description goes here.
    *
    * @param metadataURN the metadata urn
    * @param processedRequestId the processed request id
    * @param localDataSource the local data source
    * @return the message status
    */
   public MessageStatus monitorExtraction(String metadataURN, Long processedRequestId,
         String localDataSource);
}
