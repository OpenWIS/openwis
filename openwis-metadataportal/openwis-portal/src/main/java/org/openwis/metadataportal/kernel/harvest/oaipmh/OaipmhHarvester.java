/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.oaipmh;

import java.io.File;
import java.net.URL;
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
import org.fao.geonet.lib.Lib;
import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.exceptions.NoRecordsMatchException;
import org.fao.oaipmh.requests.GetRecordRequest;
import org.fao.oaipmh.requests.ListIdentifiersRequest;
import org.fao.oaipmh.requests.ListRecordsRequest;
import org.fao.oaipmh.requests.TokenListRequest;
import org.fao.oaipmh.requests.Transport;
import org.fao.oaipmh.responses.GetRecordResponse;
import org.fao.oaipmh.responses.Header;
import org.fao.oaipmh.responses.ListResponse;
import org.fao.oaipmh.responses.Record;
import org.fao.oaipmh.util.ISODate;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.collector.LocalImportDateCollector;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.HarvestingSource;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OaipmhHarvester extends AbstractHarvester {

   private Set<Metadata> processedMetadatas = new HashSet<Metadata>();

   private IMetadataAligner metadataAligner;

   private HarvestingTask task;

   private int processed = 0;

   private int total = 0;

   /**
    * Default constructor.
    * Builds a OaipmhHarvester.
    * @param context
    */
   public OaipmhHarvester(ServiceContext context, Dbms dbms) {
      super(context, dbms);
   }

   /**
    * {@inheritDoc}
    * @throws Exception 
    * @see org.openwis.metadataportal.kernel.harvest.AbstractHarvester#harvest(org.openwis.metadataportal.model.harvest.HarvestingTask)
    */
   @Override
   public MetadataAlignerResult harvest(HarvestingTask task) throws Exception {
      // Initialize dataManager, DBMS and a task for the harvesting
      this.task = task;

      GeonetContext gc = ((GeonetContext) getContext().getHandlerContext(Geonet.CONTEXT_NAME));
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);

      this.metadataAligner = new MetadataAligner(getDbms(), gc.getDataManager(),
            gc.getSearchmanager(), dataDir);

      File schemaFile = new File(getContext().getAppPath() + Geonet.SchemaPath.OAI_PMH);

      TokenListRequest req = null;

      // Incremental harvesting tasks use the ListRecords operation
      // for performance issues (i.e synchronization).
      // If dateFrom is blank, we consider that incremental task should behave as a standard harvesting task (ListIdentifiers).
      // Also, if last run is empty (first run, ListRecords is performed).
      if (task.isIncremental() && StringUtils.isNotBlank(task.getConfiguration().get("dateFrom"))
            || task.getLastRun() == null) {
         req = new ListRecordsRequest();
      } else {
         req = new ListIdentifiersRequest();
      }
      Transport t = req.getTransport();
      req.setSchemaPath(schemaFile);

      t.setUrl(new URL(task.getConfiguration().get("url")));

      if (StringUtils.isNotBlank(task.getConfiguration().get("userName"))
            && StringUtils.isNotBlank(task.getConfiguration().get("password"))) {
         t.setCredentials(task.getConfiguration().get("userName"),
               task.getConfiguration().get("password"));
      }

      //--- set the proxy info if necessary
      Lib.net.setupProxy(getContext(), t);

      // Handle the FROM date parameter
      ISODate dateFrom = null;
      if (StringUtils.isNotBlank(task.getConfiguration().get("dateFrom"))) {
         dateFrom = new ISODate(task.getConfiguration().get("dateFrom"));
      }
      req.setFrom(dateFrom);

      // Handle the UNTIL date parameter
      ISODate dateUntil = null;
      if (StringUtils.isNotBlank(task.getConfiguration().get("dateTo"))) {
         dateUntil = new ISODate(task.getConfiguration().get("dateTo"));
      }
      req.setUntil(dateUntil);

      // Handle SET criteria.
      String set = null;
      if (StringUtils.isNotBlank(task.getConfiguration().get("criteriaSet"))) {
         set = task.getConfiguration().get("criteriaSet");
      }
      req.setSet(set);

      // Handle metadataPrefix (default is oai_dc).
      String metadataPrefix = "oai_dc";
      if (StringUtils.isNotBlank(task.getConfiguration().get("criteriaPrefix"))) {
         metadataPrefix = task.getConfiguration().get("criteriaPrefix");
      }
      req.setMetadataPrefix(metadataPrefix);

      List<Record> records = new ArrayList<Record>();

      // Execute request
      ListResponse response;
      try {
         response = (ListResponse) req.execute();
      } catch (NoRecordsMatchException e) {
         // response null means no result!
         response = null;
         Log.info(Geonet.OAI_HARVESTER,
               "  - No results for harvesting task : " + task.getName() + "(" + task.getId() + ")");
      }

      HarvestingTaskManager htm = new HarvestingTaskManager(getDbms());

      // Update the configuration dateFrom
      if (task.isIncremental()) {
         // FIXME should be last run ? now() - 5 min? ... TBD for synchro
         htm.updateTaskConfiguration(task.getId(), "dateFrom", new ISODate().toString());
      }
      
      //Get the count of elements to be processed.
      if (response != null) {
         if (response.getResumptionToken() != null
               && response.getResumptionToken().getCompleteListSize() != null) {
            this.total = response.getResumptionToken().getCompleteListSize();
         } else {
            this.total = response.getSize();
         }
      } else {
         this.total = 0;
      }

      //Iterating over the pages.
      while (response != null && (response.hasNextItem() || response.hasNextPage())) {
         // Item should be a Record or an Header, according to the ListRequest operation.
         Object item = null;
         if (!response.hasNextItem()) {
            processRecords(req, records);
            item = response.nextPage();
            records.clear();
         } else {
            item = response.nextItem();
         }
         Record rec = null;
         if (item instanceof Header) {
            rec = new Record();
            rec.setHeader((Header) item);
         } else if (item instanceof Record) {
            rec = (Record) item;
         }
         records.add(rec);
      }
      processRecords(req, records);

      if (!task.isIncremental()) {
         Set<Metadata> localMds = Sets.newHashSet(htm.getAllMetadataByHarvestingTask(task
               .getId()));
         Collection<Metadata> metadataToDelete = Sets.difference(localMds, processedMetadatas);
         total += metadataToDelete.size();

         this.metadataAligner.deleteMetadatas(metadataToDelete);

         processed += this.metadataAligner.getResult().getTotal();
      }

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
      return "oaipmh";
   }

   /**
    * Description goes here.
    * @param request 
    * @param records
    * @param harvestingTaskId 
    * @throws Exception 
    */
   private void processRecords(TokenListRequest request, List<Record> records) throws Exception {
      //Builds a source.
      HarvestingSource source = new HarvestingSource(this.task);

      List<Metadata> mds = new ArrayList<Metadata>();
      Collection<String> urnsToDelete = new HashSet<String>();
      for (Record rec : records) {
         processedMetadatas.add(new Metadata(rec.getHeader().getIdentifier()));
         if (rec.getHeader().isDeleted()) {
            urnsToDelete.add(rec.getHeader().getIdentifier());
         } else {
            if (rec.getMetadata() == null) {
               Element data = retrieveMetadata(request, rec.getHeader());
               if (data == null) {
                  //An error occured during retrieving... Skipping the metadata.
                  continue;
               }
               rec.setMetadata(data);
            }
            Metadata m = new Metadata();
            m.setData(rec.getMetadata());
            m.setUrn(rec.getHeader().getIdentifier());
            m.setChangeDate(rec.getHeader().getDateStamp().toString());
            m.setCategory(this.task.getCategory());
            m.setSource(source);
            mds.add(m);
         }
      }

      PredicatedStylesheet oaiToDublinCoreStyleSheet = new PredicatedStylesheet(
            "conversion/oai_dc-to-dublin-core/main.xsl", new Predicate<Element>() {

               @Override
               public boolean apply(Element input) {
                  return input.getName().equals("dc")
                        && input.getNamespace().equals(OaiPmh.Namespaces.OAI_DC);
               }
            });

      this.metadataAligner.importMetadatas(mds, this.task.getValidationMode(),
            Lists.newArrayList(oaiToDublinCoreStyleSheet), new LocalImportDateCollector());
      this.metadataAligner.deleteMetadatasByUrns(urnsToDelete);

      //Index all.
      this.metadataAligner.indexImportedMetadatas();

      processed = this.metadataAligner.getResult().getTotal();
   }

   /**
    * Retrieve metadata with a GetRecord request from the remote provider.
    * @param request
    * @param header
    * @return
    */
   private Element retrieveMetadata(TokenListRequest request, Header header) {
      try {
         Log.info(Geonet.OAI_HARVESTER,
               "  - Getting remote metadata with urn : " + header.getIdentifier());

         GetRecordRequest req = new GetRecordRequest();
         req.setSchemaPath(new File(getContext().getAppPath() + Geonet.SchemaPath.OAI_PMH));
         req.setTransport(request.getTransport());
         req.setIdentifier(header.getIdentifier());
         req.setMetadataPrefix(request.getMetadataPrefix());

         GetRecordResponse res = req.execute();

         Element md = res.getRecord().getMetadata();
         if (Log.isDebug(Geonet.OAI_HARVESTER)) {
            Log.debug(Geonet.OAI_HARVESTER, " Record got:\n" + Xml.getString(md));
         }
         
         return (Element) md.detach();
      } catch (JDOMException e) {
         Log.warning(
               Geonet.OAI_HARVESTER,
               "Skipping metadata with bad XML format. Remote identifier is : "
                     + header.getIdentifier());
         this.metadataAligner
               .getResult()
               .getErrors()
               .add(new MetadataAlignerError(header.getIdentifier(), "Get Record Failed: "
                     + e.getMessage()));
         this.metadataAligner.getResult().incTotal();
         this.metadataAligner.getResult().incUnexpected();
      } catch (Exception e) {
         Log.warning(Geonet.OAI_HARVESTER, "Raised exception while getting metadata file : " + e);
         this.metadataAligner
               .getResult()
               .getErrors()
               .add(new MetadataAlignerError(header.getIdentifier(), "Get Record Failed: "
                     + e.getMessage()));
         this.metadataAligner.getResult().incTotal();
         this.metadataAligner.getResult().incUnexpected();
      }

      // We don't raise any exception here. Just try to go on
      return null;
   }
}
