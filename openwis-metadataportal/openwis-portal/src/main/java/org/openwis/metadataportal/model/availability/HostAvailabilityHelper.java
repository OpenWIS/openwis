/**
 * 
 */
package org.openwis.metadataportal.model.availability;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class HostAvailabilityHelper {

   /**
    * Default constructor.
    * Builds a HostAvailabilityHelper.
    */
   private HostAvailabilityHelper() {
      super();
   }

   /**
    * Returns <code>true</code> if server responds 200 OK with the specified URL, <code>false</code> otherwise.
    * @param url the URL to test.
    * @return <code>true</code> if server responds 200 OK with the specified URL, <code>false</code> otherwise.
    */
   public static boolean isAvailable(String url) {
      boolean result = false;

      // Create an instance of HttpClient.
      HttpClient client = new HttpClient();

      // Create a method instance. Call an OpenSSO REST Service.
      GetMethod method = new GetMethod(url);

      try {
         // Execute the method.
         int statusCode = client.executeMethod(method);

         result = (statusCode == HttpStatus.SC_OK);

      } catch (HttpException e) {
         result = false;
      } catch (IOException e) {
         result = false;
      } finally {
         // Release the connection.
         method.releaseConnection();
      }
      return result;
   }

}
