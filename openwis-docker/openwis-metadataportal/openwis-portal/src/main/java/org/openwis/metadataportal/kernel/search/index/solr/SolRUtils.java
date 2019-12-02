package org.openwis.metadataportal.kernel.search.index.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.fao.geonet.kernel.search.IndexEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * The Class SolRUtils. <P>
 * Explanation goes here. <P>
 */
public final class SolRUtils {

   /** The Constant RECHECK. */
   private static final boolean RECHECK = true;

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SolRUtils.class);

   /**
    * Instantiates a new SolR utils.
    */
   private SolRUtils() {
      super();
   }

   private static SolrClient solrClient;
   /**
    * Gets the SolR server.
    *
    * @param solrUrl the solr url
    * @return the SolR server
    * @throws MalformedURLException the malformed url exception
    */
   public static SolrClient getSolRServer(String solrUrl, SolrIndexManager indexManager)
         throws MalformedURLException {
      SolrClient result;
      if (solrClient == null) {
         SolrClient client = new HttpSolrClient.Builder(solrUrl)
                 .withConnectionTimeout(10000)
                 .withSocketTimeout(6000)
                 .build();
         solrClient = client;
//         checkServer();
         if (solrClient != null) {
            logger.info("First SolR valid connection");
            indexManager.fireIndexEvent(IndexEvent.Factory.createAvailableEvent());
         }
         result = solrClient;
      } else {
         if (RECHECK) {
//            checkServer();
            if (solrClient == null) {
               indexManager.fireIndexEvent(IndexEvent.Factory.createUnavailableEvent());
            }
         }
         result = solrClient;
      }
      return result;
   }

   /**
    * Check server.
    */
}
