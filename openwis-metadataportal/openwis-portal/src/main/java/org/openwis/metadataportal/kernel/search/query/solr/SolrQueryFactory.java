package org.openwis.metadataportal.kernel.search.query.solr;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;

import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.TermsParams;
import org.apache.solr.common.util.DateUtil;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.search.query.AbstractSearchQueryFactory;
import org.openwis.metadataportal.kernel.search.query.SearchQueryFactory;

/**
 * A factory for creating SolrQuery objects.
 */
public class SolrQueryFactory extends AbstractSearchQueryFactory<SolrSearchQuery> implements
      SearchQueryFactory<SolrSearchQuery> {

   /**
    * Instantiates a new solr query factory.
    *
    * @param context the context
    */
   public SolrQueryFactory() {
      super();
   }

   /**
    * Builds the solr query.
    *
    * @param q the q
    * @return the solr query
    */
   private SolrQuery buildSolrQuery(String q) {
      SolrQuery query = new SolrQuery();
      query.setQuery(q);
      query.setIncludeScore(true);
      return query;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#
    * buildTermQuery(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public SolrSearchQuery buildTermQuery(IndexField field) {
      SolrQuery query = new SolrQuery();
      query.set(TermsParams.TERMS, true);
      query.setQueryType("/" + CommonParams.TERMS);
      query.add(TermsParams.TERMS_FIELD, field.getField());
      SolrSearchQuery result = new SolrSearchQuery(query);
      result.setTermQuery(true);
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#
    * buildTermQuery(org.fao.geonet.kernel.search.IndexField, java.lang.String, int, int)
    */
   @Override
   public SolrSearchQuery buildTermQuery(IndexField field, String start, int maxResult,
         int countThreshold) {
      SolrSearchQuery result = this.buildTermQuery(field);

      SolrQuery query = result.getSolrQuery();
      if (StringUtils.isNotBlank(start)) {
         query.set(TermsParams.TERMS_PREFIX_STR, start);
      }
      if (maxResult > 0) {
         query.set(TermsParams.TERMS_LIMIT, maxResult);
      }
      if (countThreshold > 0) {
         query.set(TermsParams.TERMS_MINCOUNT, countThreshold);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildTermRangeQuery(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public SolrSearchQuery buildTermRangeQuery(IndexField field) {
      SolrQuery query = new SolrQuery();
      query.set(TermsParams.TERMS, true);
      query.setQueryType("/range");
      query.add(TermsParams.TERMS_FIELD, field.getField());
      SolrSearchQuery result = new SolrSearchQuery(query);
      result.setTermQuery(true);
      return result;

   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#escapeQueryChars(java.lang.String)
    */
   @Override
   public String escapeQueryChars(String value) {
      return ClientUtils.escapeQueryChars(value);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#escapeQueryCharsOmitWildCards(java.lang.String)
    */
   @Override
   public String escapeQueryCharsOmitWildCards(String s) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         // These characters are part of the query syntax and must be escaped
         if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
               || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
               || c == '|' || c == '&' || c == ';' || Character.isWhitespace(c)) {
            sb.append('\\');
         }
         sb.append(c);
      }
      return sb.toString();

   }

   /**
    * Builds the any query.
    *
    * @param value the value
    * @return the search query
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildAnyQuery(java.lang.String)
    */
   @Override
   public SolrSearchQuery buildAnyQuery(String value) {
      SolrQuery solrQuery = buildSolrQuery(value); // default search on any field
      return new SolrSearchQuery(solrQuery);
   }

   /**
    * Builds the query.
    *
    * @param field the field
    * @param value the value
    * @return the search query
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildQuery(java.lang.String, java.lang.Object)
    */
   @Override
   public SolrSearchQuery buildQuery(IndexField field, Object value) {
      String q = MessageFormat.format("{0}:{1}", field.getField(), String.valueOf(value));
      SolrQuery solrQuery = buildSolrQuery(q);
      return new SolrSearchQuery(solrQuery);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildFieldPresentQuery(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public SolrSearchQuery buildFieldPresentQuery(IndexField field) {
      String q = MessageFormat.format("{0}:*", field.getField());
      SolrQuery solrQuery = buildSolrQuery(q);
      return new SolrSearchQuery(solrQuery);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildFieldNotPresentQuery(org.fao.geonet.kernel.search.IndexField)
    */
   @Override
   public SolrSearchQuery buildFieldNotPresentQuery(IndexField field) {
      String q = MessageFormat.format("(*:* NOT {0}:*)", field.getField());
      SolrQuery solrQuery = buildSolrQuery(q);
      return new SolrSearchQuery(solrQuery);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildBetweenQuery(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public SolrSearchQuery buildBetweenQuery(IndexField field, String from, String to) {
      SolrSearchQuery result = null;
      try {
         Date fromDate = DateUtil.parseDate(from);
         String sFromDate = DateUtil.getThreadLocalDateFormat().format(fromDate);
         Date toDate = DateUtil.parseDate(to);
         String sToDate = DateUtil.getThreadLocalDateFormat().format(toDate);
         String q = MessageFormat.format("{0}:[{1} TO {2}]", field.getField(), sFromDate, sToDate);
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
      } catch (ParseException e) {
         Log.warning(
               Geonet.SEARCH_ENGINE,
               MessageFormat.format("Could not create query ''{0} between {1} & {2}''",
                     field.getField(), from, to), e);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildAfterQuery(org.fao.geonet.kernel.search.IndexField, java.lang.String)
    */
   @Override
   public SolrSearchQuery buildAfterQuery(IndexField dateField, String date) {
      return buildAfterQuery(dateField, date, true);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildAfterQuery(org.fao.geonet.kernel.search.IndexField, java.lang.String, boolean)
    */
   @Override
   public SolrSearchQuery buildAfterQuery(IndexField dateField, String date, boolean inclusive) {
      // FIXME Igor: handle inclusive flag
      SolrSearchQuery result = null;
      try {
         Date theDate = DateUtil.parseDate(date);
         String sDate = DateUtil.getThreadLocalDateFormat().format(theDate);
         String q = MessageFormat.format("{0}:[{1} TO *]", dateField.getField(), sDate);
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
      } catch (ParseException e) {
         Log.warning(
               Geonet.SEARCH_ENGINE,
               MessageFormat.format("Could not create query ''{0} after {1}''",
                     dateField.getField(), date), e);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildBeforeQuery(org.fao.geonet.kernel.search.IndexField, java.lang.String)
    */
   @Override
   public SolrSearchQuery buildBeforeQuery(IndexField dateField, String date) {
      return buildBeforeQuery(dateField, date, true);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildBeforeQuery(org.fao.geonet.kernel.search.IndexField, java.lang.String, boolean)
    */
   @Override
   public SolrSearchQuery buildBeforeQuery(IndexField dateField, String date, boolean inclusive) {
      // FIXME Igor: handle inclusive flag
      SolrSearchQuery result = null;
      try {
         Date theDate = DateUtil.parseDate(date);
         String sDate = DateUtil.getThreadLocalDateFormat().format(theDate);
         String q = MessageFormat.format("{0}:[* TO {1}]", dateField.getField(), sDate);
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
      } catch (ParseException e) {
         Log.warning(
               Geonet.SEARCH_ENGINE,
               MessageFormat.format("Could not create query ''{0} after {1}''",
                     dateField.getField(), date), e);
      }
      return result;
   }

   /**
    * Builds the all.
    *
    * @return the search query
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#buildAll()
    */
   @Override
   public SolrSearchQuery buildAll() {
      SolrQuery solrQuery = buildSolrQuery("*");
      return new SolrSearchQuery(solrQuery);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#or(org.openwis.metadataportal.kernel.search.query.SearchQuery, org.openwis.metadataportal.kernel.search.query.SearchQuery)
    */
   @Override
   public SolrSearchQuery or(SolrSearchQuery leftQuery, SolrSearchQuery rightQuery) {
      SolrSearchQuery result = null;
      if (leftQuery == null) {
         result = rightQuery;
      } else if (rightQuery == null) {
         result = leftQuery;
      } else {
         String q = MessageFormat.format("({0} OR {1})", leftQuery.getSolrQuery().getQuery(),
               rightQuery.getSolrQuery().getQuery());
         //         String q = MessageFormat.format("{0} {1}", leftQuery.getSolrQuery().getQuery(), rightQuery
         //               .getSolrQuery().getQuery());
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
         result.setSpatialQuery(leftQuery.isSpatial() || rightQuery.isSpatial());
         result.setTermQuery(leftQuery.isTermQuery() || rightQuery.isTermQuery());
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#and(org.openwis.metadataportal.kernel.search.query.SearchQuery, org.openwis.metadataportal.kernel.search.query.SearchQuery)
    */
   @Override
   public SolrSearchQuery and(SolrSearchQuery leftQuery, SolrSearchQuery rightQuery) {
      SolrSearchQuery result = null;
      if (leftQuery == null) {
         result = rightQuery;
      } else if (rightQuery == null) {
         result = leftQuery;
      } else {
         String leftQ = leftQuery.getSolrQuery().getQuery();
         String rightQ = rightQuery.getSolrQuery().getQuery();
         String q = MessageFormat.format("({0} AND {1})", leftQ, rightQ);
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
         result.setSpatialQuery(leftQuery.isSpatial() || rightQuery.isSpatial());
         result.setTermQuery(leftQuery.isTermQuery() || rightQuery.isTermQuery());
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#not(org.openwis.metadataportal.kernel.search.query.SearchQuery)
    */
   @Override
   public SolrSearchQuery not(SolrSearchQuery query) {
      SolrSearchQuery result = null;
      if (query != null) {
         String q = MessageFormat.format("NOT({0})", query.getSolrQuery().getQuery());
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
         result.setSpatialQuery(query.isSpatial());
         result.setTermQuery(query.isTermQuery());
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#fuzzy(org.openwis.metadataportal.kernel.search.query.SearchQuery, float)
    */
   @Override
   public SolrSearchQuery fuzzy(SolrSearchQuery query, float fuzzyFactor) {
      SolrSearchQuery result = null;
      if (query != null) {
         String q = MessageFormat.format("{0}~{1}", query.getSolrQuery().getQuery(),
               String.valueOf(fuzzyFactor));
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
      }
      result.setSpatialQuery(query.isSpatial());
      result.setTermQuery(query.isTermQuery());
      return result;
   }
   
   // Implémentation de la methode boost
   @Override
   public SolrSearchQuery boost(SolrSearchQuery query, int boostFactor) {
      SolrSearchQuery result = null;
      if (query != null) {
         String q = MessageFormat.format("{0}^{1}", query.getSolrQuery().getQuery(),
               String.valueOf(boostFactor));
         SolrQuery solrQuery = buildSolrQuery(q);
         result = new SolrSearchQuery(solrQuery);
      }
      result.setSpatialQuery(query.isSpatial());
      result.setTermQuery(query.isTermQuery());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.query.SearchQueryFactory#addSpatialQuery(org.openwis.metadataportal.kernel.search.query.SearchQuery, org.jdom.Element)
    */
   @Override
   public SolrSearchQuery addSpatialQuery(SolrSearchQuery query, Element xml, String filterVersion) {
      SolrSearchQuery result = null;
      if (query != null) {
         result = query;
         // use special Openwis searcher
         result.getSolrQuery().set("openwisRequest", Xml.getString(xml));
         result.getSolrQuery().set("defType", "OpenwisSearch");
         if (filterVersion != null) {
            result.getSolrQuery().set("filterVersion", filterVersion);
         }
         result.setSpatialQuery(true);
      }
      return result;
   }
}
