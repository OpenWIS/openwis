/**
 *
 */
package org.openwis.metadataportal.services.metadata;

import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.SortingInfo;
import org.fao.geonet.kernel.search.SortingInfoImpl;
import org.jdom.Element;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.MonitorCatalogSearchCriteria;
import org.openwis.metadataportal.services.metadata.dto.MonitorMetadataDTO;

/**
 * List all metadata. <P>
 * Explanation goes here. <P>
 *
 */
public class All implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ISearchManager searchMan = gc.getSearchmanager();

      // Get filter param for search
      String searchParam = Util.getParam(params, "any", null);
      String searchField = Util.getParam(params, "searchField", null);
      String category = Util.getParam(params, "categories", null);
      
      boolean myMetadataOnly = Util.getParam(params, "myMetadataOnly", false);

      // Get paging parameters
      int index = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumnName = Util.getParam(params, "sort", null);
      String sortDir = Util.getParam(params, "dir", null);

      // Default value are handled by the server (dataservice).
      SortingInfo sort = new SortingInfoImpl();
      if (StringUtils.isNotBlank(sortColumnName) && StringUtils.isNotBlank(sortDir)) {
         MetadataManager mm = new MetadataManager();
         sort.add(mm.getIndexFieldColumn(sortColumnName), SortDir.valueOf(sortDir));
      }

      MonitorCatalogSearchCriteria crit = null;
      
      if(myMetadataOnly) {
         crit = new MonitorCatalogSearchCriteria(searchParam, context.getUserSession().getUsername());
      } else {
         crit = new MonitorCatalogSearchCriteria(searchParam);
      }
      if (StringUtils.isNotBlank(category)) {
          crit.setCategory(category);
       }

      if (StringUtils.isNotBlank(searchField)) {
    	  crit.setSearchField(searchField);
      }
      
      SearchResult searchResult = searchMan.getFilteredMetadata(crit, index, limit, sort);

      List<Metadata> metadatas = new ArrayList<Metadata>();

      for (SearchResultDocument document : searchResult.getDocuments()) {
         // Fill the metadata with result document.
         Metadata md = new Metadata((String) document.getField(IndexField.UUID_ORIGINAL));
         md.setTitle((String) document.getField(IndexField._TITLE));
         md.setOriginator((String) document.getField(IndexField.ORIGINATOR));
         md.setProcess((String) document.getField(IndexField.PROCESS));
         md.setGtsCategory((String) document.getField(IndexField.GTS_CATEGORY));
         md.setOverridenGtsCategory((String) document.getField(IndexField.OVERRIDDEN_GTS_CATEGORY));
         md.setFncPattern((String) document.getField(IndexField.FNC_PATTERN));
         md.setOverridenFncPattern((String) document.getField(IndexField.OVERRIDDEN_FNC_PATTERN));

         // The data policy name.
         String datapolicyName = (String) document.getField(IndexField.DATAPOLICY);
         md.setDataPolicy(new DataPolicy(datapolicyName));

         md.setOverridenDataPolicy((String) document.getField(IndexField.OVERRIDDEN_DATAPOLICY));
         md.setLocalDataSource((String) document.getField(IndexField.LOCAL_DATA_SOURCE));
         md.setFileExtension((String) document.getField(IndexField.FILE_EXTENSION));
         md.setOverridenFileExtension((String) document
               .getField(IndexField.OVERRIDDEN_FILE_EXTENSION));

         String categoryId = (String) document.getField(IndexField.CATEGORY_ID);
         String categoryName = (String) document.getField(IndexField.CATEGORY_NAME);
         md.setCategory(new Category(new Integer(categoryId), categoryName));

         String isFed = (String) document.getField(IndexField.IS_FED);
         if (StringUtils.isNotBlank(isFed)) {
            md.setFed(BooleanUtils.toBoolean(isFed));
         }

         String isIngested = (String) document.getField(IndexField.IS_INGESTED);
         if (StringUtils.isNotBlank(isIngested)) {
            md.setIngested(BooleanUtils.toBoolean(isIngested));
         }

         String priority = (String) document.getField(IndexField.PRIORITY);
         if (StringUtils.isNotBlank(priority) && StringUtils.isNumeric(priority)) {
            md.setPriority(new Integer(priority));
         }

         String overPriority = (String) document.getField(IndexField.OVERRIDDEN_PRIORITY);
         if (StringUtils.isNotBlank(overPriority) && StringUtils.isNumeric(overPriority)) {
            md.setOverridenPriority(new Integer(overPriority));
         }

         md.setSchema((String) document.getField(IndexField.SCHEMA));
         String id = (String) document.getField(IndexField.ID);
         if (StringUtils.isNotBlank(id) && StringUtils.isNumeric(id)) {
            md.setId(new Integer(id));
         }

         // Add the metadata to the list of results.
         metadatas.add(md);
      }

      MonitorMetadataDTO dto = new MonitorMetadataDTO();
      dto.setCount(searchResult.getCount());
      dto.setMetadatas(metadatas);

      return JeevesJsonWrapper.send(dto);
   }
}
