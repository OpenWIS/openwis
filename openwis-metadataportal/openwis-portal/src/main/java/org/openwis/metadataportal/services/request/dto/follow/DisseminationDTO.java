/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.follow;

import org.openwis.dataservice.Dissemination;
import org.openwis.dataservice.MssfssDissemination;
import org.openwis.dataservice.PublicDissemination;
import org.openwis.dataservice.RmdcnDissemination;
import org.openwis.dataservice.ShoppingCartDissemination;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DisseminationDTO {

   public enum Type {
      MSS_FSS,

      RMDCN,

      PUBLIC,

      STAGING_POST
   }

   private Type type;

   private Object o;

   /**
    * Default constructor.
    * Builds a DisseminationDTO.
    * @param type
    */
   public DisseminationDTO(Type type) {
      super();
      this.type = type;
   }

   /**
    * Default constructor.
    * Builds a DisseminationDTO.
    * @param type
    * @param o
    */
   public DisseminationDTO(Type type, Object o) {
      super();
      this.type = type;
      this.o = o;
   }

   /**
    * Gets the type.
    * @return the type.
    */
   public Type getType() {
      return type;
   }

   /**
    * Sets the type.
    * @param type the type to set.
    */
   public void setType(Type type) {
      this.type = type;
   }

   /**
    * Gets the o.
    * @return the o.
    */
   public Object getO() {
      return o;
   }

   /**
    * Sets the o.
    * @param o the o to set.
    */
   public void setO(Object o) {
      this.o = o;
   }

   public static DisseminationDTO disseminationToDTO(Dissemination dissemination) {
      DisseminationDTO dto = null;
      if (dissemination instanceof MssfssDissemination) {
         dto = new DisseminationDTO(Type.MSS_FSS);
      } else if (dissemination instanceof ShoppingCartDissemination) {
         dto = new DisseminationDTO(Type.STAGING_POST, dissemination);
      } else if (dissemination instanceof RmdcnDissemination) {
         dto = new DisseminationDTO(Type.RMDCN, dissemination);
      } else if (dissemination instanceof PublicDissemination) {
         dto = new DisseminationDTO(Type.PUBLIC, dissemination);
      }
      return dto;
   }
}
