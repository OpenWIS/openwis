package org.openwis.metadataportal.kernel.harvest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

public class CatalogReportHelper {

	/**
	 * Create a file report with the list of harvesting tasks urn added, updated
	 * and removed
	 * 
	 * @param taskId harvesting task identifier
	 * @param taskName harvesting task name
	 * @param lastRunDate last run date
	 * @param urnAdded urns of metadata added into the catalogue   
	 * @param urnUpdated urns of updated metadata 
	 * @param urnRemoved  urns of metadata removed from the catalogue   
	 */
	public static void createReport(Integer taskId, String taskName,
		String lastRunDate, List<String> urnAdded, List<String> urnUpdated,
		List<String> urnRemoved) throws IOException {
		FileWriter fileWriter = null;
		PrintWriter printWriter = null;
		// insert urn added, updated and removed into a file (name == (date +
		// harvesting task name + task id))
		try {
			File lastReportFile = getReportFile(taskId, taskName, lastRunDate);
	
			fileWriter = new FileWriter(lastReportFile);
			printWriter = new PrintWriter(fileWriter);
	
			// harvesting task name
			printWriter.println(taskName);
	
			// date
			printWriter.println(new Date());
	
			// added
			printWriter.println("");
			printWriter.println("urn added:");
			printWriter.println("----------");
			for (String added : urnAdded) {
				printWriter.println(added);
			}
	
			// updated
			printWriter.println("");
			printWriter.println("urn updated:");
			printWriter.println("------------");
			for (String updated : urnUpdated) {
				printWriter.println(updated);
			}
	
			// removed
			printWriter.println("");
			printWriter.println("urn removed:");
			printWriter.println("------------");
			for (String removed : urnRemoved) {
				printWriter.println(removed);
			}
	
			
			printWriter.flush();
			fileWriter.flush();
			
		} finally {
			// close writers
			if (printWriter != null) {
				printWriter.close();
			}
			if (fileWriter != null) {
				fileWriter.close();
			}
	    }
	}

	
	
	/**
	 * Build a report File object from a harvesting task then return it
	 * 
	 * @param taskId harvesting task identifier
	 * @param taskName harvesting task name
	 * @param lastRunDate last run date
	 * @return the report File object corresponding to the harvesting task
	 */
	public static File getReportFile(Integer taskId, String taskName,
			String lastRunDate) {
		String path = OpenwisMetadataPortalConfig
				.getString(ConfigurationConstants.REPORT_FILE_PATH);
		String prefix = OpenwisMetadataPortalConfig
				.getString(ConfigurationConstants.REPORT_FILE_PREFIX);
		String ext = OpenwisMetadataPortalConfig
				.getString(ConfigurationConstants.REPORT_FILE_EXT);

		String harvestingTaskName = taskName.length() > 20 ? taskName
				.substring(0, 20) : taskName;

		String fileName = prefix + "-" + lastRunDate.replaceAll("\\W+", "_")
				+ "-" + taskId + "-"
				+ harvestingTaskName.replaceAll("\\W+", "_") + "." + ext;

		File reportFile = new File(path, fileName);

		return reportFile;
	}
}
