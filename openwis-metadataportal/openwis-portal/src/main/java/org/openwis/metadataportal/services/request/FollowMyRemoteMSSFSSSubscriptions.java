/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openwis.metadataportal.kernel.deployment.OpenwisDeploymentsConfig;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;


/**
 * This class enables to return all the remote Adhoc requests of the user. <P>
 * 
 */
public class FollowMyRemoteMSSFSSSubscriptions implements Service {

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
      // Get the centre of the circle of trust.
      String deploymentName = Util.getParam(params, "deployment");
      
      //Search the url of this centre.
      String deploymentURL = OpenwisDeploymentsConfig.getURLByDeploymentName(deploymentName);
      
      //Contact the proxy service of this center, with in params :
      // - service
      String service = "xml.follow.my.external.mssfss.subscriptions";
      
      // - idpURL
      UserSession userSession = context.getUserSession();
      String idpURL = (String) userSession.getProperty(LoginConstants.PREFERRED_IDP_URL);

      // - token
      String token = (String) userSession.getProperty(LoginConstants.TOKEN);
      
      int start = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumn = Util.getParam(params, "sort", null);
      String sortDirection = Util.getParam(params, "dir", null);
      
      deploymentURL = deploymentURL + "/srv/" + service + "?idpURL=" + idpURL + "&token=" + token
                     + "&start=" +start + "&limit="+ limit;
      
      if (StringUtils.isNotBlank(sortColumn) && StringUtils.isNotBlank(sortDirection)) {
         deploymentURL = deploymentURL + "&sort="+ sortColumn + "&dir=" + sortDirection;
      }
      
      // Create an instance of HttpClient.
      HttpClient client = new HttpClient();

      // Create a method instance. Call the PROXY Service on the centre.
      GetMethod method = new GetMethod(deploymentURL);

      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler());

      Element result = null;
      try {
         // Execute the method.
         int statusCode = client.executeMethod(method);

         if (statusCode != HttpStatus.SC_OK) {
            Log.error(LoginConstants.LOG, "Method failed: " + method.getStatusLine());
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
