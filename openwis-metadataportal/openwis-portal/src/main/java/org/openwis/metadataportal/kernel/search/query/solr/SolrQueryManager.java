package org.openwis.metadataportal.kernel.search.query.solr;

import java.net.MalformedURLException;

import jeeves.server.ServiceConfig;
import jeeves.utils.Log;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.util.NamedList;
import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.search.index.solr.SolRUtils;
import org.openwis.metadataportal.kernel.search.index.solr.SolrIndexManager;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.kernel.search.query.SearchQueryFactory;
import org.openwis.metadataportal.kernel.search.query.SearchResult;

/**
 * The Class SolrQueryManager. <P>
 * Explanation goes here. <P>
 */
public class SolrQueryManager implements IQueryManager<SolrSearchQuery> {

   /** The solr server. */
   private final String solrUrl;

   /** The query factory. */
   private final SolrQueryFactory queryFactory;

   /** The index manager. */
   private final SolrIndexManager indexManager;

   /**
    * Instantiates a new solr query manager.
    *
    * @param config the config
    */
   public SolrQueryManager(ServiceConfig config, SolrIndexManager indexManager) {
      super();
      solrUrl = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SOLR_URL);
      queryFactory = new SolrQueryFactory();
      this.indexManager = indexManager;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.IQueryManager#getQueryFactory()
    */
   @Override
   public SearchQueryFactory<SolrSearchQuery> getQueryFactory() {
      return queryFactory;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.IQueryManager#search(org.openwis.metadataportal.kernel.search.query.SearchQuery)
    */
   @Override
   public SearchResult search(SolrSearchQuery query) throws SearchException {
      SearchResult result;
      try {
         SolrServer solRServer = SolRUtils.getSolRServer(solrUrl, indexManager);
         if (solRServer == null) {
            throw new SearchException("SolR not available");
         }
         SolrQuery solrQuery = query.getSolrQuery();
         Integer rows = solrQuery.getRows();
         if (rows == null) {
            solrQuery.setRows(Integer.MAX_VALUE);
         }
         QueryResponse qr = solRServer.query(solrQuery);
         if (query.isTermQuery()) {
            @SuppressWarnings("unchecked")
            NamedList<NamedList<Integer>> nl = (NamedList<NamedList<Integer>>) qr.getResponse()
                  .get(CommonParams.TERMS);
            result = new SolrSearchTermResult(query, nl);
         } else {
            SolrDocumentList results = qr.getResults();
            result = new SolrSearchResult(query, results);
         }
         return result;
      } catch (SolrServerException e) {
         Log.error(Geonet.SEARCH_ENGINE, "Error when searching with SolR", e);
         throw new SearchException(e);
      } catch (MalformedURLException e) {
         throw new SearchException(e);
      }
   }

}
