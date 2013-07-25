/**
 * 
 */
package org.openwis.usermanagement.util;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenWiS Backup Utils. <P>
 * 
 */
public final class OpenWiSBackupUtils {

   /**
    * Default constructor.
    * Builds a OpenWiSBackupUtils.
    */
   private OpenWiSBackupUtils() {

   }

   /**
    * @member: backupDelimiter Delimiter for backup.
    */
   public static final String BACKUP_DELIMITER = "@@@";

   /**
    * Transform backup object to String
    * @param backups Back Up Object
    * @return backups string
    */
   public static String convertBackUpListToString(List<String> backups) {
      String result = new String();
      if (backups != null) {
         for (String backUp : backups) {
            result = result + backUp + OpenWiSBackupUtils.BACKUP_DELIMITER;
         }
      }
      return result;
   }


   /**
    * Transform backup string to back up list.
    * @param backUps backups string
    * @return backUp list 
    */
   public static List<String> convertStringToBackUpList(String backUps) {
      List<String> result = new ArrayList<String>();
      String[] backUpsSplit = backUps.split(OpenWiSBackupUtils.BACKUP_DELIMITER);
      for (String backUp : backUpsSplit) {
         result.add(backUp);
      }
      return result;
   }
}
