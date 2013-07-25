/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.util.List;

import org.fao.geonet.kernel.search.IndexField;
import org.openwis.metadataportal.model.metadata.AbstractMetadata;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public interface IMetadataManager {

   /**
    * Updates the category of a metadata.
    * @param urn the metadata URN.
    * @param categoryId the category id.
    * @throws Exception if an error occurs.
    * @deprecated
    */
   @Deprecated
   void updateCategory(String urn, Integer categoryId) throws Exception;

   /**
    * Gets the all metadata by category.
    *
    * @param categoryId the category id
    * @return the all metadata by category
    * @throws Exception if an error occurs.
    */
   List<Metadata> getAllMetadataByCategory(Integer categoryId) throws Exception;

   /**
    * Gets information on metadata (used for import).
    * @param uuid the urn of the metadata.
    * @return the persistent metadata, <code>null</code> otherwise.
    * @throws Exception if an error occurs.
    */
   Metadata getMetadataInfoByUrn(String uuid) throws Exception;

   /**
    * Gets information on metadata or template.
    * @param id the template/metadata ID.
    * @return the persistent template/metadata, <code>null</code> otherwise.
    * @throws Exception if an error occurs.
    */
   AbstractMetadata getAbstractMetadataInfoById(Integer id) throws Exception;

   /**
    * Creates a metadata from a template.
    * @param metadata the metadata to create.
    * @param template the template.
    * @return the metadata ready to be imported.
    * @throws Exception if an error occurs.
    */
   Metadata createMetadataFromTemplate(Metadata metadata, Template template) throws Exception;

   /**
    * Gets the index field corresponding to the given column.
    * @param columnName the column name.
    * @return the index field corresponding to this column name.
    */
   IndexField getIndexFieldColumn(String columnName);

   /**
    * Gets the number of metadata.
    * @return the number of metadata.
    * @throws Exception if an error occurs.
    */
   Integer getAllMetadata() throws Exception;

}
