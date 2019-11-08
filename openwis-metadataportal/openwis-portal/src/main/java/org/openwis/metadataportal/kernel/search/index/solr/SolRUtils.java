package org.openwis.metadataportal.kernel.search.index.solr;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.fao.geonet.kernel.search.IndexEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SolRUtils. <P>
 * Explanation goes here. <P>
 */
public final class SolRUtils {

   /** The Constant RECHECK. */
   private static final boolean RECHECK = true;

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SolRUtils.class);

   /** The server. */
   private static SolrServer solrServer;

   /**
    * Instantiates a new SolR utils.
    */
   private SolRUtils() {
      super();
   }

   /**
    * Gets the SolR server.
    *
    * @param solrUrl the solr url
    * @return the SolR server
    * @throws MalformedURLException the malformed url exception
    */
   public static SolrServer getSolRServer(String solrUrl, SolrIndexManager indexManager)
         throws MalformedURLException {
      SolrServer result;
      if (solrServer == null) {
         CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrUrl);
         server.setSoTimeout(20000); // socket read timeout
         server.setConnectionTimeout(10000);
         server.setDefaultMaxConnectionsPerHost(100);
         server.setMaxTotalConnections(100);
         server.setFollowRedirects(false); // defaults to false
         server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
         solrServer = server;
         checkServer();
         if (solrServer != null) {
            logger.info("First SolR valid connection");
            indexManager.fireIndexEvent(IndexEvent.Factory.createAvailableEvent());
         }
         result = solrServer;
      } else {
         if (RECHECK) {
            checkServer();
            if (solrServer == null) {
               indexManager.fireIndexEvent(IndexEvent.Factory.createUnavailableEvent());
            }
         }
         result = solrServer;
      }
      return result;
   }

   /**
    * Check server.
    */
   private static void checkServer() {
      if (solrServer != null) {
         try {
            SolrPingResponse ping = solrServer.ping();
            if (0 != ping.getStatus()) {
               solrServer = null;
            }
         } catch (SolrServerException e) {
            logger.warn("SolR Sever not reachable !", e);
            solrServer = null;
         } catch (IOException e) {
            logger.error("SolR Sever available !", e);
            solrServer = null;
         }
      }
   }
}
