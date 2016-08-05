package io.openwis.client.auth;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Authentication which 
 */
public class FrontDoorAuthentication implements Authentication {
   
   private final String username;
   private final String password;
   private final String postbackUrlOverride;
   
   public FrontDoorAuthentication(String username, String password) {
      super();
      this.username = username;
      this.password = password;
      this.postbackUrlOverride = null;
   }
   
   public FrontDoorAuthentication(String username, String password, String postbackUrlOverride) {
      super();
      this.username = username;
      this.password = password;
      this.postbackUrlOverride = StringUtils.trimToNull(postbackUrlOverride);
   }

   @Override
   public void authenticate(CloseableHttpClient httpClient, URI baseTarget) {
      try {
         // Get the home page
         URI homePageUrl = baseTarget.resolve("srv/en/main.home");
         
         HttpGet getHomePage = new HttpGet(homePageUrl);
         IOUtils.closeQuietly(httpClient.execute(getHomePage));
         
         String initPage = getOpenWISInitPage(httpClient, baseTarget);
         
         Document initDoc = Jsoup.parse(initPage);
         
         // Get the login form
         Element loginForm = initDoc.select("form[name=Login]").first();
         if (loginForm == null) {
            throw new RuntimeException("Missing loginForm form: css = form[name=Login]");
         }
         
         Map<String, String> formValues = new HashMap<String, String>();
         for (Element inputElem : loginForm.select("input")) {
            formValues.put(inputElem.attr("name"), inputElem.attr("value"));
         }
         
         formValues.put("IDToken0", "");
         formValues.put("IDToken1", username);
         formValues.put("IDToken2", password);
         formValues.put("IDButton", "Log In");
         
         URI postbackUri;
         
         if (postbackUrlOverride == null) {
            postbackUri = baseTarget.resolve(loginForm.attr("action"));
         } else {
            String queryParametersFromForm = URI.create(baseTarget.resolve(loginForm.attr("action")).toString()).getQuery();
            if (StringUtils.isNotBlank(queryParametersFromForm)) {
               queryParametersFromForm = "?" + queryParametersFromForm;
            }
            postbackUri = new URI(postbackUrlOverride + StringUtils.trimToEmpty(queryParametersFromForm));
         }
         
         String postbackPage = sendPostback(httpClient, postbackUri, formValues);
         
         // Get the authorization postback form
         Element postbackForm = Jsoup.parse(postbackPage).select("form[action$=\"/openWisAuthorization\"]").first();
         if (postbackForm == null) {
            throw new RuntimeException("Missing postback form: css = form[action$=\"/openWisAuthorization\"]");
         }
         
         String samlResponseValue = postbackForm.select("input[name=SAMLResponse]").first().attr("value");
         String relayState = postbackForm.select("input[name=RelayState]").first().attr("value");
         URI postbackUrl = baseTarget.resolve(postbackForm.attr("action"));
         
         String postbackFormPage = sendPostbackForm(httpClient, postbackUrl, samlResponseValue, relayState);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
   /**
    * Sends the postback form.  This returned the value of the JSESSIONID cookie.
    * 
    * @param client
    * @param postbackUri
    * @param samlResponseValue
    * @return
    * @throws IOException 
    * @throws ClientProtocolException 
    */
   private String sendPostbackForm(CloseableHttpClient client, URI postbackUri, String samlResponseValue, String relayState) throws ClientProtocolException, IOException {
      List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
      formPairs.add(new BasicNameValuePair("SAMLResponse", samlResponseValue));
      formPairs.add(new BasicNameValuePair("RelayState", relayState));
      
      HttpPost postInit = new HttpPost(postbackUri);
      postInit.setEntity(new UrlEncodedFormEntity(formPairs, Consts.UTF_8));
      
      CloseableHttpResponse resp = client.execute(postInit);
      try {
         return EntityUtils.toString(resp.getEntity());
      } finally {
         resp.close();
      }
   }

   /**
    * Send the username and password to the postback URL.
    * 
    * @param client
    * @param postbackUri
    * @param string
    * @param string2
    * @return
    * @throws IOException 
    * @throws ParseException 
    */
   private String sendPostback(CloseableHttpClient client, URI postbackUri, Map<String, String> formValues) throws ParseException, IOException {
      List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
      for (Entry<String, String> entries : formValues.entrySet()) {
         formPairs.add(new BasicNameValuePair(entries.getKey(), entries.getValue()));
      }
      
      HttpPost post = new HttpPost(postbackUri);
      post.setEntity(new UrlEncodedFormEntity(formPairs, Consts.UTF_8));
      
      CloseableHttpResponse resp = client.execute(post);
      try {
         return EntityUtils.toString(resp.getEntity());
      } finally {
         resp.close();
      }
   }

   /**
    * Retrieve the init page which will be used for filling out the authentication stuff.
    * 
    * @return
    * @throws IOException 
    * @throws ParseException 
    */
   private String getOpenWISInitPage(CloseableHttpClient client, URI openwisBaseUrl) throws Exception {
      List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
      formPairs.add(new BasicNameValuePair("lang", "en"));
      
      HttpPost postInit = new HttpPost(openwisBaseUrl.resolve("openWisInit"));
      postInit.setEntity(new UrlEncodedFormEntity(formPairs, Consts.UTF_8));
      
      CloseableHttpResponse resp = client.execute(postInit);
      
      try {
         return EntityUtils.toString(resp.getEntity());
      } finally {
         resp.close();
      }
   }
}
