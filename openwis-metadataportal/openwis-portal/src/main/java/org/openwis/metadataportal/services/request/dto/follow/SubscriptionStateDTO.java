/**
 *
 */
package org.openwis.metadataportal.services.request.dto.follow;

/**
 * A DTO for the completeness state of the subscription. <P>
 */
public enum SubscriptionStateDTO {

   /** The "Invalid" Status */
   INVALID,

   /**
    * The "Active" Status.
    */
   ACTIVE,

   /**
    * The "Suspended" Status.
    */
   SUSPENDED,

   /**
    * The "Suspended by backup" Status.
    */
   SUSPENDED_BACKUP;
}
