package org.openwis.metadataportal.kernel.search.query;


/**
 * The Interface IQueryManager. <P>
 * Explanation goes here. <P>
 */
public interface IQueryManager<T extends SearchQuery> {

   /**
    * Gets the query factory.
    *
    * @return the query factory
    */
   SearchQueryFactory<T> getQueryFactory();

   /**
    * Search.
    *
    * @param query the query
    * @return the list
    * @throws SearchException the search exception
    */
   SearchResult search(T query) throws SearchException;
}
