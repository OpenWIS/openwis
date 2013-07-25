package org.openwis.metadataportal.services.request.dto.submit;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.openwis.dataservice.AdHoc;
import org.openwis.dataservice.EventBasedFrequency;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.Parameter;
import org.openwis.dataservice.RecurrentFrequency;
import org.openwis.dataservice.Request;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionState;
import org.openwis.dataservice.Value;
import org.openwis.harness.mssfss.ChangeRouting;
import org.openwis.harness.mssfss.CreateRouting;
import org.openwis.harness.mssfss.Routing;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * A DTO object to wrap the request/subscription attributes. <P>
 * This class is a DTO that wraps the request attributes. <P>
 * It provides a helper method to return a Business object ({@link AdHoc} or {@link Subscription}).<P>
 *
 */
public class SubmitRequestSubscriptionDTO {

   private String requestID;

   /**
    * The product metadata URN.
    */
   private String productMetadataURN;

   /**
    * <code>true</code> if the request is a subscription, <code>false</code> otherwise.
    */
   private boolean subscription;

   /**
    * A list of sub selection parameters.
    */
   private Set<SubmitParameterDTO> parameters;

   /**
    * The primary dissemination.
    */
   private SubmitDisseminationDTO primaryDissemination;

   /**
    * The secondary dissemination.
    */
   private SubmitDisseminationDTO secondaryDissemination;

   /**
    * The frequency.
    */
   private FrequencyDTO frequency;

   /**
    * The extract mode.
    */
   private ExtractMode extractMode;
   
   /**
    * The Backup Request Identifier.
    */
   private String backupRequestId;

   /**
    * The Backup Deployment.
    */
   private String backupDeployment;

   /**
    * Gets the backupDeployment.
    * @return the backupDeployment.
    */
   public String getBackupDeployment() {
      return backupDeployment;
   }

   /**
    * Sets the backupDeployment.
    * @param backupDeployment the backupDeployment to set.
    */
   public void setBackupDeployment(String backupDeployment) {
      this.backupDeployment = backupDeployment;
   }

   /**
    * Gets the backupRequestId.
    * @return the backupRequestId.
    */
   public String getBackupRequestId() {
      return backupRequestId;
   }

   /**
    * Sets the backupRequestId.
    * @param backupRequestId the backupRequestId to set.
    */
   public void setBackupRequestId(String backupRequestId) {
      this.backupRequestId = backupRequestId;
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
    * Gets the subscription.
    * @return the subscription.
    */
   public boolean isSubscription() {
      return subscription;
   }

   /**
    * Sets the subscription.
    * @param subscription the subscription to set.
    */
   public void setSubscription(boolean subscription) {
      this.subscription = subscription;
   }

   /**
    * Gets the parameters.
    * @return the parameters.
    */
   public Set<SubmitParameterDTO> getParameters() {
      if (parameters == null) {
         parameters = new HashSet<SubmitParameterDTO>();
      }
      return parameters;
   }

   /**
    * Sets the parameters.
    * @param parameters the parameters to set.
    */
   public void setParameters(Set<SubmitParameterDTO> parameters) {
      this.parameters = parameters;
   }

   /**
    * Gets the primaryDissemination.
    * @return the primaryDissemination.
    */
   public SubmitDisseminationDTO getPrimaryDissemination() {
      return primaryDissemination;
   }

   /**
    * Sets the primaryDissemination.
    * @param primaryDissemination the primaryDissemination to set.
    */
   public void setPrimaryDissemination(SubmitDisseminationDTO primaryDissemination) {
      this.primaryDissemination = primaryDissemination;
   }

   /**
    * Gets the secondaryDissemination.
    * @return the secondaryDissemination.
    */
   public SubmitDisseminationDTO getSecondaryDissemination() {
      return secondaryDissemination;
   }

   /**
    * Sets the secondaryDissemination.
    * @param secondaryDissemination the secondaryDissemination to set.
    */
   public void setSecondaryDissemination(SubmitDisseminationDTO secondaryDissemination) {
      this.secondaryDissemination = secondaryDissemination;
   }

   /**
    * Gets the frequency.
    * @return the frequency.
    */
   public FrequencyDTO getFrequency() {
      return frequency;
   }

   /**
    * Sets the frequency.
    * @param frequency the frequency to set.
    */
   public void setFrequency(FrequencyDTO frequency) {
      this.frequency = frequency;
   }

   /**
    * Gets the extractMode.
    * @return the extractMode.
    */
   public ExtractMode getExtractMode() {
      return extractMode;
   }

   /**
    * Sets the extractMode.
    * @param extractMode the extractMode to set.
    */
   public void setExtractMode(ExtractMode extractMode) {
      this.extractMode = extractMode;
   }

   /**
    * Converts the DTO to a Request object ({@link AdHoc} or {@link Subscription}).
    * @return a Request object ({@link AdHoc} or {@link Subscription}) built from a DTO.
    */
   public Request asRequest() throws Exception {
      Request request = null;

      // Specific attributes.
      if (isSubscription()) {
         request = new Subscription();
         Subscription subscriptionObject = (Subscription) request;
         subscriptionObject.setState(SubscriptionState.ACTIVE);
         subscriptionObject.setValid(true);
         if (frequency.getType().equals(FrequencyType.ON_PRODUCT_ARRIVAL)) {
            subscriptionObject.setFrequency(new EventBasedFrequency());
         } else {
            RecurrentFrequency recurrentFrequency = new RecurrentFrequency();
            recurrentFrequency.setReccurencePeriod(frequency.getRecurrencePeriod());
            recurrentFrequency.setReccurentScale(frequency.getRecurrentScale());
            subscriptionObject.setFrequency(recurrentFrequency);
         }

         GregorianCalendar c = (GregorianCalendar) DateTimeUtils.getUTCCalendar();
         XMLGregorianCalendar dateNow = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
         if (this.frequency.getStartingDate() != null)
         {
            Date date = DateTimeUtils.parse(frequency.getStartingDate());
            GregorianCalendar cDate = (GregorianCalendar) DateTimeUtils.getUTCCalendar();
            cDate.setTime(date);
            subscriptionObject.setStartingDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cDate));
         }
         else
         {
            subscriptionObject.setStartingDate(dateNow);
         }
      } else {
         request = new AdHoc();
      }

      if(StringUtils.isNotBlank(requestID)) {
         request.setId(Long.parseLong(requestID));
      }

      // Common Attributes.
      // 1- Parameters.
      for (SubmitParameterDTO paramDTO : getParameters()) {
         Parameter param = new Parameter();
         param.setCode(paramDTO.getCode());
         for (String str : paramDTO.getValues()) {
            Value value = new Value();
            value.setValue(str);
            param.getValues().add(value);
         }
         request.getParameters().add(param);
      }

      // 2- Disseminations.
      request.setPrimaryDissemination(primaryDissemination.asDissemination());
      if (secondaryDissemination != null) {
         request.setSecondaryDissemination(secondaryDissemination.asDissemination());
      }

      // 3- Extract mode.
      request.setExtractMode(extractMode);

      return request;
   }

   /**
    * Converts the DTO to a {@link Routing} object.
    * @return a {@link Routing} object built from a DTO.
    */
   public CreateRouting asCreateRouting() throws Exception {
      CreateRouting routing = new CreateRouting();
      routing.setUrnMd(productMetadataURN);
      routing.setChannel(primaryDissemination.getMssFssDissemination().getChannel().getChannel());

      if (frequency.getType().equals(FrequencyType.ON_PRODUCT_ARRIVAL)) {
         routing.setFrequency(new org.openwis.harness.mssfss.EventBasedFrequency());
      } else {
         org.openwis.harness.mssfss.RecurrentFrequency recurrentFrequency = new org.openwis.harness.mssfss.RecurrentFrequency();
         recurrentFrequency.setNextDate(frequency.getStartingDate());
         recurrentFrequency.setReccurencePeriod(frequency.getRecurrencePeriod());
         recurrentFrequency.setRecurrentScale(org.openwis.harness.mssfss.RecurrentScaleType
               .valueOf(frequency.getRecurrentScale().toString()));
         routing.setFrequency(recurrentFrequency);
      }

      // Common Attributes.
      // 1- Parameters.
      for (SubmitParameterDTO paramDTO : getParameters()) {
         org.openwis.harness.mssfss.Parameter param = new org.openwis.harness.mssfss.Parameter();
         param.setCode(paramDTO.getCode());
         param.getValues().addAll(paramDTO.getValues());
         routing.getSubSelectionParams().add(param);
      }

      return routing;
   }

   /**
    * Converts the DTO to a {@link Routing} object.
    * @return a {@link Routing} object built from a DTO.
    */
   public ChangeRouting asChangeRouting() throws Exception {
      ChangeRouting routing = new ChangeRouting();
      routing.setIdRequest(requestID);
      routing.setChannel(primaryDissemination.getMssFssDissemination().getChannel().getChannel());

      if (frequency.getType().equals(FrequencyType.ON_PRODUCT_ARRIVAL)) {
         routing.setFrequency(new org.openwis.harness.mssfss.EventBasedFrequency());
      } else {
         org.openwis.harness.mssfss.RecurrentFrequency recurrentFrequency = new org.openwis.harness.mssfss.RecurrentFrequency();
         recurrentFrequency.setNextDate(frequency.getStartingDate());
         recurrentFrequency.setReccurencePeriod(frequency.getRecurrencePeriod());
         recurrentFrequency.setRecurrentScale(org.openwis.harness.mssfss.RecurrentScaleType
               .valueOf(frequency.getRecurrentScale().toString()));
         routing.setFrequency(recurrentFrequency);
      }

      // Common Attributes.
      // 1- Parameters.
      for (SubmitParameterDTO paramDTO : getParameters()) {
         org.openwis.harness.mssfss.Parameter param = new org.openwis.harness.mssfss.Parameter();
         param.setCode(paramDTO.getCode());
         param.getValues().addAll(paramDTO.getValues());
         routing.getSubSelectionParams().add(param);
      }

      return routing;
   }
}
