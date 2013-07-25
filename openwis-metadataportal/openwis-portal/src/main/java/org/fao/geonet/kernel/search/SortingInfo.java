package org.fao.geonet.kernel.search;

import java.util.List;

import org.openwis.metadataportal.common.search.SortDir;

/**
 * The Interface SortingInfo. <P>
 * Explanation goes here. <P>
 */
public interface SortingInfo {

   /**
    * Gets the sorting columns.
    *
    * @return the sorting columns
    */
   List<Pair<IndexField, SortDir>> getSortingColumns();

   /**
    * Adds the.
    *
    * @param field the field
    * @param dir the dir
    */
   void add(IndexField field, SortDir dir);

}
