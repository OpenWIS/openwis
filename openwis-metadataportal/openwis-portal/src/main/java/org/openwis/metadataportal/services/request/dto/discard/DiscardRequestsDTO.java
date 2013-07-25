/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.discard;

import java.util.List;

/**
 * List of requests to discards
 * 
 */
public class DiscardRequestsDTO {

   /**
    * The discard requests.
    * @member: discardRequests
    */
   private List<DiscardRequestDTO> discardRequests;

   /**
    * Gets the discardRequests.
    * @return the discardRequests.
    */
   public List<DiscardRequestDTO> getDiscardRequests() {
      return discardRequests;
   }

   /**
    * Sets the discardRequests.
    * @param discardRequests the discardRequests to set.
    */
   public void setDiscardRequests(List<DiscardRequestDTO> discardRequests) {
      this.discardRequests = discardRequests;
   }
   
}
