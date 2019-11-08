package org.openwis.metadataportal.kernel.search.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.fao.geonet.kernel.search.IndexField;

/**
 * The Class AbstractSearchResult. <P>
 * Explanation goes here. <P>
 */
public abstract class AbstractSearchResult implements SearchResult {

   /** The query. */
   private final AbstractSearchQuery query;

   /**
    * Instantiates a new abstract search result.
    *
    * @param query the query
    */
   public AbstractSearchResult(AbstractSearchQuery query) {
      super();
      this.query = query;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getQuery()
    */
   @Override
   public SearchQuery getQuery() {
      return query;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Iterable#iterator()
    */
   @Override
   public Iterator<SearchResultDocument> iterator() {
      return getDocuments().iterator();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getDocumentStringField(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public List<String> getDocumentStringField(IndexField field) {
      List<String> result = new ArrayList<String>();
      Object val;
      for (SearchResultDocument doc : this) {
         val = doc.getField(field);
         result.add(ObjectUtils.toString(val));
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getCountStringField(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public Map<String, Integer> getCountStringField(IndexField field) {
      Map<String, Integer> result = new HashMap<String, Integer>();
      Object val;
      String sVal;
      Integer count;
      for (SearchResultDocument doc : this) {
         val = doc.getField(field);
         if (val != null) {
            sVal = ObjectUtils.toString(val);
            count = result.get(sVal);
            if (count == null) {
               result.put(sVal, 1);
            } else {
               result.put(sVal, count + 1);
            }
         }
      }
      return result;
   }

}
