/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.submit;

/**
 * The frequency type (event based or recurrent processing). <P>
 * For a subscription, a frequency must be set : <P>
 * <ul>
 *    <li>Event-Based : Each time a new product instance is found, it is returned to the client.</li>
 *    <li>Recurrent processing : Each time defined by a given period and scale, new product 
 *    instances are searched and returned to the client.</li>
 * </ul>
 */
public enum FrequencyType {
   /**
    * The event based processing.
    */
   ON_PRODUCT_ARRIVAL,
   
   /**
    * The recurrent processing.
    */
   RECURRENT_PROCESSING
}
