package org.openwis.metadataportal.search.solr.update;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.DirectUpdateHandler2;
import org.apache.solr.util.RefCounted;
import org.openwis.metadataportal.search.solr.spatial.OpenwisGeometryTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OpenwisUpdateHandler. <P>
 * Explanation goes here. <P>
 */
public class OpenwisUpdateHandler extends DirectUpdateHandler2 {

   /** The Constant FIELD_UUID. */
   private static final String FIELD_UUID = "_uuid";

   /** The Constant FIELD_GEOMETRY. */
   private static final String FIELD_GEOMETRY = "_geometry";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(OpenwisUpdateHandler.class);

   /** The openwis geometry tool. */
   private final OpenwisGeometryTool geometryTool;

   /**
    * Instantiates a new openwis update handler.
    *
    * @param core the core
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public OpenwisUpdateHandler(SolrCore core) throws IOException {
      super(core);
      geometryTool = OpenwisGeometryTool.getInstance();
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.update.DirectUpdateHandler2#addDoc(org.apache.solr.update.AddUpdateCommand)
    */
   @Override
   public int addDoc(AddUpdateCommand cmd) throws IOException {
      logger.info("AddDoc: {}", cmd);
      int result = super.addDoc(cmd);

      if (result == 1) {
         try {
            // update the spatial index
            SolrInputDocument solrDoc = cmd.getSolrInputDocument();
            String uuid = (String) solrDoc.getFieldValue(FIELD_UUID);
            String geometry = (String) solrDoc.getFieldValue(FIELD_GEOMETRY);

            // Update the spatial index
            if (geometry != null && geometry.length() > 0) {
               geometryTool.addMetadata(uuid, geometry);
            } else {
               logger.warn("No Spatial data for {}", uuid);
            }
         } catch (Exception e) {
            reinitializeDataStore();
            throw new IOException(e);
         }
      }
      return result;
   }
   
   /**
    * Attempts to re-initialize the datastore in case of update failure.
    */
   private void reinitializeDataStore() {
      try {
         geometryTool.cleanDataStore();
      } catch (Exception e) {
         logger.error("Unable to re-initialize datastore", e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.update.DirectUpdateHandler2#delete(org.apache.solr.update.DeleteUpdateCommand)
    */
   @Override
   public void delete(DeleteUpdateCommand cmd) throws IOException {
      logger.info("delete: {}", cmd);
      super.delete(cmd);

      // delete into the spatial index
      try {
         String uuid = cmd.id;
         geometryTool.removeMetadata(uuid);
      } catch (Exception e) {
         reinitializeDataStore();
         throw new IOException(e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.update.DirectUpdateHandler2#deleteByQuery(org.apache.solr.update.DeleteUpdateCommand)
    */
   @Override
   @SuppressWarnings("rawtypes")
   public void deleteByQuery(DeleteUpdateCommand cmd) throws IOException {
      logger.info("Delete by query: {}", cmd);
      try {
         // Get Query
         Query q = QueryParsing.parseQuery(cmd.query, schema);

         if (MatchAllDocsQuery.class.equals(q.getClass())) {
            geometryTool.reset();
            super.deleteByQuery(cmd);
         } else {
            // Retrieve document to delete
            Future[] waitSearcher = new Future[1];
            RefCounted<SolrIndexSearcher> searcher = core.getSearcher(true, true, waitSearcher);
            SolrIndexSearcher indexSearcher = searcher.get();
            DocList docList = indexSearcher.getDocList(q, (Query) null, null, 0, Integer.MAX_VALUE);

            // Delete all document
            int docId;
            String uuid;
            Document luceneDoc;
            for (DocIterator iterator = docList.iterator(); iterator.hasNext();) {
               docId = iterator.nextDoc();
               luceneDoc = indexSearcher.doc(docId);
               uuid = luceneDoc.get(FIELD_UUID);
               geometryTool.removeMetadata(uuid);
            }
         }
      } catch (Exception e) {
         reinitializeDataStore();
         throw new IOException(e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.update.DirectUpdateHandler2#commit(org.apache.solr.update.CommitUpdateCommand)
    */
   @Override
   public void commit(CommitUpdateCommand cmd) throws IOException {
      logger.info("Commit", cmd);
      super.commit(cmd);
      try {
         geometryTool.commit();
      } catch (Exception e) {
         reinitializeDataStore();
         throw new IOException(e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.apache.solr.update.DirectUpdateHandler2#close()
    */
   @Override
   public void close() throws IOException {
      logger.info("Close");
      super.close();
      try {
         geometryTool.close();
      } catch (Exception e) {
         reinitializeDataStore();
         throw new IOException(e);
      }
   }
}
