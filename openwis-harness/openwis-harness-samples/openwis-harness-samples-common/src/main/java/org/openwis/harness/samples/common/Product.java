/**
 * 
 */
package org.openwis.harness.samples.common;

import java.util.Calendar;

/**
 * The Class Product. <P>
 * Explanation goes here. <P>
 */
public interface Product {

   /**
    * Gets the metadata URN.
    *
    * @return the metadata URN
    */
   public String getMetadataURN();

   /**
    * Gets the URN.
    *
    * @return the URN
    */
   public String getURN();

   /**
    * Gets the timestamp.
    *
    * @return the timestamp
    */
   public Calendar getTimestamp();

}
