/**
 *
 */
package org.openwis.datasource.server.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.hash.HashUtils;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public final class RequestUtils {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

   /**
    * Default constructor.
    * Builds a RequestUtils.
    */
   private RequestUtils() {
      super();
   }

   /**
    * Description goes here.
    *
    * @param pr the pr
    * @return the string
    */
   public static String composeUriForSubscription(ProcessedRequest pr) {
      StringBuffer sb = new StringBuffer();
      try {
         sb.append(HashUtils.getMD5Digest(pr.getRequest().getUser()));

         sb.append('/');
         Calendar c = DateTimeUtils.getUTCCalendar();
         c.setTime(pr.getCreationDate());
         sb.append(String.format("%1$tY/%1$tm-%1$te/subscription/", c));
         sb.append(pr.getRequest().getId());
         sb.append('/');
         sb.append(HashUtils.getMD5Digest(pr.getId().toString()));
      } catch (NoSuchAlgorithmException e) {
         logger.error("Cannot create the Subscription URI", e);
      }
      return sb.toString();
   }

   /**
    * Description goes here.
    *
    * @param pr the pr
    * @return the string
    */
   public static String composeUriForRequest(ProcessedRequest pr) {
      StringBuffer sb = new StringBuffer();
      try {
         sb.append(HashUtils.getMD5Digest(pr.getRequest().getUser()));
         sb.append("/");
         Calendar c = DateTimeUtils.getUTCCalendar();
         c.setTime(pr.getCreationDate());
         sb.append(String.format("%1$tY/%1$tm-%1$te/request/", c));
         sb.append(HashUtils.getMD5Digest(pr.getId().toString()));
      } catch (NoSuchAlgorithmException e) {
         logger.error("Cannot create the Request URI", e);
      }
      return sb.toString();
   }

   /**
    * Updates the status of the request.
    *
    * @param existingRequest the existing request
    * @param messageStatus the message status
    */
   public static void updateRequestStatus(ProcessedRequest existingRequest,
         MessageStatus messageStatus) {
      switch (messageStatus.getStatus()) {
      case ERROR:
         existingRequest.setRequestResultStatus(RequestResultStatus.FAILED);
         existingRequest.setUri(null);
         break;
      case NO_RESULT_FOUND:
         existingRequest.setRequestResultStatus(RequestResultStatus.EXTRACTED);
         break;
      case ONGOING_EXTRACTION:
         existingRequest.setRequestResultStatus(RequestResultStatus.ONGOING_EXTRACTION);
         break;
      case EXTRACTED:
         existingRequest.setRequestResultStatus(RequestResultStatus.EXTRACTED);
         break;
      default:
         break;
      }
      existingRequest.setMessage(messageStatus.getMessage());
   }

}
