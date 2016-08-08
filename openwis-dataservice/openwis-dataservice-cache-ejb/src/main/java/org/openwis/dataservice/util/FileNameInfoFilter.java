/**
 *
 */
package org.openwis.dataservice.util;


/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public abstract class FileNameInfoFilter {

   // -------------------------------------------------------------------------
   // Factory Methods
   // -------------------------------------------------------------------------

   /**
    * Description goes here.
    * @param metadataURN
    * @return
    */
   public static FileNameInfoFilter createMetadataFilter(final String metadataURN) {
      return new MetadataFilter(metadataURN);
   }

   /**
    * Description goes here.
    * @param timeRangeExpr
    * @return
    */
   public static FileNameInfoFilter createProductDateFilter(final String timeRangeExpr) {
      return new ProductDateFilter(timeRangeExpr);
   }

   // -------------------------------------------------------------------------
   // Initialization
   // -------------------------------------------------------------------------

   /**
    * Tests whether or not the specified file name matches the filter criteria.
    *
    * @param fileName The file name to be tested
    * @return {@code true} if and only if <code>fileName</code> should be included
    */
   public abstract boolean accept(String fileName);

   // -------------------------------------------------------------------------
   // Initialization
   // -------------------------------------------------------------------------

}
