package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ProductMetadataColumn enumeration. <P>
 */
@XmlType(name = "RequestColumn")
@XmlEnum
public enum RequestColumn {

   /** The URN. */
   URN("pm.urn"),
   /** The Product metadata title. */
   TITLE("pm.title"),
   /** The local datasource. */
   LOCAL_DATASOURCE("pm.localDataSource"),
   /** The User. */
   USER("request.user"),
   /** The ID. */
   ID("request.id"),
   /** The Creation date. */
   CREATION_DATE("pr.creationDate"),
   /** The Status of the result. */
   STATUS("pr.requestResultStatus"),
   /** The update frequency. */
   VOLUME("pr.size"), DEPLOYMENT("");

   /** The attribute. */
   private final String attribute;

   /**
    * Default constructor.
    * Builds a ProductMetadataColumn.
    *
    * @param attribute the attribute
    */
   private RequestColumn(String attribute) {
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
