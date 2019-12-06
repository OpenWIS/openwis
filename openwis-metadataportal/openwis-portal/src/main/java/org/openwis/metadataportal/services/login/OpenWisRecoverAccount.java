package org.openwis.metadataportal.services.login;


import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

/**
 * Account Request
 * @author gibaultr
 *
 */
public class OpenWisRecoverAccount extends HttpServlet{

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException
		  {
		handleRequest(request,response);
		  }	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException
		  {
		handleRequest(request,response);

		  }
	 /**
	  * Method called once the end user has submitted his account request	  
	  * @param request HTTP request
	  * @param response HTTP response
	  * @throws ServletException 
	  * @throws IOException
	  */
	 private void handleRequest(HttpServletRequest request, HttpServletResponse response)
			    throws ServletException, IOException
	 {
		 
		 try {
			    String userCaptchaResponse = request.getParameter("jcaptcha");
			    //Check whether the captcha passed or not
			    boolean captchaPassed = OpenWisImageCaptchaServlet.validateResponse(request, userCaptchaResponse);
			    ServiceContext context = (ServiceContext) request.getSession().getAttribute("context");	
			    

                //If captcha passed, send mail to end user
			    if (captchaPassed)
			    {
			    	processRequest(context, request, response);
			    }
			    else {
			    	String errorMessage= OpenWISMessages.format("AccountRequest.captchaFailed", context.getLanguage());
			    	forwardError(request, response, errorMessage);
			    }
			    
			 } catch (Exception e) {
		         	Log.error(LoginConstants.LOG, "Error processing Account Recovery  : " + e.getMessage());
		         	forwardError(request, response, "Error during acccount recovery - " + e.getMessage());
		      }
	 }


	private void forwardError(HttpServletRequest request, HttpServletResponse response,
			      String message) throws ServletException, IOException {
		
		String[] uris=request.getRequestURI().split("/");
		String redirect = "/"+uris[1]+"/srv/en/user.accountRecovery.get?errorMessage="+message;
    	response.setStatus(307); //this makes the redirection keep your requesting method as is.
    	response.addHeader("Location", redirect);
	}
	/**
	 * Process account request when captcha passed 
	 * @param context context
	 * @param request HTTP request 
	 * @param response HTTP response
	 * @throws Exception
	 */
	private void processRequest(ServiceContext context, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String[] uris=request.getRequestURI().split("/");
    	String redirect = "/"+uris[1]+"/jsp/recoverAccountAck.jsp";
    	response.setStatus(307); //this makes the redirection keep your requesting method as is.
    	response.addHeader("Location", redirect);
    	
        String email = request.getParameter("email");

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        
        // Get User
        UserManager um = new UserManager(dbms);
        User user = um.getUserByUserName(email);
        
        //Generate new Password
        String newPassword = generatePassword();
        //Change User Password
        um.changePassword(email, newPassword);
        
        
        //Send Mail To User
        Log.debug(Geonet.SELF_REGISTER, "Sending an email to the user");
        sendEmailToUser(context, email, user.getSurname(), user.getName(), newPassword);
        
        //Send Mail To Openwis Administrator
        Log.debug(Geonet.SELF_REGISTER, "Sending an email to the administrator");
        sendEmailToAdministrator(context, email, user.getSurname(), user.getName(), newPassword);
		
	}
	/**
	 * Sending email notification to the end user just after he has requested an account 
	 * @param context context
	 * @param email user email address
	 * @param firstname firstname of the user
	 * @param lastname last name of the user
	 * @param password new password
	 */
	private void sendEmailToUser(ServiceContext context, String email, String firstname, String lastname, String password) {
		
		MailUtilities mail = new MailUtilities();
        
        String content = OpenWISMessages.format("AccountRecovery.mailContent1", context.getLanguage(),new String[]{firstname,lastname,email,password});
        String thisSite = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME);
        String subject = OpenWISMessages.format("AccountRecovery.subject1", context.getLanguage(), thisSite);
        
 	   	GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
 	   	SettingManager sm = gc.getSettingManager();
       
 	   	String host = sm.getValue("system/feedback/mailServer/host");
 	   	String port = sm.getValue("system/feedback/mailServer/port");
 	   	String from =  System.getProperty("openwis.mail.senderAddress");
 	   	if (from == null) 
 	   		from=sm.getValue("system/feedback/email");	
 	  

        

        boolean result = mail.sendMail(host, Integer.parseInt(port), subject, from, new String[]{email}, content);
        if (!result) {
           // To be confirmed: Set ack dto if error message is requested
           //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
           Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the end user("+email+")");
        } else {
           Log.info(Geonet.SELF_REGISTER, "Account recovery email sent successfully to the end user("+email+") from "+from);
        }
	}
	/**
	 * Sending email notification to the administrator after the end user has requested an account 
	 * @param context context
	 * @param email user email address
	 * @param firstname firstname of the user
	 * @param lastname last name of the user
	 */
	private void sendEmailToAdministrator(ServiceContext context, String email, String firstname, String lastname, String password) {
		
		MailUtilities mail = new MailUtilities();
        
        String content  = OpenWISMessages.format("AccountRecovery.mailContent2", context.getLanguage(),new String[]{firstname,lastname,email, password, ""});
        String thisSite = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME);
        String subject  = OpenWISMessages.format("AccountRecovery.subject2", context.getLanguage(), thisSite);
        
 	   	GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
 	   	SettingManager sm = gc.getSettingManager();
       
 	   	String host = sm.getValue("system/feedback/mailServer/host");
 	   	String port = sm.getValue("system/feedback/mailServer/port");
 	    String from =  System.getProperty("openwis.mail.senderAddress");
 	    String to =  	sm.getValue("system/feedback/email");
	   	if (from == null) 
	   		from=sm.getValue("system/feedback/email");	   

        boolean result = mail.sendMail(host, Integer.parseInt(port), subject, from, new String[]{to}, content);
        if (!result) {
           // To be confirmed: Set ack dto if error message is requested
           //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
           Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the administrator ("+to+") from "+from+" about account recovery of user "+email);
        } else {
           Log.info(Geonet.SELF_REGISTER, "Email sent successfully to the administrator ("+to+") from "+from+" about account recovery of user "+email);
        }
	}	
	
	private String generatePassword() {
	
		String[] symbols = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
		int length = 10;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
		    int indexRandom = getRandomNumberInRange( 0,symbols.length );
		    sb.append( symbols[indexRandom] );
		}
		String password = sb.toString();
		return password;
	}
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
}
