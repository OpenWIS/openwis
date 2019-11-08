package org.openwis.metadataportal.kernel.search.query.solr;

import jeeves.server.ServiceConfig;

import org.openwis.metadataportal.kernel.search.index.solr.SolrIndexManager;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory;

/**
 * A factory for creating SearchQueryManager objects.
 */
public class SolrSearchQueryManagerFactory implements SearchQueryManagerFactory<SolrSearchQuery> {

   /** The query manager. */
   private final IQueryManager<SolrSearchQuery> qm;

   /**
    * Instantiates a new search query manager factory.
    *
    * @param config the config
    * @param appPath the application path
    */
   public SolrSearchQueryManagerFactory(ServiceConfig config, String appPath,
         SolrIndexManager indexManager) {
      super();
      qm = new SolrQueryManager(config, indexManager);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory#buildIQueryManager()
    */
   @Override
   public IQueryManager<SolrSearchQuery> buildIQueryManager() throws SearchException {
      return qm;
   }
}
