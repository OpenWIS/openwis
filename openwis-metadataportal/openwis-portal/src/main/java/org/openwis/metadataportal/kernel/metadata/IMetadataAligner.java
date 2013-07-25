/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import jeeves.server.context.ServiceContext;

import org.fao.geonet.util.ISODate;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;

import com.google.common.base.Function;

/**
 * An aligner for metadata. <P>
 * Explanation goes here. <P>
 *
 */
public interface IMetadataAligner {

   /**
    * Imports the metadatas.
    * @param mds the records to import.
    * @param validation the validation mode to use.
    * @param predicatedStylesheets the style sheet to apply with a predicate.
    * @param dateCollector a date collector to be used in the update case.
    * @throws Exception if an error occurs.
    */
   void importMetadatas(List<Metadata> mds, MetadataValidation validation,
         List<PredicatedStylesheet> predicatedStylesheets, Function<Metadata, ISODate> dateCollector)
         throws Exception;

   /**
    * Imports the metadatas.
    * @param mds the records to import.
    * @param validation the validation mode to use.
    * @param predicatedStylesheets the style sheet to apply with a predicate.
    * @param dateCollector a date collector to be used in the update case.
    * @throws Exception if an error occurs.
    */
   void importMetadatas(List<Metadata> mds, MetadataValidation validation,
         List<PredicatedStylesheet> predicatedStylesheets, Function<Metadata, ISODate> dateCollector, ServiceContext context)
         throws Exception;

   /**
    * Deletes the metadata by URNs and flag them as deleted in the metadata table.
    * @param uuids the URNs.
    * @throws Exception if an error occurs.
    */
   void deleteMetadatasByUrns(Collection<String> uuids) throws Exception;

   /**
    * Deletes the metadata and flag them as deleted in the metadata table.
    * @param mds the metadatas to delete.
    * @throws Exception if an error occurs.
    */
   void deleteMetadatas(Collection<Metadata> mds) throws Exception;

   /**
    * Index the imported metadatas.
    * @throws Exception if an error occurs.
    */
   void indexImportedMetadatas() throws Exception;

   /**
    * Gets the result of the import.
    * @return the result of the alignement.
    */
   MetadataAlignerResult getResult();

   /**
    * Gets a set of processed metadatas.
    * @return a set of processed metadatas URNs.
    */
   Set<String> getProcessedMetadatas();

   /**
    * Creates the metadata.
    *
    * @param md the md
    * @throws Exception the exception
    */
   void createMetadata(Metadata md) throws Exception;
}
