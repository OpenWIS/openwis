package org.openwis.dataservice.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GlobalDataCollectionUtils {
	
	private final static Logger LOG = LoggerFactory.getLogger(GlobalDataCollectionUtils.class);
	
	public static void recursivelyDeleteEmptyParentDirectoriesUpToRoot(String path, String root){
		File dir = new File(path);
		while (!root.equals(dir.getPath().replace('\\', '/'))){
         if (dir.isDirectory()) {
            String[] files = dir.list();
            if (files != null && files.length == 0) {
               dir.delete();
            }
         }
         dir = dir.getParentFile();
			if (dir == null) break;
		}
	}
	
	public static void listAllFilesIncludingSubdirectories(String rootPath, String path, ArrayList<String> fileList, int maxResults){
		if (fileList.size() >= maxResults) return;
		File fileDir = new File(path);
		if (fileDir == null || !fileDir.exists()) return;
		if (fileDir.isFile()) {
			String fileNamePath = fileDir.getPath().replace('\\', '/').replace(rootPath, "");
			fileList.add(fileNamePath);
			return;
		} else 
		if (fileDir.isDirectory()){
         String[] files = fileDir.list();
         if (files != null) {
            for (String newPath : files) {
               listAllFilesIncludingSubdirectories(rootPath, new StringBuilder(path).append("/")
                     .append(newPath).toString().replace('\\', '/'), fileList, maxResults);
            }
         }
			return;
		}
	}
	
	public static void listAllFilesIncludingSubdirectoriesFilteredSorted(String path, FileFilter filter){
		File fileDir = new File(path);
		if (fileDir == null || !fileDir.exists()) return;
		if (fileDir.isDirectory()){
			File[] listedFiles = fileDir.listFiles(filter);
			if (listedFiles != null) {
            for (File newPath : listedFiles) {
               listAllFilesIncludingSubdirectoriesFilteredSorted(
                     newPath.getPath().replace('\\', '/'), filter);
            }
			}
			return;
		}
	}
	
	/**
	 * Returns an array of compiled regex.Patterns given an array of Strings. 
	 * Patterns are case insensitive
	 */
	public static Pattern[] getPatternsFromStrings(final String[] regexps){
		Pattern[] patterns = new Pattern[regexps.length];
		
		for (int i = 0; i < regexps.length; i++) {
			Pattern pattern = Pattern.compile(".*\\." + regexps[i],Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
			patterns[i] = pattern;
			
		}			
		return patterns;
	}
	
	public static boolean isFileReadyForUse(String fullFileName){
		boolean isReady = false;
	    try {
	    	File file = new File(fullFileName);
	    	if (!file.isFile() || !file.exists()) return isReady; 
	    	
	    	FileInputStream stream = new FileInputStream(file);
	    	
	    	try {
	    		stream.read();
	    		isReady = true;
	    	}
	    	catch (IOException e) {
	    	}
	    	finally {
	    		stream.close();
	    	}
	    }	
	    catch (IOException e) {
	    }
	    
	    return isReady;
	}
	
	public static WMOFNC parseFileName(final String source) {
		WMOFNC wmofnc = null;
		// TODO extract only filename from source (which is the absolute path)
		try {
			wmofnc = FileNameParser.parseFileName(source);
		}
		catch (java.text.ParseException e) {
			LOG.error("+++ Error while parsing " + source);
		}
		catch(StringIndexOutOfBoundsException e){
			LOG.error("+++ Error while parsing " + source);
		}
		return wmofnc;
	}
	
	public static boolean isPacked(final String filename){
		Pattern ftpPattern = Pattern.compile(".*\\_.*");
		if (!ftpPattern.matcher(filename).matches()){
			return true;
		}
		return false;
	}
}