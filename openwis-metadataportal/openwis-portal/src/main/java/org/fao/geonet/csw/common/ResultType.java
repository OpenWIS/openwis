package org.fao.geonet.csw.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.fao.geonet.csw.common.exceptions.InvalidParameterValueEx;
import org.fao.geonet.kernel.search.IndexField;

/**
 * The Enum ResultType. <P>
 * Explanation goes here. <P>
 */
public enum ResultType {

   /** The hits. */
   HITS("hits", new SummaryItem(IndexField.KEYWORD, "keyword", "keywords", 100, true)),
   /** The result. */
   RESULTS("results", new SummaryItem(IndexField.KEYWORD, "keyword", "keywords", 100, true)),
   /** The result with summary. */
   RESULTS_WITH_SUMMARY("results_with_summary", new SummaryItem(IndexField.DENOMINATOR,
         "denominator", "denominator", 10, false), new SummaryItem(IndexField.KEYWORD, "keyword",
         "keywords", 10, true), new SummaryItem(IndexField.CATEGORY_NAME, "category", "categories",
         10, false), new SummaryItem(IndexField.SPATIAL_REPRESENTATION, "spatialRepresentation",
         "spatialRepresentations", 10, true), new SummaryItem(IndexField.ORG_NAME,
         "organizationName", "organizationNames", 10, true), new SummaryItem(
         IndexField.SERVICE_TYPE, "serviceType", "serviceTypes", 10, true), new SummaryItem(
         IndexField.TYPE, "type", "types", 10, true), new SummaryItem(IndexField._CREATE_DATE,
         "created", "created", 10, true)),
   /** The validate. */
   VALIDATE("validate");

   /** The type. */
   private String type;

   /** The items. */
   private final List<SummaryItem> items;

   /**
    * Instantiates a new result type.
    *
    * @param type the type
    * @param items the items
    */
   private ResultType(String type, SummaryItem... items) {
      this.type = type;
      this.items = Arrays.asList(items);
   }

   /**
    * Gets the items.
    *
    * @return the items
    */
   public List<SummaryItem> getItems() {
      return Collections.unmodifiableList(items);
   }

   /**
    * To string.
    *
    * @return the string
    * {@inheritDoc}
    * @see java.lang.Enum#toString()
    */
   @Override
   public String toString() {
      return type;
   }

   /**
    * Parses the.
    *
    * @param type the type
    * @return the result type
    * @throws InvalidParameterValueEx the invalid parameter value ex
    */
   public static ResultType parse(String type) throws InvalidParameterValueEx {
      if (type == null)
         return HITS;
      for (ResultType rtype : ResultType.values()) {
         if (type.equals(rtype.toString()))
            return rtype;
      }
      throw new InvalidParameterValueEx("resultType", type);
   }

}
