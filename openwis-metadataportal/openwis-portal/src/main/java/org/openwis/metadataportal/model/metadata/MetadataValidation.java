/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public enum MetadataValidation {
   
   /**
    * No validation.
    */
   NONE,
   
   /**
    * XSD only.
    */
   XSD_ONLY,
   
   /**
    * Includes XSD and Schematrons.
    */
   FULL
}
