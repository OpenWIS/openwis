package org.openwis.metadataportal.kernel.search;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.IndexListener;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.fao.geonet.kernel.search.Range;
import org.fao.geonet.kernel.search.SortingInfo;
import org.fao.geonet.kernel.search.TermFrequency;
import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.services.metadata.dto.MonitorCatalogSearchCriteria;

/**
 * The Class AsyncSearchManagerImpl.
 */
public class AsyncSearchManagerImpl implements ISearchManager {

   /** The search manager. */
   private final ISearchManager sm;

   /** The executor. */
   private final ExecutorService executor;

   /**
    * Instantiates a new asynchronous search manager .
    *
    * @param searchManager the search manager
    */
   public AsyncSearchManagerImpl(ISearchManager searchManager) {
      super();
      sm = searchManager;
      executor = Executors.newSingleThreadExecutor();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#index(java.util.Collection)
    */
   @Override
   public void index(Collection<IndexableElement> elements) throws IndexException {
      IndexMetadataTask task = new IndexMetadataTask(sm, elements);
      executor.execute(task);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#delete(java.util.Collection)
    */
   @Override
   public void delete(Collection<IndexableElement> elements) throws IndexException {
      DeleteMetadataTask task = new DeleteMetadataTask(sm, elements);
      executor.execute(task);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#rebuildIndex(jeeves.server.context.ServiceContext, boolean)
    */
   @Override
   public boolean rebuildIndex(final ServiceContext context, final boolean xlinks)
         throws IndexException {
      executor.execute(new Runnable() {
         @Override
         public void run() {
            try {
               sm.rebuildIndex(context, xlinks);
            } catch (IndexException e) {
               Log.error(Geonet.INDEX_ENGINE, "Could not rebuild index", e);
            }
         }
      });
      return true;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getField(java.lang.String, org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public String getField(String uuid, IndexField field) throws SearchException {
      return sm.getField(uuid, field);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#shutdown()
    */
   @Override
   public void shutdown() throws Exception {
      sm.shutdown();
      executor.shutdown();
   }

   // Delegate

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#startup()
    */
   @Override
   public void startup() throws Exception {
      sm.startup();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#addIndexListener(org.fao.geonet.kernel.search.IndexListener)
    */
   @Override
   public void addIndexListener(IndexListener listener) {
      sm.addIndexListener(listener);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#removeIndexListener(org.fao.geonet.kernel.search.IndexListener)
    */
   @Override
   public void removeIndexListener(IndexListener listener) {
      sm.removeIndexListener(listener);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#disableOptimizer()
    */
   @Override
   public void disableOptimizer() throws Exception {
      sm.disableOptimizer();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#rescheduleOptimizer(java.util.Calendar, int)
    */
   @Override
   public void rescheduleOptimizer(Calendar optimizerBeginAt, int optimizerInterval)
         throws IndexException {
      sm.rescheduleOptimizer(optimizerBeginAt, optimizerInterval);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#newSearcher(org.fao.geonet.kernel.search.ISearchManager.Searcher)
    */
   @Override
   public MetaSearcher newSearcher(Searcher searcher) throws SearchException {
      return sm.newSearcher(searcher);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#index(org.openwis.metadataportal.kernel.search.index.IndexableElement[])
    */
   @Override
   public void index(IndexableElement... elements) throws IndexException {
      sm.index(elements);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#delete(org.openwis.metadataportal.kernel.search.index.IndexableElement[])
    */
   @Override
   public void delete(IndexableElement... elements) throws IndexException {
      sm.delete(elements);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#startIndexGroup()
    */
   @Override
   @Deprecated
   public void startIndexGroup() throws IndexException {
      sm.startIndexGroup();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#endIndexGroup()
    */
   @Override
   @Deprecated
   public void endIndexGroup() throws Exception {
      sm.endIndexGroup();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#index(org.openwis.metadataportal.kernel.search.index.IndexableElement, boolean)
    */
   @Override
   @Deprecated
   public void index(IndexableElement element, boolean commit) throws IndexException {
      sm.index(element, commit);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#delete(org.openwis.metadataportal.kernel.search.index.IndexableElement, boolean)
    */
   @Override
   @Deprecated
   public void delete(IndexableElement element, boolean commit) throws IndexException {
      sm.delete(element, commit);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#synchronizeDocs(jeeves.server.context.ServiceContext)
    */
   @Override
   public void synchronizeDocs(ServiceContext context) throws SearchException {
      sm.synchronizeDocs(context);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#optimizeIndex()
    */
   @Override
   public boolean optimizeIndex() throws IndexException {
      return sm.optimizeIndex();
   }



   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#isAvailable()
    */
   @Override
   public boolean isAvailable() throws IndexException {
      return sm.isAvailable();
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getTerm(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public List<String> getTerm(IndexField field) throws SearchException {
      return sm.getTerm(field);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getTermFrequency(org.fao.geonet.kernel.search.IndexField, int)
    */
   @Override
   public List<TermFrequency> getTermFrequency(IndexField field, int maxRecords)
         throws SearchException {
      return sm.getTermFrequency(field, maxRecords);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getRange(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public Range<String> getRange(IndexField field) throws SearchException {
      return sm.getRange(field);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getTermsFrequency(org.fao.geonet.kernel.search.IndexField, java.lang.String, int, int)
    */
   @Override
   public List<TermFrequency> getTermsFrequency(IndexField field, String searchValue,
         int maxNumberOfTerms, int threshold) throws SearchException {
      return sm.getTermsFrequency(field, searchValue, maxNumberOfTerms, threshold);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getMetadataUrnsForOAI(java.lang.String, java.lang.String, org.openwis.metadataportal.model.category.Category, java.lang.String)
    */
   @Override
   public Collection<String> getMetadataUrnsForOAI(String from, String to, Category category,
         String metadataSchema) throws SearchException {
      return sm.getMetadataUrnsForOAI(from, to, category, metadataSchema);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#isMetadataLinkedToDataPolicy(java.lang.String)
    */
   @Override
   public boolean isMetadataLinkedToDataPolicy(String dataPolicyName) throws SearchException {
      return sm.isMetadataLinkedToDataPolicy(dataPolicyName);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getAllLatestMetadata(int)
    */
   @Override
   public List<Metadata> getAllLatestMetadata(int maxItems) throws Exception {
      return sm.getAllLatestMetadata(maxItems);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getFilteredMetadata(org.openwis.metadataportal.services.metadata.dto.MonitorCatalogSearchCriteria, int, int, org.fao.geonet.kernel.search.SortingInfo)
    */
   @Override
   public SearchResult getFilteredMetadata(MonitorCatalogSearchCriteria criteria, int index,
         int hits, SortingInfo sort) throws Exception {
      return sm.getFilteredMetadata(criteria, index, hits, sort);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.ISearchManager#getAllTemplates()
    */
   @Override
   public List<Template> getAllTemplates() throws Exception {
      return sm.getAllTemplates();
   }

}
