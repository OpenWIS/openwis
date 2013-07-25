package org.openwis.metadataportal.kernel.search.query.solr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jeeves.constants.Jeeves;
import jeeves.utils.Log;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.csw.common.SummaryItem;
import org.hibernate.cfg.NotYetImplementedException;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.AbstractSearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;

/**
 * The Class SolrSearchResult. <P>
 * Explanation goes here. <P>
 */
public class SolrSearchResult extends AbstractSearchResult implements SearchResult {

   /** The solr results. */
   private final SolrDocumentList solrResults;

   /** The summary. */
   private Element summary;

   /** The present. */
   private Element present;

   /** The hit per page. */
   private final int hitsPerPage;

   /** The solr query. */
   private final SolrSearchQuery solrQuery;

   /**
    * Instantiates a new solr search result.
    *
    * @param query the query
    * @param results
    */
   public SolrSearchResult(SolrSearchQuery query, SolrDocumentList results) {
      super(query);
      solrQuery = query;
      solrResults = results;
      hitsPerPage = query.getHitsPerPage();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[SearchResult]");
      sb.append("\n\tQuery: ");
      sb.append(getQuery());
      sb.append("\n\tFound: ");
      sb.append(getCount());
      for (SearchResultDocument doc : getDocuments()) {
         sb.append('\n');
         sb.append(doc);
      }
      return sb.toString();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getFrom()
    */
   @Override
   public int getFrom() {
      return (int) solrResults.getStart();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getTo()
    */
   @Override
   public int getTo() {
      return getFrom() + solrResults.size() - 1;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getCount()
    */
   @Override
   public int getCount() {
      return (int) solrResults.getNumFound();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#getDocuments()
    */
   @Override
   public List<SearchResultDocument> getDocuments() {
      List<SearchResultDocument> result = new ArrayList<SearchResultDocument>();
      if (solrQuery.isTermQuery()) {
         solrResults.toString();
         for (SolrDocument doc : solrResults) {
            result.add(new SolrSearchResultDocument(doc));
         }
      } else {
         for (SolrDocument doc : solrResults) {
            result.add(new SolrSearchResultDocument(doc));
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#toSummary()
    */
   @Override
   public Element toSummary(ResultType resultType) {
      // lazy
      if (summary == null) {
         Log.debug(Geonet.SEARCH_ENGINE, "Building summary");

         summary = new Element("summary");
         summary.setAttribute("count", String.valueOf(solrResults.getNumFound()));
         summary.setAttribute("type", "local");
         summary.setAttribute("hitsusedforsummary", String.valueOf(solrResults.size()));

         // Sum Items
         Element sumElt = buildSummaryElement(resultType);
         if (sumElt != null) {
            summary.addContent(sumElt);
         }
      }
      return summary;
   }

   /**
    * Builds the summary element.
    *
    * @param resultType the result type
    * @return the element
    */
   private Element buildSummaryElement(ResultType resultType) {
      Element result = null;
      Map<String, Integer> termFreq;
      List<Entry<String, Integer>> keys;
      Element fChild;
      for (SummaryItem sumItem : resultType.getItems()) {
         termFreq = getCountStringField(sumItem.getIndexField()); // FIXME Igor: use all result rather than use one page of result
         keys = new ArrayList<Entry<String, Integer>>(termFreq.entrySet());

         // Sorting
         Comparator<Entry<String, Integer>> comparator;
         if (sumItem.isSortByCount()) {
            // Sort by value
            comparator = new Comparator<Map.Entry<String, Integer>>() {
               @Override
               public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                  return o1.getValue().compareTo(o2.getValue());
               }
            };
         } else {
            // Sort by key
            comparator = new Comparator<Map.Entry<String, Integer>>() {
               @Override
               public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                  return o1.getKey().compareTo(o2.getKey());
               }
            };
         }
         // Add children
         Collections.sort(keys, comparator);
         result = new Element(sumItem.getParentName());
         for (Entry<String, Integer> entry : keys) {
            fChild = new Element(sumItem.getName());
            result.addContent(fChild);
            fChild.setAttribute("count", String.valueOf(entry.getValue()));
            fChild.setAttribute("name", entry.getKey());
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#toPresent()
    */
   @Override
   public Element toPresent() {
      // lazy
      if (present == null) {
         present = new Element(Jeeves.Elem.RESPONSE);
         if (solrResults.isEmpty()) {
            present.setAttribute("from", String.valueOf(0));
         } else {
            present.setAttribute("from", String.valueOf(getFrom()));
         }
         present.setAttribute("to", String.valueOf(getTo()));
         present.setAttribute("hitsPerPage", String.valueOf(hitsPerPage));

         present.addContent(toSummary(ResultType.HITS));
         // add doc
         SolrSearchResultDocument document;
         for (SearchResultDocument doc : getDocuments()) {
            if (doc instanceof SolrSearchResultDocument) {
               document = (SolrSearchResultDocument) doc;
               present.addContent(document.getElement());
            }
         }
      }
      return present;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchResult#get()
    */
   @Override
   public Element get() {
      // TODO Auto-generated method stub
      throw new NotYetImplementedException("SolrSearchResult#get()");
   }

}
