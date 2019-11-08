package org.openwis.metadataportal.kernel.search.query;

import java.util.List;
import java.util.Map;

import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;

/**
 * The Interface SearchResult. <P>
 * Explanation goes here. <P>
 */
public interface SearchResult extends Iterable<SearchResultDocument> {

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
    * Gets the query.
    *
    * @return the query
    */
   SearchQuery getQuery();

   /**
    * Gets the count.
    *
    * @return the count
    */
   int getCount();

   /**
    * Gets the documents.
    *
    * @return the documents
    */
   List<SearchResultDocument> getDocuments();

   /**
    * To summary.
    * @param resultType
    *
    * @return the element
    */
   Element toSummary(ResultType resultType);

   /**
    * To present.
    *
    * @return the element
    */
   Element toPresent();

   /**
    * Gets the.
    *
    * @return the element
    */
   Element get();

   /**
    * Gets the document string field.
    *
    * @param field the field
    * @return the document string field
    */
   List<String> getDocumentStringField(IndexField field);

   /**
    * Gets the count string field.
    *
    * @param field the field
    * @return the count string field
    */
   Map<String, Integer> getCountStringField(IndexField field);
}
