package org.openwis.metadataportal.kernel.external;

import javax.xml.ws.BindingProvider;

/**
 * Utility class for Service Provider.
 */
public final class ServiceProviderUtil {

   private static final String TRAILING_WSDL_PARAM = "?wsdl";

   /**
    * In case of a generated WSDL (URL ending with ?wsdl), set the endpoint address 
    * to the binding provider, to avoid using the endpoint returned by
    * the generated wsdl. The endpoint is set to the WSDL location without the "?wsdl" param.
    * 
    * @param bindingProvider the {@link BindingProvider}
    * @param wsdl the wsdl URL
    */
   public static void enforceServiceEndpoint(BindingProvider bindingProvider, String wsdl) {
      if (wsdl.endsWith(TRAILING_WSDL_PARAM)) {
         // Set endpoint
         bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
               wsdl.replace(TRAILING_WSDL_PARAM, ""));
      }
   }

}
