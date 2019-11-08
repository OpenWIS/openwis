/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.extractor;

import java.io.File;
import java.util.List;

import org.openwis.metadataportal.model.metadata.Metadata;

/**
 * An interface for metadata extractors that creates a metadata from a file. <P>
 * Explanation goes here. <P>
 * 
 */
public interface IMetadataAlignerExtractor {
   
   /**
    * Extract a metadata from a file.
    * @param f the file.
    * @return a metadata object.
    * @throws Exception if an error occurs.
    */
   List<Metadata> extract(File f) throws Exception;

}
