/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

/**
 * The request DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class RequestDTO extends AbstractRequestDTO {
   
   /**
    * The size.
    */
   private long size;

   /**
    * The URL.
    */
   private String url;
   
   /**
    * Gets the size.
    * @return the size.
    */
   public long getSize() {
      return size;
   }

   /**
    * Sets the size.
    * @param size the size to set.
    */
   public void setSize(long size) {
      this.size = size;
   }
   
   /**
    * Gets the url.
    * @return the url.
    */
   public String getUrl() {
      return url;
   }

   /**
    * Sets the url.
    * @param url the url to set.
    */
   public void setUrl(String url) {
      this.url = url;
   }

   /**
    * The utility method to convert processedRequest objects to DTO.
    * @param processedRequests the processed requests to convert.
    * @return the DTO for displaying last processed requests.
    */
   public static List<RequestDTO> processedRequestsToDTO(List<ProcessedRequest> processedRequests) {

      List<RequestDTO> requests = new ArrayList<RequestDTO>();
      
      String stagingPostUrl = OpenwisMetadataPortalConfig
      .getString(ConfigurationConstants.URL_STAGING_POST);
      
      for (ProcessedRequest processedRequest : processedRequests) {
         RequestDTO request = new RequestDTO();
         request.setProductMetadataURN(processedRequest.getRequest().getProductMetadata().getUrn());
         request.setProductMetadataTitle(processedRequest.getRequest().getProductMetadata().getTitle());
         request.setSize(processedRequest.getSize());
         if (StringUtils.isNotBlank(processedRequest.getUri())) {
            request.setUrl(stagingPostUrl + processedRequest.getUri());
         }
         
         requests.add(request);
      }
      
      return requests;
   }
}
