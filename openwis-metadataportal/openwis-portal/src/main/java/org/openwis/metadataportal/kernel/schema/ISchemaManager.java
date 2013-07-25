/**
 * 
 */
package org.openwis.metadataportal.kernel.schema;

import java.util.List;

import org.openwis.metadataportal.model.metadata.Schema;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public interface ISchemaManager {
   
   List<Schema> getAllAvailableSchemas(String templateDirectoryPath) throws Exception;

}
