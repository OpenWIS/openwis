/**
 *
 */
package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.fao.geonet.util.ISODate;
import org.openwis.dataservice.EventBasedFrequency;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.Parameter;
import org.openwis.dataservice.RecurrentFrequency;
import org.openwis.dataservice.RecurrentScale;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionState;
import org.openwis.dataservice.Value;
import org.openwis.harness.mssfss.Routing;
import org.openwis.harness.mssfss.RoutingState;
import org.openwis.metadataportal.common.utils.Utils;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;
import org.openwis.metadataportal.services.request.dto.submit.FrequencyDTO;
import org.openwis.metadataportal.services.request.dto.submit.FrequencyType;

import com.google.common.base.Joiner;

/**
 * A DTO to wrap all elements needed to display the "Follow my subscriptions" pages. <P>
 */
public class SubscriptionDTO extends AbstractRequestDTO {

   /**
    * The starting date.
    */
   private String startingDate;

   /**
    * The last processing date.
    */
   private String lastProcessingDate;

   /** If valid. */
   private boolean valid;

   /** The state. */
   private SubscriptionStateDTO state;

   /**
    * The frequency.
    */
   private FrequencyDTO frequency;

   /**
    * Gets the startingDate.
    * @return the startingDate.
    */
   public String getStartingDate() {
      return startingDate;
   }

   /**
    * Sets the startingDate.
    * @param startingDate the startingDate to set.
    */
   public void setStartingDate(String startingDate) {
      this.startingDate = startingDate;
   }

   /**
    * Gets the lastProcessingDate.
    * @return the lastProcessingDate.
    */
   public String getLastProcessingDate() {
      return lastProcessingDate;
   }

   /**
    * Sets the lastProcessingDate.
    * @param lastProcessingDate the lastProcessingDate to set.
    */
   public void setLastProcessingDate(String lastProcessingDate) {
      this.lastProcessingDate = lastProcessingDate;
   }

   /**
    * Checks if is valid.
    *
    * @return true, if is valid
    */
   public boolean isValid() {
      return valid;
   }

   /**
    * Sets the valid.
    *
    * @param valid the new valid
    */
   public void setValid(boolean valid) {
      this.valid = valid;
   }

   /**
    * Gets the state.
    *
    * @return the state
    */
   public SubscriptionStateDTO getState() {
      return state;
   }

   /**
    * Sets the state.
    *
    * @param state the new state
    */
   public void setState(SubscriptionStateDTO state) {
      this.state = state;
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
    * This method takes a list of subscriptions and returns a list of DTO objects.
    * @param subscriptions the subscriptions.
    * @return a list of DTO objects.
    */
   public static List<SubscriptionDTO> subscriptionsToDTO(List<Subscription> subscriptions) {
      List<SubscriptionDTO> dtos = new ArrayList<SubscriptionDTO>();
      for (Subscription subscription : subscriptions) {
         dtos.add(subscriptionToDTO(subscription));
      }
      return dtos;
   }

   /**
    * Subscription to dto.
    *
    * @param subscription the subscription
    * @return the subscription dto
    */
   @SuppressWarnings("unchecked")
   public static SubscriptionDTO subscriptionToDTO(Subscription subscription) {
      SubscriptionDTO dto = new SubscriptionDTO();
      dto.setProductMetadataTitle(subscription.getProductMetadata().getTitle());
      dto.setProductMetadataURN(subscription.getProductMetadata().getUrn());
      dto.setRequestID(Utils.formatRequestID(subscription.getId()));
      dto.setStartingDate(new ISODate(subscription.getStartingDate().toGregorianCalendar()
            .getTimeInMillis()).toString()
            + "Z");
      dto.setUserName(subscription.getUser());
      dto.setDeployment(new DeploymentManager().getLocalDeployment());
      if (subscription.getExtractMode().equals(ExtractMode.GLOBAL)) {
         dto.setExtractMode("CACHE");
      } else {
         dto.setExtractMode(subscription.getProductMetadata().getLocalDataSource());
      }

      dto.setValid(subscription.isValid());
      if (!dto.isValid()) {
         dto.setState(SubscriptionStateDTO.INVALID);
      } else {
         SubscriptionState subscriptionState = subscription.getState();
         if (subscriptionState != null) {
            switch (subscriptionState) {
            case ACTIVE:
               dto.setState(SubscriptionStateDTO.ACTIVE);
               break;
            case SUSPENDED:
               dto.setState(SubscriptionStateDTO.SUSPENDED);
               break;
            case SUSPENDED_BACKUP:
               dto.setState(SubscriptionStateDTO.SUSPENDED_BACKUP);
               break;
            }
         }
      }
      //Frequency.
      dto.setFrequency(new FrequencyDTO());
      if (subscription.getFrequency() instanceof EventBasedFrequency) {
         dto.getFrequency().setType(FrequencyType.ON_PRODUCT_ARRIVAL);
      } else if (subscription.getFrequency() instanceof RecurrentFrequency) {
         RecurrentFrequency recFrec = (RecurrentFrequency) subscription.getFrequency();
         dto.getFrequency().setType(FrequencyType.RECURRENT_PROCESSING);
         dto.getFrequency().setRecurrencePeriod(recFrec.getReccurencePeriod());
         dto.getFrequency().setRecurrentScale(recFrec.getReccurentScale());
         dto.getFrequency().setStartingDate(new ISODate(subscription.getStartingDate().toGregorianCalendar()
               .getTimeInMillis()).toString()
               + "Z");
      }

      //Last processing
      if (subscription.getLastEventDate() != null) {
         dto.setLastProcessingDate(new ISODate(subscription.getLastEventDate()
               .toGregorianCalendar().getTimeInMillis()).toString()
               + "Z");
      }

      //SSP
      for (Parameter param : subscription.getParameters()) {
         Collection<String> values = CollectionUtils.collect(param.getValues(), new Transformer() {
            @Override
            public Object transform(Object input) {
               return ((Value) input).getValue();
            }
         });
         dto.getSsp().add(new SubSelectionParametersDTO(param.getCode(), values));
      }

      //Dissemination.
      if (subscription.getPrimaryDissemination() != null) {
         dto.setPrimaryDissemination(DisseminationDTO.disseminationToDTO(subscription
               .getPrimaryDissemination()));
      }

      if (subscription.getSecondaryDissemination() != null) {
         dto.setSecondaryDissemination(DisseminationDTO.disseminationToDTO(subscription
               .getSecondaryDissemination()));
      }

      return dto;
   }

   /**
    * This method takes a list of subscriptions and returns a list of DTO objects.
    * @param subscriptions the subscriptions.
    * @return a list of DTO objects.
    */
   public static List<SubscriptionDTO> mssFssSubscriptionsToDTO(List<Routing> subscriptions) {
      List<SubscriptionDTO> dtos = new ArrayList<SubscriptionDTO>();
      for (Routing subscription : subscriptions) {
         dtos.add(mssFssSubscriptionsToDTO(subscription));
      }
      return dtos;
   }

   /**
    * This method takes a list of subscriptions and returns a list of DTO objects.
    *
    * @param subscription the subscription
    * @return a list of DTO objects.
    */
   public static SubscriptionDTO mssFssSubscriptionsToDTO(Routing subscription) {
      SubscriptionDTO dto = new SubscriptionDTO();
      dto.setProductMetadataURN(subscription.getMdURN());
      dto.setRequestID(subscription.getId());
      dto.setStartingDate(subscription.getCreationDate());
      dto.setUserName(subscription.getUser());
      dto.setDeployment(new DeploymentManager().getLocalDeployment());
      dto.setExtractMode("CACHE");

      if ((subscription.getState() != null)
            && (subscription.getState().equals(RoutingState.ACTIVE))) {
         dto.setState(SubscriptionStateDTO.ACTIVE);
      } else {
         dto.setState(SubscriptionStateDTO.SUSPENDED);
      }

      //Last processing
      if (subscription.getLastEventDate() != null) {
         dto.setLastProcessingDate(subscription.getLastEventDate());
      }

      //Frequency.
      dto.setFrequency(new FrequencyDTO());
      if (subscription.getFrequency() instanceof org.openwis.harness.mssfss.EventBasedFrequency) {
         dto.getFrequency().setType(FrequencyType.ON_PRODUCT_ARRIVAL);
      } else if (subscription.getFrequency() instanceof org.openwis.harness.mssfss.RecurrentFrequency) {
         org.openwis.harness.mssfss.RecurrentFrequency recFrec = (org.openwis.harness.mssfss.RecurrentFrequency) subscription
               .getFrequency();
         dto.getFrequency().setType(FrequencyType.RECURRENT_PROCESSING);
         dto.getFrequency().setStartingDate(recFrec.getNextDate());
         dto.getFrequency().setRecurrencePeriod(recFrec.getReccurencePeriod());
         dto.getFrequency().setRecurrentScale(
               RecurrentScale.valueOf(recFrec.getRecurrentScale().toString()));
      }

      //SSP
      for (org.openwis.harness.mssfss.Parameter param : subscription.getSubSelectionParams()) {
         dto.getSsp().add(new SubSelectionParametersDTO(param.getCode(), param.getValues()));
      }

      //Dissemination
      dto.setPrimaryDissemination(new DisseminationDTO(DisseminationDTO.Type.MSS_FSS, subscription
            .getChannel()));

      return dto;
   }

}
