/**
 *
 */
package org.openwis.dataservice.util;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public class MetadataFilter extends FileNameInfoFilter {

   // -------------------------------------------------------------------------
   // Instance Variables
   // -------------------------------------------------------------------------
   private final Logger LOG = LoggerFactory.getLogger(MetadataFilter.class);

   // metadata URN
   private final String metadataURN;

   // -------------------------------------------------------------------------
   // Initialization
   // -------------------------------------------------------------------------

   /**
    * Default constructor.
    *
    * Builds a MetadataFilter.
    * @param metadata
    */
   public MetadataFilter(final String metadata) {
      // check: pattern
      if (metadata == null || "".equals(metadata.trim())) {
         throw new IllegalArgumentException("Invalid metadata URN specified : " + metadata);
      }
      metadataURN = metadata.trim();
   }

   // -------------------------------------------------------------------------
   // ExtractService Impl.
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    * @see FileNameInfoFilter#accept(FileNameInfo)
    */
   @Override
   public boolean accept(final String fileName) {
      // return value
      boolean accepted = false;

      // resolve file name info
      WMOFNC info = null;
	try {
		info = FileNameParser.parseFileName(fileName);
	} catch (ParseException e) {
         LOG.error(e.getMessage(), e);
	}
      if (info != null) {
         // compare metadata URN
         String value = info.getMetadataURN();
         accepted = metadataURN.equals(value);

         // compare metadata
         if (!accepted) {
            value = info.getMetadata();
            accepted = metadataURN.equals(value);
         }
      }

      // feedback
      return accepted;
   }

}
