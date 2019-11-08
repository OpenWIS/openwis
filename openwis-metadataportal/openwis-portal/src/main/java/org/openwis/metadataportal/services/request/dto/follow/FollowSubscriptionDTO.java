/**
 *
 */
package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.List;

import org.fao.geonet.util.ISODate;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionState;
import org.openwis.harness.mssfss.Routing;
import org.openwis.harness.mssfss.RoutingState;
import org.openwis.metadataportal.common.utils.Utils;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;

/**
 * A DTO to wrap all elements needed to display the "Follow my subscriptions" pages. <P>
 */
public class FollowSubscriptionDTO extends AbstractRequestDTO {

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

   /** If backup. */
   private boolean backup;

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
    * This method takes a list of subscriptions and returns a list of DTO objects.
    * @param subscriptions the subscriptions.
    * @return a list of DTO objects.
    */
   public static List<FollowSubscriptionDTO> subscriptionsToDTO(List<Subscription> subscriptions) {
      List<FollowSubscriptionDTO> dtos = new ArrayList<FollowSubscriptionDTO>();
      for (Subscription subscription : subscriptions) {
         FollowSubscriptionDTO dto = new FollowSubscriptionDTO();
         dto.setProductMetadataTitle(subscription.getProductMetadata().getTitle());
         dto.setProductMetadataURN(subscription.getProductMetadata().getUrn());
         dto.setRequestID(Utils.formatRequestID(subscription.getId()));
         dto.setStartingDate(new ISODate(subscription.getStartingDate().toGregorianCalendar()
               .getTimeInMillis()).toString()
               + "Z");
         dto.setUserName(subscription.getUser());
         dto.setDeployment(new DeploymentManager().getLocalDeployment());
         dto.setExtractMode(subscription.getExtractMode().toString());
         dto.setBackup(subscription.isBackup());

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

         if (subscription.getExtractMode().equals(ExtractMode.GLOBAL)) {
            dto.setExtractMode("CACHE");
         } else {
            dto.setExtractMode(subscription.getProductMetadata().getLocalDataSource());
         }

         //Last processing
         if (subscription.getLastEventDate() != null) {
            dto.setLastProcessingDate(new ISODate(subscription.getLastEventDate()
                  .toGregorianCalendar().getTimeInMillis()).toString()
                  + "Z");
         }

         dtos.add(dto);
      }
      return dtos;
   }

   /**
    * This method takes a list of subscriptions and returns a list of DTO objects.
    * @param subscriptions the subscriptions.
    * @return a list of DTO objects.
    */
   public static List<FollowSubscriptionDTO> mssFssSubscriptionsToDTO(List<Routing> subscriptions) {
      List<FollowSubscriptionDTO> dtos = new ArrayList<FollowSubscriptionDTO>();
      for (Routing subscription : subscriptions) {
         FollowSubscriptionDTO dto = new FollowSubscriptionDTO();
         dto.setProductMetadataURN(subscription.getMdURN());
         dto.setRequestID(subscription.getId());
         dto.setStartingDate(subscription.getCreationDate());
         dto.setUserName(subscription.getUser());
         dto.setDeployment(new DeploymentManager().getLocalDeployment());

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

         //Dissemination
         dto.setPrimaryDissemination(new DisseminationDTO(DisseminationDTO.Type.MSS_FSS,
               subscription.getChannel()));

         dtos.add(dto);
      }
      return dtos;
   }

   /**
    * Gets the backup.
    *
    * @return the backup
    */
   public boolean getBackup() {
      return backup;
   }

   /**
    * Sets the backup.
    *
    * @param backup the new backup
    */
   public void setBackup(boolean backup) {
      this.backup = backup;
   }

}
