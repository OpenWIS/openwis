package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The Blacklist Status.
 */
@XmlType(name = "BlacklistStatus")
@XmlEnum
public enum BlacklistStatus {

   /** Warned by number of disseminations. */
   WARNED_BY_NUMBER_OF_DISSEMINATIONS,

   /** Warned by volume of disseminations. */
   WARNED_BY_VOLUME_OF_DISSEMINATIONS,

   /** Blacklisted by administrator . */
   BLACKLISTED_BY_ADMIN,

   /** Blacklisted by number of disseminations. */
   BLACKLISTED_BY_NUMBER_OF_DISSEMINATIONS,

   /** Blacklisted by size of disseminations. */
   BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS,

   /** The user is un-blacklisted by administrator. */
   NOT_BLACKLISTED_BY_ADMIN,

   /** Not blacklisted. */
   NOT_BLACKLISTED;

}
