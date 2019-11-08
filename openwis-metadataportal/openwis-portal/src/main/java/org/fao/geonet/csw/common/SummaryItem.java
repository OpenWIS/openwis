package org.fao.geonet.csw.common;

import org.fao.geonet.kernel.search.IndexField;

/**
 * The Class SummaryItem. <P>
 * Explanation goes here. <P>
 */
public class SummaryItem {

   /** The index field. */
   private final IndexField indexField;

   /** The name. */
   private final String name;

   /** The parent name. */
   private final String parentName;

   /** The max term. */
   private final int maxTerm;

   /** The sort by count. */
   private final boolean sortByCount;

   /**
    * Instantiates a new summary item.
    *
    * @param indexField the index field
    * @param name the name
    * @param parentName the parent name
    * @param maxTerm the max term
    * @param sortByCount the sort by count
    */
   public SummaryItem(IndexField indexField, String name, String parentName, int maxTerm,
         boolean sortByCount) {
      super();
      this.indexField = indexField;
      this.name = name;
      this.parentName = name;
      this.maxTerm = maxTerm;
      this.sortByCount = sortByCount;
   }

   /**
    * Gets the index field.
    *
    * @return the index field
    */
   public IndexField getIndexField() {
      return indexField;
   }

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the parent name.
    *
    * @return the parent name
    */
   public String getParentName() {
      return parentName;
   }

   /**
    * Gets the max term.
    *
    * @return the max term
    */
   public int getMaxTerm() {
      return maxTerm;
   }

   /**
    * Checks if is sort by count.
    *
    * @return true, if is sort by count
    */
   public boolean isSortByCount() {
      return sortByCount;
   }

}
