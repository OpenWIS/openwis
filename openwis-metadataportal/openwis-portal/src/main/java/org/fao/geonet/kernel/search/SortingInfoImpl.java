package org.fao.geonet.kernel.search;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.ObjectUtils;
import org.openwis.metadataportal.common.search.SortDir;

/**
 * The Class SortingInfoImpl. <P>
 * Explanation goes here. <P>
 */
public class SortingInfoImpl implements SortingInfo {

   /** The list. */
   private final List<Pair<IndexField, SortDir>> list;

   /**
    * Instantiates a new sorting info impl.
    */
   public SortingInfoImpl() {
      super();
      list = new CopyOnWriteArrayList<Pair<IndexField, SortDir>>();
   }

   /**
    * Instantiates a new sorting info impl.
    *
    * @param field the field
    * @param dir the dir
    */
   public SortingInfoImpl(IndexField field, SortDir dir) {
      this();
      add(field, dir);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return ObjectUtils.toString(list);
   }

   /**
    * Gets the sorting columns.
    *
    * @return the sorting columns
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.SortingInfo#getSortingColumns()
    */
   @Override
   public List<Pair<IndexField, SortDir>> getSortingColumns() {
      return Collections.unmodifiableList(list);
   }

   /**
    * Adds the.
    *
    * @param field the field
    * @param dir the dir
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.SortingInfo#add(org.fao.geonet.kernel.search.IndexField, org.openwis.metadataportal.common.search.SortDir)
    */
   @Override
   public synchronized void add(IndexField field, SortDir dir) {
      if (field != null && dir != null) {
         for (Pair<IndexField, SortDir> p : list) {
            if (field.equals(p.one())) {
               // field already added
               return;
            }
         }
         list.add(Pair.read(field, dir));
      }
   }
}
