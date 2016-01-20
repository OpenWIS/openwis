/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.openwis.dataservice.CannotDeleteAllProductMetadataException_Exception;
import org.openwis.dataservice.CannotDeleteProductMetadataException_Exception;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.ProductMetadataService;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor;
import org.openwis.metadataportal.kernel.metadata.product.ProductMetadataExtractorFactory;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.util.DateTimeUtils;

import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class ProductMetadataManager implements IProductMetadataManager {

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IProductMetadataManager#getProductMetadataByUrn(java.lang.String)
    */
   @Override
   public ProductMetadata getProductMetadataByUrn(String urn) {
      return getProductMetadataService().getProductMetadataByUrn(urn);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IProductMetadataManager#saveOrUpdate(org.openwis.dataservice.ProductMetadata)
    */
   @Override
   public void saveOrUpdate(ProductMetadata pm) {
      ProductMetadataService pms = getProductMetadataService();
      pm.setStopGap(Boolean.FALSE);
      if (pm.getId() != null) {
         pms.updateProductMetadata(pm);
      } else {
         pms.createProductMetadata(pm);
      }
   }

   /**
    * {@inheritDoc}
    * @throws CannotDeleteProductMetadataException_Exception
    * @see org.openwis.metadataportal.kernel.metadata.IProductMetadataManager#delete(java.lang.String)
    */
   @Override
   public void delete(String urn) throws CannotDeleteProductMetadataException_Exception {
      ProductMetadataService pms = getProductMetadataService();
      pms.deleteProductMetadataByURN(urn);
   }

   @Override
   public void delete(List<String> urns) throws CannotDeleteAllProductMetadataException_Exception {
      ProductMetadataService pms = getProductMetadataService();
      pms.deleteProductMetadatasWithURN(urns);
   }


   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IProductMetadataManager#isValid(org.openwis.dataservice.ProductMetadata)
    */
   @Override
   public boolean isValid(ProductMetadata pm) {
      // No GTS category extracted.
      if (StringUtils.isBlank(pm.getGtsCategory())) {
         Log.warning(Geonet.EXTRACT_PRODUCT_METADATA,
               "Product metadata is not valid. No GTS category. ");
         return false;
      }
      return true;
   }

   /**
    * Synchronize stop gap metadata.
    *
    * @param lastSynchro the last synchronization date
    * @param dbms the dbms
    * @param dm the data manager
    * @param gc the context
    * @throws Exception the exception
    */
   @Override
   public void synchronizeStopGapMetadata(Date lastSynchro, Dbms dbms, DataManager dm,
         IMetadataManager mdm, GeonetContext gc) throws Exception {
      String since = null;
      if (lastSynchro != null) {
         since = DateTimeUtils.format(lastSynchro);
      }
      Log.debug(Geonet.DATA_MANAGER, "Synchonize StopGap metadata earlier from " + since);
      List<ProductMetadata> lastStopGapMetadata = getProductMetadataService()
            .getLastStopGapMetadata(since);
      if (lastStopGapMetadata == null || lastStopGapMetadata.isEmpty()) {
         Log.debug(Geonet.DATA_MANAGER, "No new StopGap metadata found.");
      } else {
         Log.info(Geonet.DATA_MANAGER,
               MessageFormat.format("Found {0} new StopGap metadata.", lastStopGapMetadata.size()));

         // Service
         CategoryManager catManager = new CategoryManager(dbms);
         IDataPolicyManager dpm = new DataPolicyManager(dbms);
         ITemplateManager tm = new TemplateManager(dbms, dm, gc.getSearchmanager());

         SiteSource source = new SiteSource();
         source.setUserName(gc.getSiteName());
         source.setSourceId(gc.getSiteId());
         source.setSourceName(gc.getSiteName());

         Metadata md;
         Date date;
         Template template = tm.getStopGapTemplate();
         if (template == null) {
            new RuntimeException("Stop-Gap template not found");
         }
         Category category = catManager.getDraftCategory();
         if (category == null) {
            new RuntimeException("Draft category not found");
         }
         DataPolicy dataPolicy = dpm.getAdditionalDefaultDataPolicy();
         for (ProductMetadata pm : lastStopGapMetadata) {
            if (mdm.getMetadataInfoByUrn(pm.getUrn()) == null) {

               // Create StopGap Metadata
               md = buildMetadata(pm);

               date = pm.getCreationDate().toGregorianCalendar().getTime();
               md.setChangeDate(DateTimeUtils.format(date));
               md.setCreateDate(DateTimeUtils.format(date));
               md.setLocalImportDate(DateTimeUtils.format(date));
               md.setCategory(category);
               md.setDataPolicy(dataPolicy);
               md.setSource(source);
               md.setStopGap(true);

               mdm.createMetadataFromTemplate(md, template);
               
               
               Log.info(Geonet.DATA_MANAGER,
                     MessageFormat.format("Importing Stop-Gap metadata {0}", pm.getUrn()));

               String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);
               IMetadataAligner metadataAligner = new MetadataAligner(dbms, gc.getDataManager(),
                     gc.getSearchmanager(), dataDir);
               metadataAligner.importMetadatas(Arrays.asList(md), MetadataValidation.NONE,
                     Lists.<PredicatedStylesheet> newArrayList(), new ChangeDateCollector());

               metadataAligner.indexImportedMetadatas();
            }
         }
      }
   }

   /**
    * Builds the metadata.
    *
    * @param pm the pm
    * @return the metadata
    */
   private Metadata buildMetadata(ProductMetadata pm) {
      Metadata md = new Metadata(pm.getUrn());
      md.setFed(pm.isFed());
      md.setFileExtension(pm.getFileExtension());
      md.setFncPattern(pm.getFncPattern());
      md.setGtsCategory(pm.getGtsCategory());
      md.setIngested(pm.isIngested());
      md.setLocalDataSource(pm.getLocalDataSource());
      md.setOriginator(pm.getOriginator());

      md.setOverridenDataPolicy(pm.getOverridenDataPolicy());
      md.setOverridenFileExtension(pm.getOverridenFileExtension());
      md.setOverridenFncPattern(pm.getOverridenFncPattern());
      md.setOverridenPriority(pm.getOverridenPriority());

      md.setPriority(pm.getPriority());
      md.setProcess(pm.getProcess());

      md.setTitle(pm.getTitle());

      return md;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IProductMetadataManager#extract(org.openwis.metadataportal.model.metadata.Metadata, boolean)
    */
   @Override
   public ProductMetadata extract(Metadata metadata, boolean isExisting) throws Exception {
      //ProductMetadata pmExisting = null;
      // Get the existing product metadata if any
      ProductMetadata pmExisting = getProductMetadataByUrn(metadata.getUrn());
      if (!isExisting && pmExisting != null) {
         // ProductMetadata was not supposed to be found in case of creation (isExisting=false)
         Log.warning(Geonet.DATA_MANAGER,
               "Found unexpected ProductMetata with URN " + metadata.getUrn()
                     + "; will be overwritten");
         pmExisting.setProcess(null);
         pmExisting.setPriority(null);
         pmExisting.setOriginator(null);
      }

      ProductMetadata pm = null;
      if (pmExisting == null) {
         // Initialize a new product metadata
         pm = new ProductMetadata();
         pm.setUrn(metadata.getUrn());

         // Initialize fed and ingested to false
         pm.setFed(false);
         pm.setIngested(false);
      } else {
         //Set the extracted fields to null to ensure that all attributes are well reextracted.
         pm = new ProductMetadata();
         pm.setId(pmExisting.getId());
         pm.setUrn(pmExisting.getUrn());
         pm.setFed(pmExisting.isFed());
         pm.setIngested(pmExisting.isIngested());
         pm.setProcess(pmExisting.getProcess());
         pm.setOverridenDataPolicy(pmExisting.getOverridenDataPolicy());
         pm.setOverridenFileExtension(pmExisting.getOverridenFileExtension());
         pm.setOverridenFncPattern(pmExisting.getOverridenFncPattern());
         pm.setOverridenPriority(pmExisting.getOverridenPriority());
      }

      //Get PM Extractor according to schema.
      IProductMetadataExtractor productMetadataExtractor = ProductMetadataExtractorFactory
            .getProductMetadataExtractor(metadata.getSchema());

      // Extract product metadata information from the metadata
      // Extract the FNC pattern
      pm.setFncPattern(productMetadataExtractor.extractFncPattern(metadata));

      // Extract the originator
      pm.setOriginator(productMetadataExtractor.extractOriginator(metadata));

      // Extract the title
      pm.setTitle(productMetadataExtractor.extractTitle(metadata));

      // Extract the local datasource
      pm.setLocalDataSource(productMetadataExtractor.extractLocalDataSource(metadata));

      // Set the process type
      if (metadata.getSource() != null) {
         pm.setProcess(metadata.getSource().getProcessType().toString());
      }

      // Extract the update frequency
      pm.setUpdateFrequency(productMetadataExtractor.extractUpdateFrequency(metadata));

      // Set the file extension
      pm.setFileExtension(productMetadataExtractor.extractFileExtension(metadata));

      // Extract GTS category, GTS priority, data policy.
      productMetadataExtractor.extractGTSCategoryGTSPriorityAndDataPolicy(metadata, pm);

      // Set default priority if any extracted.
      if (pm.getPriority() == null) {
         pm.setPriority(IProductMetadataExtractor.DEFAULT_PRIORITY);
      }

      // Set default originator if any extracted.
      if (StringUtils.isBlank(pm.getOriginator())) {
         pm.setOriginator(IProductMetadataExtractor.DEFAULT_ORIGINATOR);
      }

      return pm;
   }

   /**
    * Test if the metadata is flagged as GlobalExchange
    * @param metadata the metadata
    * @return <code>true</code> if global exchange is found
    */
   @Override
   public boolean isGlobalExchange(Metadata metadata) throws Exception {
    //Get PM Extractor according to schema.
      IProductMetadataExtractor productMetadataExtractor = ProductMetadataExtractorFactory
            .getProductMetadataExtractor(metadata.getSchema());
      return productMetadataExtractor.isGlobalExchange(metadata);
      
   }
   /**
    * Test if the metadata is iso Core Profile 1.3 or higher
    * @param metadata the metadata
    */
   @Override
   public boolean isIsoCoreProfile1_3(Metadata metadata) throws Exception {
    //Get PM Extractor according to schema.
      IProductMetadataExtractor productMetadataExtractor = ProductMetadataExtractorFactory
            .getProductMetadataExtractor(metadata.getSchema());
      return productMetadataExtractor.isIsoCoreProfile1_3(metadata);
      
   }
   
   /**
    * Gets a productMetadataService.
    * @return a productMetadataService.
    */
   private ProductMetadataService getProductMetadataService() {
      //Persist request calling external EJBs.
      return DataServiceProvider.getProductMetadataService();
   }

}
