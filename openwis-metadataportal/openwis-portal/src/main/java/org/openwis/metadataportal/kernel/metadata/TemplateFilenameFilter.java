/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class TemplateFilenameFilter implements FilenameFilter {

   /**
    * {@inheritDoc}
    * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
    */
   @Override
   public boolean accept(File dir, String name) {
      return name.endsWith(".xml");
   }

}
