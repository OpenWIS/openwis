/**
 * 
 */
package org.openwis.metadataportal.services.harvest.geonet20;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openwis.metadataportal.model.site.Site;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleStringDTO;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GetGeonetworkSourcesInfo implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      SimpleStringDTO dto = JeevesJsonWrapper.read(params, SimpleStringDTO.class);

      // Create an instance of HttpClient.
      HttpClient client = new HttpClient();

      // Create a method instance. Call the PROXY Service on the centre.
      String url = dto.getContent() + "/srv/"+context.getLanguage()+"/xml.info?type=sources";
      GetMethod method = new GetMethod(url);

      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler());

      Element result = null;
      try {
         // Execute the method.
         int statusCode = client.executeMethod(method);

         if (statusCode != HttpStatus.SC_OK) {
            Log.error(LoginConstants.LOG, "Method failed: " + method.getStatusLine());
            return null;
         }

         // Read the response body.
         byte[] responseBody = method.getResponseBody();

         // Deal with the response.
         // Use caution: ensure correct character encoding and is not binary data
         String response = new String(responseBody);
         Log.debug(LoginConstants.LOG, response);

         Reader in = new StringReader(response);
         SAXBuilder builder = new SAXBuilder();
         Document doc = builder.build(in);

         result = doc.getRootElement();

         List<Site> sites = new ArrayList<Site>();

         List<Element> sources = result.getChild("sources").getChildren();
         for (Element source : sources) {
            Site site = new Site();
            site.setId(source.getChildText("uuid"));
            site.setName(source.getChildText("name"));
            sites.add(site);
         }

         result = JeevesJsonWrapper.send(sites);

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
