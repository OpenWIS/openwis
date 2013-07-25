/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.csw;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jeeves.exceptions.BadParameterEx;
import jeeves.exceptions.OperationAbortedEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;
import jeeves.utils.XmlRequest;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.ConstraintLanguage;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.CswOperation;
import org.fao.geonet.csw.common.CswServer;
import org.fao.geonet.csw.common.ElementSetName;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.csw.common.TypeName;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.csw.common.requests.CatalogRequest;
import org.fao.geonet.csw.common.requests.GetRecordByIdRequest;
import org.fao.geonet.csw.common.requests.GetRecordsRequest;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.lib.Lib;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
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
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CSWHarvester extends AbstractHarvester {

   public static final String PREFERRED_HTTP_METHOD = CatalogRequest.Method.GET.toString();

   private static int GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE = 20;

   private static String CONSTRAINT_LANGUAGE_VERSION = "1.1.0";

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
    * Builds a CSWHarvester.
    * @param context
    * @param dbms
    */
   public CSWHarvester(ServiceContext context, Dbms dbms) {
      super(context, dbms);
   }

   /**
    * Gets the type of the harvester.
    * @return the type of the harvester.
    */
   public static String getType() {
      return "csw";
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
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.harvest.AbstractHarvester#harvest(org.openwis.metadataportal.model.harvest.HarvestingTask)
    */
   @Override
   public MetadataAlignerResult harvest(HarvestingTask task) throws Exception {

      this.task = task;

      GeonetContext gc = ((GeonetContext) getContext().getHandlerContext(Geonet.CONTEXT_NAME));
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);

      this.metadataAligner = new MetadataAligner(getDbms(), gc.getDataManager(),
            gc.getSearchmanager(), dataDir);
      HarvestingTaskManager htm = new HarvestingTaskManager(getDbms());

      CswServer server = retrieveCapabilities();

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

      //--- perform all searches

      Element searchParams = createSearchParams();
      Set<RecordInfo> recordsInfo = search(server, searchParams, source, predicatedStylesheets, htm);
      
      GetRecordByIdRequest requestById = new GetRecordByIdRequest(getContext());
      String capabUrl = this.task.getConfiguration().get("serviceURL");
      requestById.setUrl(new URL(capabUrl));
      requestById.setElementSetName(ElementSetName.FULL);
      DataManager dataMan = gc.getDataManager();

      List<Metadata> mds = new ArrayList<Metadata>();
      int i = 0;
      for (RecordInfo recordInfo : recordsInfo) {
         Element md = retrieveMetadata(requestById, recordInfo.uuid);
         if (md != null) {
            String schema = dataMan.autodetectSchema(md);
            if (schema != null) {
               Metadata metadata = new Metadata();
               metadata.setUrn(recordInfo.uuid);
               metadata.setSchema(schema);
               metadata.setChangeDate(recordInfo.changeDate);
               metadata.setSource(source);
               metadata.setCategory(this.task.getCategory());
               metadata.setData(md);
               mds.add(metadata);

               processedMetadatas.add(new Metadata(recordInfo.uuid));
               
               if (i % BATCH_SIZE == 0 || i == recordsInfo.size() - 1) {
                  //Batch of import.
                  this.metadataAligner.importMetadatas(mds, this.task.getValidationMode(),
                        predicatedStylesheets, new ChangeDateCollector());
                  //Index all.
                  this.metadataAligner.indexImportedMetadatas();

                  this.processed = this.metadataAligner.getResult().getTotal();
                  mds.clear();
               }
            }
         }
         i++;
      }
      if (mds.size() == 0) {
         metadataAligner.getResult().setDate(new Date());
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
    * Does CSW GetRecordById request.
    * @param uuid
    * @return
    */
   @SuppressWarnings("unchecked")
   private Element retrieveMetadata(GetRecordByIdRequest requestById, String uuid) {
      requestById.clearIds();
      requestById.addId(uuid);

      try {
         Log.debug(Geonet.CSW, "Getting record from : " + requestById.getHost() + " (uuid:" + uuid
               + ")");
         Element response = requestById.execute();
         Log.debug(Geonet.CSW, "Record got:\n" + Xml.getString(response));

         List<Element> list = response.getChildren();

         //--- maybe the metadata has been removed

         if (list.size() == 0)
            return null;

         response = list.get(0);

         return (Element) response.detach();
      } catch (Exception e) {
         Log.warning(Geonet.CSW, "Raised exception while getting record : " + e);
         //         result.unretrievable++;

         //--- we don't raise any exception here. Just try to go on
         return null;
      }
   }

   /**
    * Does CSW GetCapabilities request
    * and check that operations needed for harvesting
    * (ie. GetRecords and GetRecordById)
    * are available in remote node.
    */
   private CswServer retrieveCapabilities() throws Exception {
      String capabUrl = this.task.getConfiguration().get("serviceURL");

      //Create XML request. 
      if (!Lib.net.isUrlValid(capabUrl))
         throw new BadParameterEx("Capabilities URL", capabUrl);

      URL url = new URL(
            task.getConfiguration().get("serviceURL")
                  + "?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application%2Fxml");
      XmlRequest req = new XmlRequest(url);

      //Setup proxy if needed.
      Lib.net.setupProxy(getContext(), req);

      //If user and password specified, log the user on the remote site.
      if (StringUtils.isNotBlank(task.getConfiguration().get("userName"))
            && StringUtils.isNotBlank(task.getConfiguration().get("password"))) {
         req.setCredentials(task.getConfiguration().get("userName"),
               task.getConfiguration().get("password"));
      }

      Element capabil = req.execute();

      if (capabil.getName().equals("ExceptionReport"))
         CatalogException.unmarshal(capabil);

      CswServer server = new CswServer(capabil);

      if (!checkOperation(server, "GetRecords"))
         throw new OperationAbortedEx("GetRecords operation not found");

      if (!checkOperation(server, "GetRecordById"))
         throw new OperationAbortedEx("GetRecordById operation not found");

      return server;
   }

   private boolean checkOperation(CswServer server, String name) {
      CswOperation oper = server.getOperation(name);

      if (oper == null) {
         Log.warning(Geonet.CSW, "Operation not present in capabilities : " + name);
         return false;
      }

      if (oper.getUrl == null && oper.postUrl == null) {
         Log.warning(Geonet.CSW, "Operation has no GET and POST bindings : " + name);
         return false;
      }

      return true;
   }

   /**
    * Does CSW GetRecordsRequest.
    */
   private Set<RecordInfo> search(CswServer server, Element s, HarvestingSource source,
         List<PredicatedStylesheet> predicatedStylesheets, HarvestingTaskManager htm)
         throws Exception {
      int start = 1;

      GetRecordsRequest request = new GetRecordsRequest(getContext());

      request.setResultType(ResultType.RESULTS);
      //request.setOutputSchema(OutputSchema.OGC_CORE);  // Use default value
      request.setElementSetName(ElementSetName.SUMMARY);
      request.setMaxRecords(GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE + "");

      CswOperation oper = server.getOperation(CswServer.GET_RECORDS);

      // Use the preferred HTTP method and check one exist.
      if (oper.getUrl != null && PREFERRED_HTTP_METHOD.equals("GET")) {
         request.setUrl(oper.getUrl);
         request.setServerVersion(server.getPreferredServerVersion());
         request.setOutputSchema(oper.preferredOutputSchema);
         request.setConstraintLanguage(ConstraintLanguage.CQL);
         request.setConstraintLangVersion(CONSTRAINT_LANGUAGE_VERSION);
         request.setConstraint(getCqlConstraint(s));
         request.setMethod(CatalogRequest.Method.GET);
         for (String typeName : oper.typeNamesList) {
            request.addTypeName(TypeName.getTypeName(typeName));
         }
         request.setOutputFormat(oper.preferredOutputFormat);

      } else if (oper.postUrl != null && PREFERRED_HTTP_METHOD.equals("POST")) {
         request.setUrl(oper.postUrl);
         request.setServerVersion(server.getPreferredServerVersion());
         request.setOutputSchema(oper.preferredOutputSchema);
         request.setConstraintLanguage(ConstraintLanguage.FILTER);
         request.setConstraintLangVersion(CONSTRAINT_LANGUAGE_VERSION);
         request.setConstraint(getFilterConstraint(s));
         request.setMethod(CatalogRequest.Method.POST);
         for (String typeName : oper.typeNamesList) {
            request.addTypeName(TypeName.getTypeName(typeName));
         }
         request.setOutputFormat(oper.preferredOutputFormat);

      } else {
         if (oper.getUrl != null) {
            request.setUrl(oper.getUrl);
            request.setServerVersion(server.getPreferredServerVersion());
            request.setOutputSchema(oper.preferredOutputSchema);
            request.setConstraintLanguage(ConstraintLanguage.CQL);
            request.setConstraintLangVersion(CONSTRAINT_LANGUAGE_VERSION);
            request.setConstraint(getCqlConstraint(s));
            request.setMethod(CatalogRequest.Method.GET);
            for (String typeName : oper.typeNamesList) {
               request.addTypeName(TypeName.getTypeName(typeName));
            }
            request.setOutputFormat(oper.preferredOutputFormat);

         } else if (oper.postUrl != null) {
            request.setUrl(oper.postUrl);
            request.setServerVersion(server.getPreferredServerVersion());
            request.setOutputSchema(oper.preferredOutputSchema);
            request.setConstraintLanguage(ConstraintLanguage.FILTER);
            request.setConstraintLangVersion(CONSTRAINT_LANGUAGE_VERSION);
            request.setConstraint(getFilterConstraint(s));
            request.setMethod(CatalogRequest.Method.POST);

            for (String typeName : oper.typeNamesList) {
               request.addTypeName(TypeName.getTypeName(typeName));
            }
            request.setOutputFormat(oper.preferredOutputFormat);

         } else {
            throw new OperationAbortedEx("No GET or POST DCP available in this service.");
         }
      }

      //If user and password specified, log the user on the remote site.
      if (StringUtils.isNotBlank(task.getConfiguration().get("userName"))
            && StringUtils.isNotBlank(task.getConfiguration().get("password"))) {
         request.setCredentials(task.getConfiguration().get("userName"), task.getConfiguration()
               .get("password"));

      }

      Set<RecordInfo> records = new HashSet<RecordInfo>();

      while (true) {
         request.setStartPosition(start + "");
         Element response = doSearch(request, start, GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE);
         Log.debug(Geonet.CSW, "Number of child elements in response: "
               + response.getChildren().size());

         Element searchResult = response.getChild("SearchResults", Csw.NAMESPACE_CSW);
         // heikki: some providers forget to update their CSW namespace to the CSW 2.0.2 specification
         if (searchResult == null) {
            // in that case, try to accommodate them anyway:
            searchResult = response.getChild("SearchResults", Csw.NAMESPACE_CSW_OLD);
            if (searchResult == null) {
               throw new OperationAbortedEx("Missing 'SearchResults'", response);
            } else {
               Log.warning(Geonet.CSW, "Received GetRecords response with incorrect namespace: "
                     + Csw.NAMESPACE_CSW_OLD);
            }
         }
         List<Element> list = searchResult.getChildren();
         int counter = 0;

         for (Element record : list) {
            RecordInfo recInfo = getRecordInfo(record);

            if (recInfo != null)
               records.add(recInfo);

            counter++;
         }

         //--- check to see if we have to perform other searches

         int recCount = getRecordCount(searchResult);

         Log.debug(Geonet.CSW, "Records declared in response : " + recCount);
         Log.debug(Geonet.CSW, "Records found in response    : " + counter);

         if (start + GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE > recCount)
            break;

         start += GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE;
      }

      Log.info(Geonet.CSW, "Records added to result list : " + records.size());

      return records;
   }

   private int getRecordCount(Element results) throws OperationAbortedEx {
      String numRec = results.getAttributeValue("numberOfRecordsMatched");

      if (numRec == null)
         throw new OperationAbortedEx("Missing 'numberOfRecordsMatched' in 'SearchResults'");

      if (!Lib.type.isInteger(numRec))
         throw new OperationAbortedEx("Bad value for 'numberOfRecordsMatched'", numRec);

      return Integer.parseInt(numRec);
   }

   private RecordInfo getRecordInfo(Element record) {
      String name = record.getName();
      Log.debug(Geonet.CSW, "getRecordInfo (name): " + name);

      // Summary or Full
      // Note: Summary is requested, but some servers return full response.
      // As identifier and modified values are in full response it's ok
      if ((name.equals("SummaryRecord") || (name.equals("Record")))) {
         Namespace dc = Namespace.getNamespace("http://purl.org/dc/elements/1.1/");
         Namespace dct = Namespace.getNamespace("http://purl.org/dc/terms/");

         String identif = record.getChildText("identifier", dc);
         String modified = record.getChildText("modified", dct);

         if (identif == null) {
            Log.warning(Geonet.CSW, "Skipped record with no 'dc:identifier' element : " + name);
            return null;
         }

         return new RecordInfo(identif, modified);

         //log.warning("Skipped record not in 'SummaryRecord' format : "+ name);
         //return null;

         // Full record
      } else if (name.equals("MD_Metadata")) {
         try {
            XPath xpath = XPath.newInstance("gmd:fileIdentifier/gco:CharacterString");
            Element identif = (Element) xpath.selectSingleNode(record);

            if (identif == null) {
               Log.warning(Geonet.CSW, "Skipped record with no 'gmd:fileIdentifier' element : "
                     + name);
               return null;
            }

            xpath = XPath.newInstance("gmd:dateStamp/gco:DateTime");
            Element modified = (Element) xpath.selectSingleNode(record);
            if (modified == null) {
               xpath = XPath.newInstance("gmd:dateStamp/gco:Date");
               modified = (Element) xpath.selectSingleNode(record);
            }
            Log.debug(Geonet.CSW,
                  "Record info: " + identif + ", "
                        + ((modified != null) ? modified.getText() : null));

            return new RecordInfo(identif.getText(), (modified != null) ? modified.getText() : null);

         } catch (Exception e) {
            Log.warning(Geonet.CSW, "Error parsing metadata: " + e);
            return null;
         }

      } else {
         Log.warning(Geonet.CSW, "Skipped record not in supported format : " + name);
         return null;
      }
   }

   private Element doSearch(CatalogRequest request, int start, int max) throws Exception {
      try {
         Log.info(Geonet.CSW, "Searching on :  (" + start + ".." + (start + max) + ")");
         Element response = request.execute();
         Log.debug(Geonet.CSW, "Search results:\n" + Xml.getString(response));

         return response;
      } catch (Exception e) {
         Log.warning(Geonet.CSW, "Raised exception when searching : " + e);
         throw new OperationAbortedEx("Raised exception when searching", e);
      }
   }

   private String getCqlConstraint(Element s) {
      //--- collect queriables

      ArrayList<String> queryables = new ArrayList<String>();

      if (StringUtils.isNotBlank(this.task.getConfiguration().get("any"))) {
         buildCqlQueryable(queryables, "csw:AnyText", this.task.getConfiguration().get("any"));
      }

      if (StringUtils.isNotBlank(this.task.getConfiguration().get("title"))) {
         buildCqlQueryable(queryables, "dc:title", this.task.getConfiguration().get("title"));
      }

      if (StringUtils.isNotBlank(this.task.getConfiguration().get("abstract"))) {
         buildCqlQueryable(queryables, "dct:abstract", this.task.getConfiguration().get("abstract"));
      }

      if (StringUtils.isNotBlank(this.task.getConfiguration().get("subject"))) {
         buildCqlQueryable(queryables, "dc:subject", this.task.getConfiguration().get("subject"));
      }

      //--- build CQL query

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < queryables.size(); i++) {
         sb.append(queryables.get(i));

         if (i < queryables.size() - 1)
            sb.append(" AND ");
      }

      return (queryables.size() == 0) ? null : sb.toString();
   }

   /**
    * Build CQL from user entry. If parameter value
    * contains '%', then the like operator is used.
    */
   private void buildCqlQueryable(List<String> queryables, String name, String value) {
      if (value.length() != 0)
         if (value.contains("%"))
            queryables.add(name + " like '" + value + "'");
         else
            queryables.add(name + " = '" + value + "'");
   }

   private String getFilterConstraint(Element s) {
      //--- collect queriables

      ArrayList<Element> queriables = new ArrayList<Element>();

      // old GeoNetwork node does not understand AnyText (csw:AnyText instead).
      if (StringUtils.isNotBlank(this.task.getConfiguration().get("any"))) {
         buildFilterQueryable(queriables, "csw:AnyText", this.task.getConfiguration().get("any"));
      }
      if (StringUtils.isNotBlank(this.task.getConfiguration().get("title"))) {
         buildFilterQueryable(queriables, "dc:title", this.task.getConfiguration().get("title"));
      }
      if (StringUtils.isNotBlank(this.task.getConfiguration().get("abstract"))) {
         buildFilterQueryable(queriables, "dct:abstract",
               this.task.getConfiguration().get("abstract"));
      }
      if (StringUtils.isNotBlank(this.task.getConfiguration().get("subject"))) {
         buildFilterQueryable(queriables, "dc:subject", this.task.getConfiguration().get("subject"));
      }

      //--- build filter expression

      if (queriables.isEmpty())
         return null;

      Element filter = new Element("Filter", Csw.NAMESPACE_OGC);

      if (queriables.size() == 1)
         filter.addContent(queriables.get(0));
      else {
         Element and = new Element("And", Csw.NAMESPACE_OGC);

         for (Element prop : queriables)
            and.addContent(prop);

         filter.addContent(and);
      }

      return Xml.getString(filter);
   }

   private void buildFilterQueryable(List<Element> queryables, String name, String value) {
      if (value.length() == 0)
         return;

      // add Like operator
      Element prop;

      if (value.contains("%")) {
         prop = new Element("PropertyIsLike", Csw.NAMESPACE_OGC);
         prop.setAttribute("wildcard", "%");
         prop.setAttribute("singleChar", "_");
         prop.setAttribute("escapeChar", "\\");
      } else {
         prop = new Element("PropertyIsEqualTo", Csw.NAMESPACE_OGC);
      }

      Element propName = new Element("PropertyName", Csw.NAMESPACE_OGC);
      Element literal = new Element("Literal", Csw.NAMESPACE_OGC);

      propName.setText(name);
      literal.setText(value);

      prop.addContent(propName);
      prop.addContent(literal);

      queryables.add(prop);
   }

   /**
    * Creates an element to search on the remote site.
    * @return an element to search on the remote site.
    */
   private Element createSearchParams() {
      Element req = new Element("request");
      Lib.element.add(req, "any", this.task.getConfiguration().get("any"));
      Lib.element.add(req, "title", this.task.getConfiguration().get("title"));
      Lib.element.add(req, "abstract", this.task.getConfiguration().get("abstract"));
      Lib.element.add(req, "subject", this.task.getConfiguration().get("subject"));
      return req;
   }

}
