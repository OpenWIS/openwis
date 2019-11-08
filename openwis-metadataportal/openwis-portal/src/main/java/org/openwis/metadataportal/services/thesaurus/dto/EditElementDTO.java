/**
 * 
 */
package org.openwis.metadataportal.services.thesaurus.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The Edit Element DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class EditElementDTO {

   /**
    * The keywordRef.
    */
   private KeywordDTO keywordRef;

   /**
    * The allKeywordsDTO.
    */
   private List<KeywordDTO> allKeywordsDTO;

   /**
    * The broadNarrListDTO.
    */
   private List<BroadNarrListDTO> broadNarrListDTO;

   /**
    * Gets the keywordRef.
    * @return the keywordRef.
    */
   public KeywordDTO getKeywordRef() {
      return keywordRef;
   }

   /**
    * Sets the keywordRef.
    * @param keywordRef the keywordRef to set.
    */
   public void setKeywordRef(KeywordDTO keywordRef) {
      this.keywordRef = keywordRef;
   }

   /**
    * Gets the broadNarrListDTO.
    * @return the broadNarrListDTO.
    */
   public List<BroadNarrListDTO> getBroadNarrListDTO() {
      if (broadNarrListDTO == null)
      {
         broadNarrListDTO = new ArrayList<BroadNarrListDTO>();
      }
      return broadNarrListDTO;
   }

   /**
    * Sets the broadNarrListDTO.
    * @param broadNarrListDTO the broadNarrListDTO to set.
    */
   public void setBroadNarrListDTO(List<BroadNarrListDTO> broadNarrListDTO) {
      this.broadNarrListDTO = broadNarrListDTO;
   }

   /**
    * Gets the allKeywordsDTO.
    * @return the allKeywordsDTO.
    */
   public List<KeywordDTO> getAllKeywordsDTO() {
      if (allKeywordsDTO == null)
      {
         allKeywordsDTO = new ArrayList<KeywordDTO>();
      }
      return allKeywordsDTO;
   }

   /**
    * Sets the allKeywordsDTO.
    * @param allKeywordsDTO the allKeywordsDTO to set.
    */
   public void setAllKeywordsDTO(List<KeywordDTO> allKeywordsDTO) {
      this.allKeywordsDTO = allKeywordsDTO;
   }
}
