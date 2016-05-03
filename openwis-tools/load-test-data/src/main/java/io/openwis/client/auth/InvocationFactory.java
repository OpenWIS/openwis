package io.openwis.client.auth;

import io.openwis.client.dto.JsonWrappedXMLResponse;
import io.openwis.client.http.FormPoster;

import java.io.IOException;
import java.net.URI;

import javax.xml.bind.JAXB;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.Gson;

/**
 * An invocation factory which will create invocation builders from a particular base target preconfigured
 * with any necessary cookies.
 */
public class InvocationFactory {
   
   private static final Gson GSON = new Gson();

   private final CloseableHttpClient client;
   private final URI baseTarget;
   
   public InvocationFactory(CloseableHttpClient client, URI baseTarget) {
      super();
      this.client = client;
      this.baseTarget = baseTarget;
   }
   
   /**
    * Gets an entity from a particular path.
    * 
    * @param relPath
    * @param entityClass
    * @return
    */
   public JsonWrappedXMLResponse getXmlWrappedJson(String relPath) {
      URI getTarget = baseTarget.resolve(relPath);
      HttpGet get = new HttpGet(getTarget);
      
      return makeHttpCallReturningXmlWrappedJson(getTarget, get);
   }
   
   private URI getRelativeUrl(String relativeUrl) {
      return baseTarget.resolve(relativeUrl);
   }
   
   /**
    * Returns a new FormPoster referencing a resource relative to the baseTarget
    * 
    * @param relativeUrl
    * @return
    */
   public FormPoster newFormPoster(String relativeUrl) {
      return new FormPoster(getRelativeUrl(relativeUrl));
   }
   
   public void postMultipartForm(String relativeUrl, MultipartEntityBuilder multipartEntityBuilder) {
      URI target = getRelativeUrl(relativeUrl);
      
      HttpPost post = new HttpPost(target);
      post.setEntity(multipartEntityBuilder.build());
      
      try {
         CloseableHttpResponse resp = client.execute(post);
         
         try {
            if (resp.getStatusLine().getStatusCode() != 200) {
               throw new RuntimeException(String.format("Non-200 response code: GET %s (%s)", target.toString(), resp.getStatusLine().toString()));
            }
         } finally {
            IOUtils.closeQuietly(resp);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Sends a URI encoded POST call returning JSON wrapped in XML.
    * 
    * @param relPath
    * @param poster
    * @return
    */
   public JsonWrappedXMLResponse postXmlWrappedJson(FormPoster poster) {
      HttpPost post = poster.getMethod();
      
      return makeHttpCallReturningXmlWrappedJson(poster.getTarget(), post);
   }
   
   /**
    * Sends a post containing a JSON message returning a XmlWrappedJson
    * 
    * @param poster
    * @return
    */
   public JsonWrappedXMLResponse postJsonReturningXmlWrappedJson(String relativeUrl, Object json) {
      URI uri = getRelativeUrl(relativeUrl);
      
      HttpPost post = new HttpPost(uri);
      
      post.setEntity(new StringEntity(GSON.toJson(json), ContentType.APPLICATION_JSON));
      return makeHttpCallReturningXmlWrappedJson(uri, post);
   }   
   

   private JsonWrappedXMLResponse makeHttpCallReturningXmlWrappedJson(URI getTarget, HttpUriRequest request) {
      try {
         CloseableHttpResponse resp = client.execute(request);
         
         try {
            if (resp.getStatusLine().getStatusCode() != 200) {
               throw new RuntimeException(String.format("Non-200 response code: GET %s (%s)", getTarget.toString(), resp.getStatusLine().toString()));
            }
   
            // Unmarshal using JaxB
            HttpEntity entity = resp.getEntity();
            return JAXB.unmarshal(entity.getContent(), JsonWrappedXMLResponse.class);
         } finally {
            IOUtils.closeQuietly(resp);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   
//   @Override
//   public Builder requestToPath(String path) {
//      Invocation.Builder newRequest = baseTarget.path(path).request();
//      for (Cookie cookie : cookies) {
//         newRequest = newRequest.cookie(cookie);
//      }
//      return newRequest;
//   }
//
//   @Override
//   public Builder requestToPath(String path, MediaType requestContentType) {
//      Invocation.Builder newRequest = baseTarget.path(path).request(requestContentType);
//      for (Cookie cookie : cookies) {
//         newRequest = newRequest.cookie(cookie);
//      }
//      return newRequest;      
//   }
}
