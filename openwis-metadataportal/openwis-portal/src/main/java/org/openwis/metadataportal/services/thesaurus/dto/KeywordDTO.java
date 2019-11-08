//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package org.openwis.metadataportal.services.thesaurus.dto;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;

public class KeywordDTO {
   private int id;

   private String value;

   private String lang;

   private String definition;

   private String code;

   private String coordEast;

   private String coordWest;

   private String coordSouth;

   private String coordNorth;

   private String thesaurus;

   private boolean edition;

   private boolean leaf;

   private BroadNarrListDTO broadNarrListDTO;

   private BroadNarrListDTO delBroadNarrListDTO;

   private static final Namespace NS_GMD = Namespace.getNamespace("gmd",
         "http://www.isotc211.org/2005/gmd");

   private static final Namespace NS_GCO = Namespace.getNamespace("gco",
         "http://www.isotc211.org/2005/gco");

   /**
    * @param id
    * @param value
    * @param definition
    * @param code
    * @param coordEast
    * @param coordWest
    * @param coordSouth
    * @param coordNorth
    * @param thesaurus
    * @param selected
    */
   public KeywordDTO(int id, String value, String definition, String code, String coordEast,
         String coordWest, String coordSouth, String coordNorth, String thesaurus, String lang) {
      super();
      this.id = id;
      this.value = value;
      this.lang = lang;
      this.definition = definition;
      this.code = code;
      this.coordEast = coordEast;
      this.coordWest = coordWest;
      this.coordSouth = coordSouth;
      this.coordNorth = coordNorth;
      this.thesaurus = thesaurus;
   }

   public KeywordDTO(int id, String value, String definition, String code, String coordEast,
         String coordWest, String coordSouth, String coordNorth, String thesaurus, String lang,
         boolean leaf) {
      super();
      this.id = id;
      this.value = value;
      this.lang = lang;
      this.definition = definition;
      this.code = code;
      this.coordEast = coordEast;
      this.coordWest = coordWest;
      this.coordSouth = coordSouth;
      this.coordNorth = coordNorth;
      this.thesaurus = thesaurus;
      this.leaf = leaf;
   }

   public KeywordDTO() {
      super();
   }

   /**
    * @param id
    * @param value
    * @param definition
    * @param thesaurus
    * @param selected
    */
   public KeywordDTO(int id, String value, String definition, String thesaurus) {
      super();
      this.id = id;
      this.value = value;
      this.definition = definition;
      this.thesaurus = thesaurus;
   }

   /**
    * @param value
    * @param definition
    * @param thesaurus
    * @param selected
    */
   public KeywordDTO(String value, String definition, String thesaurus) {
      super();
      this.value = value;
      this.definition = definition;
      this.thesaurus = thesaurus;
   }

   public String getDefinition() {
      return definition;
   }

   public void setDefinition(String definition) {
      this.definition = definition;
   }

   public String getLang() {
      return lang;
   }

   public void setLang(String lang) {
      this.lang = lang;
   }

   public String getThesaurus() {
      return thesaurus;
   }

   public void setThesaurus(String thesaurus) {
      this.thesaurus = thesaurus;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getCode() {
      return code;
   }

   public String getRelativeCode() {
      if (code != null && code.contains("#"))
         return code.split("#")[1];
      else
         return code;
   }

   public String getNameSpaceCode() {
      if (code != null && code.contains("#"))
         return code.split("#")[0] + "#";
      else
         return "";
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getCoordEast() {
      return coordEast;
   }

   public void setCoordEast(String coordEast) {
      this.coordEast = coordEast;
   }

   public String getCoordNorth() {
      return coordNorth;
   }

   public void setCoordNorth(String coordNorth) {
      this.coordNorth = coordNorth;
   }

   public String getCoordWest() {
      return coordWest;
   }

   public void setCoordWest(String coordWest) {
      this.coordWest = coordWest;
   }

   public String getCoordSouth() {
      return coordSouth;
   }

   public void setCoordSouth(String coordSouth) {
      this.coordSouth = coordSouth;
   }

   public String getType() {
      if (thesaurus != null) {
         int tmpDotIndex = thesaurus.indexOf('.');
         return thesaurus.substring(tmpDotIndex + 1, thesaurus.indexOf(".", tmpDotIndex + 1));
      } else {
         return null;
      }
   }

   public boolean isEdition() {
      return edition;
   }

   public void setEdition(boolean edition) {
      this.edition = edition;
   }

   public boolean isLeaf() {
      return leaf;
   }

   public void setLeaf(boolean leaf) {
      this.leaf = leaf;
   }

   /**
    * Gets the broaders or narrowers list.
    * @return the broadNarrListDTO.
    */
   public BroadNarrListDTO getBroadNarrListDTO() {
      return broadNarrListDTO;
   }

   /**
    * Sets the broaders or narrowers list.
    * @param bn the broadNarrListDTO to set.
    */
   public void setBroadNarrListDTO(BroadNarrListDTO broadNarrListDTO) {
      this.broadNarrListDTO = broadNarrListDTO;
   }

   /**
    * Gets the broaders or narrowers to delete list.
    * @return the delBroadNarrListDTO.
    */
   public BroadNarrListDTO getDelBroadNarrListDTO() {
      return delBroadNarrListDTO;
   }

   /**
    * Sets the broaders or narrowers to delete list.
    * @param bn the delBroadNarrListDTO to set.
    */
   public void setDelBroadNarrListDTO(BroadNarrListDTO delBroadNarrListDTO) {
      this.delBroadNarrListDTO = delBroadNarrListDTO;
   }

   /**
    * Transform a KeywordBean object into its
    * iso19139 representation
    * 
    * <pre>
    *       <gmd:keyword xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="">
    *       <gco:CharacterString>A KEYWORD GENERATED BY XLINK SERVICE</gco:CharacterString>
    *    </gmd:keyword>
    *    <gmd:type>
    *       <gmd:MD_KeywordTypeCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode" codeListValue="TYPE"/>
    *    </gmd:type>
    *    <gmd:thesaurusName>
    *       <gmd:CI_Citation>
    *          <gmd:title>
    *             <gco:CharacterString>THESAURUS NAME</gco:CharacterString>
    *          </gmd:title>
    *          <gmd:date gco:nilReason="unknown"/>
    *       </gmd:CI_Citation>
    *    </gmd:thesaurusName>
    * </pre>
    * 
    * @return an iso19139 representation of the keyword
    */
   public Element createIso19139() {
      Element ele = new Element("MD_Keywords", NS_GMD);
      Element el = new Element("keyword", NS_GMD);
      Element cs = new Element("CharacterString", NS_GCO);
      cs.setText(this.value);

      Element type = KeywordDTO.createKeywordTypeElt(this);

      Element thesaurusName = KeywordDTO.createThesaurusNameElt(this);

      el.addContent(cs);

      ele.addContent(el);
      ele.addContent(type);
      ele.addContent(thesaurusName);

      return ele;
   }

   /**
    * Transform a list of KeywordBean object into its
    * iso19139 representation
    *  
    *  <pre>
    *  <gmd:MD_Keywords>
    *    <gmd:keyword>
    *       <gco:CharacterString>Keyword 1</gco:CharacterString>
    *    </gmd:keyword>
    *       <gmd:keyword>
    *       <gco:CharacterString>Keyword 2</gco:CharacterString>
    *    </gmd:keyword>
    *    <gmd:type>
    *       <gmd:MD_KeywordTypeCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode" codeListValue="TYPE"/>
    *    </gmd:type>
    *    <gmd:thesaurusName>
    *       <gmd:CI_Citation>
    *          <gmd:title>
    *             <gco:CharacterString>THESAURUS NAME</gco:CharacterString>
    *          </gmd:title>
    *          <gmd:date gco:nilReason="unknown"/>
    *       </gmd:CI_Citation>
    *    </gmd:thesaurusName>
    *  </gmd:MD_Keywords>
    *  </pre>
    *       
    *  
    * @param kbList
    * @return a complex iso19139 representation of the keyword
    */
   public static Element createComplexIso19139Elt(List<KeywordDTO> kbList) {
      Element root = new Element("MD_Keywords", NS_GMD);

      Element cs = new Element("CharacterString", NS_GCO);

      List<Element> keywords = new ArrayList<Element>();

      String thName = "";
      Element type = null;
      Element thesaurusName = null;

      for (KeywordDTO kb : kbList) {
         Element keyword = new Element("keyword", NS_GMD);

         cs.setText(kb.getValue());
         keyword.addContent((Content) cs.clone());
         keywords.add((Element) keyword.detach());
         thName = kb.getThesaurus();
         if (type == null)
            type = KeywordDTO.createKeywordTypeElt(kb);
         if (thesaurusName == null)
            thesaurusName = KeywordDTO.createThesaurusNameElt(kb);
      }

      // Add elements to the root MD_Keywords element.
      root.addContent(keywords);
      root.addContent(type);
      root.addContent(thesaurusName);

      return root;
   }

   /**
    * Create keyword type element.
    * 
    * @param kb
    * @return
    */
   private static Element createKeywordTypeElt(KeywordDTO kb) {
      Element type = new Element("type", NS_GMD);
      Element keywordTypeCode = new Element("MD_KeywordTypeCode", NS_GMD);
      keywordTypeCode.setAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode");
      keywordTypeCode.setAttribute("codeListValue", kb.getType());
      type.addContent(keywordTypeCode);
      
      return type;
   }
   
   /**
    * Create thesaurus name element.
    * 
    * @param kb
    * @return
    */
   private static Element createThesaurusNameElt (KeywordDTO kb) {
      Element thesaurusName = new Element("thesaurusName", NS_GMD);
      Element citation = new Element("CI_Citation", NS_GMD);
      Element title = new Element("title", NS_GMD);
      Element cs = new Element("CharacterString", NS_GCO);
      Element date = new Element("date", NS_GMD);
      date.setAttribute("nilReason", "unknown",NS_GCO);
      cs.setText(kb.thesaurus);
      title.addContent((Content) cs.clone());
      citation.addContent(0,title);
      citation.addContent(1, date);
      thesaurusName.addContent(citation);
      
      return thesaurusName;
   }
}
