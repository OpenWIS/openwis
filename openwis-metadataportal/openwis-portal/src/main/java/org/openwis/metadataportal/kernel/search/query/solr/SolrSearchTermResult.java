package org.openwis.metadataportal.kernel.search.query.solr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.ObjectUtils;
import org.apache.solr.common.util.NamedList;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.AbstractSearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;

/**
 * The Class SolrSearchTermResult. <P>
 * Explanation goes here. <P>
 */
public class SolrSearchTermResult extends AbstractSearchResult implements SearchResult {

   /** The Solr terms result. */
   private final NamedList<Integer> terms;

   /** The field. */
   private final IndexField field;

   /** The docs. */
   private List<SearchResultDocument> docs;

   /**
    * Instantiates a new solr search term result.
    *
    * @param query the query
    * @param nl the solr terms result
    */
   public SolrSearchTermResult(SolrSearchQuery query, NamedList<NamedList<Integer>> nl) {
      super(query);
      // only one return value
      field = IndexField.getField(nl.getName(0));
      terms = nl.getVal(0);
   }

   /**
    * Gets the from.
    *
    * @return the from
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getFrom()
    */
   @Override
   public int getFrom() {
      // No paging here
      return 0;
   }

   /**
    * Gets the to.
    *
    * @return the to
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getTo()
    */
   @Override
   public int getTo() {
      int to = 0;
      if (getCount() > 0) {
         to = getCount() - 1;
      }
      return to;
   }

   /**
    * Gets the count.
    *
    * @return the count
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getCount()
    */
   @Override
   public int getCount() {
      return terms.size();
   }

   /**
    * Gets the documents.
    *
    * @return the documents
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getDocuments()
    */
   @Override
   public List<SearchResultDocument> getDocuments() {
      if (docs == null) {
         docs = new ArrayList<SearchResultDocument>();
         SolrSearchTermDocumentResult doc;
         Entry<String, Integer> value;
         for (Iterator<Entry<String, Integer>> it = terms.iterator(); it.hasNext();) {
            value = it.next();
            doc = new SolrSearchTermDocumentResult(field, value.getKey(), value.getValue());
            docs.add(doc);
         }
      }
      return docs;
   }

   /**
    * To summary.
    *
    * @return the element
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#toSummary()
    */
   @Override
   public Element toSummary(ResultType resultType) {
      throw new IllegalAccessError("Should not being call for Term query");
   }

   /**
    * To present.
    *
    * @return the element
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#toPresent()
    */
   @Override
   public Element toPresent() {
      throw new IllegalAccessError("Should not being call for Term query");
   }

   /**
    * Gets the.
    *
    * @return the element
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#get()
    */
   @Override
   public Element get() {
      throw new IllegalAccessError("Should not being call for Term query");
   }

   /**
    * The Class SolrSearchTermDocumentResult. <P>
    * Explanation goes here. <P>
    */
   private static class SolrSearchTermDocumentResult implements SearchResultDocument {

      /** The field. */
      private final IndexField field;

      /** The count. */
      private final int count;

      /** The value. */
      private final String value;

      /**
       * Instantiates a new solr search term document result.
       *
       * @param field the field
       * @param value the value
       * @param count the count
       */
      private SolrSearchTermDocumentResult(IndexField field, String value, int count) {
         super();
         this.field = field;
         this.value = value;
         this.count = count;
      }

      /**
       * Gets the score.
       *
       * @return the score
       */
      @Override
      public float getScore() {
         throw new IllegalAccessError("Should not being call for Term query");
      }

      /**
       * Gets the id.
       *
       * @return the id
       */
      @Override
      public String getId() {
         throw new IllegalAccessError("Should not being call for Term query");
      }

      /**
       * Gets the field.
       *
       * @param field the field
       * @return the field
       */
      @Override
      public Object getField(IndexField field) {
         Object result;
         if (this.field.equals(field)) {
            result = value;
         } else if (IndexField.TERM.equals(field)) {
            result = value;
         } else if (IndexField.TERM_COUNT.equals(field)) {
            result = count;
         } else {
            throw new IllegalAccessError("Should not being call for Term query");
         }
         return result;
      }

      /**
       * {@inheritDoc}
       * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getFieldAsString(org.fao.geonet.kernel.search.IndexField)
       */
      @Override
      public String getFieldAsString(IndexField field) {
         return ObjectUtils.toString(getField(field));
      }

      /**
       * {@inheritDoc}
       * @see org.openwis.metadataportal.kernel.search.query.SearchResultDocument#getFieldAsListOfString(org.fao.geonet.kernel.search.IndexField)
       */
      @Override
      public List<String> getFieldAsListOfString(IndexField field) {
         return Collections.singletonList(getFieldAsString(field));
      }

      /**
       * Gets the element.
       *
       * @return the element
       */
      @Override
      public Element getElement() {
         throw new IllegalAccessError("Should not being call for Term query");
      }
   }
}
