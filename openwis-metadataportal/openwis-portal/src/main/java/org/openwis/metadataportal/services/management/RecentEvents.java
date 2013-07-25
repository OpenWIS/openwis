/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.management.alert.AlarmEvent;
import org.openwis.management.alert.AlertService;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.mock.MockStatistics;
import org.openwis.metadataportal.services.util.ServiceParameter;

/**
 * <code>Service</code> extensions used to query the <code>AlertService</code>.
 */
public class RecentEvents implements Service {

   private static final int MAX_COUNT = 1000;

   private static final String DB_TABLE = "openwis_alarms";

   private static final String COL_DATE = "date";
   private static final String COL_MESSAGE = "message";
   private static final String COL_MODULE = "module";
   private static final String COL_SEVERITY = "severity";
   private static final String COL_SOURCE = "source";

   private static final String PARAM_DATA_FROM = COL_DATE + "_from";
   private static final String PARAM_DATA_TO = COL_DATE + "_to";
   private static final String PARAM_MESSAGE = COL_MESSAGE;
   private static final String PARAM_MODULE = COL_MODULE;
   private static final String PARAM_SEVERITY = COL_SEVERITY;
   private static final String PARAM_SOURCE = COL_SOURCE;

   private static final String PARAM_SORT_COLUMN = "sort";
   private static final String PARAM_SORT_DIR = "dir";

   private static final String PARAM_REQUEST_TYPE = "requestType";
   private static final String PARAM_CONFIG_MAX_EVENTS = "maxEventCount";

   private static final String LOG_MODULE = "openwis.service.management";

   private static final String REQUEST_RESET_FILTER = "RESET_FILTER";
   private static final String REQUEST_GET_FILTER = "GET_FILTER_PARAMS";

   private static final String RESPONSE_RESULT = "filter";
   private static final String RESPONSE_ATTRIB_SUCCESS = "success";
   private static final String RESPONSE_ATTRIB_TARGET = "target";

   private static final String DEFAULT_SORT_FIELD = "date";
   private static final String DEFAULT_SORT_ORDER = "DESC";

   private int maxRowCount = MAX_COUNT;

   private Element filterParams = null;

   private String sortColumn = DEFAULT_SORT_FIELD;
   private String sortOrder = DEFAULT_SORT_ORDER;

   /**
    * {@inheritDoc}
    */
   @Override
   public void init(final String appPath, final ServiceConfig params) throws Exception {
      String configCountValue = params.getValue(PARAM_CONFIG_MAX_EVENTS, String.valueOf(MAX_COUNT));
      int count = 0;
      try {
         count = Integer.parseInt(configCountValue);
      }
      catch (NumberFormatException e) {
         count = MAX_COUNT;
         Log.error(LOG_MODULE, "Invalid configuration value for " + PARAM_CONFIG_MAX_EVENTS + ".Using default " + MAX_COUNT);
      }
      maxRowCount = count;
      Log.info(LOG_MODULE, PARAM_CONFIG_MAX_EVENTS + "=" + maxRowCount);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Element exec(final Element params, final ServiceContext context) throws Exception {
      // check for special requests
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

      sortColumn = getNamedParam(params, PARAM_SORT_COLUMN, DEFAULT_SORT_FIELD);
      sortOrder = getNamedParam(params, PARAM_SORT_DIR, DEFAULT_SORT_ORDER);

      int index = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");

      // perform search
      ServiceParameter serviceParameter = new ServiceParameter(params);
      List<AlarmEvent> elements = null;

      int totalCount = 0;

      // Test mode
      if (MockMode.isMockModeMonitoringService()) {
         elements = MockStatistics.getRecentEvents();
      }
      // Operation mode
      else {
         // Delegate to the MC service
         // Request for recent events are handled by the MC Monitoring and Control Service
         // Alarms are maintained by the AlarmManager component.
         // This component exposes the MC capabilities allowing creating reports of recent events
         if (hasFilters(params)) {
            filterParams = params;
         } else {
        	 filterParams = null;
         }

         if (hasFilters(filterParams)) {
            elements = getFilteredEvents(index, limit, filterParams);
            totalCount = (int) getFilteredEventsCount(filterParams);
         }
         else {
            elements = getRecentEvents(index, limit);
            totalCount = (int) getRecentEventsCount();
         }
      }

      // encode and send...
      SearchResultWrapper<AlarmEvent> wrapper = new SearchResultWrapper<AlarmEvent>(totalCount, elements);

      return JeevesJsonWrapper.send(wrapper);
   }

   private long getFilteredEventsCount(Element filterParams) {
	   long numberOfRecentEvents = 0;
	   AlertService service = ManagementServiceProvider.getAlertService();
	   if (service != null) {
		   String filterExp = getFilterExpression(filterParams).trim();
		   numberOfRecentEvents = service.getFilteredEventsCount(filterExp);
	   }
	   return numberOfRecentEvents;
   }

   private long getRecentEventsCount() {
	   long numberOfRecentEvents = 0;
	   AlertService service = ManagementServiceProvider.getAlertService();
	   if (service != null) {
		   XMLGregorianCalendar from = getFromDate();
		   XMLGregorianCalendar to = getToDate();
		   numberOfRecentEvents = service.getRecentEventsCount(from, to);
	   }
	   return numberOfRecentEvents;
   }

   /**
    * Gets a default list from service (no custom filters). Delivers events from
    * <current date> - 1 day until <current date>
    * @return list of <code>AlarmEvent</code>s
    */
   private List<AlarmEvent> getRecentEvents(int index, int limit) {
      List<AlarmEvent> elements = null;
      AlertService service = ManagementServiceProvider.getAlertService();
      if (service != null) {
         XMLGregorianCalendar from = getFromDate();
         XMLGregorianCalendar to = getToDate();
         int maxCount = maxRowCount;
         // query...
         Log.info(LOG_MODULE, "Sorted: " + sortColumn + ", Order: " + sortOrder);
         elements = service.getRecentEventsSorted(from, to, sortColumn, sortOrder, index, limit);
      }
      return elements;
   }

   /**
    * Gets a filtered list from service (if any custom filters).
    * @return list of <code>AlarmEvent</code>s
    */
   private List<AlarmEvent> getFilteredEvents(int index, int limit, final Element params) {
      List<AlarmEvent> elements = null;
      AlertService service = ManagementServiceProvider.getAlertService();
      if (service != null) {
         String filterExp = getFilterExpression(params).trim();
         int maxCount = maxRowCount;
         Log.info(LOG_MODULE, "Filter: " + filterExp);
         Log.info(LOG_MODULE, "Sorted: " + sortColumn + ", Order: " + sortOrder);
         elements = service.getFilteredEventsSorted(filterExp, sortColumn, sortOrder, index, limit);
      }
      return elements;
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
         result.setAttribute(PARAM_DATA_FROM, getNamedParam(filterParams, PARAM_DATA_FROM, ""));
         result.setAttribute(PARAM_DATA_TO, getNamedParam(filterParams, PARAM_DATA_TO, ""));
         result.setAttribute(PARAM_SEVERITY, getNamedParam(filterParams, PARAM_SEVERITY, ""));
         result.setAttribute(PARAM_MODULE, getNamedParam(filterParams, PARAM_MODULE, ""));
         result.setAttribute(PARAM_SOURCE, getNamedParam(filterParams, PARAM_SOURCE, ""));
         result.setAttribute(PARAM_MESSAGE, getNamedParam(filterParams, PARAM_MESSAGE, ""));
      }
      else {
         result.setAttribute(PARAM_DATA_FROM, "");
         result.setAttribute(PARAM_DATA_TO, "");
         result.setAttribute(PARAM_SEVERITY, "");
         result.setAttribute(PARAM_MODULE, "");
         result.setAttribute(PARAM_SOURCE, "");
         result.setAttribute(PARAM_MESSAGE, "");
      }
      return result;
   }


   /**
    * Gets the default start date.
    * @return
    */
   private static XMLGregorianCalendar getFromDate() {
      XMLGregorianCalendar from;
      try {
         from = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
         from.setYear(from.getYear() - 1);
      } catch (DatatypeConfigurationException e) {
         from = null;
      }
      return from;
   }

   /**
    * Gets the default end date.
    * @return
    */
   private static XMLGregorianCalendar getToDate() {
      XMLGregorianCalendar to;
      try {
         to = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
      } catch (DatatypeConfigurationException e) {
         to = null;
      }
      return to;
   }

   /**
    * Builds a SQL where clause from service parameter.
    * @param params
    * @return
    */
   private static String getFilterExpression(final Element params) {
      StringBuffer fb = new StringBuffer();

      boolean hasDateFrom = hasParam(params, PARAM_DATA_FROM);
      boolean hasDateTo = hasParam(params, PARAM_DATA_TO);

      // openwis_alarms.date
      if (hasDateFrom && hasDateTo) {
         String fromDate = getNamedParam(params, PARAM_DATA_FROM);
         String toDate = getNamedParam(params, PARAM_DATA_TO);

         fb.append(appendColumn(COL_DATE));
         if (fromDate.equals(toDate)) {
            fb.append(" = '");
            fb.append(fromDate);
            fb.append("'");
         }
         else {
            fb.append(" between '");
            fb.append(fromDate);
            fb.append("' and '");
            fb.append(toDate);
            fb.append("'");
         }
      } else if (hasDateFrom) {
         String fromDate = getNamedParam(params, PARAM_DATA_FROM);
         fb.append(appendColumn(COL_DATE));
         fb.append(" >= '");
         fb.append(fromDate);
         fb.append("'");
      } else if (hasDateTo) {
         String toDate = getNamedParam(params, PARAM_DATA_TO);
         fb.append(appendColumn(COL_DATE));
         fb.append(" <= '");
         fb.append(toDate);
         fb.append("'");
      }
      // openwis_alarms.severity
      if (hasParam(params, PARAM_SEVERITY)) {
         String severity = getNamedParam(params, PARAM_SEVERITY);
         fb.append(appendColumnValue(fb, COL_SEVERITY, severity));
      }
      // openwis_alarms.module
      if (hasParam(params, PARAM_MODULE)) {
         String module = '%' + getNamedParam(params, PARAM_MODULE) + '%';
         fb.append(appendColumnValue(fb, COL_MODULE, module));
      }
      // openwis_alarms.source
      if (hasParam(params, PARAM_SOURCE)) {
         String source = '%' + getNamedParam(params, PARAM_SOURCE) + '%';
         fb.append(appendColumnValue(fb, COL_SOURCE, source));
      }
      // openwis_alarms.message
      if (hasParam(params, PARAM_MESSAGE)) {
         String message = '%' + getNamedParam(params, PARAM_MESSAGE) + '%';
         fb.append(appendColumnValue(fb, COL_MESSAGE, message));
      }
      return fb.toString();
   }

   /**
    * Checks the existing of valid filter parameters
    * @param params
    * @return
    */
   private static boolean hasFilters(final Element params) {
      return params != null &&
             (hasParam(params, PARAM_DATA_FROM) || hasParam(params, PARAM_DATA_TO) ||
              hasParam(params, PARAM_MESSAGE) || hasParam(params, PARAM_MODULE) ||
              hasParam(params, PARAM_SEVERITY) || hasParam(params, PARAM_SOURCE));
   }

   /**
    * Checks the existing of valid sort order parameters.
    * @param params
    * @return
    */
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
    * @param columnName
    * @param value
    * @return
    */
   private static String appendColumnValue(final StringBuffer filterBuffer, final String columnName, final String value) {
      StringBuffer sql = new StringBuffer();
      if (filterBuffer.length() > 0) {
         sql.append(" and ");
      }
      sql.append("LOWER(" + appendColumn(columnName) + ")");
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
    * @param columnName
    * @return
    */
   private static String appendColumn(final String columnName) {
      StringBuffer sql = new StringBuffer();
      sql.append(DB_TABLE);
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

   public static void main(String[] args) {
      testHasFilters();
   }

   private static void testHasFilters() {
      Element params = new Element("testHasFilters");
      addTestParam(params, "start", "0");
      addTestParam(params, "limit", "25");
      addTestParam(params, PARAM_DATA_FROM, "2011-07-04 00:00");
      addTestParam(params, PARAM_DATA_TO, "2011-07-05 00:00");
      addTestParam(params, PARAM_MESSAGE, "message");
      addTestParam(params, PARAM_MODULE, "module");
      addTestParam(params, PARAM_SEVERITY, "ERROR");
      addTestParam(params, PARAM_SOURCE, "test%");
      addTestParam(params, PARAM_SORT_COLUMN, COL_MODULE);
      addTestParam(params, PARAM_SORT_DIR, "ASC");

      String filter = getFilterExpression(params);
      String sort = getNamedParam(params, PARAM_SORT_COLUMN, DEFAULT_SORT_FIELD);
      String dir = getNamedParam(params, PARAM_SORT_DIR, DEFAULT_SORT_ORDER);

      System.out.println(listParams(params));
      System.out.println("HasFilters: " + hasFilters(params));
      System.out.println("HasSort: " + hasSort(params));
      System.out.println("SQL: " + filter);
      System.out.println("Sort: " + sort);
      System.out.println("Dir: " + dir);
      // System.out.println("Query: " + getQuery(filter, sort, dir, 100));
   }

   private static void addTestParam(Element container, final String name, final String value) {
      Element child = new Element(name);
      container.addContent(child.setText(value));
   }

   /*
   private static String getQuery(final String filterExp,
                                  final String sortColumn,
                                  final String sortOrder,
                                  final int maxCount) {
      final String SQL_SELECT = "SELECT openwis_alarms FROM AlarmEvent openwis_alarms";
      final String DATABASE_TABLE_NAME = "openwis_alarms";

      StringBuffer sql = new StringBuffer();
      sql.append(SQL_SELECT);

      if (filterExp != null && !filterExp.isEmpty()) {
         sql.append(" WHERE ");
         sql.append(filterExp);
      }

      sql.append(" ORDER BY ");
      if (sortColumn != null && !sortColumn.isEmpty()) {
         if (!sortColumn.startsWith(DATABASE_TABLE_NAME)) {
            sql.append(DATABASE_TABLE_NAME);
            sql.append(".");
         }
         sql.append(sortColumn);
      }
      else {
         sql.append(DEFAULT_SORT_FIELD);
      }
      sql.append(" ");
      if (sortOrder != null && !sortOrder.isEmpty()) {
         sql.append(sortOrder);
      }
      else {
         sql.append(DEFAULT_SORT_ORDER);
      }

      sql.append(" LIMIT '");
      sql.append(maxCount);
      sql.append("'");

      return sql.toString();
   }
   */
}
