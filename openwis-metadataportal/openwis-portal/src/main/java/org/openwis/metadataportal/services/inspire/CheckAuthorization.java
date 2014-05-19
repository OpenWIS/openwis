package org.openwis.metadataportal.services.inspire;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
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
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

public class CheckAuthorization implements Service {

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
	private static final String REST_USER_BY_TOKEN = "/identity/attributes?subjectid=";

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

	@Override
	public void init(String appPath, ServiceConfig params) throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public Element exec(Element params, ServiceContext context)
			throws Exception {
		Dbms dbms = (Dbms) context.getResourceManager()
				.open(Geonet.Res.MAIN_DB);
		String tokenDecode = Util.getParam(params, "token");
		String idpUrl = Util.getParam(params, "idpUrl");
		String urn = Util.getParam(params, "urn");
		String tokenEncode = URLEncoder.encode(tokenDecode, "UTF-8");

		Map<String, Object> mapAttributes = getUserAttributes(idpUrl,
				tokenEncode);
		
		if ( mapAttributes == null ){
         return createNotAllowedResponse("Could not validate token");
		   
		}

		List<String> isMemberOf;
		Object isMemberValue=mapAttributes.get(LoginConstants.IS_MEMBER_OF.toLowerCase());
      if (isMemberValue instanceof String) {
         isMemberOf = Collections.singletonList((String) isMemberValue);
      } else {
         isMemberOf = (List<String>) isMemberValue;
      }
		boolean needLocalAccount = Boolean.valueOf(String.valueOf(mapAttributes
				.get(LoginConstants.NEED_USER_ACCOUNT.toLowerCase())));

		// Check if user belongs to group
		List<String> applyGroup = new ArrayList<String>();
		boolean foundLocalGroup = false;
		for (String group : isMemberOf) {

			if (groupContainsCentre(group,
					OpenwisMetadataPortalConfig
							.getString(ConfigurationConstants.DEPLOY_NAME))) {
				applyGroup.add(group);
				foundLocalGroup = true;
			} else if (groupContainsCentre(group, LoginConstants.GLOBAL)) {
				applyGroup.add(group);
			}
		}
		// Check need user account constraint
		// if needUserAccount enabled, the list of groups must contain at least
		// one local group
		// otherwise we ignore the global groups
		if (needLocalAccount && !foundLocalGroup) {
			// clean the list of global groups
			applyGroup.clear();
		}
		if (applyGroup.isEmpty()) {
			return createNotAllowedResponse("User "
					+ mapAttributes.get(LoginConstants.UID)
					+ " not authorized on this centre");
		}

		DataPolicyManager dataPolicyManager = new DataPolicyManager(dbms);
		GroupManager groupManager = new GroupManager(dbms);
		// Remplacer getAllGroup par ceux du user

		ArrayList<Group> groups = new ArrayList<Group>();
		for (String group : applyGroup) {

			// get Group Name
			String[] groupDN = group.split(",");
			String groupName = groupDN[0].split("cn=")[1];
			String centre = groupDN[1].split("ou=")[1];
			Group openWisgroup = groupManager.getGroupByName(groupName,
					LoginConstants.GLOBAL.equals(centre));
			if (openWisgroup != null) {
				groups.add(openWisgroup);
			}

		}
		// temporaire Collection<Group> groups = groupManager.getAllGroups();

		Collection<Operation> operations;
		try {
			operations = dataPolicyManager.getAllOperationAllowedByMetadataUrn(
					urn, groups);
		} catch (Exception e) {
			Log.error(LoginConstants.LOG, e.getMessage(), e);

			return createNotAllowedResponse("Unable to find allowed operations for the metadata "
					+ urn
					+ " (user "
					+ mapAttributes.get(LoginConstants.UID)
					+ ")");
		}

		Element element;
		if (containsDownload(operations)) {
			element = createAllowedResponse(String.valueOf(mapAttributes
					.get("uid")));
		} else {
			element = createNotAllowedResponse("Not authorized for user "
					+ mapAttributes.get("uid") + " and for metadata " + urn);
		}

		return element;
	}

	private boolean containsDownload(Collection<Operation> operations) {
		for (Operation operation : operations) {
			if (operation.getId().equals(OperationEnum.DOWNLOAD.getId())) {
				return true;
			}

		}
		return false;

	}

	private Element createNotAllowedResponse(String message) {
		Element element = createResponseElement(false);

		Element messageElement = new Element("message");
		messageElement.addContent(message);
		element.addContent(messageElement);

		return element;
	}

	private Element createAllowedResponse(String uid) {
		Element element = createResponseElement(true);
		Element usernameElement = new Element("username");
		usernameElement.addContent(uid);

		element.addContent(usernameElement);
		return element;
	}

	private Element createResponseElement(boolean authorized) {
		Element element = new Element("CheckAuthorization");

		Element allowedElement = new Element("allowed");
		allowedElement.addContent(Boolean.toString(authorized));
		element.addContent(allowedElement);
		return element;
	}

	/**
	 * Determine if the given centre is contained in the group line. Kind of
	 * group line: cn=DEFAULT,ou=Centre,ou=groups,dc=opensso,dc=java,dc=net
	 */
	private boolean groupContainsCentre(String group, String centre) {
		String[] groupItems = group.split(",");
		if (groupItems.length > 2) {
			for (String item : groupItems) {
				if (item.startsWith(LoginConstants.OU)) {
					return centre.equals(item.substring(3));
				}
			}
		}

		return false;
	}

	/**
	 * Get user by token
	 * 
	 * @param idpUrl
	 *            The preferred IdP URL
	 * @param token
	 *            The token
	 * @return the user name (same as "cn" in LDAP)
	 * @throws OpenWisLoginEx
	 *             if an error occurs
	 */
	public Map<String, Object> getUserAttributes(String idpUrl, String token)
			throws OpenWisLoginEx {

		// public String getUserByToken(String idpUrl, String token)
		// throws OpenWisLoginEx {

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance. Call an OpenSSO REST Service.
		GetMethod method = new GetMethod(idpUrl + REST_USER_BY_TOKEN + token);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		
		String response = "";
		
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
			   //renvoie null car le token n'est pas validé
			   return null;
			}

			// Read the response body.
			response = method.getResponseBodyAsString();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			
			Log.debug(LoginConstants.LOG, response);


		} catch (HttpException e) {
			Log.error(LoginConstants.LOG, e.getMessage());
			throw new OpenWisLoginEx(
					"Error during get User By Token : Fatal protocol violation");
		} catch (IOException e) {
			Log.error(LoginConstants.LOG, e.getMessage());
			throw new OpenWisLoginEx(
					"Error during get User By Token : Fatal transport error");
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		
		return createAttributeMap(response);

	}

   private Map<String, Object> createAttributeMap(String result) {
      Map<String, Object> attributeMap = new HashMap<String, Object>();
      String [] attributes = result.replace("\n", "").split("userdetails.attribute.name=");
      // on ignore le 1er (role et token)
      for (int i= 1; i<attributes.length;i++){
         String [] details = attributes[i].split("userdetails.attribute.value=");
         String attributeName = details[0];
         // on teste si l'attribut contient une valeur ou plusieurs 
         // dans le cas où c'est une, le hashmap contient une string
         // si plusieurs, ce sera une liste de strings
         if (details.length==2){
            attributeMap.put(attributeName, details[1]);
         }else if (details.length>2){
            // création d'une liste modifiable qu'on initialise 
            //à partir de la liste non modifiable(nbre d'elements) Arrays.asList(details)
            List<String>listeValue = new ArrayList<String>(Arrays.asList(details)); 
            // on enlève la clé : attributeName = details[0]
            listeValue.remove(0);
            attributeMap.put(attributeName, listeValue);
            
         }
         
      }
      return attributeMap;
   }
}
