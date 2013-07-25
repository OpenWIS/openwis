/**
 *
 */
package org.openwis.metadataportal.services.availability;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.fao.geonet.constants.Geonet;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.availability.IAvailabilityManager;
import org.openwis.metadataportal.kernel.deployment.OpenwisDeploymentsConfig;
import org.openwis.metadataportal.model.availability.DeploymentAvailability;
import org.openwis.metadataportal.services.availability.dto.RemoteAvailabilityDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleStringDTO;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

/**
 * This class enables to return all the remote Adhoc requests of the user. <P>
 *
 */
public class GetRemote implements Service {

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
      SimpleStringDTO simpleStringDTO = JeevesJsonWrapper.read(params, SimpleStringDTO.class);

      String deploymentName = simpleStringDTO.getContent();

      //Search the url of this centre.
      String deploymentURL = OpenwisDeploymentsConfig.getURLByDeploymentName(deploymentName);

      //Contact the proxy service of this center, with in params :
      // - service
      String service = "xml.availability.external.get";

      deploymentURL = deploymentURL + "/srv/" + service;

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

         DeploymentAvailability remoteDeploymentAvailability = JeevesJsonWrapper.read(
               result.getText(), DeploymentAvailability.class);

         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         IAvailabilityManager availabilityManager = new AvailabilityManager(dbms);
         boolean backupedByLocalServer = availabilityManager
               .isLocalServerBackupingDeployment(deploymentName);

         RemoteAvailabilityDTO dto = new RemoteAvailabilityDTO(remoteDeploymentAvailability,
               backupedByLocalServer);
         result = JeevesJsonWrapper.send(dto);
      } catch (HttpException e) {
         Log.error(LoginConstants.LOG, e.getMessage(), e);
         throw new OpenWisLoginEx("Error during get User By Token : Fatal protocol violation");
      } catch (IOException e) {
         Log.error(LoginConstants.LOG, e.getMessage(), e);
         throw new OpenWisLoginEx("Error during get User By Token : Fatal transport error");
      } finally {
         // Release the connection.
         method.releaseConnection();
      }

      return result;
   }

}
