package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserDTO;

/**
 * Saves the details of the current user.  Similar to ".Save" apart from the following
 * things:
 * 
 * <ul>
 * <li>No other user except the current logged in user can be changed.
 * <li>The user's password cannot be changed.
 * <li>The user's profile cannot be changed.
 * </li>
 */
public class SaveSelf implements Service {

	@Override
	public Element exec(Element params, ServiceContext context) throws Exception {
	   UserDTO user = JeevesJsonWrapper.read(params, UserDTO.class);
	   Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
		
	   String currentUsername = context.getUserSession().getUsername();
	   
	   UserManager um = new UserManager(dbms);
	   
	   // Confirm the user currently be modified is the current user
	   if (! user.getUser().getUsername().equals(currentUsername)) {
	      return JeevesJsonWrapper.send(new AcknowledgementDTO(false, 
	            "You do not have permission to modify any other user but yourself"));
	   }
	   
	   // Update fields from the DTO.  Passwords are not included, they're handled
	   // by another service.  Other fields like groups and profiles are also not
	   // permitted as the user is not authorized to make these changes.
	   User existingUser = um.getUserByUserName(currentUsername);
	   User newUserDetails = user.getUser();
	   
	   existingUser.setName(newUserDetails.getName());
	   existingUser.setSurname(newUserDetails.getSurname());
	   existingUser.setEmailContact(newUserDetails.getEmailContact());
	   existingUser.setAddress(newUserDetails.getAddress());
	   existingUser.setFtps(newUserDetails.getFtps());
	   existingUser.setEmails(newUserDetails.getEmails());
	   
      um.updateUser(existingUser);
      
      // call method checkSubscription on RequestManager service.
      // (not sure if this is necessary).
      RequestManager requestManager = new RequestManager();
      requestManager.checkUserSubscription(user.getUser().getUsername(), dbms);	   
		
	   return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
	}

	@Override
	public void init(String appPath, ServiceConfig params) throws Exception {

	}
}
