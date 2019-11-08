/**
 * 
 */
package org.openwis.metadataportal.common.io;

import java.io.File;
import java.io.FileFilter;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class FileFileFilter implements FileFilter {

   /**
    * {@inheritDoc}
    * @see java.io.FileFilter#accept(java.io.File)
    */
   @Override
   public boolean accept(File pathname) {
      return pathname.isFile();
   }

}
