/**
 *
 */
package org.openwis.dataservice.util;

import java.util.Date;

import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.harness.dissemination.DisseminationInfo;
import org.openwis.harness.dissemination.FTPDiffusion;
import org.openwis.harness.dissemination.MailAttachmentMode;
import org.openwis.harness.dissemination.MailDiffusion;
import org.openwis.harness.dissemination.MailDispatchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */
public class DisseminationUtils {
	
	/** Logging tool. */
	private static final Logger LOG = LoggerFactory.getLogger(DisseminationUtils.class);
	
	/** Separator to find in filename to interpret a date/time pattern (eg: #yyyy.MM.dd HH:mm#) */
	private static final String FILENAME_DATE_TIME_SEPARATOR = "#";

   // -------------------------------------------------------------------------
   // Initialization
   // -------------------------------------------------------------------------

	public static DisseminationInfo createDisseminationInfo(int priority, int userSLA, String dataPolicy)
	{
		DisseminationInfo disseminationInfo = new DisseminationInfo();
						
		disseminationInfo.setPriority(priority);
		
		disseminationInfo.setSLA(userSLA);
		
		disseminationInfo.setDataPolicy(dataPolicy);
		
		return disseminationInfo;
	}

	public static void setMailDiffusion(DisseminationInfo disseminationInfo, String fileName, String address, String subject, String headerLine, String attachmentMode, String dispatchMode)
	{		
		MailDiffusion mailDiffusion = getMailDiffusion(fileName, address, subject, headerLine, attachmentMode, dispatchMode);

		disseminationInfo.setDiffusion(mailDiffusion);
	}
	public static void setAlternativeMailDiffusion(DisseminationInfo disseminationInfo, String fileName, String address, String subject, String headerLine, String attachmentMode, String dispatchMode)
	{		
		MailDiffusion mailDiffusion = getMailDiffusion(fileName, address, subject, headerLine, attachmentMode, dispatchMode);

		disseminationInfo.setAlternativeDiffusion(mailDiffusion);
	}

	public static void setFtpDiffusion(DisseminationInfo disseminationInfo, String fileName, String host, String port, String user, String password, boolean passive, String remotePath, boolean checkFileSize, boolean encrypted)
	{		
		FTPDiffusion ftpDiffusion = getFtpDiffusion(fileName, host, port, user, password, passive, remotePath, checkFileSize, encrypted);

		disseminationInfo.setDiffusion(ftpDiffusion);
	}
	public static void setAlternativeFtpDiffusion(DisseminationInfo disseminationInfo, String fileName, String host, String port, String user, String password, boolean passive, String remotePath, boolean checkFileSize, boolean encrypted)
	{		
		FTPDiffusion ftpDiffusion = getFtpDiffusion(fileName, host, port, user, password, passive, remotePath, checkFileSize, encrypted);

		disseminationInfo.setAlternativeDiffusion(ftpDiffusion);
	}
	public static void logDissInfo(DisseminationInfo dissInfo)
	{
		int priority = dissInfo.getPriority();
		int sla = dissInfo.getSLA();
		String dataPolicy = dissInfo.getDataPolicy();

		String diffusion = "Unknown";
		String alternativeDiffusion = "Unknown";
	  
		if (dissInfo.getDiffusion() instanceof MailDiffusion)
			diffusion = "MailDiffusion";
		else if (dissInfo.getDiffusion() instanceof FTPDiffusion)
			diffusion = "FTPDiffusion";

		if (dissInfo.getAlternativeDiffusion() instanceof MailDiffusion)
			alternativeDiffusion = "MailDiffusion";
		else if (dissInfo.getAlternativeDiffusion() instanceof FTPDiffusion)
			alternativeDiffusion = "FTPDiffusion";
	  
		LOG.info("Dissemination info! Priority: " + priority + " SLA: " + sla + " Policy: " + dataPolicy + " Diffusion: " + diffusion + " Alt.Diffusion: " + alternativeDiffusion);
	}
	
	private static MailDiffusion getMailDiffusion(String fileName, String address, String subject, String headerLine, String attachmentMode, String dispatchMode)
	{
		MailDiffusion mailDiffusion = new MailDiffusion();

		mailDiffusion.setFileName(parseFileName(fileName));
		mailDiffusion.setAddress(address);
		mailDiffusion.setHeaderLine(headerLine);
		mailDiffusion.setSubject(subject);
		mailDiffusion.setAttachmentMode(MailAttachmentMode.fromValue(attachmentMode));
		mailDiffusion.setDispatchMode(MailDispatchMode.fromValue(dispatchMode));
		
		return mailDiffusion;
	}

	private static FTPDiffusion getFtpDiffusion(String fileName, String host, String port, String user, String password, boolean passive, String remotePath, boolean checkFileSize, boolean encrypted)
	{
		FTPDiffusion ftpDiffusion = new FTPDiffusion();
		
		ftpDiffusion.setFileName(parseFileName(fileName));
		ftpDiffusion.setHost(host);
		ftpDiffusion.setPort(port);
		ftpDiffusion.setUser(user);
		ftpDiffusion.setPassword(password);
		ftpDiffusion.setPassive(passive);
		ftpDiffusion.setRemotePath(remotePath);
      ftpDiffusion.setCheckFileSize(checkFileSize);
		ftpDiffusion.setEncrypted(encrypted);
		
		return ftpDiffusion;
	}
	
   /**
    * Parse file name to interpret a date/time pattern (ssd-262).
    * @param filename the given filename
    * @return the new filename with date/time processed
    */
   static String parseFileName(String filename) {
      if (filename == null || filename.length() == 0) {
         return filename;
      }
      String[] tokens = filename.split(FILENAME_DATE_TIME_SEPARATOR);
      if (tokens.length != 3) {
         return filename;
      }

      try {
         String pattern = tokens[1];
         if (pattern.length() == 0) {
            return filename;
         }

         return tokens[0] + DateTimeUtils.formatUTC(new Date(), pattern) + tokens[2];
      } catch (Exception e) {
         return filename;
      }
   }
}
