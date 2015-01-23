package org.openwis.metadataportal.kernel.search;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;
import jeeves.xlink.Processor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.IndexListener;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.fao.geonet.kernel.search.Range;
import org.fao.geonet.kernel.search.SearchManagerFactory;
import org.fao.geonet.kernel.search.SortingInfo;
import org.fao.geonet.kernel.search.SortingInfoImpl;
import org.fao.geonet.kernel.search.TermFrequency;
import org.fao.geonet.kernel.search.UnusedSearcher;
import org.fao.geonet.kernel.search.Z3950Searcher;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.kernel.search.index.IIndexManager;
import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.kernel.search.query.SearchQuery;
import org.openwis.metadataportal.kernel.search.query.SearchQueryFactory;
import org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.services.metadata.dto.MonitorCatalogSearchCriteria;

import com.google.common.collect.Sets;

/**
 * The Class SearchManagerImlp. <P>
 * Explanation goes here. <P>
 */
public class SearchManagerImpl extends AbstractManager implements ISearchManager {

   /** minutes between optimizations of the  index. */
   private int _optimizerInterval;

   /** The _optimizer begin at. */
   private Calendar _optimizerBeginAt;

   /** The _date format. */
   private final SimpleDateFormat _dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

   /** The index. */
   private final IIndexManager index;

   /** The query manager factory. */
   @SuppressWarnings("rawtypes")
   private final SearchQueryManagerFactory queryManagerFactory;

   /** The setting info. */
   private final SettingInfo settingInfo;

   /** The executor service. */
   private final ScheduledExecutorService executorService;

   /** The optimize future. */
   private ScheduledFuture<?> optimizeFuture;

   /** The html cache dir. */
   private final String htmlCacheDir;

   /** The elements to index. */
   private final Set<IndexableElement> elementsToIndex;

   /** The elements to delete. */
   private final Set<IndexableElement> elementsToDelete;

   /**
    * Instantiates a new SearchManagerImpl.
    *
    * @param dbms the dbms
    * @param appPath the application path
    * @param config the config
    * @param si the setting info
    * @throws Exception the exception
    */
   public SearchManagerImpl(Dbms dbms, String appPath, ServiceConfig config, SettingInfo si) throws Exception {
      super(dbms);
      settingInfo = si;
      index = SearchManagerFactory.getIndexManager(config, appPath);

      // Create query manager
      queryManagerFactory = SearchManagerFactory.getQueryManagerFactory(config, appPath);

      // Executor service
      executorService = Executors.newScheduledThreadPool(1); // Only one thread needed

      // Html CacheDir
      htmlCacheDir = config.getMandatoryValue(Geonet.Config.HTMLCACHE_DIR);

      // initialize elements set
      elementsToDelete = new HashSet<IndexableElement>();
      elementsToIndex = new HashSet<IndexableElement>();
   }

   /**
    * Startup.
    *
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#startup()
    */
   @Override
   public void startup() throws Exception {
      initZ3950();

      if (settingInfo.getIndexOptimizerSchedulerEnabled()) {
         _optimizerBeginAt = settingInfo.getIndexOptimizerSchedulerAt();
         _optimizerInterval = settingInfo.getIndexOptimizerSchedulerInterval();
         scheduleOptimizerThread();
      } else {
         Log.info(Geonet.INDEX_ENGINE, "Scheduling thread that optimizes index is disabled");
      }
   }

   /**
    * Initializes the Z3950 client searcher.
    */
   protected void initZ3950() {
      // Nothing to do
   }

   /**
    * Shutdown.
    *
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#shutdown()
    */
   @Override
   public void shutdown() throws Exception {
      endZ3950();
      executorService.shutdown();
   }

   /**
    * end the Z3950 client searcher.
    */
   protected void endZ3950() {
      // Nothing to do
   }

   /**
    * Adds the index listener.
    *
    * @param listener the listener
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#addIndexListener(org.fao.geonet.kernel.search.IndexListener)
    */
   @Override
   public void addIndexListener(IndexListener listener) {
      index.addIndexListener(listener);
   }

   /**
    * Removes the index listener.
    *
    * @param listener the listener
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#removeIndexListener(org.fao.geonet.kernel.search.IndexListener)
    */
   @Override
   public void removeIndexListener(IndexListener listener) {
      index.removeIndexListener(listener);
   }

   /**
    * Disable optimizer.
    *
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#disableOptimizer()
    */
   @Override
   public synchronized void disableOptimizer() throws IndexException {
      Log.info(Geonet.INDEX_ENGINE, "Scheduling thread that optimizes index is disabled");
      if (optimizeFuture != null) {
         optimizeFuture.cancel(false);
         optimizeFuture = null;
      }
   }

   /**
    * Reschedule optimizer.
    *
    * @param optimizerBeginAt the optimizer begin at
    * @param optimizerInterval the optimizer interval
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#rescheduleOptimizer(java.util.Calendar, int)
    */
   @Override
   public synchronized void rescheduleOptimizer(Calendar optimizerBeginAt, int optimizerInterval)
         throws IndexException {
      if (_dateFormat.format(optimizerBeginAt.getTime()).equals(
            _dateFormat.format(_optimizerBeginAt.getTime()))
            && (optimizerInterval == _optimizerInterval))
         return; // do nothing unless at and interval has changed

      _optimizerInterval = optimizerInterval;
      _optimizerBeginAt = optimizerBeginAt;
      if (optimizeFuture != null) {
         disableOptimizer();
      }
      scheduleOptimizerThread();
   }

   /**
    * Schedule optimizer thread.
    *
    * @throws IndexException the index exception
    */
   private void scheduleOptimizerThread() throws IndexException {
      // at _optimizerBeginAt and again at every _optimizerInterval minutes
      Date beginAt = getBeginAt(_optimizerBeginAt);

      long initiaDelay = beginAt.getTime() - Calendar.getInstance().getTimeInMillis();
      optimizeFuture = executorService.scheduleAtFixedRate(new OptimizeTask(index), initiaDelay,
            _optimizerInterval * 60 * 1000, TimeUnit.MILLISECONDS);

      Log.info(
            Geonet.INDEX_ENGINE,
            MessageFormat
                  .format(
                        "Scheduling thread that optimizes index to run at {0} and every {1} minutes afterwards",
                        _dateFormat.format(beginAt), _optimizerInterval));
   }

   /**
    * Gets the begin at.
    *
    * @param timeToStart the time to start
    * @return the begin at
    */
   private Date getBeginAt(Calendar timeToStart) {
      Calendar now = Calendar.getInstance();
      Calendar ts = Calendar.getInstance();

      ts.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
      ts.set(Calendar.MONTH, now.get(Calendar.MONTH));
      ts.set(Calendar.YEAR, now.get(Calendar.YEAR));

      ts.set(Calendar.HOUR, timeToStart.get(Calendar.HOUR));
      ts.set(Calendar.MINUTE, timeToStart.get(Calendar.MINUTE));
      ts.set(Calendar.SECOND, timeToStart.get(Calendar.SECOND));

      // if the start time has already past then schedule for tomorrow
      if (now.after(ts))
         ts.add(Calendar.DAY_OF_MONTH, 1);

      return ts.getTime();
   }

   /**
    * New searcher.
    *
    * @param searcher the searcher
    * @return the meta searcher
    * @throws SearchException the search exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#newSearcher(org.fao.geonet.kernel.search.ISearchManager.Searcher, java.lang.String)
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public MetaSearcher newSearcher(Searcher searcher) throws SearchException {
      switch (searcher) {
      case INDEX:
         return new GenericMetaSearcher(queryManagerFactory);
      case Z3950:
         return new Z3950Searcher(htmlCacheDir, Geonet.File.SEARCH_Z3950_CLIENT);
      case UNUSED:
         return new UnusedSearcher();
      default:
         throw new SearchException("unknown MetaSearcher type: " + searcher);
      }
   }

   // indexing methods

   /**
    * Indexes a metadata record.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    */
   @Override
   public void index(IndexableElement... elements) throws IndexException {
      this.index(Arrays.asList(elements));
   }

   /**
    * Index.
    *
    * @param element the element
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#index(java.util.Collection)
    */
   @Override
   public void index(Collection<IndexableElement> element) throws IndexException {
      index.add(element, true);
   }

   /**
    * Delete.
    *
    * @param element the element
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#delete(java.util.Collection)
    */
   @Override
   public void delete(Collection<IndexableElement> element) throws IndexException {
      index.remove(element, true);
   }

   /**
    * Delete.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#delete(java.lang.String, java.lang.String)
    */
   @Override
   public void delete(IndexableElement... elements) throws IndexException {
      this.delete(Arrays.asList(elements));
   }

   //

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#startIndexGroup()
    */
   @Override
   public void startIndexGroup() throws IndexException {
      // Handle elementsToIndex
      synchronized (elementsToIndex) {
         if (!elementsToIndex.isEmpty()) {
            Log.warning(Geonet.INDEX_ENGINE, "Some elements havn't been indexed : "
                  + elementsToIndex);
         }
         elementsToIndex.clear();
      }
      // Handle elementsToDelete
      synchronized (elementsToDelete) {
         if (!elementsToDelete.isEmpty()) {
            Log.warning(Geonet.INDEX_ENGINE, "Some elements havn't been deleted into the index: "
                  + elementsToDelete);
         }
         elementsToDelete.clear();
      }
   }

   /**
    * End index group.
    *
    * @throws Exception the exception
    */
   @Override
   public void endIndexGroup() throws Exception {
      // Handle elementsToIndex
      synchronized (elementsToIndex) {
         if (!elementsToIndex.isEmpty()) {
            this.index(elementsToIndex);
         } else {
            Log.info(Geonet.INDEX_ENGINE, "No elements to index");
         }
         elementsToIndex.clear();
      }
      // Handle elementsToDelete
      synchronized (elementsToDelete) {
         if (!elementsToDelete.isEmpty()) {
            this.delete(elementsToDelete);
         } else {
            Log.info(Geonet.INDEX_ENGINE, "No elements to delete");
         }
         elementsToDelete.clear();
      }
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#index(org.openwis.metadataportal.kernel.search.index.IndexableElement, boolean)
    */
   @Override
   public void index(IndexableElement element, boolean commit) throws IndexException {
      if (commit) {
         this.index(element);
      } else {
         synchronized (elementsToIndex) {
            elementsToIndex.add(element);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#delete(org.openwis.metadataportal.kernel.search.index.IndexableElement, boolean)
    */
   @Override
   public void delete(IndexableElement element, boolean commit) throws IndexException {
      if (commit) {
         this.delete(element);
      } else {
         synchronized (elementsToDelete) {
            elementsToDelete.add(element);
         }
      }
   }

   // Business methods
   /**
    * Gets the docs with x links.
    *
    * @return the docs with x links
    * @throws SearchException the search exception
    */
   @SuppressWarnings("rawtypes")
   private Set<String> getDocsWithXLinks() throws SearchException {
      Set<String> result = new HashSet<String>();

      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();
      // Build query
      SearchQuery query = queryFactory.buildQuery(IndexField.HAS_XLINK, 1);
      query.setReturnFields(IndexField.ID, IndexField.HAS_XLINK, IndexField.UUID);

      @SuppressWarnings("unchecked")
      SearchResult searchResult = queryManager.search(query);
      if (searchResult != null) {
         for (SearchResultDocument document : searchResult.getDocuments()) {
            result.add((String) document.getField(IndexField.UUID));
         }
      }

      return result;
   }

   /**
    * Synchronize docs.
    *
    * @param context the context
    * @throws SearchException the search exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#synchronizeDocs(jeeves.resources.dbms.Dbms)
    */
   @Override
   public void synchronizeDocs(ServiceContext context) throws SearchException {
      Dbms dbms = null;
      try {
         dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         this.synchronizeDocs(context, false, dbms);
      } catch (Exception e) {
         throw new SearchException(e);
      } finally {
         if (dbms != null) {
            try {
               context.getResourceManager().close();
            } catch (Exception e) {
               Log.error(Geonet.INDEX_ENGINE,
                     "Exception while closing the dbms connection: " + e.getMessage(), e);
            }
         }
      }
   }

   /**
    * Synchronize docs.
    *
    * @param context the context
    * @param force the force
    * @throws Exception the exception
    */
   private void synchronizeDocs(ServiceContext context, boolean force, Dbms dbms) throws Exception {
      // get last change date of all metadata in index.  The keys will be in lowercase.
      Map<String, Date> indexedDocs = getIndexedDocsChangeDate();

      // get all metadata from DB.  The keys are converted to lowercase.
      Map<String, DbmsDocChangeDateResult> dbmsDocs = getDbmsDocsChangeDate(dbms);
      

      // set up results HashMap for post processing of records to be indexed
      Set<IndexableElement> toUpdate = new HashSet<IndexableElement>();
      Set<IndexableElement> toDelete = new HashSet<IndexableElement>();

      Set<String> intersection = Sets.intersection(indexedDocs.keySet(), dbmsDocs.keySet());

      IndexableElement ie;
      for (String uuid : Sets.difference(dbmsDocs.keySet(), intersection)) {
         DbmsDocChangeDateResult changeDateInfo = dbmsDocs.get(uuid);
         if (changeDateInfo == null) {
            throw new RuntimeException("Could not extract changeDateInfo for metadata with URN: " + uuid);
         }
         ie = new DbmsIndexableElement(dbms, changeDateInfo.urnCasedPreserved);
         toUpdate.add(ie);
      }
      for (String uuid : Sets.difference(indexedDocs.keySet(), intersection)) {
         ie = new DbmsIndexableElement(dbms, uuid);
         toDelete.add(ie);
      }
      
      Log.info(Geonet.INDEX_ENGINE, String.format("Running document synchronization: indexCount = %d, dbCount = %d - will update = %d, will delete = %d",
            indexedDocs.size(),
            dbmsDocs.size(),
            toUpdate.size(),
            toDelete.size()));

      // Find doc to update
      Date indexDate;
      Date dbmsDate;
      for (String urn : intersection) {
         indexDate = indexedDocs.get(urn);
         DbmsDocChangeDateResult changeDateInfo = dbmsDocs.get(urn);
         dbmsDate = changeDateInfo.changeDate;
         if (!indexDate.equals(dbmsDate)) {
            if (Log.isInfo(Geonet.INDEX_ENGINE)) {
               Log.info(
                     Geonet.INDEX_ENGINE,
                     MessageFormat
                           .format(
                                 "The metadata {0} is not up to date; BD change date {1}, index change date {2}",
                                 urn, dbmsDate, indexDate));
            }
            toUpdate.add(new DbmsIndexableElement(dbms, changeDateInfo.urnCasedPreserved));
         }
      }

      //  Update
      if (!toUpdate.isEmpty()) {
         if (Log.isInfo(Geonet.INDEX_ENGINE)) {
            Log.info(Geonet.INDEX_ENGINE, "Update " + toUpdate.size() + " documents");
         }
         this.index(toUpdate);
      }
      // Delete
      if (!toDelete.isEmpty()) {
         if (Log.isInfo(Geonet.INDEX_ENGINE)) {
            Log.info(Geonet.INDEX_ENGINE, "Delete " + toDelete.size() + " documents");
         }
         this.delete(toDelete);
      }
   }

   /**
    * Gets the dbms documents change date.  The URNS will be returned IN LOWERCASE.
    *
    * @param dbms the dbms
    * @return the dbms documents change date
    * @throws Exception the exception
    */
   @SuppressWarnings("unchecked")
   private Map<String, DbmsDocChangeDateResult> getDbmsDocsChangeDate(Dbms dbms) throws Exception {
      Map<String, DbmsDocChangeDateResult> result = new LinkedHashMap<String, DbmsDocChangeDateResult>();

      Element elements = dbms.select("SELECT uuid, changeDate FROM Metadata");

      if (Log.isDebug(Geonet.DATA_MANAGER)) {
         Log.debug(Geonet.DATA_MANAGER,
               MessageFormat.format("DB CONTENT:\n''{0}''", Xml.getString(elements)));
      }

      String uuid;
      String lastChange;
      Date date;
      for (Element record : (List<Element>) elements.getChildren()) {
         // get metadata
         uuid = record.getChildText("uuid");
         lastChange = record.getChildText("changedate");
         date = index.parseDate(lastChange);
         
         DbmsDocChangeDateResult res = new DbmsDocChangeDateResult();
         res.changeDate = date;
         res.urnCasedPreserved = uuid;
         
         result.put(uuid.toLowerCase(), res);
      }

      return result;
   }
   
   /**
    * Gets the indexed documents change date.  The returned field will be the "_uuid" unique field,
    * which is the URN IN LOWERCASE.
    *
    * @return the documents change date
    * @throws SearchException the search exception
    */
   @SuppressWarnings("rawtypes")
   private Map<String, Date> getIndexedDocsChangeDate() throws SearchException {
      Map<String, Date> result = new HashMap<String, Date>();

      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();
      // Build query
      IndexField changeDate = IndexField._CHANGE_DATE;
      SearchQuery query = queryFactory.buildQuery(changeDate, "*");
      query.setReturnFields(IndexField.UUID, changeDate);

      @SuppressWarnings("unchecked")
      SearchResult searchResult = queryManager.search(query);
      if (searchResult != null) {
         String uuid;
         for (SearchResultDocument document : searchResult.getDocuments()) {
            uuid = (String) document.getField(IndexField.UUID);
            result.put(uuid, (Date) document.getField(changeDate));
         }
      }
      return result;
   }

   /**
    * Browse the index and return all values for the index field.
    *
    * @param field the field
    * @return  The list of values for the field
    * @throws SearchException the search exception
    */
   @SuppressWarnings("rawtypes")
   @Override
   public List<String> getTerm(IndexField field) throws SearchException {
      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();
      // Build query
      SearchQuery query = queryFactory.buildTermQuery(field);

      @SuppressWarnings("unchecked")
      SearchResult searchResult = queryManager.search(query);
      return searchResult.getDocumentStringField(field);
   }

   /**
    * Gets the term frequency.
    *
    * @param field the field
    * @param maxRecords the max records
    * @return the term frequency
    * @throws SearchException the search exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getTermFrequency(org.fao.geonet.kernel.search.IndexField, int)
    */
   @Override
   public List<TermFrequency> getTermFrequency(IndexField field, int maxRecords)
         throws SearchException {
      return getTermsFrequency(field, null, maxRecords, 0);
   }

   /**
    * Gets the range.
    *
    * @param field the field
    * @return the range
    * @throws SearchException the search exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getRange(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   @SuppressWarnings("rawtypes")
   public Range<String> getRange(IndexField field) throws SearchException {
      Range<String> result = null;
      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();
      // Build query
      SearchQuery query = queryFactory.buildTermRangeQuery(field);

      @SuppressWarnings("unchecked")
      SearchResult searchResult = queryManager.search(query);

      String min = null;
      String max = null;

      String term;
      int count;
      for (SearchResultDocument doc : searchResult) {
         term = (String) doc.getField(IndexField.TERM);
         count = Integer.valueOf((Integer) doc.getField(IndexField.TERM_COUNT));
         if (count == 0) {
            min = term;
         } else if (count == 1) {
            max = term;
         }
      }
      if (min != null && max != null) {
         result = Range.buildRange(min, max);
      }
      return result;
   }

   /**
    * Browse the index for the specified index field and return the list
    * of terms found containing the search value with their frequency.
    *
    * @param field the field
    * @param searchValue   The value to search for. Could be "".
    * @param maxNumberOfTerms Max number of term's values to look in the index. For large catalog
    * this value should be increased in order to get better results. If this
    * value is too high, then looking for terms could take more times. The use
    * of good analyzer should allow to reduce the number of useless values like
    * (a, the, ...).
    * @param threshold  Minimum frequency for a term to be returned.
    * @return  An unsorted and unordered list of terms with their frequency.
    * @throws SearchException the search exception
    */
   @Override
   @SuppressWarnings("rawtypes")
   public List<TermFrequency> getTermsFrequency(IndexField field, String searchValue,
         int maxNumberOfTerms, int threshold) throws SearchException {
      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();
      // Build query
      SearchQuery query = queryFactory.buildTermQuery(field, searchValue, maxNumberOfTerms,
            threshold);

      @SuppressWarnings("unchecked")
      SearchResult searchResult = queryManager.search(query);

      // Build Result
      List<TermFrequency> result = new ArrayList<TermFrequency>();
      TermFrequency tf;
      String term;
      int count;
      for (SearchResultDocument doc : searchResult) {
         term = ObjectUtils.toString(doc.getField(IndexField.TERM));
         count = Integer.valueOf((Integer) doc.getField(IndexField.TERM_COUNT));
         tf = new TermFrequency(term, count);
         result.add(tf);
      }
      return result;
   }

   /**
    * Optimize index.
    *
    * @return true, if successful
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#optimizeIndex()
    */
   @Override
   public boolean optimizeIndex() {
      boolean result = false;
      try {
         result = index.optimize();
      } catch (Exception e) {
         Log.error(Geonet.INDEX_ENGINE, "Exception while optimizing index", e);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#isAvailable()
    */
   @Override
   public boolean isAvailable() throws IndexException {
      boolean result = false;
      try {
         result = index.isAvailable();
      } catch (Exception e) {
         Log.error(Geonet.INDEX_ENGINE, "Exception while checking availability", e);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @throws SearchException
    * @see org.fao.geonet.kernel.search.ISearchManager#getField(java.lang.String, org.fao.geonet.kernel.search.IndexField)
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public String getField(String uuid, IndexField field) throws SearchException {
      String result = null;

      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();

      SearchQuery query;
      // Build default query

      query = queryFactory.buildQuery(IndexField.UUID, queryFactory.escapeQueryChars(uuid));
      query.setReturnFields(field);

      SearchResult search = queryManager.search(query);
      if (search.getCount() == 1) {
         SearchResultDocument doc = search.getDocuments().get(0);
         result = doc.getFieldAsString(field);
      }

      return result;
   }

   /**
    * Rebuild index.
    *
    * @param context the context
    * @param xlinks the xlinks
    * @return true, if successful
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#rebuildIndex(jeeves.server.context.ServiceContext, boolean)
    */
   @Override
   public boolean rebuildIndex(ServiceContext context, boolean xlinks) throws IndexException {
      boolean result = false;
      Dbms dbms = null;
      try {
         dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         if (!xlinks) {
            index.clear();
            synchronizeDocs(context, true, dbms);
         } else {
            Processor.clearCache();
            Set<IndexableElement> elementsToUpdate = new HashSet<IndexableElement>();
            IndexableElement ie;
            for (String uuid : getDocsWithXLinks()) {
               ie = new DbmsIndexableElement(dbms, uuid);
               elementsToUpdate.add(ie);
            }
            if (!elementsToUpdate.isEmpty()) {
               this.index(elementsToUpdate);
            }
         }
         result = true;
      } catch (Exception e) {
         Log.error(Geonet.INDEX_ENGINE, "Exception while rebuilding index, going to rebuild it: "
               + e.getMessage(), e);
      } finally {
         if (dbms != null) {
            try {
               context.getResourceManager().close();
            } catch (Exception e) {
               Log.error(Geonet.INDEX_ENGINE,
                     "Exception while closing the dbms connection: " + e.getMessage(), e);
            }
         }
      }
      return result;
   }

   /**
    * Gets the metadata urns for OAI.
    *
    * @param from the from
    * @param to the to
    * @param category the category
    * @param metadataSchema the metadata schema
    * @return the metadata urns for OAI
    * @throws SearchException the search exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getMetadataUrnsForOAI(java.lang.String, java.lang.String, org.openwis.metadataportal.model.category.Category, java.lang.String)
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public Collection<String> getMetadataUrnsForOAI(String from, String to, Category category,
         String metadataSchema) throws SearchException {
      List<String> result = new ArrayList<String>();

      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();

      SearchQuery query;
      // Build default query
      query = queryFactory.buildQuery(IndexField.SCHEMA, metadataSchema);
      query = queryFactory.and(query, queryFactory.buildQuery(IndexField.IS_TEMPLATE, "n"));

      // Add optional fields
      if (StringUtils.isNotBlank(from) && StringUtils.isBlank(to)) {
         query = queryFactory.and(query,
               queryFactory.buildAfterQuery(IndexField._LOCAL_IMPORT_DATE, from));
      }
      if (StringUtils.isNotBlank(to) && StringUtils.isBlank(from)) {
         query = queryFactory.and(query,
               queryFactory.buildBeforeQuery(IndexField._LOCAL_IMPORT_DATE, to));
      }
      if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
         query = queryFactory.and(query,
               queryFactory.buildBetweenQuery(IndexField._LOCAL_IMPORT_DATE, from, to));
      }
      if (category.getId() != null) {
         query = queryFactory.and(query,
               queryFactory.buildQuery(IndexField.CATEGORY_ID, String.valueOf(category.getId())));
      }
      query.setReturnFields(IndexField.UUID_ORIGINAL);

      SearchResult searchResult = queryManager.search(query);

      // FIXME update the API to get all results in one query or refactor the OAI resumptionToken...
      if (searchResult != null && searchResult.getCount() > 10) {
         query.setRange(0, searchResult.getCount());
         searchResult = queryManager.search(query);
      }

      if (searchResult != null) {
         for (SearchResultDocument document : searchResult.getDocuments()) {
            result.add(document.getField(IndexField.UUID_ORIGINAL).toString());
         }
      }
      return result;
   }

   /**
    * Gets the all latest metadata.
    *
    * @param maxItems the max items
    * @return the all latest metadata
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getAllLatestMetadata(int)
    */
   @Override
   @SuppressWarnings("rawtypes")
   public List<Metadata> getAllLatestMetadata(int maxItems) throws Exception {
      List<Metadata> result = new ArrayList<Metadata>();
      if (maxItems > 0) {
         IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
         SearchQueryFactory queryFactory = queryManager.getQueryFactory();

         SearchQuery query;
         // Build default query
         query = queryFactory.buildQuery(IndexField.IS_TEMPLATE, "n");

         query.setRange(0, maxItems - 1);
         SortingInfo sort = new SortingInfoImpl();
         sort.add(IndexField._CHANGE_DATE, SortDir.DESC);
         query.setSortFields(sort);
         query.setReturnFields(IndexField.UUID, IndexField._CHANGE_DATE, IndexField._TITLE,
               IndexField.ID);

         @SuppressWarnings("unchecked")
         SearchResult searchResult = queryManager.search(query);
         if (searchResult != null) {
            Metadata md;
            Date date;
            for (SearchResultDocument document : searchResult.getDocuments()) {
               md = new Metadata();
               date = (Date) document.getField(IndexField._CHANGE_DATE);
               md.setChangeDate(new ISODate(date.getTime()).getDate());
               md.setTitle((String) document.getField(IndexField._TITLE));
               md.setUrn((String) document.getField(IndexField.UUID));
               md.setId(Integer.valueOf(document.getId()));
               result.add(md);
            }
         }
      }
      return result;
   }

   /**
    * Gets the filtered metadata.
    *
    * @param criteria the criteria
    * @param index the index
    * @param hits the hits
    * @param sort the sort
    * @return the filtered metadata
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getFilteredMetadata(java.lang.String, int, int, org.fao.geonet.kernel.search.IndexField, org.openwis.dataservice.SortDirection)
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public SearchResult getFilteredMetadata(MonitorCatalogSearchCriteria criteria, int index,
         int hits, SortingInfo sort) throws Exception {

      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();

      // Build query
      SearchQuery query = queryFactory.buildQuery(IndexField.IS_TEMPLATE, "n");

      if (StringUtils.isNotBlank(criteria.getFullText())) {
    	  if (StringUtils.isNotBlank(criteria.getSearchField())) {
    		  IndexField field =IndexField.getField(criteria.getSearchField());
    		  String queryChars = queryFactory.escapeQueryChars(criteria.getFullText());
			query = queryFactory.and(query, queryFactory.buildQuery(field,queryChars));
    	  }else {
    		  String queryChars = queryFactory.escapeQueryChars(criteria.getFullText());
    		  query = queryFactory.and(query, queryFactory.buildAnyQuery(queryChars));
    	  }
      }
      if (StringUtils.isNotBlank(criteria.getCategory())) {
          query = queryFactory.and(query,
                queryFactory.buildQuery(IndexField.CATEGORY_ID, criteria.getCategory()));
       }

      if (StringUtils.isNotBlank(criteria.getOwner())) {
         query = queryFactory.and(query,
               queryFactory.buildQuery(IndexField.OWNER, criteria.getOwner()));
      }
      query.setRange(index, index + hits - 1);

      // Auto append uuid for sorting
      sort.add(IndexField.UUID, SortDir.ASC);
      query.setSortFields(sort);

      query.setReturnFields(IndexField.UUID, IndexField.UUID_ORIGINAL, IndexField._TITLE, IndexField.ORIGINATOR,
            IndexField.PROCESS, IndexField.GTS_CATEGORY, IndexField.FNC_PATTERN,
            IndexField.PRIORITY, IndexField.DATAPOLICY, IndexField.LOCAL_DATA_SOURCE,
            IndexField.IS_FED, IndexField.IS_INGESTED, IndexField.FILE_EXTENSION,
            IndexField.OVERRIDDEN_GTS_CATEGORY, IndexField.OVERRIDDEN_DATAPOLICY,
            IndexField.OVERRIDDEN_FILE_EXTENSION, IndexField.OVERRIDDEN_FNC_PATTERN,
            IndexField.OVERRIDDEN_PRIORITY, IndexField.CATEGORY_ID, IndexField.CATEGORY_NAME,
            IndexField.SCHEMA, IndexField.ID);

      return queryManager.search(query);
   }

   /**
    * Gets the all templates.
    *
    * @return the all templates
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getAllTemplates()
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public List<Template> getAllTemplates() throws Exception {
      List<Template> solrResult = new ArrayList<Template>();
      
      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();

      // Build query
      SearchQuery query = queryFactory.buildQuery(IndexField.IS_TEMPLATE, "y");
      query.setReturnFields(IndexField.ID, IndexField.UUID, IndexField._TITLE, IndexField.SCHEMA,
            IndexField.DISPLAY_ORDER);

      SearchResult sr = queryManager.search(query);

      Template template;
      String name;
      String urn;
      String schema;
      Integer order;
      int id;
      for (SearchResultDocument doc : sr) {
         id = Integer.valueOf(doc.getId());
         name = (String) doc.getField(IndexField._TITLE);
         urn = (String) doc.getField(IndexField.UUID);
         schema = (String) doc.getField(IndexField.SCHEMA);
         try {
            order = Integer.valueOf((String) doc.getField(IndexField.DISPLAY_ORDER));
         } catch (Exception e) {
            order = 0;
         }
         template = new Template(id, urn);
         template.setTitle(name);
         template.setDisplayOrder(order);
         template.setSchema(schema);
         solrResult.add(template);  
      }
      
      //The displayOrder is not set in Solr, A request in the database is done to set the display order field.
      int lastIndex = solrResult.size() - 1; // user for templates that have no display order
      for (Template solrTemplate : solrResult) {
         String queryDB = "SELECT * FROM Metadata WHERE id=" + solrTemplate.getId();
         List<Element> records = getDbms().select(queryDB).getChildren();
         
         String displayOrderStr = records.get(0).getChildText("displayorder");
         Integer displayOrder = null;
         if (StringUtils.isNotBlank(displayOrderStr)) {
            displayOrder = Integer.parseInt(displayOrderStr);
         } else {
            displayOrder = lastIndex;
            lastIndex--;
         }
         solrTemplate.setDisplayOrder(displayOrder);
      }
      
      Collections.sort(solrResult, new Comparator<Template>() {
         @Override
         public int compare(Template t0, Template t1) {
            return t0.getDisplayOrder().compareTo(t1.getDisplayOrder());
         }
      });
     
      return solrResult;
   }

   /**
    * Checks if is metadata linked to data policy.
    *
    * @param dataPolicyName the data policy name
    * @return true, if is metadata linked to data policy
    * @throws SearchException the search exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#isMetadataLinkedToDataPolicy(java.lang.String)
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   @Override
   public boolean isMetadataLinkedToDataPolicy(String dataPolicyName) throws SearchException {

      IQueryManager queryManager = queryManagerFactory.buildIQueryManager();
      SearchQueryFactory queryFactory = queryManager.getQueryFactory();

      // Build query
      SearchQuery query = queryFactory.buildQuery(IndexField.IS_TEMPLATE, "n");

      SearchQuery byDataPolicyQuery = queryFactory.buildQuery(IndexField.EFFECTIVE_DATAPOLICY,
            dataPolicyName);
      byDataPolicyQuery = queryFactory.or(byDataPolicyQuery,
            queryFactory.buildQuery(IndexField.DATAPOLICY, dataPolicyName));
      byDataPolicyQuery = queryFactory.or(byDataPolicyQuery,
            queryFactory.buildQuery(IndexField.OVERRIDDEN_DATAPOLICY, dataPolicyName));

      query = queryFactory.and(query, byDataPolicyQuery);
      query.setReturnFields(IndexField.UUID);

      SearchResult sr = queryManager.search(query);

      return sr.getCount() > 0;
   }

   /**
    * Result object for {@link getDbmsDocsChangeDate}.
    */
   private static class DbmsDocChangeDateResult
   {
      /**
       * The metadata change date.
       */
      public Date changeDate;
      
      /**
       * The metadata URN with case preserved.
       */
      public String urnCasedPreserved;
   }
}
