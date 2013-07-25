/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.validator;

import jeeves.server.context.ServiceContext;

import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;

/**
 * A common interface for metadata validator. <P>
 * Explanation goes here. <P>
 * 
 */
public interface IMetadataValidator {
  
   static final int ERROR_SIZE_LIMIT = 5;
   
   /**
    * Validates a metadata.
    * @param dm the data manager.
    * @param md the metadata.
    * @param schema the schema.
    * @return the violations if validation fails.
    */
   MetadataValidatorResult validate(DataManager dm, Element md, String schema, ServiceContext context);

}
