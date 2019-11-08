package org.openwis.metadataportal.search.solr.spatial.parser;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrQueryParser;
import org.fao.geonet.kernel.search.spatial.XmlUtils;
import org.jdom.Element;
import org.openwis.metadataportal.search.solr.spatial.OpenwisGeometryTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OpenwisLuceneQParser. <P>
 * Explanation goes here. <P>
 */
public class OpenwisLuceneQParser extends QParser {

   /** The openwis spatial request parameter. */
   private static final String OPENWIS_SPATIAL_REQUEST = "openwisRequest";

   /** The filter version parameter. */
   private static final String FILTER_VERSION = "filterVersion";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(OpenwisLuceneQParser.class);

   /** The parser. */
   private SolrQueryParser lparser;

   /** The geometry filter. */
   private final OpenwisGeometryTool geometryFilter;

   /**
    * Instantiates a new OpenWOS lucene q parser.
    *
    * @param qstr the query strings
    * @param localParams the local params
    * @param params the params
    * @param req the request
    */
   public OpenwisLuceneQParser(String qstr, SolrParams localParams, SolrParams params,
         SolrQueryRequest req) {
      super(qstr, localParams, params, req);
      OpenwisGeometryTool filter = null;
      try {
         filter = OpenwisGeometryTool.getInstance();
      } catch (SecurityException e) {
         logger.error("Fail to create geometry filter", e);
      } finally {
         geometryFilter = filter;
      }
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.search.QParser#parse()
    */
   @Override
   public Query parse() throws ParseException {
      String qstr = getString();

      String defaultField = getParam(CommonParams.DF);
      if (defaultField == null) {
         defaultField = getReq().getSchema().getDefaultSearchFieldName();
      }
      lparser = new SolrQueryParser(this, defaultField);

      // these could either be checked & set here, or in the SolrQueryParser constructor
      String opParam = getParam(QueryParsing.OP);
      if (opParam != null) {
         lparser.setDefaultOperator("AND".equals(opParam) ? QueryParser.Operator.AND
               : QueryParser.Operator.OR);
      } else {
         // try to get default operator from schema
         QueryParser.Operator operator = getReq().getSchema().getSolrQueryParser(null)
               .getDefaultOperator();
         lparser.setDefaultOperator(null == operator ? QueryParser.Operator.OR : operator);
      }

      // Parse query
      Query query = lparser.parse(qstr);
      query = addSpatialFilter(query);
      return query;
   }

   /**
    * Adds the spatial filter.
    *
    * @param query the query
    * @return the query
    */
   private Query addSpatialFilter(Query query) {
      Query result = query;
      String sXml = getParam(OPENWIS_SPATIAL_REQUEST);
      String filterVersion = getParam(FILTER_VERSION);
      if (sXml != null && !"".equals(sXml)) {
         try {
            Element request = XmlUtils.loadString(sXml, false);

            Filter filter = null;
            if (filterVersion != null) {
               filter = geometryFilter.buildFilter(query, request, filterVersion);
            } else {
               filter = geometryFilter.buildFilter(query, request);
            }
            if (filter != null) {
               result = new FilteredQuery(query, new CachingWrapperFilter(filter));
            }
         } catch (Exception e) {
            logger.error("Could not create spatial filter", e);
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.search.QParser#getDefaultHighlightFields()
    */
   @Override
   public String[] getDefaultHighlightFields() {
      return new String[] {lparser.getField()};
   }

}