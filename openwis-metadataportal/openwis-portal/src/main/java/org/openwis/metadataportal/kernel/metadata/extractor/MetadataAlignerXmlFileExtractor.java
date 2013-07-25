/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.extractor;

import java.io.File;
import java.util.List;

import jeeves.utils.Xml;

import org.jdom.Element;
import org.openwis.metadataportal.model.metadata.Metadata;

import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataAlignerXmlFileExtractor implements IMetadataAlignerExtractor {

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.extractor.IMetadataAlignerExtractor#extract(java.io.File)
    */
   @Override
   public List<Metadata> extract(File f) throws Exception {
      Element e = Xml.loadFile(f);
      if(e == null) {
         return null;
      }
      return Lists.newArrayList(new Metadata(e));
   }

}
