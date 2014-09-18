/**
 * 
 */
package org.openwis.metadataportal.services.search.dto;

import java.util.List;


/**
 * DTO (Data Transfer Object) to transfer the list of metadata. <P>
 * 
 */
public class RelatedMetadataListDTO {

   private List<RelatedMetadataDTO> metadataList;

   public List<RelatedMetadataDTO> getMetadataList() {
      return metadataList;
   }

   public void setMetadataList(List<RelatedMetadataDTO> metadataList) {
      this.metadataList = metadataList;
   }

}