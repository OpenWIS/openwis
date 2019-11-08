package org.fao.geonet.kernel.search;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import jeeves.server.context.ServiceContext;

import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.services.metadata.dto.MonitorCatalogSearchCriteria;

/**
 * Indexes metadata.
 * IndexableElement
 */
public interface ISearchManager {

   /**
    * The available Searcher kind. <P>
    * Explanation goes here. <P>
    */
   enum Searcher {
      /** The INDEX. */
      INDEX,
      /** The UNUSED. */
      UNUSED,
      /** The Z3950. */
      Z3950;
   }

   // Lifecyle methods
   /**
    * Startup.
    *
    * @throws Exception the exception
    */
   public void startup() throws Exception;

   /**
    * End.
    *
    * @throws Exception the exception
    */
   public void shutdown() throws Exception;

   // Listener methods
   /**
    * Adds the index listener.
    *
    * @param listener the listener
    */
   void addIndexListener(IndexListener listener);

   /**
    * Removes the index listener.
    *
    * @param listener the listener
    */
   void removeIndexListener(IndexListener listener);

   // Optimizer methods
   /**
    * Disable optimizer.
    *
    * @throws Exception the exception
    */
   void disableOptimizer() throws Exception;

   /**
    * Reschedule optimizer.
    *
    * @param optimizerBeginAt the optimizer begin at
    * @param optimizerInterval the optimizer interval
    * @throws IndexException the index exception
    */
   void rescheduleOptimizer(Calendar optimizerBeginAt, int optimizerInterval) throws IndexException;

   // Searcher
   /**
    * New searcher.
    *
    * @param searcher the searcher
    * @return the meta searcher
    * @throws SearchException the search exception
    */
   public MetaSearcher newSearcher(Searcher searcher) throws SearchException;

   // Indexing methods
   /**
    * Index.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    */
   public void index(IndexableElement... elements) throws IndexException;

   /**
    * Index.
    *
    * @param element the element
    * @throws IndexException the index exception
    */
   public void index(Collection<IndexableElement> element) throws IndexException;

   /**
    * Delete.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    */
   public void delete(Collection<IndexableElement> elements) throws IndexException;

   /**
    * Delete.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    */
   public void delete(IndexableElement... elements) throws IndexException;

   /**
    * Start index group.
    * Start a transaction on index operation
    *
    * @throws IndexException the index exception
    * @deprecated prefer the use of {@link #index(Collection)} and {@link #delete(Collection)} methods
    */
   @Deprecated
   public void startIndexGroup() throws IndexException;

   /**
    * End index group.
    * Commit the transation on index operation
    *
    * @throws Exception the exception
    * @deprecated prefer the use of {@link #index(Collection)} and {@link #delete(Collection)} methods
    */
   @Deprecated
   public void endIndexGroup() throws Exception;

   /**
    * Index.
    *
    * @param element the element
    * @param commit if we commit after index
    * @throws IndexException the index exception
    * @deprecated prefer the use of {@link #index(Collection)} and {@link #delete(Collection)} methods
    */
   @Deprecated
   public void index(IndexableElement element, boolean commit) throws IndexException;

   /**
    * Delete.
    *
    * @param element the element
    * @param commit if we commit after delete
    * @throws IndexException the index exception
    * @deprecated prefer the use of {@link #index(Collection)} and {@link #delete(Collection)} methods
    */
   @Deprecated
   public void delete(IndexableElement element, boolean commit) throws IndexException;

   // Maintenance methods
   /**
    * Rebuild the Indexed index.
    *
    * @param context the context
    * @param xlinks the xlinks
    * @return true, if successful
    * @throws IndexException the index exception
    */
   public boolean rebuildIndex(ServiceContext context, boolean xlinks) throws IndexException;

   /**
    * Synchronize indexed documents.
    *
    * @param context the context
    * @throws SearchException the search exception
    */
   public void synchronizeDocs(ServiceContext context) throws SearchException;

   /**
    * Optimize the Indexed index.
    *
    * @return true, if successful
    * @throws IndexException the index exception
    */
   public boolean optimizeIndex() throws IndexException;

   /**
    * Checks if the index is available.
    * @return <code>true</code> if the index is available.
    * @throws IndexException if an error occurs.
    */
   boolean isAvailable() throws IndexException;

   // Functional methods

   /**
    * Browse the index and return all values for the Indexed field.
    *
    * @param field the field
    * @return The list of values for the field
    * @throws SearchException the search exception
    */
   public List<String> getTerm(IndexField field) throws SearchException;

   /**
    * Gets the term.
    *
    * @param field the field
    * @param maxRecords the max records
    * @return the term
    * @throws SearchException the search exception
    */
   public List<TermFrequency> getTermFrequency(IndexField field, int maxRecords)
         throws SearchException;

   /**
    * Gets the range.
    *
    * @param field the field
    * @return the range
    * @throws SearchException the search exception
    */
   public Range<String> getRange(IndexField field) throws SearchException;

   /**
    * Browse the index for the specified Indexed field and return the list
    * of terms found containing the search value with their frequency.
    *
    * @param field the field
    * @param searchValue The value to search for. Could be "".
    * @param maxNumberOfTerms Max number of term's values to look in the index. For large catalog
    * this value should be increased in order to get better results. If this
    * value is too high, then looking for terms could take more times. The use
    * of good analyzer should allow to reduce the number of useless values like
    * (a, the, ...).
    * @param threshold Minimum frequency for a term to be returned.
    * @return An unsorted and unordered list of terms with their frequency.
    * @throws SearchException the search exception
    */
   public List<TermFrequency> getTermsFrequency(IndexField field, String searchValue,
         int maxNumberOfTerms, int threshold) throws SearchException;

   /**
    * Returns a list of metadata URNs matching the given elements.
    *
    * @param from the lower bound for datestamp-based selective harvesting.
    * @param to the upper bound for datestamp-based selective harvesting.
    * @param category the category of the metadata.
    * @param metadataSchema the schema of the metadata.
    * @return a list of metadata URNs.
    * @throws SearchException the search exception
    */
   public Collection<String> getMetadataUrnsForOAI(String from, String to, Category category,
         String metadataSchema) throws SearchException;

   /**
    * Returns <code>true</code> if a metadata is linked to a Data Policy, <code>false</code> otherwise.
    * @param dataPolicyName the name of the data policy.
    * @return <code>true</code> if a metadata is linked to a Data Policy, <code>false</code> otherwise.
    * @throws SearchException if an error occurs.
    */
   boolean isMetadataLinkedToDataPolicy(String dataPolicyName) throws SearchException;

   /**
    * Returns a list of metadata sorted by changeDate DESC.
    * @param maxItems the number of maximum items to be returned.
    * @return a list of metadata sorted by changeDate DESC.
    * @throws Exception if an error occurs.
    */
   public List<Metadata> getAllLatestMetadata(int maxItems) throws Exception;

   /**
    * Return a list of metadata according to the filter text.
    *
    * @param criteria the criteria
    * @param index the first result index
    * @param hits the number of hits
    * @param sort the sort direction
    * @return a search result
    * @throws Exception the exception
    */
   public SearchResult getFilteredMetadata(MonitorCatalogSearchCriteria criteria, int index,
         int hits, SortingInfo sort) throws Exception;

   /**
    * Get All templates.
    *
    * @return a list of templates
    * @throws Exception the exception
    */
   public List<Template> getAllTemplates() throws Exception;

   /**
    * Gets the field.
    *
    * @param uuid the uuid
    * @param field the field
    * @return the field
    * @throws SearchException the search exception
    */
   public String getField(String uuid, IndexField field) throws SearchException;

}
