/**
 *
 */
package org.openwis.dataservice.gts.collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.dataservice.CacheTestHelpers;
import org.openwis.dataservice.util.FileNameParser;
import org.openwis.dataservice.util.GlobalDataCollectionUtils;
import org.openwis.dataservice.util.WMOFNC;

/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public class CollectorTimerServiceTest extends CacheTestHelpers {

   private static File workingDirectory;

   /**
    * Description goes here.
    * @throws Exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {

      String tmpdir = System.getProperty("java.io.tmpdir");
      File directory = new File(tmpdir, "openwis-dataservice-cache");

      // check existence
      if (directory.isDirectory()) {
         workingDirectory = directory;
      }
      else {
         if (directory.mkdirs()) {
            workingDirectory = directory;
            workingDirectory.deleteOnExit();
         }
      }

      // create source and target directories
      if (workingDirectory != null) {
         new File(workingDirectory, "target").mkdir();

         File sourceDir = new File(workingDirectory, "source");
         if (sourceDir.mkdir() || sourceDir.exists()) {
//            createFiles(sourceDir.getAbsolutePath(), AHL_FILENAMES);
//            createFiles(sourceDir.getAbsolutePath(), FNC_FILENAMES);
        	 createFiles(sourceDir.getAbsolutePath(), FILENAMES);
         }
      }
   }

   /**
    * Description goes here.
    * @throws Exception
    */
   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      // remove all entries in working directory
      if (workingDirectory != null) {
         deleteFiles(workingDirectory);
      }
   }

   /**
    * Test method for {@link CollectorTimerServiceImpl#scan(String, String, String)}.
    */
   @Test
   public void testScanAHL() {
      testScan(FILENAMES);
   }

   /**
    * Test method for {@link CollectorTimerServiceImpl#scan(String, String, String)}.
    */
   @Test
   public void testScanFNC() {
      testScan(FILENAMES);
   }

   /**
    * Test method for {@link CollectorTimerServiceImpl#scan(String, String, String)}.
    */
   private void testScan(final List<String> targetFilenames) {
      // check state
      if (workingDirectory == null) {
         return;
      }

      // AHL files
      File sourceDir = new File(workingDirectory, "source");
      if (sourceDir.mkdir() || sourceDir.exists()) {
         createFiles(sourceDir.getAbsolutePath(), targetFilenames);
      }

      String[] filenames = scan(sourceDir.getPath(), INCLUDE_PATTERNS, EXCLUDE_PATTERNS,100);

      assertNotNull(filenames);
      assertTrue(filenames.length <= targetFilenames.size());

      List<String> includedFiles = Arrays.asList(filenames);
      for (String targetFile : targetFilenames) {
         assertTrue(includedFiles.contains(targetFile));
      }
   }

   /**
    * Test method for {@link CollectorTimerServiceImpl#parseFileNameInfo(String, String, String[])}.
    */
   @Test
   public void testGetMetadataURN() {
      // check state

      for (String filename : FILENAMES) {
    	  WMOFNC info = null;
		try {
			info = FileNameParser.parseFileName(filename);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         assertNotNull(info);

         assertNotNull(info.getMetadataURN());

         System.out.format("%s => %s [%s]%n",
               filename, info.getMetadataURN(), info.getProductDate());
      }

      for (String filename : FILENAMES) {
    	  WMOFNC info = null;
		try {
			info = FileNameParser.parseFileName(filename);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         assertNotNull(info);

         assertNotNull(info.getMetadataURN());
         assertNotNull(info.getProductDate());

         System.out.format("%s => %s [%s]%n",
               filename, info.getMetadataURN(), info.getProductDate());
      }
   }
   
   private String[] scan(final String searchDirectory, final String[] include,final String[] exclude, int maxNumberOfIncludedFiles){
		Pattern[] includePatterns = GlobalDataCollectionUtils.getPatternsFromStrings(include);
		Pattern[] excludePatterns = GlobalDataCollectionUtils.getPatternsFromStrings(exclude);
		
		int includedFiles = 0;
		
		List<String> includedFileNames = new ArrayList<String>();
		File dir = new File(searchDirectory);			
						
		if (dir.isDirectory()){
			for (String filename : dir.list()){
				
//				if (!GlobalDataCollectionUtils.isFileReadyForUse(searchDirectory + "/" + filename)) continue;
				
				// excluding files
				boolean excluded = false;
				for (Pattern ex : excludePatterns){					
					if (ex.matcher(filename).matches()){
						excluded = true;
						break;
					}
				}
				if (excluded) {
					continue;
				}

				// including files
				boolean included = false;
				for (Pattern in : includePatterns){
					if (in.matcher(filename).matches()){
						included = true;
						break;
					}
				}
				if (included){
					includedFileNames.add(filename);
					includedFiles++;
					if (includedFiles >= maxNumberOfIncludedFiles) break;
				}
			}
		}
		// clean up
		includePatterns = null;
		excludePatterns = null;
		
		String[] array = new String[includedFileNames.size()];
		return includedFileNames.toArray(array);
	}

}
