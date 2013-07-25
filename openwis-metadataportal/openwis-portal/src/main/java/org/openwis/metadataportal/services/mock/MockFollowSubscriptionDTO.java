/**
 *
 */
package org.openwis.metadataportal.services.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.common.utils.Utils;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.metadataportal.services.request.dto.follow.FollowSubscriptionDTO;
import org.openwis.metadataportal.services.request.dto.follow.SubscriptionStateDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public final class MockFollowSubscriptionDTO {

   /**
    * Default constructor.
    * Builds a MockFollowAdhocDTO.
    */
   private MockFollowSubscriptionDTO() {
   }

   /**
    * Description goes here.
    * @return a followAdhocDTO
    */
   public static List<FollowSubscriptionDTO> getFollowSubscriptionDTO() {
      SimpleDateFormat sdf = new SimpleDateFormat(
            OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DATETIME_FORMAT));

      List<FollowSubscriptionDTO> dtos = new ArrayList<FollowSubscriptionDTO>();

      for (long i = 0; i < 5; i++) {
         FollowSubscriptionDTO subscription = new FollowSubscriptionDTO();
         subscription.setRequestID(Utils.formatRequestID(i));
         subscription.setProductMetadataURN("urn-wmo-meteo:" + subscription.getRequestID());
         subscription.setDeployment(new Deployment("GISC-A", "http://www.akka.eu"));
         subscription.setProductMetadataTitle("Warnings " + i);
         subscription.setStartingDate(sdf.format(new Date()));
         subscription.setLastProcessingDate(sdf.format(new Date()));
         subscription.setValid((i % 2 == 0));
         subscription.setBackup(RandomUtils.nextBoolean());
         if (!subscription.isValid()) {
            subscription.setState(SubscriptionStateDTO.INVALID);
         } else {
            switch ((int) (i % 3)) {
            case 1:
               subscription.setState(SubscriptionStateDTO.SUSPENDED);
               break;
            case 2:
               subscription.setState(SubscriptionStateDTO.SUSPENDED_BACKUP);
               subscription.setBackup(true);
               break;
            default:
               subscription.setState(SubscriptionStateDTO.ACTIVE);
               break;
            }
         }
         dtos.add(subscription);
      }

      return dtos;
   }

}
