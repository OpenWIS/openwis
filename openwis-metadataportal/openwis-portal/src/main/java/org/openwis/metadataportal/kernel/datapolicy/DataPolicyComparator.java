/**
 * 
 */
package org.openwis.metadataportal.kernel.datapolicy;

import java.util.Comparator;

import org.openwis.metadataportal.model.datapolicy.DataPolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicyComparator implements Comparator<DataPolicy> {

   /**
    * {@inheritDoc}
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(DataPolicy o1, DataPolicy o2) {
      return o1.getName().compareToIgnoreCase(o2.getName());
   }

}
