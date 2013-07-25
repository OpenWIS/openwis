/**
 * 
 */
package org.openwis.metadataportal.kernel.common;

/**
 * A common interface that provides methods to monitor processes. <P>
 * Explanation goes here. <P>
 * 
 */
public interface IMonitorable {
   
   /**
    * Gets the count of processed elements.
    * @return the number of processed elements.
    */
   int getProcessed();
   
   /**
    * Gets the count of elements.
    * @return the count of elements.
    */
   int getTotal();

}
