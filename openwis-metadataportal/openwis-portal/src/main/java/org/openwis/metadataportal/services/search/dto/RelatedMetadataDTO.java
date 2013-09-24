/**
 * 
 */
package org.openwis.metadataportal.services.search.dto;


/**
 * DTO (Data Transfer Object) to transfer information for one related metadata. <P>
 * 
 */
public class RelatedMetadataDTO {

   private String id;
   
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   private String uuid;

   private String title;

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }
}