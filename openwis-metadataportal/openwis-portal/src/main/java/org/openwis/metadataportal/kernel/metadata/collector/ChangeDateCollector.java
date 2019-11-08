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
public class ChangeDateCollector implements Function<Metadata, ISODate> {

   /**
    * {@inheritDoc}
    * @see com.google.common.base.Function#apply(java.lang.Object)
    */
   @Override
   public ISODate apply(Metadata input) {
      return new ISODate(input.getChangeDate());
   }

}
