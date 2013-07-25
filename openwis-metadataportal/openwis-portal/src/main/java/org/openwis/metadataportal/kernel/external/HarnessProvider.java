/**
 * 
 */
package org.openwis.metadataportal.kernel.external;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.harness.mssfss.MSSFSSImplService;
import org.openwis.harness.subselectionparameters.SubSelectionParameters;
import org.openwis.harness.subselectionparameters.SubSelectionParametersService;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class HarnessProvider {

   /**
    * The sub-selection parameters service.
    */
   private static SubSelectionParameters subSelectionParametersService;

   /**
    * The sub-selection parameters service.
    */
   private static MSSFSS mssFssService;

   /**
    * Default constructor.
    * Builds a HarnessProvider.
    */
   private HarnessProvider() {
      super();
   }

   /**
   * Gets the sub selection parameters service.
   * @return the sub selection parameters service.
   */
   public static SubSelectionParameters getSubSelectionParametersService() {
      try {
         if (subSelectionParametersService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.HARNESS_SUBSELECTIONPARAMETERS_WSDL);
            SubSelectionParametersService subSelectionParameters = new SubSelectionParametersService(
                  new URL(wsdl));
            subSelectionParametersService = subSelectionParameters.getSubSelectionParametersPort();
            ServiceProviderUtil.enforceServiceEndpoint(
                  (BindingProvider) subSelectionParametersService, wsdl);
         }
         return subSelectionParametersService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the sub selection parameters service.
    * @return the sub selection parameters service.
    */
   public static MSSFSS getMSSFSSService() {
      try {
         if (mssFssService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.HARNESS_MSSFSS_WSDL);
            MSSFSSImplService mssFss = new MSSFSSImplService(new URL(wsdl));
            mssFssService = mssFss.getMSSFSSImplPort();
            ServiceProviderUtil.enforceServiceEndpoint(
                  (BindingProvider) mssFssService, wsdl);
         }
         return mssFssService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   // --------------------------------------------------------------------------------
}
