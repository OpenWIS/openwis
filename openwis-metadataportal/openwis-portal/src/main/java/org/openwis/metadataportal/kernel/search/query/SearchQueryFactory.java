package org.openwis.metadataportal.kernel.search.query;

import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.solr.SolrSearchQuery;

/**
 * A factory for creating SearchQuery objects.
 */
public interface SearchQueryFactory<T extends SearchQuery> {

   /**
    * Builds the any query.
    *
    * @param value the value
    * @return the search query
    */
   T buildAnyQuery(String value);

   /**
    * Builds the field present query.
    *
    * @param field the field
    * @return the t
    */
   T buildFieldPresentQuery(IndexField field);

   /**
    * Builds the field not present query.
    *
    * @param field the field
    * @return the t
    */
   T buildFieldNotPresentQuery(IndexField field);

   /**
    * Builds the query.
    *
    * @param field the field
    * @param value the value
    * @return the search query
    */
   T buildQuery(IndexField field, Object value);

   /**
    * Builds the after date query.
    *
    * @param dateField the date field
    * @param date the date
    * @return the search query
    */
   T buildAfterQuery(IndexField dateField, String date);

   /**
    * Builds the before date query.
    *
    * @param dateField the date field
    * @param date the date
    * @return the search query
    */
   T buildBeforeQuery(IndexField dateField, String date);

   /**
    * Builds the after date query.
    *
    * @param dateField the date field
    * @param date the date
    * @return the search query
    */
   T buildAfterQuery(IndexField dateField, String date, boolean inclusive);

   /**
    * Builds the before date query.
    *
    * @param dateField the date field
    * @param date the date
    * @return the search query
    */
   T buildBeforeQuery(IndexField dateField, String date, boolean inclusive);

   /**
    * Build between date query.
    *
    * @param field the field
    * @param from the from
    * @param to the queryo
    * @return the query
    */
   T buildBetweenQuery(IndexField field, String from, String to);

   /**
    * Builds the all.
    *
    * @return the search query
    */
   T buildAll();

   /**
    * Or.
    *
    * @param leftQuery the left query
    * @param rightQuery the right query
    * @return the search query
    */
   T or(T leftQuery, T rightQuery);

   /**
    * And.
    *
    * @param leftQuery the left query
    * @param rightQuery the right query
    * @return the search query
    */
   T and(T leftQuery, T rightQuery);

   /**
    * Not.
    *
    * @param query the query
    * @return the search query
    */
   T not(T query);

   /**
    * Fuzzy.
    *
    * @param fuzzyFactor the fuzzy factor
    * @return the search query
    */
   T fuzzy(T query, float fuzzyFactor);

   /**
    * Builds the term query.
    *
    * @param field the field
    * @return
    */
   T buildTermQuery(IndexField field);

   /**
    * Builds the term query.
    *
    * @param field the field
    * @param start the start
    * @param maxResult the max result
    * @param countThreshold the count threshold
    * @return
    */
   T buildTermQuery(IndexField field, String start, int maxResult, int countThreshold);

   /**
    * Builds the term range query.
    *
    * @param field the field
    * @return the search query
    */
   T buildTermRangeQuery(IndexField field);

   /**
    * Escape query chars.
    * {@link http://lucene.apache.org/java/docs/nightly/queryparsersyntax.html#Escaping%20Special%20Characters}
    * @param value the value
    * @return the escaped query
    */
   String escapeQueryChars(String value);

   /**
    * Escape query chars omit wild cards.
    *
    * @param value the value
    * @return the string
    */
   String escapeQueryCharsOmitWildCards(String value);

   /**
    * Builds the spatial query.
    *
    * @param query the query
    * @param xml the xml
    * @param filterVersion the filter version
    * @return the query
    */
   T addSpatialQuery(T query, Element xml, String filterVersion);
   /**
    * Boost.
    * Nouvelle methode pour prendre en compte le boost
    * @param boostFactor the boost factor
    * @return the search query
    */
   T boost(T query, int boostFactor);


}
