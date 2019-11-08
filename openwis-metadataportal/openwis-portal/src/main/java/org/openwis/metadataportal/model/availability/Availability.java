/**
 * 
 */
package org.openwis.metadataportal.model.availability;

import java.util.HashMap;
import java.util.Map;

/**
 * An availability object. <P>
 * <code>up</code> states the module as working or not.<P>
 * <code>additionalInfo</code> gives more information if needed.<P>
 */
public class Availability {

   private AvailabilityLevel level;

   private Map<String, String> additionalInfo;
   
   /**
    * Default constructor.
    * Builds a Availability.
    */
   public Availability() {
      super();
   }

   /**
    * Default constructor.
    * Builds a Availability.
    * @param up
    */
   public Availability(AvailabilityLevel level) {
      super();
      this.level = level;
   }

   /**
    * Gets the level.
    * @return the level.
    */
   public AvailabilityLevel getLevel() {
      return level;
   }

   /**
    * Sets the level.
    * @param level the level to set.
    */
   public void setLevel(AvailabilityLevel level) {
      this.level = level;
   }

   /**
    * Gets the additionalInfo.
    * @return the additionalInfo.
    */
   public Map<String, String> getAdditionalInfo() {
      if(additionalInfo == null) {
         additionalInfo = new HashMap<String, String>();
      }
      return additionalInfo;
   }

   /**
    * Sets the additionalInfo.
    * @param additionalInfo the additionalInfo to set.
    */
   public void setAdditionalInfo(Map<String, String> additionalInfo) {
      this.additionalInfo = additionalInfo;
   }
}
