package io.openwis.client.auth;

import io.openwis.client.http.FormPoster;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Login credentials which use the secret developer xml.login service.
 */
public class BackDoorAuthentication implements Authentication {
   
   private final String username;
   private final String password;
   
   public BackDoorAuthentication(String username, String password) {
      super();
      this.username = username;
      this.password = password;
   }

   @Override
   public void authenticate(CloseableHttpClient httpClient, URI baseTarget) {

      CloseableHttpResponse resp;
      try {
         URI target = baseTarget.resolve("./srv/xml.user.login");
         
         resp = new FormPoster(target)
            .setValue("username", username)
            .setValue("password", password)
            .post(httpClient);
      
         try {
            if (resp.getStatusLine().getStatusCode() != 200) {
               String msg = String.format("Non 200 error code when logging in: %s, creds=%s:%s, code=%d, reason=%s",
                     target, username, StringUtils.repeat('*', password.length()), resp.getStatusLine().getStatusCode(), resp.getStatusLine().getReasonPhrase());
               throw new RuntimeException(msg);
            }
         } finally {
            IOUtils.closeQuietly(resp);
         }
      } catch (ClientProtocolException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
