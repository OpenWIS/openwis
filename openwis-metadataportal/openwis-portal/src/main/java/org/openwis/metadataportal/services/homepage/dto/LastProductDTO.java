/**
 * 
 */
package org.openwis.metadataportal.services.homepage.dto;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class LastProductDTO {

    private String id;

    private String name;

    private String date;

    private String url;

    private String extractMode;
    
    private String requestId;

    private String requestType;
    
    private String productMetadataURN;


    /**
     * Default constructor.
     * Builds a LastProductDTO.
     */
    public LastProductDTO() {
        super();
    }

    /**
     * Default constructor.
     * Builds a LastProductDTO.
     * @param id
     * @param name
     */
    public LastProductDTO(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

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
     * Gets the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
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
    
   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   /**
    * Gets the extractMode.
    * @return the extractMode.
    */
   public String getExtractMode() {
      return extractMode;
   }

   /**
    * Sets the extractMode.
    * @param extractMode the extractMode to set.
    */
   public void setExtractMode(String extractMode) {
      this.extractMode = extractMode;
   }
   
   /**
    * Gets the requestType.
    * @return the requestType.
    */
   public String getRequestType() {
      return requestType;
   }

   /**
    * Sets the requestType.
    * @param requestType the requestType to set.
    */
   public void setRequestType(String requestType) {
      this.requestType = requestType;
   }
   
   public String getRequestId() {
      return requestId;
   }
   
   public void setRequestId(String requestId) {
      this.requestId = requestId;
   }
   
   /**
    * Gets the productMetadataURN.
    * @return the productMetadataURN.
    */
   public String getProductMetadataURN() {
      return productMetadataURN;
   }

   /**
    * Sets the productMetadataURN.
    * @param productMetadataURN the productMetadataURN to set.
    */
   public void setProductMetadataURN(String productMetadataURN) {
      this.productMetadataURN = productMetadataURN;
   }
}
