package org.openwis.usermanagement.util;

import java.util.ArrayList;
import java.util.List;

import org.openwis.usermanagement.model.user.DisseminationTool;
import org.openwis.usermanagement.model.user.OpenWISEmail;

/**
 * Utilities for transform email object to String 
 * or String to email object. <P>
 */
public final class OpenWISEmailUtils {

   /**
    * Default constructor.
    * Builds a OpenWISEmailUtils.
    */
   private OpenWISEmailUtils() {

   }

   /**
    * @member: emailDelimiter Delimiter for email.
    */
   private final String emailDelimiter = "@@@";

   /**
    * 
    * @member: emailParameterDelimiter Delimiter for email parameter.
    */
   private final String emailParameterDelimiter = "!DEL!";

   /**
    * Transform email object to String
    * @param openWISEmail Email Object
    * @return email string
    */
   private String convertToString(OpenWISEmail openWISEmail) {
      String result = new String();
      result = openWISEmail.getDisseminationTool().toString() + emailParameterDelimiter
            + openWISEmail.getHeaderLine()
            + emailParameterDelimiter + openWISEmail.getMailDispatchMode()
            + emailParameterDelimiter + openWISEmail.getSubject() + emailParameterDelimiter;
      
      if (openWISEmail.getMailAttachmentMode() != null) {
         result = result + openWISEmail.getMailAttachmentMode();
      }

      result = result + emailParameterDelimiter + openWISEmail.getFileName()
            + emailParameterDelimiter  + openWISEmail.getAddress() + emailParameterDelimiter;
      return result;
   }

   /**
    * Transform email objects to String
    * @param openWISemails Open WIS Email Object list
    * @return emails string
    */
   public static String convertToString(List<OpenWISEmail> openWISemails) {
      OpenWISEmailUtils openWISEmailUtils = new OpenWISEmailUtils();

      String result = new String();
      if (openWISemails != null) {
         for (OpenWISEmail openWISEmail : openWISemails) {
            result = result + openWISEmailUtils.convertToString(openWISEmail);
            result = result + openWISEmailUtils.emailDelimiter;
         }
      }
      return result;
   }

   /**
    * Transform email string to email object.
    * @param email string
    * @return Open WIS Email Object
    */
   private OpenWISEmail convertToOpenWISEmail(String email) {
      OpenWISEmail openWisEmail = new OpenWISEmail();

      String[] result = email.split(emailParameterDelimiter);
      openWisEmail.setDisseminationTool(DisseminationTool.valueOf(result[0]));
      openWisEmail.setHeaderLine(result[1]);
      openWisEmail.setMailDispatchMode(result[2]);
      openWisEmail.setSubject(result[3]);
      openWisEmail.setMailAttachmentMode(result[4]);
      openWisEmail.setFileName(result[5]);
      openWisEmail.setAddress(result[6]);

      return openWisEmail;
   }

   /**
    * Transform email string to email objects.
    * @param openWISemails emails string
    * @return openWISemails Open WIS Email Object list
    */
   public static List<OpenWISEmail> convertToOpenWISEmails(String openWISemails) {
      List<OpenWISEmail> openWisEmails = new ArrayList<OpenWISEmail>();
      OpenWISEmailUtils openWISEmailUtils = new OpenWISEmailUtils();
      String[] emails = openWISemails.split(openWISEmailUtils.emailDelimiter);
      for (String email : emails) {
         OpenWISEmail openWisEmail = openWISEmailUtils.convertToOpenWISEmail(email);
         openWisEmails.add(openWisEmail);
      }

      return openWisEmails;
   }
}
