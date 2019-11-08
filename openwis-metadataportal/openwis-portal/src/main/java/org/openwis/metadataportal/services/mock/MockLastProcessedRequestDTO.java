/**
 * 
 */
package org.openwis.metadataportal.services.mock;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.services.request.dto.follow.RequestDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class MockLastProcessedRequestDTO {

   /**
    * Default constructor.
    * Builds a MockLastProcessedRequestDTO.
    */
   private MockLastProcessedRequestDTO() {
   }
   
   /**
    * Description goes here.
    * @param maxItems the max number of processed requests retrieved.
    * @return a list of requestDTO
    */
   public static List<RequestDTO> getLastProcessedRequestDTO(int maxItems) {
      List<RequestDTO> requests = new ArrayList<RequestDTO>();
      
      for (int i = 0; i < maxItems; i++) {
         RequestDTO req = new RequestDTO();
         req.setProductMetadataTitle("Metadata "+i);
         req.setProductMetadataURN("urn-wmo-meteo::TTAiiMD"+i);
         req.setRequestID("REQ"+i);
         req.setSize(10+i);
         req.setUrl("http://google.fr/");
         
         requests.add(req);
      }
      
      return requests;
   }

}
