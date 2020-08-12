package org.openwis.usermanagement.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openwis.usermanagement.UserManagementServiceImpl;
import org.openwis.usermanagement.model.user.DisseminationTool;
import org.openwis.usermanagement.model.user.OpenWISFTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for transform FTP object to String 
 * or String to FTP object. <P>
 */
public final class OpenWISFTPUtils {

   /** The logger */
   private final Logger logger = LoggerFactory.getLogger(OpenWISFTPUtils.class);

   /**
    * Default constructor
    */
   private OpenWISFTPUtils() {

   }

   /**
    * Transform ftp objects to String
    * @param openWISFTPs Open WIS FTP Object list
    * @return ftp The ftp string
    */
   private String toString(List<OpenWISFTP> openWISFTPs) {
      String result = "";
      if (openWISFTPs == null) {
         return result;
      }
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final ObjectMapper objectMapper = new ObjectMapper();
      try {
         objectMapper.writeValue(out, openWISFTPs);
         // encode to base64
         result = Base64.getEncoder().encodeToString(out.toByteArray());

      } catch (IOException e) {
         logger.error(e.toString());
      }
      return result;
   }


   private List<OpenWISFTP> fromString(String ftps) {
      //decode to json
      String json = new String(Base64.getDecoder().decode(ftps));
      ObjectMapper mapper = new ObjectMapper();
      try {
         return mapper.readValue(json, new TypeReference<List<OpenWISFTP>>(){});
      } catch (JsonProcessingException e) {
         logger.error(e.toString());
      }
      return new ArrayList<>();
   }

   /**
    * Transform ftp objects to a json encoded in base64
    * @param openWISFTPs
    * @return json as base64 string
    */
   public static String convertToString(List<OpenWISFTP> openWISFTPs) {
      OpenWISFTPUtils openWISFTPUtils = new OpenWISFTPUtils();
      return openWISFTPUtils.toString(openWISFTPs);
   }

   /**
    * Transform ftp string to ftp objects
    * @param openWISFTPs ftps string
    * @return Open WIS FTP Object list
    */
   public static List<OpenWISFTP> convertToOpenWISFTPs(String openWISFTPs) {
       OpenWISFTPUtils openWISFTPUtils = new OpenWISFTPUtils();
       return openWISFTPUtils.fromString(openWISFTPs);
   }
}
