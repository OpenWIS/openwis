/**
 * 
 */
package org.openwis.metadataportal.services.thesaurus.dto;

/**
 * The Search DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class SearchDTO {

   /**
    * The keyword.
    */
   private String keyword;

   /**
    * The thesauri.
    */
   private String thesauri;

   /**
    * The typeSearch.
    */
   private String typeSearch;

   /**
    * The maxResults.
    */
   private String maxResults;

   /**
    * Gets the keyword.
    * @return the keyword.
    */
   public String getKeyword() {
      return keyword;
   }

   /**
    * Sets the keyword.
    * @param keyword the keyword to set.
    */
   public void setKeyword(String keyword) {
      this.keyword = keyword;
   }

   /**
    * Gets the thesauri.
    * @return the thesauri.
    */
   public String getThesauri() {
      return thesauri;
   }

   /**
    * Sets the thesauri.
    * @param thesauri the thesauri to set.
    */
   public void setThesauri(String thesauri) {
      this.thesauri = thesauri;
   }

   /**
    * Gets the typeSearch.
    * @return the typeSearch.
    */
   public String getTypeSearch() {
      return typeSearch;
   }

   /**
    * Sets the typeSearch.
    * @param typeSearch the typeSearch to set.
    */
   public void setTypeSearch(String typeSearch) {
      this.typeSearch = typeSearch;
   }

   /**
    * Gets the maxResults.
    * @return the maxResults.
    */
   public String getMaxResults() {
      return maxResults;
   }

   /**
    * Sets the maxResults.
    * @param maxResults the maxResults to set.
    */
   public void setMaxResults(String maxResults) {
      this.maxResults = maxResults;
   }

}
