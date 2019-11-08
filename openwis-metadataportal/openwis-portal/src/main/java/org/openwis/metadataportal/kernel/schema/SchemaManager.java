/**
 * 
 */
package org.openwis.metadataportal.kernel.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.metadata.Schema;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SchemaManager implements ISchemaManager {

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.schema.ISchemaManager#getAllAvailableSchemas(java.lang.String)
    */
   @Override
   public List<Schema> getAllAvailableSchemas(String templateDirectoryPath) throws Exception {
      File templateDirectory = new File(templateDirectoryPath);
      File[] schemaDirectories = templateDirectory.listFiles(new SchemaFilenameFilter());
      List<Schema> schemaNames = new ArrayList<Schema>();
      
      for (File f : schemaDirectories){
         schemaNames.add(new Schema(f.getName()));
      }
      return schemaNames;
   }

}
