/**
 * 
 */
package org.openwis.metadataportal.kernel.schema;

import java.io.File;
import java.io.FileFilter;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SchemaFilenameFilter implements FileFilter {

   /**
    * {@inheritDoc}
    * @see java.io.FileFilter#accept(java.io.File)
    */
   @Override
   public boolean accept(File file) {
      return file.isDirectory() && !file.getName().startsWith(".") && file.listFiles().length > 0;
   }

}
