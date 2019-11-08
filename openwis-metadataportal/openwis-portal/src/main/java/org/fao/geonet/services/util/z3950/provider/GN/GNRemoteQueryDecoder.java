package org.fao.geonet.services.util.z3950.provider.GN;

import java.util.Stack;

import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import org.jzkit.search.util.QueryModel.Internal.ComplexNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.Internal.QueryNodeVisitor;
import org.openwis.metadataportal.kernel.search.query.IQueryManager;
import org.openwis.metadataportal.kernel.search.query.SearchQuery;
import org.openwis.metadataportal.kernel.search.query.SearchQueryFactory;
import org.springframework.context.ApplicationContext;

//--------------------------------------------------------------------------
//converts an internal query format query to GN xml
public class GNRemoteQueryDecoder<T extends SearchQuery> {

   /**
    * The Class QueryNodeVisitorExtension.
    */
   private final class QueryNodeVisitorExtension<Q extends SearchQuery> extends QueryNodeVisitor {

      /** The query. */
      private final Stack<Q> queries;

      /** The query factory. */
      private final SearchQueryFactory<Q> queryFactory;

      /**
       * Instantiates a new query node visitor extension.
       * @param searchQueryFactory
       */
      public QueryNodeVisitorExtension(SearchQueryFactory<Q> searchQueryFactory) {
         super();
         this.queryFactory = searchQueryFactory;
         this.queries = new Stack<Q>();
      }

      /**
       * {@inheritDoc}
       * @see org.jzkit.search.util.QueryModel.Internal.QueryNodeVisitor#visit(org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode)
       */
      @Override
      public void visit(AttrPlusTermNode aptn) {
         super.visit(aptn);

         Q query = null;

         // Attributes
         AttrValue accessPoint = (AttrValue) aptn.getAccessPoint();
         AttrValue relation = (AttrValue) aptn.getRelation();

         String use = getAttrVal(accessPoint);
         String rel = getAttrVal(relation);
         String content = aptn.getTermAsString(false);

         // Build field query
         if (StringUtils.isNumeric(use) && !use.isEmpty()) {
            switch (Integer.parseInt(use)) {
            case 4:
               // title & altTitle
               query = queryFactory.or(queryFactory.buildQuery(IndexField.TITLE, content),
                     queryFactory.buildQuery(IndexField.ALT_TITLE, content));
               break;
            case 62:
               // Abstract
               query = queryFactory.and(queryFactory.buildQuery(IndexField.ABSTRACT, content),
                     queryFactory.buildQuery(IndexField.DESCRIPTION, content));
               break;
            case 3102:
               // Abstract
               query = queryFactory.buildQuery(IndexField.DESCRIPTION, content);
               break;
            case 1012:
               // ChangeDate
               query = this.buildDateQuery(IndexField._CHANGE_DATE, rel, content);
               break;
            case 30:
               // CreateDate
               query = this.buildDateQuery(IndexField._CREATE_DATE, rel, content);
               break;
            case 31:
               // PublicationDate
               query = this.buildDateQuery(IndexField.PUBLICATION_DATE, rel, content);
               break;
            case 2072:
               // Temporal Extent Begin
               query = this.buildDateQuery(IndexField.TEMPORALEXTENT_BEGIN, rel, content);
               break;
            case 2073:
               // Temporal Extent End
               query = this.buildDateQuery(IndexField.TEMPORALEXTENT_END, rel, content);
               break;
            case 2059:
            case 3302:
               // SpatialDomain
               query = queryFactory.buildQuery(IndexField.CRS, content);
               break;
            case 2042:
            case 2061:
               // Place Keyword, Place
               query = queryFactory.buildQuery(IndexField.GEO_DESC_CODE, content);
               break;
            case 1031:
               // Type, ResourceType
               query = queryFactory.buildQuery(IndexField.KEYWORD_TYPE, content);
               break;
            case 1003:
               // Author
               query = queryFactory.buildQuery(IndexField.ORG_NAME, content);
               break;
            case 1034:
               // Format
               query = queryFactory.buildQuery(IndexField.FORMAT, content);
               break;
            case 2012:
               // FileId
               query = queryFactory.buildQuery(IndexField.FILE_ID, content);
               break;
            case 12:
               // Identifier
               query = queryFactory.buildQuery(IndexField.IDENTIFIER, content);
               break;
            case 21:
            case 29:
            case 2002:
            case 3121:
            case 3122:
               // Keyword, Subject, TopicCat
               query = queryFactory.or(queryFactory.or(
                     queryFactory.buildQuery(IndexField.KEYWORD, content),
                     queryFactory.buildQuery(IndexField.SUBJECT, content)), queryFactory
                     .buildQuery(IndexField.TOPIC_CAT, content));
               break;
            case 1016:
               // Any
               query = queryFactory.buildQuery(IndexField.ANYTEXT, content);
               break;
            case 2060:
               // Spatial Query
               query = this.handleSpatialQuery(rel, content);
               break;
            default:
               Log.warning(Geonet.Z3950_SERVER, "Access point unknow: " + use);
               break;
            }
         }
         // Push the query
         if (query != null) {
            queries.push(query);
         }
      }

      /**
       * Handle spatial query.
       *
       * @param relation the relation
       * @param content the content
       * @return the q
       */
      private Q handleSpatialQuery(String relation, String content) {
         Q query = null;
         // TODO Igor: Check content==WKT
         String wkt = content;
         String spatialRelation = null;
         if (StringUtils.isNumeric(relation) && !relation.isEmpty()) {
            switch (Integer.parseInt(relation)) {
            case 3:
               // Equals
               spatialRelation = "equal";
               break;
            case 7:
               // Overlaps
               spatialRelation = "overlaps";
               break;
            case 10:
               // fullyOutsideOf
               spatialRelation = "fullyOutsideOf";
               break;
            case 9:
               // Encloses
               spatialRelation = "encloses";
               break;
            case 8:
               // fullyEnclosedWithin
               spatialRelation = "within";
               break;
            default:
               break;
            }
            if (wkt != null && spatialRelation != null) {
               // Build XML
               Element xml = new Element("request");
               // Relation
               Element child = new Element("relation");
               xml.addContent(child);
               child.setText(spatialRelation);
               // Geometry
               child = new Element("geometry");
               xml.addContent(child);
               child.setText(wkt);
               query = queryFactory.addSpatialQuery(null, xml, null);
            }
         }
         return query;
      }

      /**
       * Builds the date query.
       *
       * @param field the field
       * @param relation the relation
       * @param structure the structure
       * @param date the date
       * @return the q
       */
      private Q buildDateQuery(IndexField field, String relation, String date) {
         Q query = null;

         if (StringUtils.isNumeric(relation) && !relation.isEmpty()) {
            switch (Integer.parseInt(relation)) {
            case 1:
               // Less Than
               query = queryFactory.buildBeforeQuery(field, date, false);
               break;
            case 2:
               // Less Or Equals Than
               query = queryFactory.buildBeforeQuery(field, date, true);
               break;
            case 3:
               // Equals
               query = queryFactory.buildQuery(field, date);
               break;
            case 4:
               // Greater Than
               query = queryFactory.buildAfterQuery(field, date, false);
               break;
            case 5:
               // Greater Or Equals Than
               query = queryFactory.buildAfterQuery(field, date, true);
               break;
            default:
               break;
            }
         }
         return query;
      }

      /**
       * {@inheritDoc}
       * @see org.jzkit.search.util.QueryModel.Internal.QueryNodeVisitor#visit(org.jzkit.search.util.QueryModel.Internal.ComplexNode)
       */
      @Override
      public void visit(ComplexNode cn) {
         super.visit(cn);

         Q query = null;
         Q rightQuery = queries.pop();
         Q leftQuery;
         switch (cn.getOp()) {
         case ComplexNode.COMPLEX_AND:
            leftQuery = queries.pop();
            query = queryFactory.and(leftQuery, rightQuery);
            break;
         case ComplexNode.COMPLEX_ANDNOT:
            query = queryFactory.not(rightQuery);
            break;
         case ComplexNode.COMPLEX_OR:
            leftQuery = queries.pop();
            query = queryFactory.or(leftQuery, rightQuery);
            break;
         case ComplexNode.COMPLEX_PROX:
         default:
            break;
         }
         queries.push(query);
      }

      /**
       * {@inheritDoc}
       * @see org.jzkit.search.util.QueryModel.Internal.QueryNodeVisitor#visit(org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode)
       */
      @Override
      public void visit(InternalModelRootNode rn) {
         super.visit(rn);
      }

      /**
       * {@inheritDoc}
       * @see org.jzkit.search.util.QueryModel.Internal.QueryNodeVisitor#onAttrPlusTermNode(org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode)
       */
      @Override
      public void onAttrPlusTermNode(AttrPlusTermNode aptn) {
         Log.debug(Geonet.Z3950_SERVER, "doing nothing..." + aptn); //TODO: find out how this is supposed to be used
      }

      /**
       * Gets the attr val.
       *
       * @param val the val
       * @return the attr val
       * extracts the last index of an attribute (e.G 1.4 becomes 4)
       */
      private String getAttrVal(AttrValue val) {

         if ((val == null) || (val.getValue() == null)) {
            return null;
         }

         String value = val.getValue();
         String ret = value;

         String[] temp = value.split("\\.");

         if ((temp != null) && (temp.length > 1)) {
            ret = temp[temp.length - 1];
         }

         return ret;
      }

      /**
       * Gets the query.
       *
       * @return the query
       */
      public Q getQuery() {
         // Only retrieve Metadata
         Q query = queryFactory.buildQuery(IndexField.IS_TEMPLATE, "n");
         for (Q q : queries) {
            query = queryFactory.and(query, q);
         }
         return query;
      }
   }

   /** The qnv. */
   private QueryNodeVisitorExtension<T> qnv;

   /**
    * Instantiates a new gN remote query decoder.
    *
    * @param queryMngr the query manager
    * @param qm the query model
    * @param ctx the application context
    */
   public GNRemoteQueryDecoder(IQueryManager<T> queryMngr, QueryModel qm, ApplicationContext ctx) {
      try {
         InternalModelRootNode rn = qm.toInternalQueryModel(ctx);
         this.qnv = new QueryNodeVisitorExtension<T>(queryMngr.getQueryFactory());
         qnv.visit(rn);
      } catch (InvalidQueryException e) {
         Log.error(Geonet.Z3950_SERVER, "Could not parse the query", e);
      }
   }

   /**
    * Gets the query.
    *
    * @return the query
    */
   public T getQuery() {
      return qnv.getQuery();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return this.getQuery().toString();
   }

}
