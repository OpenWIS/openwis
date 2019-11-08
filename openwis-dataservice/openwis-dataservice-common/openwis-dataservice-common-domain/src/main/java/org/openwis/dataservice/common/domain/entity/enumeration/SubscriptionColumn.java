package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ProductMetadataColumn enumeration. <P>
 */
@XmlType(name = "SubscriptionColumn")
@XmlEnum
public enum SubscriptionColumn {

   /** The URN. */
   URN("pm.urn"),
   /** The Product metadata title. */
   TITLE("pm.title"),
   /** The User. */
   USER("subscription.user"),
   /** The ID. */
   ID("subscription.id"),
   /** The Status of the result. */
   STATUS("pr.requestResultStatus"),

   /** The BACKUP. */
   BACKUP("subscription.backup"),

   /** The CREATIO n_ date. */
   STARTING_DATE("subscription.startingDate");

   /** The attribute. */
   private final String attribute;

   /**
    * Default constructor.
    * Builds a ProductMetadataColumn.
    *
    * @param attribute the attribute
    */
   private SubscriptionColumn(String attribute) {
      this.attribute = attribute;
   }

   /**
    * Gets the attribute.
    * @return the attribute.
    */
   public String getAttribute() {
      return attribute;
   }

}
