package org.openwis.dataservice.gts.collection;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class IngestionFilenameFilter implements FileFilter {
	

   //	private final static Logger LOG = LoggerFactory.getLogger(IngestionFilenameFilter.class);
	
	private Pattern[] includePatterns;
	private Pattern[] excludePatterns;
	
	private SortedSet<File> sortedFiles;
	
	// --------------------------------------------
	
		private static class FileTimestampComparator implements Comparator<File>, Serializable {

			@Override
			public int compare(File f1, File f2) {
				return (f1.lastModified() < f2.lastModified()) ? -1 : (f1.lastModified() == f2.lastModified() ? (f1.getPath().equals(f2.getPath()) ? 0 : 1) : 1);
				
				
			}
		};
	
	public IngestionFilenameFilter(Pattern[] includePatterns, Pattern[] excludePatterns){
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		
		this.sortedFiles = new TreeSet<File>(new FileTimestampComparator());
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		
		String filename = file.getPath();
		filename = filename.substring(filename.lastIndexOf('/') + 1);
		
		for (Pattern ex : excludePatterns){					
			if (ex.matcher(filename).matches()){
				return false;
			}
		}
		
		for (Pattern in : includePatterns){					
			if (in.matcher(filename).matches()){
//				LOG.debug("Accepted file : " + file.getPath());
				sortedFiles.add(file);
//				LOG.debug("Now there are " + sortedFiles.size() + " elements in the sorted list.");
				return false;
			}
		}
		
		return false;
	}
	
	public List<File> getSortedFiles(int lengthOfHead){
		List<File> sortedFileList = new ArrayList<File>(sortedFiles);
		
		int maxIndex = lengthOfHead;
		if (maxIndex > sortedFiles.size()){
			maxIndex = sortedFiles.size();
		}
		
		return sortedFileList.subList(0, maxIndex);
	}
}