package org.openwis.metadataportal.services.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jeeves.constants.Jeeves;
import jeeves.interfaces.Service;
import jeeves.interfaces.ServiceWithJsp;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.ISearchManager.Searcher;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.services.util.SearchDefaults;
import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.search.GenericMetaSearcher;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.search.dto.RelatedMetadataDTO;

/**
 * The Class UserSearch. <P>
 * Explanation goes here. <P>
 */
public class UserSearch implements ServiceWithJsp, Service {
   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      // Nothing to do
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      throw new IllegalAccessException("Should not be called, use execWithJsp");
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.ServiceWithJsp#execWithJsp(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @SuppressWarnings("rawtypes")
   @Override
   public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception {
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      UserSession session = context.getUserSession();

      Element request = SearchDefaults.getDefaultSearch(session, params);

      // perform the search and save search query into session
      GenericMetaSearcher searcher;
      ISearchManager searchMan = gc.getSearchmanager();
      searcher = (GenericMetaSearcher) searchMan.newSearcher(Searcher.INDEX);

      // Searching
      searcher.search(context, request, null);
      SearchResult searchResult = searcher.getResult();

      // Getting operation allowed
      Map<String, Set<OperationEnum>> operationsAllowed = null;
      Map<String, List<RelatedMetadataDTO>> relatedMetadata = null;
      if (searchResult.getCount() > 0) {
         Set<String> ids = getMetadataIds(searchResult);
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         operationsAllowed = getOperations(dbms, session, ids);
         
         // Build a map to get all the related services for each metadata
         // Map key=UUID, value=List of RelatedMetadataDTO
         relatedMetadata = getRelatedMetadata(context, searcher, ids);
      } 

      // Build jsp args
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("searchResult", searchResult);
      result.put("operationsAllowed", operationsAllowed);
      result.put("relatedMetadata", relatedMetadata);
      result.put("username", session.getUsername());
      result.put("isCacheEnable",
            OpenwisMetadataPortalConfig.getBoolean(ConfigurationConstants.CACHE_ENABLE));
      boolean isBlacklisted = false;
      if (session.isAuthenticated())
      {
         isBlacklisted = DataServiceProvider.getBlacklistService().isUserBlacklisted(session.getUsername());
      }
      result.put("isBlacklisted", isBlacklisted);
      return result;
   }

   /**
    * Gets the operations.
    *
    * @param dbms the dbms
    * @param session the session
    * @param metadataId the metadata id
    * @return the operations.  The URNs are returned in lowercase.
    * @throws SQLException the SQL exception
    */
   public Map<String, Set<OperationEnum>> getOperations(Dbms dbms, UserSession session,
         Set<String> metadataId) throws Exception {
      Map<String, Set<OperationEnum>> result;

      // add operations
      GroupManager gm = new GroupManager(dbms);
      List<Group> groups = null;

      if (session.isAuthenticated()) {
         if (session.getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
            groups = gm.getAllGroups();
            
            // Grant everything for every metadata
            result = new HashMap<String, Set<OperationEnum>>();
            for (String mdId : metadataId) {
            	result.put(mdId, EnumSet.allOf(OperationEnum.class));
            }
         } else {
            groups = gm.getAllUserGroups(session.getUserId());
            
            IDataPolicyManager dpm = new DataPolicyManager(dbms);
            result = dpm.getAllOperationAllowedByMetadataId(metadataId, groups);
         }
      } else {
         result = new LinkedHashMap<String, Set<OperationEnum>>();
      }
      
      // convert the keys to lowercase to support case-insensitive lookups
      Map<String, Set<OperationEnum>> resultsWithLowercaseKeys = new HashMap<String, Set<OperationEnum>>();
      for (Entry<String, Set<OperationEnum>> resultEntry : result.entrySet()) {
         final String lowercaseKey = resultEntry.getKey().toLowerCase();
         // Sanity check: there must not be duplicate lowercase keys
         if (resultsWithLowercaseKeys.containsKey(lowercaseKey)) {
            throw new RuntimeException("URN '" + lowercaseKey + "' already exists in the results with lower case keys.  Is this a duplicate?!");
         }
         resultsWithLowercaseKeys.put(lowercaseKey, resultEntry.getValue());
      }

      // Always grant VIEW privileges for non authenticated users.
      Set<OperationEnum> ops;
      for (String id : metadataId) {
         ops = resultsWithLowercaseKeys.get(id.toLowerCase());
         if (ops == null) {
            resultsWithLowercaseKeys.put(id, Collections.singleton(OperationEnum.VIEW));
         } else {
            ops.add(OperationEnum.VIEW);
         }
      }
      return resultsWithLowercaseKeys;
   }
   
   @SuppressWarnings("rawtypes")
   public Map<String, List<RelatedMetadataDTO>> getRelatedMetadata(ServiceContext context,
         GenericMetaSearcher searcher, Set<String> metadataIds) throws Exception {
      Map<String, List<RelatedMetadataDTO>> result = new LinkedHashMap<String, List<RelatedMetadataDTO>>();
      
      for (String mdId : metadataIds) {
         Element requestParameters = new Element(Jeeves.Elem.REQUEST);
         requestParameters.addContent(new Element("operatesOn").setText(mdId));
         //requestParameters.addContent(new Element("fast").addContent("true"));
         searcher.search(context, requestParameters, null);
         
         ArrayList<RelatedMetadataDTO> metadataList = new ArrayList<RelatedMetadataDTO>();
         for (SearchResultDocument doc : searcher.getResult().getDocuments()) {
            RelatedMetadataDTO md = new RelatedMetadataDTO();
            md.setId(doc.getFieldAsString(IndexField.ID));
            md.setUuid(doc.getFieldAsString(IndexField.UUID));
            md.setTitle(doc.getFieldAsString(IndexField.TITLE));
            metadataList.add(md);
         }
         result.put(mdId, metadataList);
      }
      
      return result;
   }

   /**
    * Gets the metadata ids.
    *
    * @param searchResult the search result
    * @return the data policies
    */
   private Set<String> getMetadataIds(SearchResult sr) {
      Set<String> result = new LinkedHashSet<String>();
      String id;
      for (SearchResultDocument doc : sr) {
         // get data policy
         id = (String) doc.getField(IndexField.UUID);
         if (id != null) {
            result.add(id);
         }
      }
      return result;
   }

}
