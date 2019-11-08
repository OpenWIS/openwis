package org.openwis.metadataportal.kernel.search.query;


/**
 * A factory for creating SearchQueryManager objects.
 */
public interface SearchQueryManagerFactory<T extends SearchQuery> {

   /**
    * Builds the query manager.
    *
    * @return the i query manager
    * @throws SearchException the search exception
    */
   public IQueryManager<T> buildIQueryManager() throws SearchException;
}
