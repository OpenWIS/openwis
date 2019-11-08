/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.io.IOException;

import jeeves.utils.Log;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

/**
 * Utilities class for manage requests between sp and preferred idp. <br>
 * (token validity, get user by token, etc...) <P>
 * 
 */
public class TokenUtilities {

   /**
    * @member: REST_TOKEN_VALID
    */
   private static final String REST_TOKEN_VALID = "/identity/isTokenValid?tokenid=";

   /**
    * @member: REST_TOKEN_EXPECTED_RESULT
    */
   private static final String REST_TOKEN_EXPECTED_RESULT = "boolean=true";

   /**
    * @member: REST_USER_BY_TOKEN
    */
   private static final String REST_USER_BY_TOKEN = "/identity/attributes?attributes_names=uid&subjectid=";

   /**
    * @member: UD_ATT_NAME_EQUAL_CN
    */
   private static final String UD_ATT_NAME_EQUAL_CN = "userdetails.attribute.name=cn";

   /**
    * @member: UD_ATT_NAME
    */
   private static final String UD_ATT_NAME = "userdetails.attribute.name=";

   /**
    * @member: UD_ATT_VALUE
    */
   private static final String UD_ATT_VALUE = "userdetails.attribute.value=";

   /**
    * Test the token validity.
    * @param idpUrl The IdP URL.
    * @param token The token.
    * @return true if the token is valid, false otherwise.
    * @throws OpenWisLoginEx if an error occurs.
    */
   public boolean isTokenValid(String idpUrl, String token) throws OpenWisLoginEx {

      boolean result = false;

      // Create an instance of HttpClient.
      HttpClient client = new HttpClient();

      // Create a method instance. Call an OpenSSO REST Service.
      GetMethod method = new GetMethod(idpUrl + REST_TOKEN_VALID + token);

      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler());

      try {
         // Execute the method.
         int statusCode = client.executeMethod(method);

         //return error 401 in the case that it is a no valid token
         if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            result = false;
         //return boolean=true in the case that it is a valid token
         } else if (statusCode == HttpStatus.SC_OK) {
            // Read the response body.
            byte[] responseBody = method.getResponseBody();
            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            result = StringUtils.contains(new String(responseBody), REST_TOKEN_EXPECTED_RESULT);
         } else {
            Log.error(LoginConstants.LOG, "Method failed : " + method.getStatusLine());
            throw new OpenWisLoginEx(
                  "Error during test if token is valid : Server returned response code "
                        + statusCode);
         }
      } catch (HttpException e) {
         Log.error(LoginConstants.LOG, e.getMessage());
         throw new OpenWisLoginEx("Error during test if token is valid : Fatal protocol violation");
      } catch (IOException e) {
         Log.error(LoginConstants.LOG, e.getMessage());
         throw new OpenWisLoginEx("Error during test if token is valid : Fatal transport error");
      } finally {
         // Release the connection.
         method.releaseConnection();
      }
      return result;
   }

   /**
    * Get user by token
    * @param idpUrl The preferred IdP URL
    * @param token The token
    * @return the user name (same as "cn" in LDAP)
    * @throws OpenWisLoginEx if an error occurs
    */
   public String getUserByToken(String idpUrl, String token) throws OpenWisLoginEx {

      String result = new String();

      // Create an instance of HttpClient.
      HttpClient client = new HttpClient();

      // Create a method instance. Call an OpenSSO REST Service.
      GetMethod method = new GetMethod(idpUrl + REST_USER_BY_TOKEN + token);

      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler());

      try {
         // Execute the method.
         int statusCode = client.executeMethod(method);

         if (statusCode != HttpStatus.SC_OK) {
            Log.error(LoginConstants.LOG, "Method failed: " + method.getStatusLine());
            throw new OpenWisLoginEx(
                  "Error during test if token is valid : Server returned response code "
                        + statusCode);
         }

         // Read the response body.
         byte[] responseBody = method.getResponseBody();

         // Deal with the response.
         // Use caution: ensure correct character encoding and is not binary data
         String response = new String(responseBody);
         Log.debug(LoginConstants.LOG, response);

         String[] userdetails = response.split(UD_ATT_NAME_EQUAL_CN);

         if (userdetails.length == 2) {
            String[] details = userdetails[1].split(UD_ATT_NAME);
            if (details.length >= 2) {
               result = details[0];
               result = result.replaceAll(UD_ATT_VALUE, "");
            }
         }
         result = StringUtils.deleteWhitespace(result);
      } catch (HttpException e) {
         Log.error(LoginConstants.LOG, e.getMessage());
         throw new OpenWisLoginEx("Error during get User By Token : Fatal protocol violation");
      } catch (IOException e) {
         Log.error(LoginConstants.LOG, e.getMessage());
         throw new OpenWisLoginEx("Error during get User By Token : Fatal transport error");
      } finally {
         // Release the connection.
         method.releaseConnection();
      }
      return result;
   }

}
