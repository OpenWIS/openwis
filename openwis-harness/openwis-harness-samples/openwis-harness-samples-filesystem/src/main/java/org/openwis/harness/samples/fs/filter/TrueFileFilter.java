/**
 * 
 */
package org.openwis.harness.samples.fs.filter;

import java.io.File;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class TrueFileFilter implements FsFileFilter {

   /** The Constant INSTANCE. */
   public static final FsFileFilter INSTANCE = new TrueFileFilter();

   /**
    * Default constructor.
    * Builds a TrueFileFilter.
    */
   private TrueFileFilter() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "True";
   }

   /**
    * {@inheritDoc}
    * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
    */
   @Override
   public boolean accept(File file) {
      return true;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.fs.filter.FsFileFilter#acceptStrict(java.io.File)
    */
   @Override
   public boolean acceptStrict(File file) {
      return false;
   }

}
