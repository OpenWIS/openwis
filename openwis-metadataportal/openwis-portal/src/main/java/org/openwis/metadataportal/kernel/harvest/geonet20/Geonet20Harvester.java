/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.geonet20;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jeeves.exceptions.UserNotFoundEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.XmlRequest;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.lib.Lib;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.HarvestingSource;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Geonet20Harvester extends AbstractHarvester {

   private static final int BATCH_SIZE = 10;

   /**
    * The task harvested.
    */
   private HarvestingTask task;

   /**
    * The metadata aligner.
    */
   private IMetadataAligner metadataAligner;

   /**
    * The number of processed elements.
    */
   private int processed = 0;

   /**
    * The number of elements to be processed.
    */
   private int total = 0;

   /**
    * The processed metadatas.
    */
   private Set<Metadata> processedMetadatas = new HashSet<Metadata>();

   /**
    * Default constructor.
    * Builds a Geonet20Harvester.
    * @param context
    * @param dbms
    */
   public Geonet20Harvester(ServiceContext context, Dbms dbms) {
      super(context, dbms);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.harvest.AbstractHarvester#harvest(org.openwis.metadataportal.model.harvest.HarvestingTask)
    */
   @SuppressWarnings("unchecked")
   @Override
   public MetadataAlignerResult harvest(HarvestingTask task) throws Exception {
      this.task = task;

      GeonetContext gc = ((GeonetContext) getContext().getHandlerContext(Geonet.CONTEXT_NAME));
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);

      this.metadataAligner = new MetadataAligner(getDbms(), gc.getDataManager(),
            gc.getSearchmanager(), dataDir);
      HarvestingTaskManager htm = new HarvestingTaskManager(getDbms());

      //Create XML request.
      XmlRequest req = new XmlRequest(task.getConfiguration().get("host"), new Integer(task
            .getConfiguration().get("port")));

      //Setup proxy if needed.
      Lib.net.setupProxy(getContext(), req);

      //If user and password specified, log the user on the remote site.
      if (StringUtils.isNotBlank(task.getConfiguration().get("userName"))
            && StringUtils.isNotBlank(task.getConfiguration().get("password"))) {
         req.setAddress(buildRequestUrl(Geonet.Service.XML_LOGIN));
         req.addParam("username", task.getConfiguration().get("userName"));
         req.addParam("password", task.getConfiguration().get("password"));

         Element response = req.execute();

         if (!response.getName().equals("ok")) {
            throw new UserNotFoundEx(task.getConfiguration().get("userName"));
         }
      }

      //Predicated style sheets.
      List<PredicatedStylesheet> predicatedStylesheets = new ArrayList<PredicatedStylesheet>();
      String styleSheet = task.getConfiguration().get("styleSheet");
      if (StringUtils.isNotBlank(styleSheet)) {
         PredicatedStylesheet ps = new PredicatedStylesheet(styleSheet,
               Predicates.<Element> alwaysTrue());
         predicatedStylesheets.add(ps);
      }
      
      HarvestingSource source = new HarvestingSource(this.task);

      //Perform the search on the remote site.
      Element searchParams = createSearchParams();
      req.setAddress(buildRequestUrl(Geonet.Service.XML_SEARCH));
      Element searchResult = req.execute(searchParams);
      List<Element> metadataResultList = searchResult.getChildren("metadata");
      List<Metadata> mds = new ArrayList<Metadata>();

      this.total = metadataResultList.size();

      for (int i = 0; i < metadataResultList.size(); i++) {
         Element metadataResult = metadataResultList.get(i);
         Element info = metadataResult.getChild("info", Edit.NAMESPACE);

         String remoteId = info.getChildText("id");
         String remoteUuid = info.getChildText("uuid");
         String schema = info.getChildText("schema");
         String createDate = info.getChildText("createDate");
         String changeDate = info.getChildText("changeDate");

         processedMetadatas.add(new Metadata(remoteUuid));

         Metadata metadata = new Metadata();
         metadata.setUrn(remoteUuid);
         metadata.setSchema(schema);
         metadata.setCreateDate(createDate);
         metadata.setChangeDate(changeDate);
         metadata.setSource(source);
         metadata.setCategory(this.task.getCategory());

         Element data = fetchMetadata(req, remoteId, remoteUuid);
         if(data == null) {
          //An error occured during retrieving... Skipping the metadata.
            continue;
         }
         metadata.setData(data);

         mds.add(metadata);
         if (i % BATCH_SIZE == 0 || i == metadataResultList.size() - 1) {
            //Batch of import.
            this.metadataAligner.importMetadatas(mds, this.task.getValidationMode(),
                  predicatedStylesheets, new ChangeDateCollector());
            //Index all.
            this.metadataAligner.indexImportedMetadatas();

            this.processed = this.metadataAligner.getResult().getTotal();
            mds.clear();
         }
      }

      //If user and password specified, logout the user on the remote site.
      if (StringUtils.isNotBlank(task.getConfiguration().get("userName"))
            && StringUtils.isNotBlank(task.getConfiguration().get("password"))) {
         req.setAddress(buildRequestUrl(Geonet.Service.XML_LOGOUT));

         req.execute();
      }

      //Delete the metadata not retrieved by the harvesting task.
      Set<Metadata> localMds = Sets.newHashSet(htm.getAllMetadataByHarvestingTask(task.getId()));
      Collection<Metadata> metadataToDelete = Sets.difference(localMds, processedMetadatas);
      total += metadataToDelete.size();

      this.metadataAligner.deleteMetadatas(metadataToDelete);

      processed += this.metadataAligner.getResult().getTotal();

      return this.metadataAligner.getResult();
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
      return "geonetwork20";
   }

   //-------------------------------------------------------------------------------- Private methods.

   /**
    * Creates an element to search on the remote site.
    * @return an element to search on the remote site.
    */
   private Element createSearchParams() {
      Element req = new Element("request");

      Lib.element.add(req, "any", this.task.getConfiguration().get("any"));
      Lib.element.add(req, "title", this.task.getConfiguration().get("title"));
      Lib.element.add(req, "abstract", this.task.getConfiguration().get("abstract"));
      Lib.element.add(req, "themekey", this.task.getConfiguration().get("themekey"));
      Lib.element.add(req, "siteId", this.task.getConfiguration().get("siteId"));

      if (Boolean.valueOf(this.task.getConfiguration().get("digital"))) {
         Lib.element.add(req, "digital", "on");
      }

      if (Boolean.valueOf(this.task.getConfiguration().get("paper"))) {
         Lib.element.add(req, "paper", "on");
      }

      return req;
   }

   /**
    * Creates an element to search on the remote site.
    * @return an element to search on the remote site.
    */
   private String buildRequestUrl(String service) {
      return "/" + this.task.getConfiguration().get("servlet") + "/srv/en/" + service;
   }

   /**
    * Fetch a metadata on a Geonetwork site.
    * @param req the XML request.
    * @param remoteId the remote id of the metadata.
    * @param uuid the UUID of the metadata.
    * @return an element.
    * @throws Exception if an error occurs.
    */
   private Element fetchMetadata(XmlRequest req, String remoteId, String uuid) throws Exception {
      req.setAddress(buildRequestUrl(Geonet.Service.XML_METADATA_GET));
      req.clearParams();
      req.addParam("id", remoteId);

      try {
         Element md = req.execute();
         Element info = md.getChild("info", Edit.NAMESPACE);

         if (info != null)
            info.detach();

         return md;
      } catch (Exception e) {
         Log.warning(Geonet.OAI_HARVESTER, "Raised exception while getting metadata file : " + e);
         this.metadataAligner.getResult().getErrors()
               .add(new MetadataAlignerError(uuid, "Get Record Failed: " + e.getMessage()));
         this.metadataAligner.getResult().incTotal();
         this.metadataAligner.getResult().incUnexpected();

         return null;
      }
   }
}
