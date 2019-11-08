package org.openwis.metadataportal.services.util;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.common.search.SearchResultWrapper;

/**
 * Provides "paging" functions for evaluation of a service search result
 * (if configured by the service parameter "start" and "limit")
 * 
 */
public class ServiceResultPager<T> extends SearchResultWrapper<T> {
   
   private ServiceParameter serviceParameter;
   
   /**
    * Default constructor.
    * Builds a ServiceResultLimiter.
    * @param parameter service parameter if defined
    * @param totalCount total count of service results
    * @param searchResult search result list
    */
   public ServiceResultPager(final ServiceParameter parameter, final int totalCount, final List<T> searchResult)  {
      super(totalCount, searchResult);
      serviceParameter = parameter;      
   }

   
   /**
    * @see org.openwis.metadataportal.common.search.SearchResultWrapper#getRows()
    * @return
    */
   @Override
   public List<T> getRows() {
      if (hasPagingParameters()) {
         return extractPage();
      }
      else {
         return super.getRows();
      }
   }   
   
   /**
    * Extracts a "page" of elements from an original search result list
    * determined by service parameter start and limit (if configured)
    * If not configured the entire result list will be returned unchanged.
    * @return
    */
   private List<T> extractPage() {
      List<T> searchResultList = super.getRows();
      List<T> pageResult = new ArrayList<T>();
      
      if (searchResultList != null) {
         if (serviceParameter != null) {
            int start = serviceParameter.getStart();
            int limit = serviceParameter.getLimit();
            
            for (int i = start; i < start + limit && i < searchResultList.size(); i++) {
               pageResult.add(searchResultList.get(i));
            }
         }
         else {
            // should not get here
            pageResult.addAll(searchResultList);
         }
      }
      return pageResult;
   }
   
   private boolean hasPagingParameters() {
      return serviceParameter != null && 
             serviceParameter.hasStart() && 
             serviceParameter.hasLimit();      
   }
}
