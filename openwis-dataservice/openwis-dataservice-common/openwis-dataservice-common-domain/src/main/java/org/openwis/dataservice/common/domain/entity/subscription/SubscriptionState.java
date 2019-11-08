package org.openwis.dataservice.common.domain.entity.subscription;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The Enum SubscriptionState.
 */

@XmlType(name = "SubscriptionState")
@XmlEnum
public enum SubscriptionState {

   /** The ACTIVE. */
   ACTIVE,

   /** The SUSPENDE d_ backup. */
   SUSPENDED_BACKUP,

   /** The SUSPENDED. */
   SUSPENDED;

}
