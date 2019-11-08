package io.openwis.client.http;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * A simple utility class for posting form data.
 */
public class FormPoster {

   private final URI target;
   private final Map<String, String> formValues;
   
   public FormPoster(URI target) {
      super();
      this.target = target;
      this.formValues = new HashMap<String, String>();
   }
   
   /**
    * Sets the name of the form value.
    * 
    * @param name
    * @param value
    * @return
    */
   public FormPoster setValue(String name, String value) {
      this.formValues.put(name, value);
      return this;
   }
   
   /**
    * Returns the target.
    * @return
    */
   public URI getTarget() {
      return target;
   }
   
   /**
    * Returns a new HttpPost method set with the form values.
    * @return
    */
   public HttpPost getMethod() {
      List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
      for (Entry<String, String> formValue : formValues.entrySet()) {
         formPairs.add(new BasicNameValuePair(formValue.getKey(), formValue.getValue()));
      }
      
      HttpPost postInit = new HttpPost(target);
      postInit.setEntity(new UrlEncodedFormEntity(formPairs, Consts.UTF_8));
      
      return postInit;
   }
   
   /**
    * Post the form and return the response.
    * @return
    * @throws IOException 
    * @throws ClientProtocolException 
    */
   public CloseableHttpResponse post(CloseableHttpClient client) throws ClientProtocolException, IOException {
      return client.execute(getMethod());
   }
}

