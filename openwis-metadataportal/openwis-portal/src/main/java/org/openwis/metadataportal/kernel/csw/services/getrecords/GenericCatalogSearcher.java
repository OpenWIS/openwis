package org.openwis.metadataportal.kernel.csw.services.getrecords;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.csw.common.exceptions.NoApplicableCodeEx;
import org.fao.geonet.kernel.csw.services.getrecords.FieldMapper;
import org.fao.geonet.kernel.csw.services.getrecords.ICatalogSearcher;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.SortingInfo;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchQuery;
import org.openwis.metadataportal.kernel.search.query.SearchQueryFactory;
import org.openwis.metadataportal.kernel.search.query.SearchQueryManagerFactory;
import org.openwis.metadataportal.kernel.search.query.SearchResult;

/**
 * The Class SolrCatalogSearcher. <P>
 * Explanation goes here. <P>
 *
 * @param <T> the generic SearchQuery type
 */
public class GenericCatalogSearcher<T extends SearchQuery> implements ICatalogSearcher {

   /** The query manager factory. */
   private final SearchQueryManagerFactory<T> queryManagerFactory;

   /** List of available geographical operations . */
   private static final List<String> GEOGRAPHICAL_OPERATIONS = Arrays.asList("Equals", "Disjoint",
         "Touches", "Within", "Overlaps", "Crosses", "Intersects", "Contains", "DWithin", "Beyond",
         "BBOX");

   /** List of logical operations . */
   private static final List<String> LOGICAL_OPERATIONS = Arrays.asList("And", "Or", "Not");

   /**
    * Instantiates a new solr catalog searcher.
    *
    * @param queryManagerFactory the query manager factory
    */
   public GenericCatalogSearcher(SearchQueryManagerFactory<T> queryManagerFactory) {
      this.queryManagerFactory = queryManagerFactory;
   }

   /**
    * Search.
    *
    * @param context the context
    * @param filterExpr the filter expression
    * @param filterVersion the filter version
    * @param sort the sort
    * @param resultType the result type
    * @param startPosition the start position
    * @param maxRecords the max records
    * @param maxHitsInSummary the max hits in summary
    * @return the search result
    * @throws CatalogException the catalog exception
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.csw.services.getrecords.ICatalogSearcher#search(ServiceContext, Element, String, SortingInfo, ResultType, int, int, int)
    */
   @Override
   public SearchResult search(ServiceContext context, Element filterExpr, String filterVersion,
         SortingInfo sort, ResultType resultType, int startPosition, int maxRecords,
         int maxHitsInSummary) throws CatalogException {
      SearchResult result = null;
      try {
         T query = null;

         IQueryManager<T> qm = this.queryManagerFactory.buildIQueryManager();
         SearchQueryFactory<T> queryFactory = qm.getQueryFactory();

         query = this.buildQuery(queryFactory, filterExpr, filterVersion);

         // Range
         // CSW starts index at 1 whereas SolR starts at 0...
         int solrStartPosition = startPosition;
         if (solrStartPosition > 0) {
            solrStartPosition--;
         }
         query.setRange(solrStartPosition, solrStartPosition + maxRecords - 1);

         // Sorting
         query.setSortFields(sort);

         // IndexField restriction for ResultType
         switch (resultType) {
         case HITS:
         case VALIDATE:
         case RESULTS:
            query.setReturnFields(IndexField.ID, IndexField.KEYWORD);
            break;
         case RESULTS_WITH_SUMMARY:
            query.setReturnFields(IndexField.ID, IndexField.KEYWORD, IndexField._CREATE_DATE,
                  IndexField.DENOMINATOR, IndexField.CATEGORY_NAME,
                  IndexField.SPATIAL_REPRESENTATION, IndexField.ORG_NAME, IndexField.TYPE);
         default:
            // Return all fields
            break;
         }

         result = qm.search(query);
      } catch (Exception e) {
         Log.error(Geonet.CSW_SEARCH, e.getMessage(), e);
         throw new NoApplicableCodeEx("Error during search : " + e);
      }
      return result;
   }

   /**
    * Builds the query.
    *
    * @param queryFactory the query factory
    * @param filter the filter
    * @param filterVersion the filter version
    * @return the query
    * @throws JDOMException the jDOM exception
    * @throws ParseException the parse exception
    */
   @SuppressWarnings("unchecked")
   private T buildQuery(SearchQueryFactory<T> queryFactory, Element filter, String filterVersion)
         throws JDOMException, ParseException {
      T query;

      Element selectElement = Xml.selectElement(filter, "//*[ogc:PropertyName='_isTemplate']");
      if (selectElement == null) {
         // By default select not template
         query = queryFactory.buildQuery(IndexField.IS_TEMPLATE, "n");
      } else {
         query = null;
      }
      // build Query
      for (Element filterElt : (List<Element>) filter.getChildren()) {
         try {
            query = applyFilterElement(query, queryFactory, filterElt, filterVersion);
         } catch (InvalidFilter e) {
            Log.warning(Geonet.CSW_SEARCH,
                  "Could not handle this element:\n" + Xml.getString(e.getFilter()));
         }
      }

      if (Log.isInfo(Geonet.CSW_SEARCH)) {
         Log.info(Geonet.CSW_SEARCH, "Search Query: " + query);
      }
      return query;
   }

   /**
    * Apply filter element.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @param filterVersion the filter version
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyFilterElement(T query, SearchQueryFactory<T> queryFactory, Element filterElt,
         String filterVersion) throws ParseException, InvalidFilter {
      T result = null;
      String name = filterElt.getName();

      Namespace ns = filterElt.getNamespace();
      if (Csw.NAMESPACE_OGC.equals(ns)) {
         if (name.startsWith("Property")) {
            // Property operations
            result = applyPropertyOperation(query, queryFactory, filterElt);
         } else if (LOGICAL_OPERATIONS.contains(name)) {
            // Logical Operation
            result = applyLogicalOperation(query, queryFactory, filterElt, filterVersion);
         } else if (GEOGRAPHICAL_OPERATIONS.contains(name)) {
            // Geographical Operation
            result = applyGeoOperation(query, queryFactory, filterElt, filterVersion);
         }
      }
      // check result
      if (result == null) {
         throw new InvalidFilter(filterElt);
      }

      return result;
   }

   /**
    * Apply logical operation.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter elt
    * @param filterVersion the filter version
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyLogicalOperation(T query, SearchQueryFactory<T> queryFactory, Element filterElt,
         String filterVersion) throws ParseException, InvalidFilter {
      T result = null;
      String name = filterElt.getName();
      if ("And".equals(name)) {
         result = applyAnd(query, queryFactory, filterElt, filterVersion);
      } else if ("Or".equals(name)) {
         result = applyOr(query, queryFactory, filterElt, filterVersion);
      } else if ("Not".equals(name)) {
         result = applyNot(query, queryFactory, filterElt, filterVersion);
      }
      return result;
   }

   /**
    * Apply geographical operation.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @param filterVersion the filter version
    * @return the query
    */
   private T applyGeoOperation(T query, SearchQueryFactory<T> queryFactory, Element filterElt,
         String filterVersion) {
      return queryFactory.addSpatialQuery(query, filterElt, filterVersion);
   }

   /**
    * Apply property operation.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyPropertyOperation(T query, SearchQueryFactory<T> queryFactory, Element filterElt)
         throws ParseException, InvalidFilter {
      T result = null;

      String name = filterElt.getName();
      if ("PropertyIsLike".equals(name)) {
         result = applyPropertyIsLike(query, queryFactory, filterElt);
      } else if ("PropertyIsBetween".equals(name)) {
         result = applyPropertyIsBetween(query, queryFactory, filterElt);
      } else if ("PropertyIsNull".equals(name)) {
         throw new InvalidFilter(filterElt);
      } else {
         String property = filterElt.getChildText("PropertyName", Csw.NAMESPACE_OGC);
         String literal = filterElt.getChildText("Literal", Csw.NAMESPACE_OGC);
         IndexField field = FieldMapper.map(property);

         // Check Args
         if (field == null) {
            throw new InvalidFilter(filterElt);
         } else if (literal == null) {
            throw new InvalidFilter(filterElt);
         }

         if ("PropertyIsEqualTo".equals(name)) {
            result = applyPropertyIsEqualTo(query, queryFactory, field, literal);
         } else if ("PropertyIsNotEqualTo".equals(name)) {
            result = queryFactory.not(applyPropertyIsEqualTo(query, queryFactory, field, literal));
         } else if ("PropertyIsLessThan".equals(name)) {
            result = applyPropertyIsLessThan(query, queryFactory, field, literal, false);
         } else if ("PropertyIsLessThanOrEqualTo".equals(name)
               || ("PropertyIsLessThanEqualTo".equals(name))) {
            result = applyPropertyIsLessThan(query, queryFactory, field, literal, true);
         } else if ("PropertyIsGreaterThan".equals(name)) {
            result = applyPropertyIsGreaterThan(query, queryFactory, field, literal, false);
         } else if ("PropertyIsGreaterThanOrEqualTo".equals(name)
               || "PropertyIsGreaterThanEqualTo".equals(name)) {
            result = applyPropertyIsGreaterThan(query, queryFactory, field, literal, true);
         }
      }

      // check result
      if (result == null) {
         throw new InvalidFilter(filterElt);
      }
      return result;
   }

   /**
    * Apply and.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @param filterVersion the filter version
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyAnd(T query, SearchQueryFactory<T> queryFactory, Element filterElt,
         String filterVersion) throws ParseException, InvalidFilter {
      T result = null;
      @SuppressWarnings("unchecked")
      List<Element> children = filterElt.getChildren();
      if (!children.isEmpty()) {
         T q;
         for (Element child : children) {
            q = applyFilterElement(null, queryFactory, child, filterVersion);
            if (q.isSpatial()) {
               throw new InvalidFilter(filterElt);
            } else {
               result = queryFactory.and(result, q);
            }
         }
      } else {
         throw new InvalidFilter(filterElt);
      }
      return result;
   }

   /**
    * Apply or.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @param filterVersion the filter version
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyOr(T query, SearchQueryFactory<T> queryFactory, Element filterElt,
         String filterVersion) throws ParseException, InvalidFilter {
      T result = null;
      @SuppressWarnings("unchecked")
      List<Element> children = filterElt.getChildren();
      if (!children.isEmpty()) {
         T q;
         for (Element child : children) {
            q = applyFilterElement(null, queryFactory, child, filterVersion);
            result = queryFactory.or(result, q);
         }
      } else {
         throw new InvalidFilter(filterElt);
      }
      return result;
   }

   /**
    * Apply not.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @param filterVersion the filter version
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyNot(T query, SearchQueryFactory<T> queryFactory, Element filterElt,
         String filterVersion) throws ParseException, InvalidFilter {
      T result;
      @SuppressWarnings("unchecked")
      List<Element> children = filterElt.getChildren();
      if (children.size() == 1) { // Unary operator
         Element child = children.get(0);
         T q = applyFilterElement(null, queryFactory, child, filterVersion);
         result = queryFactory.and(query, queryFactory.not(q));
      } else {
         throw new InvalidFilter(filterElt);
      }
      return result;
   }

   /**
    * Apply property is equal to.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param field the field
    * @param literal the literal
    * @return the query
    * @throws ParseException the parse exception
    */
   private T applyPropertyIsEqualTo(T query, SearchQueryFactory<T> queryFactory, IndexField field,
         String literal) throws ParseException {
      T result = null;

      // FIXME Igor: check similarity
      T right;
      if (field != null) {
         right = queryFactory.buildQuery(field, queryFactory.escapeQueryChars(literal));
         result = queryFactory.and(query, right);
      }
      return result;
   }

   /**
    * Apply property is like.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyPropertyIsLike(T query, SearchQueryFactory<T> queryFactory, Element filterElt)
         throws ParseException, InvalidFilter {
      T result;

      String property = filterElt.getChildText("PropertyName", Csw.NAMESPACE_OGC);
      String literal = filterElt.getChildText("Literal", Csw.NAMESPACE_OGC);

      String wildCard = filterElt.getAttributeValue("wildCard");
      String singleChar = filterElt.getAttributeValue("singleChar");
      String escapeChar = filterElt.getAttributeValue("escapeChar");
      // FIXME not handled String matchCase = filterElt.getAttributeValue("matchCase");

      IndexField field = FieldMapper.map(property);
      T right;

      if (field != null && wildCard != null && StringUtils.isNotBlank(wildCard)
            && singleChar != null && StringUtils.isNotBlank(singleChar) && escapeChar != null
            && StringUtils.isNotBlank(escapeChar)) {
         String queryChars = this.escapeQueryCharsOmitWildCards(literal, wildCard.charAt(0),
               singleChar.charAt(0), escapeChar.charAt(0));
         right = queryFactory.buildQuery(field, queryChars);
         result = queryFactory.and(query, right);
      } else {
         throw new InvalidFilter(filterElt);
      }
      return result;
   }

   /**
    * Escape query chars omit wild cards.
    *
    * @param s the string
    * @param wildCard the wild card
    * @param singleChar the single char
    * @param escapeChar the escape char
    * @return the string
    * @throws ParseException the parse exception
    */
   private String escapeQueryCharsOmitWildCards(String s, char wildCard, char singleChar,
         char escapeChar) throws ParseException {
      StringBuilder sb = new StringBuilder();
      char c;
      char d;
      boolean escaped = false;
      for (int i = 0; i < s.length(); i++) {
         c = s.charAt(i);
         if (i < (s.length() - 1)) {
            d = s.charAt(i + 1);
            if (c == escapeChar) {
               if ((d == wildCard) || (d == singleChar) || (d == escapeChar)) {
                  escaped = true;
                  continue;
               } else {
                  throw new ParseException(s, i);
               }
            }
         }

         if (c == wildCard && !escaped) {
            sb.append('*');
            continue;
         } else if (c == singleChar && !escaped) {
            sb.append('?');
            continue;
         }

         // These characters are part of the query syntax and must be escaped
         if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
               || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
               || c == '|' || c == '&' || c == ';' || c == '*' || c == '?'
               || Character.isWhitespace(c)) {
            sb.append('\\');
         }
         sb.append(c);
         escaped = false;
      }
      return sb.toString();
   }

   /**
    * Apply property is less than.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param field the field
    * @param literal the literal
    * @param inclusive the inclusive
    * @return the query
    * @throws ParseException the parse exception
    */
   private T applyPropertyIsLessThan(T query, SearchQueryFactory<T> queryFactory, IndexField field,
         String literal, boolean inclusive) throws ParseException {
      T result = null;

      T right;
      if (field != null) {
         right = queryFactory.buildBeforeQuery(field, literal, inclusive);
         result = queryFactory.and(query, right);
      }
      return result;
   }

   /**
    * Apply property is less than.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param field the field
    * @param literal the literal
    * @param inclusive the inclusive
    * @return the query
    * @throws ParseException the parse exception
    */
   private T applyPropertyIsGreaterThan(T query, SearchQueryFactory<T> queryFactory,
         IndexField field, String literal, boolean inclusive) throws ParseException {
      T result = null;

      T right;
      if (field != null) {
         right = queryFactory.buildAfterQuery(field, literal, inclusive);
         result = queryFactory.and(query, right);
      }
      return result;
   }

   /**
    * Apply property is between.
    *
    * @param query the query
    * @param queryFactory the query factory
    * @param filterElt the filter element
    * @return the query
    * @throws ParseException the parse exception
    * @throws InvalidFilter the invalid filter
    */
   private T applyPropertyIsBetween(T query, SearchQueryFactory<T> queryFactory, Element filterElt)
         throws ParseException, InvalidFilter {
      T result;

      String property = filterElt.getChildText("PropertyName", Csw.NAMESPACE_OGC);

      Element lowerElt = filterElt.getChild("LowerBoundary", Csw.NAMESPACE_OGC);
      Element upperElt = filterElt.getChild("UpperBoundary", Csw.NAMESPACE_OGC);

      if (lowerElt == null || upperElt == null) {
         throw new InvalidFilter(filterElt);
      } else {
         String lower = lowerElt.getChildText("Literal", Csw.NAMESPACE_OGC);
         String upper = upperElt.getChildText("Literal", Csw.NAMESPACE_OGC);

         IndexField field = FieldMapper.map(property);
         T right;

         if (field != null) {
            right = queryFactory.buildBetweenQuery(field, lower, upper);
            result = queryFactory.and(query, right);
         } else {
            throw new InvalidFilter(filterElt);
         }
      }
      return result;
   }
}
