/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata;

import java.util.Collection;

import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public interface IDeletedMetadataManager {

   /**
    * Create a deleted metadata object from a metadata URN.
    * @param urn the metadata URN
    * @return a DeletedMetadata
    * @throws Exception
    */
   DeletedMetadata createDeletedMetadataFromMetadataUrn(String urn) throws Exception;

   /**
    * Insert a deleted metadata in the database.
    * @param dm the deleted metadata to insert
    * @throws Exception 
    */
   void insertDeletedMetadata(DeletedMetadata dm) throws Exception;

   /**
    * Get a collection of deleted metadata URNs.
    * @param from
    * @param until
    * @param category
    * @return
    * @throws Exception
    */
   Collection<String> getDeletedMetadataUrns(String from, String until, Category category)
         throws Exception;

   /**
    * Get a deleted metadata by URN.
    * @param urn
    * @param category
    * @return
    * @throws Exception 
    */
   DeletedMetadata getDeletedMetadataByUrn(String urn, Category category) throws Exception;

   /**
    * Clean deleted metadata table.
    * @param urn the metadata urn
    * @param categoryId the md category id
    * @throws Exception 
    */
   void clean(String urn, Integer categoryId) throws Exception;
   
   /**
    * Clean deleted metadata table for metadata of a given category.
    * @param categoryId the md category id
    * @throws Exception 
    */
   void clean(Integer categoryId) throws Exception;

}