package org.openwis.usermanagement.util;

import java.util.ArrayList;
import java.util.List;

import org.openwis.usermanagement.model.user.DisseminationTool;
import org.openwis.usermanagement.model.user.OpenWISFTP;

/**
 * Utilities for transform FTP object to String 
 * or String to FTP object. <P>
 */
public final class OpenWISFTPUtils {

   /**
    * @member: ftpParameter Delimiter for email.
    */
   private final String ftpDelimiter = "@@@";

   /**
    * 
    * @member: ftpParameterDelimiter Delimiter for email parameter.
    */
   private final String ftpParameterDelimiter = "!DEL!";

   /**
    * Default constructor.
    * Builds a OpenWISFTPUtils.
    */
   private OpenWISFTPUtils() {
      
   }
   
   /**
    * Transform ftp object to String
    * @param openWISFTP FTP Object
    * @return ftp string
    */
   private String convertToString(OpenWISFTP openWISFTP) {
      String result = new String();
      result = openWISFTP.getDisseminationTool().toString() + ftpParameterDelimiter
            + openWISFTP.getFileName() + ftpParameterDelimiter + openWISFTP.getPath()
            + ftpParameterDelimiter + openWISFTP.getUser() + ftpParameterDelimiter
            + openWISFTP.getPassword() + ftpParameterDelimiter + openWISFTP.getPort()
            + ftpParameterDelimiter + openWISFTP.isPassive() + ftpParameterDelimiter
            + openWISFTP.isCheckFileSize() + ftpParameterDelimiter + openWISFTP.getHost()
            + ftpParameterDelimiter + openWISFTP.isEncrypted() + ftpParameterDelimiter;
      return result;
   }

   /**
    * Transform ftp objects to String
    * @param openWISFTPs Open WIS FTP Object list
    * @return ftp The ftp string
    */
   public static String convertToString(List<OpenWISFTP> openWISFTPs) {

      OpenWISFTPUtils openWISFTPUtils = new OpenWISFTPUtils();
      String result = new String();

      if (openWISFTPs != null) {
         for (OpenWISFTP openWISFTP : openWISFTPs) {
            result = result + openWISFTPUtils.convertToString(openWISFTP);
            result = result + openWISFTPUtils.ftpDelimiter;
         }
      }
      return result;
   }

   /**
    * Transform ftp string to ftp object
    * @param ftp string of ftp
    * @return Open WIS FTP Object
    */
   private OpenWISFTP convertToOpenWISFTP(String ftp) {
      OpenWISFTP openWISFTP = new OpenWISFTP();

      String[] result = ftp.split(ftpParameterDelimiter);
      openWISFTP.setDisseminationTool(DisseminationTool.valueOf(result[0]));
      openWISFTP.setHost(result[8]);
      openWISFTP.setPath(result[2]);
      openWISFTP.setUser(result[3]);
      openWISFTP.setPassword(result[4]);
      openWISFTP.setPort(result[5]);
      openWISFTP.setPassive(new Boolean(result[6]));
      openWISFTP.setCheckFileSize(new Boolean(result[7]));
      openWISFTP.setFileName(result[1]);
      if (result.length >= 10) {
         openWISFTP.setEncrypted(new Boolean(result[9]));
      }

      return openWISFTP;
   }

   /**
    * Transform ftp string to ftp objects
    * @param openWISFTPs ftps string
    * @return Open WIS FTP Object list
    */
   public static List<OpenWISFTP> convertToOpenWISFTPs(String openWISFTPs) {
      List<OpenWISFTP> openWisFTPs = new ArrayList<OpenWISFTP>();
      OpenWISFTPUtils openWISFTPUtils = new OpenWISFTPUtils();
      String[] ftps = openWISFTPs.split(openWISFTPUtils.ftpDelimiter);
      for (String ftp : ftps) {
         OpenWISFTP openWisFTP = openWISFTPUtils.convertToOpenWISFTP(ftp);
         openWisFTPs.add(openWisFTP);
      }

      return openWisFTPs;
   }
}
