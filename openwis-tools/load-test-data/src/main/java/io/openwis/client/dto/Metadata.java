package io.openwis.client.dto;

/**
 * A metadata record
 */
public class Metadata {
   /*
   Priority         int          `json:"priority"`
   Category         OWCategory   `json:"category"`
   Fed              bool         `json:"fed"`
   Ingested         bool         `json:"ingested"`
   StopGap          bool         `json:"stopGap"`
   GtsCategory      string       `json:"gtsCategory"`
   Originator       string       `json:"originator"`
   Process          string       `json:"process"`
   FileExtension    string       `json:"fileExtension"`
   PrivateDocs      interface{}  `json:"privateDocs,omitempty"`
   PublicDocs       interface{}  `json:"publicDocs,omitempty"`
   RelatedMetadatas interface{}  `json:"relatedMetadatas,omitempty"`
   Id               int          `json:"id"`
   Schema           string       `json:"schema"`
   Title            string       `json:"title"`
   DataPolicy       OWDataPolicy `json:"dataPolicy"`
   Urn              string       `json:"urn"`
    LocalDataSource  string       `json:"localDataSource,omitempty"`    */

   private String urn;
   private String title;

   /**
    * The metadata URN
    */
   public String getUrn() {
      return urn;
   }
   
   public void setUrn(String urn) {
      this.urn = urn;
   }

   /**
    * The title.
    */
   public String getTitle() {
      return title;
   }
   
   public void setTitle(String title) {
      this.title = title;
   }
}
