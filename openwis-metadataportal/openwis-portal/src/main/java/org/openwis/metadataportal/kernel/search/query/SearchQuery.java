package org.openwis.metadataportal.kernel.search.query;

import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.SortingInfo;

/**
 * The Interface SearchQuery. <P>
 * Explanation goes here. <P>
 */
public interface SearchQuery {

   /**
    * Sets the return fields.
    *
    * @param fields the new return fields
    */
   void setReturnFields(IndexField... fields);

   /**
    * Sets the sort fields
    *
    * @param sort the sort direction.
    * @param fields the new sort fields.
    */
   void setSortFields(SortingInfo sort);

   /**
    * Sets the range.
    *
    * @param from the from
    * @param to the to
    */
   void setRange(int from, int to);

   /**
    * Gets the hits per page.
    *
    * @return the hits per page
    */
   int getHitsPerPage();

   /**
    * Sets the hits per page.
    *
    * @param hits the new hits per page
    */
   void setHitsPerPage(int hits);

   /**
    * Gets the from.
    *
    * @return the from
    */
   int getFrom();

   /**
    * Gets the to.
    *
    * @return the to
    */
   int getTo();

   /**
    * Checks if is the query contains spatial constraints.
    *
    * @return true, if is spatial
    */
   boolean isSpatial();

}
