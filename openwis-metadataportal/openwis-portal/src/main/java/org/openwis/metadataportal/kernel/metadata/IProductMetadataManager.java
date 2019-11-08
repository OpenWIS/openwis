/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.util.Date;
import java.util.List;

import jeeves.resources.dbms.Dbms;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.kernel.DataManager;
import org.openwis.dataservice.CannotDeleteAllProductMetadataException_Exception;
import org.openwis.dataservice.CannotDeleteProductMetadataException_Exception;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.metadataportal.model.metadata.Metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public interface IProductMetadataManager {

   /**
    * Get a product metadata by URN
    * @param urn the metadata urn
    * @return a product metadata
    */
   ProductMetadata getProductMetadataByUrn(String urn);

   /**
    * Save or Update a product metadata
    * @param pm the product metadata
    */
   void saveOrUpdate(ProductMetadata pm);

   /**
    * Delete a product metadata.
    *
    * @param urn the product metadata URN
    * @throws CannotDeleteProductMetadataException_Exception the cannot delete product metadata exception_ exception
    */
   void delete(String urn) throws CannotDeleteProductMetadataException_Exception;

   void delete(List<String> urns) throws CannotDeleteAllProductMetadataException_Exception;


   /**
    * Synchronize stop gap metadata.
    *
    * @param lastSynchro the last synchronization date
    * @param dbms the dbms
    * @param dm the data manager
    * @param mdm the mdm
    * @param gnContext the geonetwork context
    * @throws Exception the exception
    */
   public void synchronizeStopGapMetadata(Date lastSynchro, Dbms dbms, DataManager dm,
         IMetadataManager mdm, GeonetContext gnContext)
         throws Exception;

   /**
    * Returns <code>true</code> if the product metadata is valid, <code>false</code> otherwise.
    * @param productMetadata the product metadata to test.
    * @return <code>true</code> if the product metadata is valid, <code>false</code> otherwise.
    */
   boolean isValid(ProductMetadata pm);

   /**
    * Extracts a product metadata from a metadata handling the schema.
    * @param metadata the object wrapping all elements needed for extarction.
    * @param isExisting <code>true</code> if the element exists, <code>false</code> otherwise.
    * @return the product metadata extracted with all attributes.
    * @throws Exception if an error occurs.
    */
   ProductMetadata extract(Metadata metadata, boolean isExisting) throws Exception;
   
   /**
    * Test if the metadata is flagged as GlobalExchange
    * @param metadata the metadata
    * @return <code>true</code> if global exchange is found
    */
   boolean isGlobalExchange(Metadata metadata) throws Exception;

   /**
    * Test if the metadata is iso Core Profile 1.3 or higher
    * @param metadata the metadata
    */
   boolean isIsoCoreProfile1_3(Metadata metadata) throws Exception;
}