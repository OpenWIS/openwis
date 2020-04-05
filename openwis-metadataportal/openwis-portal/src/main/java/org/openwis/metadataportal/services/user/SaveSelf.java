package org.openwis.metadataportal.services.user;

import java.util.List;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.user.BackUp;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;

import org.openwis.securityservice.OpenWISUserUpdateLog;
import com.google.common.collect.Lists;
import org.openwis.metadataportal.services.util.UserLogUtils;

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
	   
	   // Explicitly null out certain fields to prevent changes to their privileges
	   user.getUser().setProfile(null);
	   user.getUser().setClassOfService(null);
	   user.getUser().setGroups(Lists.<Group>newArrayList());
	   user.getUser().setBackUps(Lists.<BackUp>newArrayList());
	   
		List<OpenWISUserUpdateLog> updateLogs = um.updateUser(user.getUser());
		for (OpenWISUserUpdateLog updateLog: updateLogs) {
		    UserLogDTO userActionLogDTO = UserLogUtils.buildLog(updateLog);
		    userActionLogDTO.setActioner(context.getUserSession().getUsername());
			UserLogUtils.save(dbms, userActionLogDTO);
		}
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
