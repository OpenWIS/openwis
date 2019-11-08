package org.fao.geonet.kernel.csw.services.getrecords;

import jeeves.server.context.ServiceContext;

import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.kernel.search.SortingInfo;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.SearchResult;

public interface ICatalogSearcher {

   /**
    * Convert a filter to a search query and run the search.
    *
    * @param context the context
    * @param filterExpr the filter expression
    * @param filterVersion the filter version
    * @param sort the sort
    * @param resultType the result type
    * @param startPosition the start position
    * @param maxRecords the max records
    * @param maxHitsInSummary the max hits in summary
    * @return a list of id that match the given filter, ordered by sortFields
    * @throws CatalogException the catalog exception
    */
   public abstract SearchResult search(ServiceContext context,
         Element filterExpr, String filterVersion, SortingInfo sort,
         ResultType resultType, int startPosition, int maxRecords, int maxHitsInSummary)
         throws CatalogException;

}