/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.collector;

import org.fao.geonet.util.ISODate;
import org.openwis.metadataportal.model.metadata.Metadata;

import com.google.common.base.Function;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class LocalImportDateCollector implements Function<Metadata, ISODate> {

   /**
    * {@inheritDoc}
    * @see com.google.common.base.Function#apply(java.lang.Object)
    */
   @Override
   public ISODate apply(Metadata input) {
      ISODate localDateStamp = null;
      if (input.getLocalImportDate() != null) {
         localDateStamp = new ISODate(input.getLocalImportDate());
      } else {
         localDateStamp = new ISODate(input.getChangeDate());
      }
      return localDateStamp;
   }

}
