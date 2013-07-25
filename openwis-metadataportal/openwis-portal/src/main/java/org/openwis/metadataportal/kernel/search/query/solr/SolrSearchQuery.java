package org.openwis.metadataportal.kernel.search.query.solr;

import java.text.MessageFormat;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.Pair;
import org.fao.geonet.kernel.search.SortingInfo;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.search.query.AbstractSearchQuery;
import org.openwis.metadataportal.kernel.search.query.SearchQuery;

/**
 * The Class SolrSearchQuery. <P>
 * Explanation goes here. <P>
 */
public class SolrSearchQuery extends AbstractSearchQuery implements SearchQuery {

   /** The solr query. */
   private final SolrQuery solrQuery;

   /** Is spatial query. */
   private boolean spatialQuery = false;

   /** Is term query. */
   private boolean termQuery = false;

   /**
    * Instantiates a new solr search query.
    *
    * @param serviceContext the service context
    * @param solrQuery the solr query
    */
   public SolrSearchQuery(SolrQuery solrQuery) {
      super(null);
      this.solrQuery = solrQuery;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[Solr query] {0}", solrQuery.getQuery());
   }

   /**
    * Gets the solr query.
    *
    * @return the solr query
    */
   protected SolrQuery getSolrQuery() {
      return solrQuery;
   }

   /**
    * Sets the return fields.
    *
    * @param fields the new return fields
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#setReturnFields(java.lang.String[])
    */
   @Override
   public void setReturnFields(IndexField... fields) {
      for (IndexField field : fields) {
         solrQuery.addField(field.getField());
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQuery#setSortFields(org.openwis.metadataportal.common.search.SortDir, java.lang.String[])
    */
   @Override
   public void setSortFields(SortingInfo sort) {
      if (sort != null) {
         ORDER order;
         for (Pair<IndexField, SortDir> p : sort.getSortingColumns()) {
            order = ORDER.valueOf(p.two().name().toLowerCase());
            solrQuery.addSortField(p.one().getField(), order);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.AbstractSearchQuery#setRange(int, int)
    */
   @Override
   public void setRange(int from, int to) {
      super.setRange(from, to);
      if (solrQuery != null) {
         solrQuery.setStart(getFrom());
         solrQuery.setRows(getTo() - getFrom() + 1);
      }
   }

   /**
    * Checks if is spatial query.
    *
    * @return true, if is spatial query
    */
   @Override
   public boolean isSpatial() {
      return spatialQuery;
   }

   /**
    * Sets the spatial query.
    *
    * @param spatialQuery the new spatial query
    */
   protected void setSpatialQuery(boolean spatialQuery) {
      this.spatialQuery = spatialQuery;
   }

   /**
    * Checks if is term query.
    *
    * @return true, if is term query
    */
   protected boolean isTermQuery() {
      return termQuery;
   }

   /**
    * Sets the term query.
    *
    * @param termQuery the new term query
    */
   protected void setTermQuery(boolean termQuery) {
      this.termQuery = termQuery;
   }

}
