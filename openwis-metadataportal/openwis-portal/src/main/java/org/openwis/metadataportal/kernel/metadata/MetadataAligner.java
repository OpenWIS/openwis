/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.XmlSerializer;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.Pair;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.MetadataServiceAlerts;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor;
import org.openwis.metadataportal.kernel.metadata.validator.IMetadataValidator;
import org.openwis.metadataportal.kernel.metadata.validator.MetadataValidatorFactory;
import org.openwis.metadataportal.kernel.metadata.validator.MetadataValidatorResult;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.MetadataResource;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.services.login.LoginConstants;

import com.google.common.base.Function;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class MetadataAligner extends AbstractManager implements IMetadataAligner {

   public static final String EXTRACT_IMPORT_INFO = "extract-import-info.xsl";

   private final MetadataAlignerResult result;

   private final ISearchManager searchManager;

   private final DataManager dataManager;

   private final String dataDir;

   private final IDataPolicyManager dataPolicyManager;

   private final IDeletedMetadataManager deletedMetadataManager;

   private final IProductMetadataManager productMetadataManager;

   private final IMetadataManager metadataManager;

   private final Set<String> processedMetadatas = new HashSet<String>();

   private final Collection<IndexableElement> indexableElements = new ArrayList<IndexableElement>();

   /**
    * Default constructor.
    * Builds a MetadataImportManager.
    * @param dbms
    */
   public MetadataAligner(Dbms dbms, DataManager dataManager, ISearchManager searchManager,
         String dataDir) {
      super(dbms);
      this.dataManager = dataManager;
      this.searchManager = searchManager;
      dataPolicyManager = new DataPolicyManager(dbms);
      deletedMetadataManager = new DeletedMetadataManager(dbms);
      productMetadataManager = new ProductMetadataManager();
      metadataManager = new MetadataManager(dbms);
      this.dataDir = dataDir;
      result = new MetadataAlignerResult();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataAligner#importMetadatas(java.util.List, org.openwis.metadataportal.model.metadata.MetadataValidation, org.openwis.metadataportal.model.category.Category, java.util.List)
    */
   @Override
   public void importMetadatas(List<Metadata> mds, MetadataValidation validation,
         List<PredicatedStylesheet> predicatedStylesheets, Function<Metadata, ISODate> dateCollect)
         throws Exception {
      long before, after;
      for (Metadata md : mds) {
         //Inc. Total.
         result.incTotal();

         before = System.currentTimeMillis();

         //Import the metadata in the system.
         importMetadata(md, validation, predicatedStylesheets, dateCollect, null);

         processedMetadatas.add(md.getUrn());

         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#importMetadatas",
                  "Import one metadata", after - before);
         }
      }
      result.setDate(new Date());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataAligner#deleteMetadatas(java.util.Collection)
    */
   @Override
   public void deleteMetadatasByUrns(Collection<String> uuids) throws Exception {
      Collection<Metadata> mds = new HashSet<Metadata>();
      for (String uuid : uuids) {
         Metadata md = metadataManager.getMetadataInfoByUrn(uuid);
         if (md == null) {
            Log.warning(Geonet.METADATA_ALIGNER,
                  "Skipping metadata with deleted status and probably already deleted:" + uuid);
            result.incIgnored();
            result.incTotal();

            //Add as processed.
            processedMetadatas.add(uuid);
         } else {
            mds.add(md);
         }
      }
      deleteMetadatas(mds);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataAligner#deleteMetadatas(java.util.Collection)
    */
   @Override
   public void deleteMetadatas(Collection<Metadata> mds) throws Exception {
      long before, after;
      for (Metadata md : mds) {
         result.incTotal();

         //Add as processed.
         processedMetadatas.add(md.getUrn());

         before = System.currentTimeMillis();
         Log.info(Geonet.METADATA_ALIGNER, "Removing metadata with URN : " + md.getUrn());
         try {
            dataManager.deleteMetadata(getDbms(), md.getUrn(), true);
            getDbms().commit();
            result.incLocallyRemoved();
            result.getUrnRemoved().add(md.getUrn());
         } catch (Exception e) {
            result.getErrors().add(new MetadataAlignerError(md.getUrn(), e.getMessage()));
            result.incUnexpected();
         }
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#deleteMetadatas",
                  "Delete one metadata", after - before);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataAligner#indexImportedMetadatas()
    */
   @Override
   public void indexImportedMetadatas() throws Exception {
      if (!indexableElements.isEmpty()) {
         Collection<IndexableElement> elements = new ArrayList<IndexableElement>(indexableElements);
         indexableElements.clear();
         searchManager.index(elements);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataAligner#getResult()
    */
   @Override
   public MetadataAlignerResult getResult() {
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataAligner#getProcessedMetadatas()
    */
   @Override
   public Set<String> getProcessedMetadatas() {
      return processedMetadatas;
   }

   //------------------------------------------------------------------- Private methods.

   @Override
   public void importMetadatas(List<Metadata> mds, MetadataValidation validation,
         List<PredicatedStylesheet> predicatedStylesheets,
         Function<Metadata, ISODate> dateCollector, ServiceContext context) throws Exception {
      long before, after;
      for (Metadata md : mds) {
         //Inc. Total.
         result.incTotal();

         before = System.currentTimeMillis();

         //Import the metadata in the system.
         importMetadata(md, validation, predicatedStylesheets, dateCollector, context);

         processedMetadatas.add(md.getUrn());

         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#importMetadatas",
                  "Import one metadata", after - before);
         }
      }
      result.setDate(new Date());
   }

   /**
    * Imports a metadata in the system.
    * @param newMetadata the metadata to import.
    * @param validation the validation mode to apply.
    * @param predicatedStylesheets the XSL style sheets to apply.
    */
   private void importMetadata(Metadata newMetadata, MetadataValidation validation,
         List<PredicatedStylesheet> predicatedStylesheets, Function<Metadata, ISODate> dateCollect,
         ServiceContext context) {
      try {
         long before, after;

         //Schema management.
         before = System.currentTimeMillis();
         String schema = getMetadataSchema(newMetadata, predicatedStylesheets);
         if (schema == null) {
            return;
         }
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#importMetadata", "Get MD Schema",
                  after - before);
         }

         
         //Extract UUID and date stamp from the metadata contents and set it to the metadata being aligned.
         before = System.currentTimeMillis();
         Pair<String, ISODate> uuidAndDateStamp = extractMetadataImportInfo(newMetadata, schema);
         if (uuidAndDateStamp == null) {
            Log.warning(Geonet.METADATA_ALIGNER, "Cannot extract URN or dateStamp.");
            return;
         }
         if (StringUtils.isBlank(newMetadata.getUrn()) || newMetadata.getChangeDate() == null
               || StringUtils.isBlank(newMetadata.getChangeDate().toString())) {
            newMetadata.setUrn(uuidAndDateStamp.one());      
         }
         
         // Always set the change date to the date stored in the metadata record
         newMetadata.setChangeDate(uuidAndDateStamp.two().toString());
         
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#importMetadata",
                  "Extract UUID and DateStamp", after - before);
         }         
                 
         //Validate metadata.  This requires the extracted UUID to display the proper URNs
         before = System.currentTimeMillis();
         IMetadataValidator validator = MetadataValidatorFactory.getValidator(validation);
         MetadataValidatorResult metadataValidatorResult = validator.validate(dataManager,
               newMetadata.getData(), schema, context);
         if (!metadataValidatorResult.isValidate()) {
            Log.warning(Geonet.METADATA_ALIGNER, "Does not validate.");
            result.getErrors().add(
                  new MetadataAlignerError(newMetadata.getUrn(), metadataValidatorResult.getMessage()));
            result.incDoesNotValidate();
            // Raise an alarm
            AlertService alertService = ManagementServiceProvider.getAlertService();
            if (alertService == null) {
               Log.error(LoginConstants.LOG,
                     "Could not get hold of the AlertService. No alert was passed!");
               return;
            }
            String source = "metadata-validation";
            String location = "Catalogue";
            String eventId = MetadataServiceAlerts.MDTA_VALIDATION_FAILED.getKey();
            List<Object> arguments = new ArrayList<Object>();
            String urn = "";
            if (newMetadata.getUrn() != null)
            {
               urn = newMetadata.getUrn();
            }
            arguments.add(urn);
            // Error message has to be truncated
            // We replace &#148 (for tooltip report) by "
            String errors = metadataValidatorResult.getMessage();
            if (errors != null && errors.length() > 200)
            {
               errors = errors.substring(0, 200);
               errors = errors.replaceAll("&#148;", "\"");
               errors = errors + " ...";
            }
            arguments.add(errors);
            alertService.raiseEvent(source, location, null, eventId, arguments);
            return;
         }
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#importMetadata", "Validate MD", after
                  - before);
         }

         //Check if metadata exists.
         before = System.currentTimeMillis();
         Metadata existingMetadata = metadataManager.getMetadataInfoByUrn(newMetadata.getUrn());
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#importMetadata",
                  "Get MD Info from DB", after - before);
         }
         
         // TODO: A better way to do this is to separate the decision process and the actual actions based on the 
         // outcome.  This will make it easier to read the code.
         if (existingMetadata != null) {
            if (existingMetadata.getCategory().getId() != newMetadata.getCategory().getId() && !existingMetadata.isStopGap()) {
               Log.warning(Geonet.METADATA_ALIGNER, "Unmatching category for an existing metadata "
                     + newMetadata.getUrn() + ": " + existingMetadata.getCategory().getName() + " (local) / "
                     + newMetadata.getCategory().getName() + " (target)");
               // do not update the existing category
               newMetadata.setCategory(existingMetadata.getCategory());
               // FIXME Raise an alarm
            }
            //Metadata exists in OpenWIS. Get localDateStamp to test if it needs to be updated.
            ISODate localDateStamp = dateCollect.apply(existingMetadata);

            //Check localDateStamp against imported metadata local date stamped.
            ISODate changeDate = new ISODate(newMetadata.getChangeDate());
            
            if (existingMetadata.isStopGap()) {
               Log.info(Geonet.METADATA_ALIGNER, "Stop-Gap Metadata " + newMetadata.getUrn()
                     + " will be updated, and moved to category " + newMetadata.getCategory().getName());
               newMetadata.setSchema(schema);
               newMetadata.setCreateDate(newMetadata.getChangeDate().toString());
               newMetadata.setId(existingMetadata.getId());
               updateMetadata(newMetadata);
            } else {
               // Previously, meta-data was only updated if the change date of the newer meta-data is later than the
               // change-date of the older meta-data.  Now, meta-data is updated regardless.
               newMetadata.setSchema(schema);
               newMetadata.setId(existingMetadata.getId());               
               updateMetadata(newMetadata);               
            }
//            } else if (changeDate.sub(localDateStamp) > 0) {
//               //Metadata needs to be updated.
//               newMetadata.setId(existingMetadata.getId());
//               newMetadata.setSchema(schema);
//               updateMetadata(newMetadata);
//            } else {
//               Log.info(Geonet.METADATA_ALIGNER, "Metadata XML not changed: " + newMetadata.getUrn());
//               result.incUnchanged();
//            }
         } else {
            // Fill the metadata object to insert.
            newMetadata.setSchema(schema);
            newMetadata.setCreateDate(newMetadata.getChangeDate().toString());
            createMetadata(newMetadata);
         }
      } catch (Exception e) {
         getDbms().abort();
         Log.warning(Geonet.METADATA_ALIGNER, "Metadata skipped with URN : " + newMetadata.getUrn()
               + " ### error is :" + e.getMessage(), e);
         result.getErrors().add(new MetadataAlignerError(newMetadata.getUrn(), e.getMessage()));
         result.incUnexpected();
      }
   }

   /**
    * Updates a metadata.
    *
    * @param md the metadata to update.
    * @throws Exception if an error occurs.
    */
   private void updateMetadata(Metadata md) throws Exception {
      long before, after;
      Log.info(Geonet.METADATA_ALIGNER, "Updating local metadata for identifier: " + md.getUrn());

      before = System.currentTimeMillis();
      //-- Extract Metadata Product
      ProductMetadata pm = productMetadataManager.extract(md, true);

      if (!ensureProductMetadataValid(pm, md)) {
         return;
      }

      productMetadataManager.saveOrUpdate(pm);

      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#updateMetadata",
               "Updating PM in DataService.", after - before);
      }

      //Update in DB.
      before = System.currentTimeMillis();
      XmlSerializer.updateMetadata(getDbms(), md);
      getDbms().commit();

      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#updateMetadata",
               "Updating Metadata in portal DB.", after - before);
      }

      //Handle metadata resources.
      handleMetadataResources(md);

      //Handle Feature catalog.
      handleMetadataRelations(md);

      // Adding to the indexable elements.
      indexableElements.add(new DbmsIndexableElement(getDbms(), md.getUrn(), pm));

      //Inc Updated.
      result.incUpdated();   
      result.getUrnUpdated().add(md.getUrn());

      // Increment the volume of processed metadata
      result.incVolume(Xml.getString(md.getData()).length());
   }

   /**
    * Creates a metadata.
    * @param md the metadata to create.
    * @throws Exception if an error occurs.
    */
   @Override
   public void createMetadata(Metadata md) throws Exception {
      long before, after;

      Log.info(Geonet.METADATA_ALIGNER, "Creating local metadata for identifier: " + md.getUrn());

      before = System.currentTimeMillis();
      dataManager.setNamespacePrefixUsingSchemas(md.getData(), md.getSchema());
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#createMetadata",
               "dataManager.setNamespacePrefixUsingSchemas", after - before);
      }

      before = System.currentTimeMillis();
      ProductMetadata pm;
      if (md.isStopGap()) {
         pm = productMetadataManager.extract(md, true);
      } else {
         pm = productMetadataManager.extract(md, false);
      }
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#createMetadata",
               "Extract info using XPath", after - before);
      }

      before = System.currentTimeMillis();

      if (!ensureProductMetadataValid(pm, md)) {
         return;
      }
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#createMetadata", "Get Data policy.",
               after - before);
      }

      //Create the metadata in the DS.
      before = System.currentTimeMillis();
      productMetadataManager.saveOrUpdate(pm);
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#createMetadata",
               "Creating PM in DataService", after - before);
      }

      // Ensure deleted metadata table is clean
      before = System.currentTimeMillis();
      deletedMetadataManager.clean(md.getUrn(), md.getCategory().getId());
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("MetadataAligner", "MetadataAligner#createMetadata",
               "Cleaning Delete metadata table.", after - before);
      }

      //--- Create metadata in system.
      try {
         before = System.currentTimeMillis();
         Integer id = XmlSerializer.insertMetadata(getDbms(), md);
         getDbms().commit();
         md.setId(id);
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("MetadataAligner", "MetadataAligner#createMetadata",
                  "Creating Metadata in Portal DB.", after - before);
         }
      } catch (SQLException e) {
         // Compensation mechanisms to clean product metadata
         Log.error(Geonet.METADATA_ALIGNER, "Error while inserting metadata: " + e.getMessage());
         Log.info(Geonet.METADATA_ALIGNER, "Cleaning associated product metadata");
         productMetadataManager.delete(pm.getUrn());
         throw e;
      }

      //Handle metadata resources.
      handleMetadataResources(md);

      //Handle Feature catalog.
      handleMetadataRelations(md);

      // Adding to the indexable elements.
      indexableElements.add(new DbmsIndexableElement(getDbms(), md.getUrn(), pm));

      //Result added.
      result.incAdded();
      result.getUrnAdded().add(md.getUrn());

      // Increment the volume of processed metadata
      result.incVolume(Xml.getString(md.getData()).length());
   }

   /**
    * Handle related metadata (feature catalog).
    * It is assumed that the related metadata are NEW in the system (e.g. URN of related must not exist).
    * @param metadata the metadata.
    * @throws Exception if an error occurs.
    */
   private void handleMetadataRelations(Metadata metadata) throws Exception {
      if (!metadata.getRelatedMetadatas().isEmpty()) {
         for (Metadata related : metadata.getRelatedMetadatas().keySet()) {
            createMetadata(related);
            result.setAdded(result.getAdded() - 1);
            if (Boolean.TRUE.equals(metadata.getRelatedMetadatas().get(related))) {
               String query = "INSERT INTO Relations (id, relatedId) VALUES (?, ?)";
               getDbms().execute(query, metadata.getId(), related.getId());
            }
         }
      }
   }

   /**
    * Handle the metadata resources.
    * @param md the metadata.
    * @throws Exception if an error occurs.
    */
   private void handleMetadataResources(Metadata md) throws Exception {
      //If there is private documents, copy it.
      if (CollectionUtils.isNotEmpty(md.getPrivateDocs())) {
         String dir = Lib.resource.getDir(dataDir, "private", md.getId().toString());
         new File(dir).mkdirs();

         for (MetadataResource resource : md.getPrivateDocs()) {
            File outFile = new File(dir, resource.getName());
            FileOutputStream os = new FileOutputStream(outFile);
            try {
               os.write(resource.getData());
            } finally {
               os.close();
            }
            outFile.setLastModified(resource.getChangeDate());
         }
      }

      //If there is public documents, copy it.
      if (CollectionUtils.isNotEmpty(md.getPublicDocs())) {
         String dir = Lib.resource.getDir(dataDir, "public", md.getId().toString());
         new File(dir).mkdirs();

         for (MetadataResource resource : md.getPublicDocs()) {
            File outFile = new File(dir, resource.getName());
            FileOutputStream os = new FileOutputStream(outFile);
            try {
               os.write(resource.getData());
            } finally {
               os.close();
            }
            outFile.setLastModified(resource.getChangeDate());
         }
      }
   }

   /**
       * Ensure product metadata is valid regarding the extracted info and
       * manage the dp case:locally unknown datapolicy in case of GTS category (WMO Additional)
       * And manage GlobalExchange flag
       */
   private boolean ensureProductMetadataValid(ProductMetadata pm, Metadata md) throws Exception {
      if (!productMetadataManager.isValid(pm)) {
         Log.warning(Geonet.METADATA_ALIGNER,
               "Unable to extract OpenWIS information for metadata: " + md.getUrn());
         result.incBadFormat();
         result.getErrors().add(
               new MetadataAlignerError(md.getUrn(), "Unable to extract OpenWIS information."));
         return false;
      }

      enforceDataPolicy(pm, md, dataPolicyManager);
      enforceGlobalExchange(productMetadataManager, md, pm);
      
      md.setTitle(pm.getTitle());
      return true;
   }
   
   /**
    * Set Data Policy of PM and MD and manage the case of unknown data policy.
    * This method is static to be available from here and from 
    * {@link DataManager#updateMetadata(jeeves.server.UserSession, Dbms, String, Element, boolean, String, String)}
    * 
    * @param pm the {@link ProductMetadata}
    * @param md the {@link Metadata}
    * @param dataPolicyManager the data policy manager
    * @throws Exception if an error occurs
    */
   public static void enforceDataPolicy(ProductMetadata pm, Metadata md, IDataPolicyManager dataPolicyManager) throws Exception {
      // Handle locally unknown data policy:
      // -> in case of GTS Category WMO Additional, the default dp is additional-default
      // -> other case: unknown as data policy
      String defaultDp = IProductMetadataExtractor.UNKNOWN_DATAPOLICY;
      if (Pattern.matches(IProductMetadataExtractor.GTS_CATEGORY_ADDITIONAL, pm.getGtsCategory())) {
         defaultDp = IProductMetadataExtractor.DEFAULT_ADDITIONAL_DATAPOLICY;
      }
      DataPolicy dp = dataPolicyManager.getDataPolicyByNameOrAlias(pm.getDataPolicy(), defaultDp);
      // Warn about unknown data policy
      if (IProductMetadataExtractor.UNKNOWN_DATAPOLICY.equals(dp.getName())) {
         Log.warning(Geonet.METADATA_ALIGNER, "Unknown data policy for metadata " + md.getUrn());
         // FIXME Raise an alarm
      } else if (IProductMetadataExtractor.DEFAULT_ADDITIONAL_DATAPOLICY.equals(defaultDp)) {
         if (!pm.getDataPolicy().equals(dp.getName())) {
            Log.warning(Geonet.METADATA_ALIGNER, "Unknown data policy for WMO Additional metadata "
                  + md.getUrn());
         }
      }
      pm.setDataPolicy(dp.getName());

      // in case of update, check if overridden dp exists
      if (StringUtils.isNotBlank(pm.getOverridenDataPolicy())) {
         if (pm.getDataPolicy().equals(pm.getOverridenDataPolicy())) {
            Log.info(Geonet.METADATA_ALIGNER,
                  "Removing overridden data policy as the same is found in metadata");
            pm.setOverridenDataPolicy(null);
         } else {
            DataPolicy overriddenDp = dataPolicyManager.getDataPolicyByNameOrAlias(
                  pm.getOverridenDataPolicy(), dp.getName());
            Log.info(Geonet.METADATA_ALIGNER,
                  "Data policy is overridden: " + overriddenDp.getName());
            md.setDataPolicy(overriddenDp);
         }
      } else {
         md.setDataPolicy(dp);
      }
   }
   
   public static void enforceGlobalExchange(IProductMetadataManager pmm, Metadata md, ProductMetadata pm) throws Exception {
      if ( pmm.isIsoCoreProfile1_3(md)){
         if (pmm.isGlobalExchange(md)) {
            pm.setGtsCategory(IProductMetadataExtractor.GLOBAL_EXCHANGE);
            md.setGtsCategory(IProductMetadataExtractor.GLOBAL_EXCHANGE);
         }else{
            pm.setGtsCategory(IProductMetadataExtractor.CORE_PROFILE_1_3_NOT_GLOBAL_EXCHANGE);
            md.setGtsCategory(IProductMetadataExtractor.CORE_PROFILE_1_3_NOT_GLOBAL_EXCHANGE);
            
         }
      }
   }

   /**
    * Apply the XSL Style sheets if applicable, detect and return the schema of the metadata.
    * @param md the metadata.
    * @param predicatedStylesheets the XSL style sheet to apply.
    * @return the schema or <code>null</code> if schema is not retrievable.
    */
   private String getMetadataSchema(Metadata md, List<PredicatedStylesheet> predicatedStylesheets) {

      //Apply style sheet if predicate matches.
      for (PredicatedStylesheet predicatedStylesheet : predicatedStylesheets) {
         if (predicatedStylesheet.getPredicate().apply(md.getData())) {
            try {
               md.setData(Xml.transform(md.getData(), predicatedStylesheet.getStylesheet()));
            } catch (Exception e) {
               Log.warning(Geonet.METADATA_ALIGNER, "Cannot convert md: " + e.getMessage());
               result.incUnexpected();
               return null;
            }
         }
      }

      //Auto detect schema.
      String schema = dataManager.autodetectSchema(md.getData());

      if (schema == null) {
         Log.warning(Geonet.METADATA_ALIGNER, "Skipping metadata with unknown schema.");
         result.incUnknownSchema();
         return null;
      }

      return schema;
   }

   /**
    * Extracts the URN and the date stamp from the metadata data (XML).  This runs the metadata through an XSLT stylesheet
    * which is expected to return the following XML:
    * 
    *    <importInfo>
    *       <uuid>{metadataUuid}</uuid>
    *       <dateStamp>{metadata datestamp}</dateStamp>
    *    </importInfo>
    * 
    * @param md the metadata.
    * @param schema the schema of the metadata.
    * @return a pair containing the trimmed UUID from the metadata data, and the change datestamp.
    * @throws Exception if an error occurs.
    */
   private Pair<String, ISODate> extractMetadataImportInfo(Metadata md, String schema) throws Exception {
      String styleSheet = dataManager.getSchemaDir(schema) + EXTRACT_IMPORT_INFO;
      Element elt = Xml.transform(md.getData(), styleSheet);
      String uuid = elt.getChildText("uuid");
      String trimmedUuid = StringUtils.trimToEmpty(uuid);
      ISODate isoDate = null;
      String dateStamp = elt.getChildText("dateStamp");
      if (StringUtils.isNotBlank(dateStamp) && StringUtils.isNotBlank(trimmedUuid)) {
         // Check that the trimmed UUID is different that the UUID read from the metadata.  Raise a warning if it is.
         if (! trimmedUuid.equals(uuid)) {
            Log.warning(Geonet.METADATA_ALIGNER, "UUID [" + trimmedUuid + "] will be trimmed as it contains leading/trailing whitespace.");
         }
         isoDate = new ISODate(dateStamp);
         return Pair.read(trimmedUuid, isoDate);
      } else {
         result.getErrors().add(
               new MetadataAlignerError(trimmedUuid,
                     "Unable to extract an URN or a datestamp from the metadata record."));
         result.incUnexpected();
         return null;
      }
   }

}
