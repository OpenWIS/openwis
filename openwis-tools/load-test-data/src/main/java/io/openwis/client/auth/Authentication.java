package io.openwis.client.auth;

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * A means of authenticating a user of OpenWIS.
 */
public interface Authentication {

   /**
    * Authenticate the user with OpenWIS.  Returns the list of session Cookies that are to be set
    * for each invocation.
    * 
    * @param httpClient
    */
   public void authenticate(CloseableHttpClient httpClient, URI baseTarget);
}
