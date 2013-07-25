/**
 * 
 */
package org.openwis.harness.samples.fs.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Explanation goes here. <P>
 * 
 */
public interface FsFileFilter extends FileFilter {

   /**
    * Accept strict.
    *
    * @param file the file
    * @return true, if successful
    */
   public boolean acceptStrict(File file);

}
