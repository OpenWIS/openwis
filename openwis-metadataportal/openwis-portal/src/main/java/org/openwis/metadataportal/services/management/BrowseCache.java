/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.cache.CacheIndexWebService;
import org.openwis.dataservice.cache.CachedFileInfo;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockCachedFiles;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.util.ServiceParameter;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 */
public class BrowseCache implements Service {
   private static final int MAX_COUNT = 1000;

   private static final String LOG_MODULE = "openwis.service.management";

   private static final String COL_FILE_NAME = "filename";
   private static final String COL_METADATA_ID = "urn";

   private static final String PARAM_REQUEST_TYPE = "requestType";
   private static final String PARAM_SORT_COLUMN = "sort";
   private static final String PARAM_SORT_DIR = "dir";

   private static final String PARAM_FILE_NAME = COL_FILE_NAME;
   private static final String PARAM_METADATA_ID = "metadataUrn";

   private static final String PARAM_CONFIG_MAX_FILES = "maxFileCount";

   private static final String DEFAULT_SORT_FIELD = "insertionDate";
   private static final String DEFAULT_SORT_ORDER = "DESC";

   private static final String REQUEST_RESET_FILTER = "RESET_FILTER";
   private static final String REQUEST_GET_FILTER = "GET_FILTER_PARAMS";

   private static final String RESPONSE_RESULT = "filter";
   private static final String RESPONSE_ATTRIB_SUCCESS = "success";
   private static final String RESPONSE_ATTRIB_TARGET = "target";

   private int maxRowCount = MAX_COUNT;

   private String sortColumn = DEFAULT_SORT_FIELD;
   private String sortOrder = DEFAULT_SORT_ORDER;

   private Element filterParams = null;

   /**
    * {@inheritDoc}
    */
   @Override
   public void init(final String appPath, final ServiceConfig params) throws Exception {
      String configCountValue = params.getValue(PARAM_CONFIG_MAX_FILES, String.valueOf(MAX_COUNT));
      int count = 0;
      try {
         count = Integer.parseInt(configCountValue);
      }
      catch (NumberFormatException e) {
         count = MAX_COUNT;
         Log.error(LOG_MODULE, "Invalid configuration value for " + PARAM_CONFIG_MAX_FILES + ".Using default " + MAX_COUNT);
      }
      maxRowCount = count;
      Log.info(LOG_MODULE, PARAM_CONFIG_MAX_FILES + "=" + maxRowCount);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Element exec(final Element params, final ServiceContext context) throws Exception {
      Log.info(LOG_MODULE, listParams(params));

      String requestType = Util.getParam(params, PARAM_REQUEST_TYPE, null);
      if (requestType != null) {
         if (requestType.equals(REQUEST_RESET_FILTER)) {
            return resetFilter();
         }
         else if (requestType.equals(REQUEST_GET_FILTER)) {
            return getFilter();
         }
      }

      // search result
      ServiceParameter serviceParameter = new ServiceParameter(params);
      List<CachedFileInfo> elements = null;

      sortColumn = getNamedParam(params, PARAM_SORT_COLUMN, DEFAULT_SORT_FIELD);
      sortOrder = getNamedParam(params, PARAM_SORT_DIR, DEFAULT_SORT_ORDER);

      int index = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      if (limit == 0) {
         limit = maxRowCount;
      }

      int totalCount = 0;

      // Test mode
      if (MockMode.isMockModeDataServiceCache()) {
         elements = MockCachedFiles.browse();
         totalCount = elements != null ? elements.size() : 0;
      }
      // Operation mode
      else {
         // Delegate to the CacheIndex service
         CacheIndexWebService service = DataServiceProvider.getCacheIndexService();
         if (service != null) {
            if (hasFilters(params)) {
               filterParams = params;
            } else {
            	filterParams = null;
            }

            if (hasFilters(filterParams)) {
               // query filtered
               String filenameFilter = getFilenameFilterExpression(filterParams);
               String metadataFilter = getMetadataFilterExpression(filterParams);
               totalCount = (int) service.getCacheContentCount(filenameFilter, metadataFilter);
               elements = service.getCacheContentFilteredSorted(filenameFilter, metadataFilter, sortColumn, sortOrder, index, limit);
            }
            else {
               // query unfiltered ...
               totalCount = (int) service.getCacheContentCount(null, null);
               elements = service.getCacheContentSorted(sortColumn, sortOrder, index, limit);
            }
         }
      }

      // encode and send...
      SearchResultWrapper<CachedFileInfo> wrapper = new SearchResultWrapper<CachedFileInfo>(totalCount, elements);

      return JeevesJsonWrapper.send(wrapper);
   }

   private static String getFilenameFilterExpression(final Element params) {
      StringBuffer fb = new StringBuffer();
      if (hasParam(params, PARAM_FILE_NAME)) {
         String fileName = '%' + getNamedParam(params, PARAM_FILE_NAME) + '%';
         fb.append(appendColumnValue(fb, "cf", COL_FILE_NAME, fileName));
      }
      return fb.toString();
   }

   private static String getMetadataFilterExpression(final Element params) {
      StringBuffer fb = new StringBuffer();
      if (hasParam(params, PARAM_METADATA_ID)) {
         String urn = '%' + getNamedParam(params, PARAM_METADATA_ID) + '%';
         fb.append(appendColumnValue(fb, "pm", COL_METADATA_ID, urn));
      }
      return fb.toString();
   }

   /**
    * Resets the filter parameters.
    * @return response element
    */
   private Element resetFilter() {
      filterParams = null;
      Element result = new Element(RESPONSE_RESULT);

      result.setAttribute(RESPONSE_ATTRIB_TARGET, REQUEST_RESET_FILTER);
      result.setAttribute(RESPONSE_ATTRIB_SUCCESS, "true");

      return result;
   }

   /**
    * Returns the current filter parameters as response attributes.
    * @return response element
    */
   private Element getFilter() {
      Element result = new Element(RESPONSE_RESULT);

      result.setAttribute(RESPONSE_ATTRIB_TARGET, REQUEST_GET_FILTER);
      result.setAttribute(RESPONSE_ATTRIB_SUCCESS, "true");

      if (filterParams != null) {
         result.setAttribute(PARAM_FILE_NAME, getNamedParam(filterParams, PARAM_FILE_NAME, ""));
         result.setAttribute(PARAM_METADATA_ID, getNamedParam(filterParams, PARAM_METADATA_ID, ""));
      }
      else {
         result.setAttribute(PARAM_FILE_NAME, "");
         result.setAttribute(PARAM_METADATA_ID, "");
      }
      return result;
   }

   /**
    * Checks the existing of valid filter parameters
    * @param params
    * @return
    */
   private static boolean hasFilters(final Element params) {
      return params != null &&
             (hasParam(params, PARAM_FILE_NAME) || hasParam(params, PARAM_METADATA_ID));
   }

   /**
    * Checks the existing of valid sort order parameters.
    * @param params
    * @return
    */
   @SuppressWarnings("unused")
   private static boolean hasSort(final Element params) {
      return params != null &&
            hasParam(params, PARAM_SORT_COLUMN) &&
            hasParam(params, PARAM_SORT_DIR);
   }

   /**
    * Checks the existing for a filter parameter for a given database table column.
    * @param params
    * @param column
    * @return
    */
   private static boolean hasParam(final Element params, final String column) {
      String param = getNamedParam(params, column);
      return (param != null) && !(param.trim().isEmpty());
   }

   /**
    * Gets a parameter value by its name.
    * @param params
    * @param name
    * @return
    */
   private static String getNamedParam(final Element params, final String name) {
      return Util.getParam(params, name, null);
   }
   /**
    * Gets a parameter by its name (if any). Returns default value if no parameter has been found.
    * Description goes here.
    * @param params
    * @param name
    * @param defaultValue
    * @return
    */
   private static String getNamedParam(final Element params, final String name, final String defaultValue) {
      return Util.getParam(params, name, defaultValue);
   }

   /**
    * Formats a column and its filter value to a SQL literal.
    * @param filterBuffer
    * @param tableName
    * @param columnName
    * @param value
    * @return
    */
   private static String appendColumnValue(final StringBuffer filterBuffer, final String tableName, final String columnName, final String value) {
      StringBuffer sql = new StringBuffer();
      if (filterBuffer.length() > 0) {
         sql.append(" and ");
      }
      sql.append("lower(" + appendColumn(tableName, columnName) + ")");
      if (value.contains("%")) {
         sql.append(" like '");
      } else {
         sql.append(" = '");
      }
      sql.append(value.toLowerCase());
      sql.append("'");
      return sql.toString();
   }

   /**
    * Formats a data base table column to a SQL literal.
    * @param tableName
    * @param columnName
    * @return
    */
   private static String appendColumn(final String tableName, final String columnName) {
      StringBuffer sql = new StringBuffer();
      sql.append(tableName);
      sql.append(".");
      sql.append(columnName);
      return sql.toString();
   }

   private static String listParams(final Element params) {
      StringBuffer list = new StringBuffer();
      for (Object o: params.getChildren()) {
         Element e = (Element) o;
         if (list.length() > 0) {
            list.append(",");
         }
         list.append(e.getName());
         list.append("=");
         list.append(e.getTextTrim());
      }
      list.insert(0, "Params: ");
      return list.toString();
   }
}
