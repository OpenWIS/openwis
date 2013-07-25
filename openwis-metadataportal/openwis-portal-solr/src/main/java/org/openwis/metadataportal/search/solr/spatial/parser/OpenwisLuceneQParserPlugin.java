package org.openwis.metadataportal.search.solr.spatial.parser;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;

/**
 * The Class OpenwisLuceneQParserPlugin. <P>
 * Explanation goes here. <P>
 */
public class OpenwisLuceneQParserPlugin extends LuceneQParserPlugin {

   /** The NAME. */
   public static String NAME = "OpenwisSearch";

   /**
    * {@inheritDoc}
    * @see org.apache.solr.search.LuceneQParserPlugin#createParser(java.lang.String, org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams, org.apache.solr.request.SolrQueryRequest)
    */
   @Override
   public QParser createParser(String qstr, SolrParams localParams, SolrParams params,
         SolrQueryRequest req) {
      return new OpenwisLuceneQParser(qstr, localParams, params, req);
   }
}

