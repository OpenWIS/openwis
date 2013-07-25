package org.fao.geonet.kernel.oaipmh.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.apache.commons.collections.CollectionUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.oaipmh.Lib;
import org.fao.geonet.kernel.oaipmh.OaiPmhDispatcher;
import org.fao.geonet.kernel.oaipmh.OaiPmhService;
import org.fao.geonet.kernel.oaipmh.ResumptionTokenCache;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.oaipmh.exceptions.BadArgumentException;
import org.fao.oaipmh.exceptions.BadResumptionTokenException;
import org.fao.oaipmh.exceptions.NoRecordsMatchException;
import org.fao.oaipmh.requests.AbstractRequest;
import org.fao.oaipmh.requests.TokenListRequest;
import org.fao.oaipmh.responses.AbstractResponse;
import org.fao.oaipmh.responses.GeonetworkResumptionToken;
import org.fao.oaipmh.responses.Header;
import org.fao.oaipmh.responses.ListResponse;
import org.fao.oaipmh.util.ISODate;
import org.fao.oaipmh.util.SearchResult;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.DeletedMetadataManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;

public abstract class AbstractTokenLister implements OaiPmhService {

   protected ResumptionTokenCache cache;

   private final SettingManager settingMan;

   public AbstractTokenLister(ResumptionTokenCache cache, SettingManager sm) {
      this.cache = cache;
      settingMan = sm;
   }

   @Override
   @SuppressWarnings("unchecked")
   public AbstractResponse execute(AbstractRequest request, ServiceContext context)
         throws Exception {

      Log.debug(Geonet.OAI_HARVESTER, "OAI " + this.getClass().getSimpleName() + " execute: ");

      TokenListRequest req = (TokenListRequest) request;

      SearchResult result;

      String strToken = req.getResumptionToken();
      GeonetworkResumptionToken token = null;

      int pos = 0;

      Category category = new Category();
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      if (strToken == null) {
         GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
         ISearchManager searchMan = gc.getSearchmanager();
         
         Log.debug(Geonet.OAI_HARVESTER, "OAI " + this.getClass().getSimpleName()
               + " : new request (no resumptionToken)");

         ISODate from = req.getFrom();
         ISODate until = req.getUntil();
         String set = req.getSet();
         String prefix = req.getMetadataPrefix();

         String sFrom = null;
         if (from != null) {
            sFrom = from.isShort ? from.getDate() : from.toString();
         }
         
         String sTo = null;
         if (until != null) {
            sTo = until.isShort ? until.getDate() : until.toString();
         }

         if (from != null && until != null && from.sub(until) > 0)
            throw new BadArgumentException("From is greater than until");

         if (set != null) {
            // Get the category if exists, if not returns null.
            category = new CategoryManager(dbms).getCategoryByName(set);
         }
         
         result = new SearchResult(prefix);
         
         // Initialize collections.         
         Collection<String> metadataUrns = new ArrayList<String>();
         Collection<String> deletedResults = new ArrayList<String>();
         
         // Do not process to the request if the set does not exist locally.
         if (category != null) {
            // Get standards results from Solr search
            metadataUrns = searchMan.getMetadataUrnsForOAI(sFrom, sTo, category, prefix);
            
            // Get deleted metadata results
            deletedResults = new DeletedMetadataManager(dbms).getDeletedMetadataUrns(sFrom, sTo, category); 
         }
         
         // Union of both searches (index + deleted).
         Collection<String> results = CollectionUtils.union(metadataUrns, deletedResults);
         
         result.setUrns(new ArrayList<String>(results));

         if (result.getUrns().isEmpty())
            throw new NoRecordsMatchException("No results");

         // we only need a new token if the result set is big enough
         if (result.getUrns().size() > OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.OAI_MAX_RECORDS)) {
            token = new GeonetworkResumptionToken(req, result);
            cache.storeResumptionToken(token);
         }

      } else {
         //result = (SearchResult) session.getProperty(Lib.SESSION_OBJECT);
         token = cache.getResumptionToken(GeonetworkResumptionToken.buildKey(req));
         if (Log.isDebug(Geonet.OAI_HARVESTER)) {
            Log.debug(Geonet.OAI_HARVESTER, "OAI ListRecords : using ResumptionToken : "
                  + GeonetworkResumptionToken.buildKey(req));
         }

         if (token == null) {
            throw new BadResumptionTokenException("No session for token : "
                  + GeonetworkResumptionToken.buildKey(req));
         }

         result = token.getRes();

         //pos = result.parseToken(token);
         pos = GeonetworkResumptionToken.getPos(req);
         
         // Get the category
         category = new CategoryManager(dbms).getCategoryByName(token.getSet());
      }

      ListResponse res = processRequest(req, pos, result, context, category);
      pos = pos + res.getSize();

      if (token == null && res.getSize() == 0) {
         throw new NoRecordsMatchException("No results");
      }

      // put the token on only if we have enough results
      if (res.getSize() == OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.OAI_MAX_RECORDS)) { 
         if (token != null) {
            token.setupToken(pos); 
         }
         res.setResumptionToken(token);
      } else  {
         res.setResumptionToken(null);
      }
      
      return res;

   }

   /**
    * @return the mode
    * 
    */
   public int getMode() {
      return settingMan.getValueAsInt("system/oai/mdmode");
   }

   /**
    * Get the dateFrom
    * @return the dateFrom
    */
   public String getDateFrom() {
      // Default mode is set to OaiPmhDispatcher.MODE_MODIFIDATE
      String dateFrom = "dateFrom";
      if (getMode() == OaiPmhDispatcher.MODE_TEMPEXTEND) {
         dateFrom = "extFrom";
      }
      return dateFrom;
   }

   /**
    * Get the dateUntil
    * @return the dateUntil
    */
   public String getDateUntil() {
      // Default mode is set to OaiPmhDispatcher.MODE_MODIFIDATE
      String dateUntil = "dateTo";
      if (getMode() == OaiPmhDispatcher.MODE_TEMPEXTEND) {
         dateUntil = "extTo";
      }
      return dateUntil;
   }

   @Override
   public abstract String getVerb();

   public abstract ListResponse processRequest(TokenListRequest req, int pos, SearchResult result,
         ServiceContext context, Category category) throws Exception;
   
   /**
    * Build an OAI-PMH header from a deleted metadata
    * 
    * @param dbms
    * @param dm
    * @param prefix
    * @param appPath
    * @return
    * @throws SQLException
    */
   protected static Header buildHeaderFromDeletedMetadata(Dbms dbms, DeletedMetadata dm, String prefix, String appPath) throws SQLException {
      // Try to disseminate format if not by schema then by conversion
      if (!prefix.equals(dm.getSchema())) {
         if (!Lib.existsConverter(dm.getSchema(), appPath, prefix)) {
            return null;
         }
      }

      Header header = new Header();
      header.setIdentifier(dm.getUrn());
      header.setDeleted(true);
      header.setDateStamp(new ISODate(dm.getDeletionDate()));
      header.addSet(new CategoryManager(dbms).getCategoryById(dm.getCategory()).getName());
      
      return header;
   }

}
