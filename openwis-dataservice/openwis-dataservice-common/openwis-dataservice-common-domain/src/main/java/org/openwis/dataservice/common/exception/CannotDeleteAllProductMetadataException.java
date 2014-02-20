package org.openwis.dataservice.common.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebFault;

/**
 * Exception thrown when an attempt to remove multiple metadata records was partially successful.
 *
 * @author lmika
 *
 */
@WebFault(name = "CannotDeleteAllProductMetadataException")
@XmlAccessorType(XmlAccessType.FIELD)
public class CannotDeleteAllProductMetadataException extends Exception {
   private static final long serialVersionUID = 1L;

   private String[] urns;

   public CannotDeleteAllProductMetadataException(String[] urns) {
      super();
      this.urns = urns;
   }

   public String[] getUrns() {
      return urns;
   }

   public void setUrns(String[] urns) {
      this.urns = urns;
   }
}
