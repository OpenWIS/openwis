/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.webdav;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.HarvestingSource;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * WebDav Harvester.
 * 
 */
public class WebDavHarvester extends AbstractHarvester {

   private Set<Metadata> processedMetadatas = new HashSet<Metadata>();

   private IMetadataAligner metadataAligner;

   private HarvestingTask task;

   private int processed = 0;

   private int total = 0;

   private static final int BATCH_SIZE = 10;

   /**
    * Default constructor.
    * Builds a WebDav Harvester.
    * @param context The context
    * @param dbms The dbms
    */
   public WebDavHarvester(ServiceContext context, Dbms dbms) {
      super(context, dbms);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.common.IMonitorable#getProcessed()
    */
   @Override
   public int getProcessed() {
      return processed;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.common.IMonitorable#getTotal()
    */
   @Override
   public int getTotal() {
      return total;
   }

   /**
    * Gets the type of the harvester.
    * @return the type of the harvester.
    */
   public static String getType() {
      return "webdav";
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.harvest.AbstractHarvester#harvest(org.openwis.metadataportal.model.harvest.HarvestingTask)
    */
   @Override
   public MetadataAlignerResult harvest(HarvestingTask task) throws Exception {
      //The task.
      this.task = task;

      //Get the geonetwork context.
      GeonetContext gc = ((GeonetContext) getContext().getHandlerContext(Geonet.CONTEXT_NAME));
      
    //Creating the managers.
      DataManager dataManager = gc.getDataManager();
      ISearchManager searchManager = gc.getSearchmanager();
      SettingManager sm = gc.getSettingManager();
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);

      this.metadataAligner = new MetadataAligner(getDbms(), dataManager, searchManager, dataDir);
      
      //Predicated style sheets.
      List<PredicatedStylesheet> predicatedStylesheets = new ArrayList<PredicatedStylesheet>();
      String styleSheet = task.getConfiguration().get("styleSheet");
      if (StringUtils.isNotBlank(styleSheet)) {
         PredicatedStylesheet ps = new PredicatedStylesheet(styleSheet,
               Predicates.<Element> alwaysTrue());
         predicatedStylesheets.add(ps);
      }
      
      HarvestingSource source = new HarvestingSource(this.task);
      HarvestingTaskManager htm = new HarvestingTaskManager(getDbms());
      
      String webDavName = task.getConfiguration().get("name");
      String webDavURL = task.getConfiguration().get("dir");
      String username = task.getConfiguration().get("userName");
      String pswd = task.getConfiguration().get("password");
      boolean validate = Boolean.valueOf(task.getConfiguration().get("validate"));
      boolean recurse = Boolean.valueOf(task.getConfiguration().get("recursive"));
      
      Log.debug(Geonet.WEB_DAV, "Retrieving remote metadata information for : " + webDavName);
      WebDavRetriever wr = new WebDavRetriever();
      List<WebDavRemoteFile> files = wr.retrieve(webDavName, webDavURL, username, pswd, recurse, sm);
      Log.debug(Geonet.WEB_DAV, "Remote files found : " + files.size());
      Log.info(Geonet.WEB_DAV, "Start of alignment for : " + webDavName);
      
      List<Metadata> mds = new ArrayList<Metadata>();
      
      int i = 0;
      for (WebDavRemoteFile webDavRemoteFile : files) {
         Element md = retrieveMetadata(webDavRemoteFile, dataManager, validate);
         if (md != null) {
            Metadata metadata = new Metadata();

            String schema = dataManager.autodetectSchema(md);
            metadata.setSchema(schema);
            
            String styleSheet2 = dataManager.getSchemaDir(schema) + MetadataAligner.EXTRACT_IMPORT_INFO;
            Element elt = Xml.transform(md, styleSheet2);
            String uuid = elt.getChildText("uuid");
            metadata.setUrn(uuid);
            
            metadata.setChangeDate(webDavRemoteFile.getChangeDate());
            metadata.setSource(source);
            metadata.setCategory(this.task.getCategory());
            metadata.setData(md);
            mds.add(metadata);

            processedMetadatas.add(new Metadata(uuid));
            
            if (i % BATCH_SIZE == 0 || i == files.size() - 1) {
               //Batch of import.
               this.metadataAligner.importMetadatas(mds, this.task.getValidationMode(),
                     predicatedStylesheets, new ChangeDateCollector());
               //Index all.
               this.metadataAligner.indexImportedMetadatas();

               this.processed = this.metadataAligner.getResult().getTotal();
               mds.clear();
            }
         }
         i++;
      }

      
      //Delete the metadata not retrieved by the harvesting task.
      Set<Metadata> localMds = Sets.newHashSet(htm.getAllMetadataByHarvestingTask(task.getId()));
      Collection<Metadata> metadataToDelete = Sets.difference(localMds, processedMetadatas);
      total += metadataToDelete.size();

      this.metadataAligner.deleteMetadatas(metadataToDelete);

      processed += this.metadataAligner.getResult().getTotal();

      return this.metadataAligner.getResult();
   }

   private Element retrieveMetadata(WebDavRemoteFile rf, DataManager dataManager, boolean validate) {
      try {
         Log.debug(Geonet.WEB_DAV, "Getting remote file : " + rf.getPath());
         Element md = rf.getMetadata();
         Log.debug(Geonet.WEB_DAV, "Record got:\n" + Xml.getString(md));

         String schema = dataManager.autodetectSchema(md);
         if (schema == null) {
            Log.warning(Geonet.WEB_DAV,
                  "Skipping metadata with unknown schema. Path is : " + rf.getPath());
            //            result.unknownSchema++;
         } else {
            if (!validate || validates(schema, md, dataManager)) {
               return (Element) md.detach();
            }
            Log.warning(Geonet.WEB_DAV,
                  "Skipping metadata that does not validate. Path is : " + rf.getPath());
            //            result.doesNotValidate++;
         }
//         
//         
//         MetadataAlignerXmlFileExtractor metadataAlignerXmlFile = new MetadataAlignerXmlFileExtractor();
//         metadataAlignerXmlFile.extract(rf.)
         
      } catch (JDOMException e) {
         Log.warning(Geonet.WEB_DAV,
               "Skipping metadata with bad XML format. Path is : " + rf.getPath());
         //         result.badFormat++;
      } catch (Exception e) {
         Log.warning(Geonet.WEB_DAV, "Raised exception while getting metadata file : " + e);
         //         result.unretrievable++;
      }
      //--- we don't raise any exception here. Just try to go on
      return null;
   }

   private boolean validates(String schema, Element md, DataManager dataManager) {
      try {
         dataManager.validate(schema, md);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

}
