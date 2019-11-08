/**
 * 
 */
package org.openwis.metadataportal.services.register;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.user.BackUp;
import org.openwis.metadataportal.model.user.Profile;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;

import com.google.common.collect.Lists;

/**
 * Self Registration Services. <P>
 * 
 */
public class SelfRegister implements Service {

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
      UserDTO userDTO = JeevesJsonWrapper.read(params, UserDTO.class);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      SettingManager sm = gc.getSettingManager();
      boolean selfRegistrationEnabled = sm.getValueAsBool("system/userSelfRegistration/enable");

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);

      if (selfRegistrationEnabled) {
         User user = userDTO.getUser();
         try {

            // Check that the user profile is anything other than "User".  If it isn't,
            // deny the request.  Allow blanks through: the profile will be hard coded
            // below anyway.
            if ((user.getProfile() != null) && (!"".equals(user.getProfile()))) {
               if (!Profile.User.name().equals(user.getProfile())) {
                  throw new IllegalArgumentException("User profile cannot be anything other than "
                        + Profile.User.name());
               }
            }

            // Force the user's profile
            user.setProfile(Profile.User.name());
            user.setGroups(Lists.<Group> newArrayList());
            user.setBackUps(Lists.<BackUp> newArrayList());

            Log.info(Geonet.SELF_REGISTER, "Self-registration: username=" + user.getUsername()
                  + ", firstName=" + user.getName() + ", lastName=" + user.getSurname());
            // Create User
            UserManager um = new UserManager(dbms);
            um.createUser(userDTO.getUser());
            Log.debug(Geonet.SELF_REGISTER, "User created on Security Server");

            //Send Mail To User
            Log.debug(Geonet.SELF_REGISTER, "Sending an email to the user");
            String thisSite = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DEPLOY_NAME);
            String subject = OpenWISMessages.format("SelfRegister.subject", context.getLanguage(),
                  thisSite);

            SettingInfo si = new SettingInfo(context);
            String siteURL = si.getSiteUrl() + context.getBaseUrl();
            String content = getContent(userDTO.getUser().getUsername(), userDTO.getUser()
                  .getPassword(), siteURL, thisSite, context.getLanguage());

            String host = sm.getValue("system/feedback/mailServer/host");
            String port = sm.getValue("system/feedback/mailServer/port");
            String from = sm.getValue("system/feedback/email");
            Log.debug(Geonet.SELF_REGISTER, "host : " + host + " port: " + port + " from : " + from
                  + " to : " + userDTO.getUser().getEmailContact());

            MailUtilities mail = new MailUtilities();

            boolean result = mail.sendMail(host, Integer.parseInt(port), subject, from,
                  new String[] {userDTO.getUser().getEmailContact()}, content);
            if (!result) {
               // To be confirmed: Set ack dto if error message is requested
               //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
               Log.error(Geonet.SELF_REGISTER,
                     "Error during Self-Registration : error while sending email");
            } else {
               Log.info(Geonet.SELF_REGISTER, "Self-Registration Successfull");
            }

         } catch (UserAlreadyExistsException e) {
            String msg = OpenWISMessages.format("SelfRegister.errorUserExists",
                  context.getLanguage(), e.getUserName());
            acknowledgementDTO = new AcknowledgementDTO(false, msg);
            Log.error(Geonet.SELF_REGISTER,
                  "Error during self registration : the user " + e.getUserName()
                        + " already exists");
         } catch (IllegalArgumentException e) {
            String msg = OpenWISMessages.format("SelfRegister.errorInvalidUser",
                  context.getLanguage(), user.getUsername(), e.getMessage());
            acknowledgementDTO = new AcknowledgementDTO(false, msg);
            Log.error(
                  Geonet.SELF_REGISTER,
                  "Error during self registration : invalid user parameters for "
                        + user.getUsername() + ": " + e.getMessage());
         }
      } else {
         acknowledgementDTO = new AcknowledgementDTO(false,
               "SeflRegistration attempt while disabled !!");
         Log.error(Geonet.SELF_REGISTER, "!! SeflRegistration attempt while disabled : params="
               + Xml.getString(params));
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

   // --------------------------------------------------------------------------

   /**
    * Get content for the email message to user.
    * 
    * @param username The user name
    * @param siteURL The site URL
    * @param thisSite This site
    * @return the mail content.
    */
   private String getContent(String username, String password, String siteURL, String thisSite,
         String lang) {
      String mailContent = OpenWISMessages.format("SelfRegister.mailContent", lang, thisSite,
            username, password, siteURL, thisSite);

      Log.debug(Geonet.SELF_REGISTER, "mail content : " + mailContent);
      return mailContent;
   }

}
