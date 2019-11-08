package org.fao.geonet.kernel.search;

import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;

import org.fao.geonet.kernel.setting.SettingInfo;
import org.openwis.metadataportal.kernel.search.AsyncSearchManagerImpl;
import org.openwis.metadataportal.kernel.search.SearchManagerImpl;
import org.openwis.metadataportal.kernel.search.index.IIndexManager;
import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.solr.SolrIndexManager;
import org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory;
import org.openwis.metadataportal.kernel.search.query.solr.SolrSearchQueryManagerFactory;

/**
 * A factory for creating SearchManager objects.
 * FIXME Igor: Use config.xml for IOC
 */
public class SearchManagerFactory {
   /** The search manager. */
   private static ISearchManager searchManager;

   /** The search manager factory. */
   @SuppressWarnings("rawtypes")
   private static SearchQueryManagerFactory searchManagerFactory;

   /** The index manager. */
   private static IIndexManager indexManager;

   /**
    * Builds the search manager.
    *
    * @param appPath the application path
    * @param config the config
    * @param si the setting info
    * @return the search manager
    * @throws Exception the exception
    */
   public static synchronized ISearchManager createSearchManager(Dbms dbms, String appPath,
         ServiceConfig config, SettingInfo si) throws Exception {
      if (searchManager == null) {
         SearchManagerImpl sm = new SearchManagerImpl(dbms, appPath, config, si);
         searchManager = new AsyncSearchManagerImpl(sm);
      }
      return searchManager;
   }

   /**
    * Gets the query manager factory.
    *
    * @param config the config
    * @param appPath the application path
    * @return the query manager factory
    */
   @SuppressWarnings("rawtypes")
   public static synchronized SearchQueryManagerFactory getQueryManagerFactory(
         ServiceConfig config, String appPath) {
      if (searchManagerFactory == null) {
         try {
            searchManagerFactory = new SolrSearchQueryManagerFactory(config, appPath,
                  (SolrIndexManager) getIndexManager(config, appPath));
         } catch (IndexException e) {
            // Nothing to do
         }
      }
      return searchManagerFactory;
   }

   /**
    * Gets the index manager.
    *
    * @param config the config
    * @param appPath the application path
    * @return the index manager
    * @throws IndexException the index exception
    */
   public static IIndexManager getIndexManager(ServiceConfig config, String appPath)
         throws IndexException {
      if (indexManager == null) {
         indexManager = new SolrIndexManager(config, appPath);
      }
      return indexManager;
   }
}
