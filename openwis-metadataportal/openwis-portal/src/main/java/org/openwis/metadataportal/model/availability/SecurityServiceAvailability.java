/**
 * 
 */
package org.openwis.metadataportal.model.availability;

/**
 * An object to list all modules of security service with corresponding availability. <P>
 * Explanation goes here. <P>
 * 
 */
public class SecurityServiceAvailability {

   private Availability securityService;

   private Availability ssoService;

   /**
    * Gets the securityService.
    * @return the securityService.
    */
   public Availability getSecurityService() {
      return securityService;
   }

   /**
    * Sets the securityService.
    * @param securityService the securityService to set.
    */
   public void setSecurityService(Availability securityService) {
      this.securityService = securityService;
   }

   /**
    * Gets the ssoService.
    * @return the ssoService.
    */
   public Availability getSsoService() {
      return ssoService;
   }

   /**
    * Sets the ssoService.
    * @param ssoService the ssoService to set.
    */
   public void setSsoService(Availability ssoService) {
      this.ssoService = ssoService;
   }
}
