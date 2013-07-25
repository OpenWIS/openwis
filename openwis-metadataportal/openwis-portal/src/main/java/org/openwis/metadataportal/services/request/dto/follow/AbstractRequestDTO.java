/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.deployment.Deployment;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public abstract class AbstractRequestDTO {

   /**
    * The title of the product.
    */
   private String productMetadataTitle;

   /**
    * The URN of the product.
    */
   private String productMetadataURN;

   /**
    * The product metadata datasource.
    */
   private String productMetadataDataSource;

   /**
    * The request ID.
    */
   private String requestID;

   /**
    * The request type.
    */
   private String requestType;

   /**
    * The request type.
    */
   private String extractMode;

   /**
    * The deployment.
    */
   private Deployment deployment;

   /**
    * The username.
    */
   private String userName;

   /**
    * The sub-selection parameters.
    */
   private List<SubSelectionParametersDTO> ssp;

   /**
    * The dissemination used.
    */
   private DisseminationDTO primaryDissemination;

   /**
    * The dissemination used.
    */
   private DisseminationDTO secondaryDissemination;

   /**
    * Gets the userName.
    * @return the userName.
    */
   public String getUserName() {
      return userName;
   }

   /**
    * Sets the userName.
    * @param userName the userName to set.
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

   /**
    * Gets the productMetadataTitle.
    * @return the productMetadataTitle.
    */
   public String getProductMetadataTitle() {
      return productMetadataTitle;
   }

   /**
    * Sets the productMetadataTitle.
    * @param productMetadataTitle the productMetadataTitle to set.
    */
   public void setProductMetadataTitle(String productMetadataTitle) {
      this.productMetadataTitle = productMetadataTitle;
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

   /**
    * Gets the productMetadataDataSource.
    * @return the productMetadataDataSource.
    */
   public String getProductMetadataDataSource() {
      return productMetadataDataSource;
   }

   /**
    * Sets the productMetadataDataSource.
    * @param productMetadataDataSource the productMetadataDataSource to set.
    */
   public void setProductMetadataDataSource(String productMetadataDataSource) {
      this.productMetadataDataSource = productMetadataDataSource;
   }

   /**
    * Gets the requestID.
    * @return the requestID.
    */
   public String getRequestID() {
      return requestID;
   }

   /**
    * Sets the requestID.
    * @param requestID the requestID to set.
    */
   public void setRequestID(String requestID) {
      this.requestID = requestID;
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
    * Gets the deployment.
    * @return the deployment.
    */
   public Deployment getDeployment() {
      return deployment;
   }

   /**
    * Sets the deployment.
    * @param deployment the deployment to set.
    */
   public void setDeployment(Deployment deployment) {
      this.deployment = deployment;
   }

   /**
    * Gets the ssp.
    * @return the ssp.
    */
   public List<SubSelectionParametersDTO> getSsp() {
      if (ssp == null) {
         ssp = new ArrayList<SubSelectionParametersDTO>();
      }
      return ssp;
   }

   /**
    * Sets the ssp.
    * @param ssp the ssp to set.
    */
   public void setSsp(List<SubSelectionParametersDTO> ssp) {
      this.ssp = ssp;
   }

   /**
    * Gets the primaryDissemination.
    * @return the primaryDissemination.
    */
   public DisseminationDTO getPrimaryDissemination() {
      return primaryDissemination;
   }

   /**
    * Sets the primaryDissemination.
    * @param primaryDissemination the primaryDissemination to set.
    */
   public void setPrimaryDissemination(DisseminationDTO primaryDissemination) {
      this.primaryDissemination = primaryDissemination;
   }

   /**
    * Gets the secondaryDissemination.
    * @return the secondaryDissemination.
    */
   public DisseminationDTO getSecondaryDissemination() {
      return secondaryDissemination;
   }

   /**
    * Sets the secondaryDissemination.
    * @param secondaryDissemination the secondaryDissemination to set.
    */
   public void setSecondaryDissemination(DisseminationDTO secondaryDissemination) {
      this.secondaryDissemination = secondaryDissemination;
   }
}
