package org.openwis.metadataportal.services.thesaurus.dto;

/**
 * The Node DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class NodeDTO {
   
   /**
    * The id.
    */
   private String id;

   /**
    * The keywordDTO.
    */
   private KeywordDTO keyword;

   /**
    * Gets the id.
    * @return the id.
    */
   public String getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * Gets the keyword.
    * @return the keyword.
    */
   public KeywordDTO getKeyword() {
      return keyword;
   }

   /**
    * Sets the keyword.
    * @param keyword the keyword to set.
    */
   public void setKeyword(KeywordDTO keyword) {
      this.keyword = keyword;
   }
}
