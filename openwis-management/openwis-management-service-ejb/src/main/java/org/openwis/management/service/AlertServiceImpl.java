/**
 *
 */
package org.openwis.management.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Level;
import org.openwis.management.entity.AlarmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the web service exposing the public interface of the
 * {@link AlertService}.
 */
@WebService(
      name = "AlertService",
      portName = "AlertServicePort",
      serviceName = "AlertService",
      targetNamespace = "http://alert.management.openwis.org/")
@SOAPBinding(
      use = SOAPBinding.Use.LITERAL,
      style = SOAPBinding.Style.DOCUMENT,
      parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(AlertService.class)
@Stateless(name = "AlertService")
public class AlertServiceImpl implements AlertService {

   // Entity manager.
   @PersistenceContext
   private EntityManager entityManager;

   private static final String UNKNOWN = "Unknown";
   private static final String UNKNOWN_SOURCE = UNKNOWN;
   private static final String UNKNOWN_LOCATION = UNKNOWN;

   private static final String DEFAULT_SORT_FIELD = "date";
   private static final String DEFAULT_SORT_ORDER = "DESC";

   private static final String SQL_SELECT = "SELECT openwis_alarms FROM AlarmEvent openwis_alarms";
   private static final String DATABASE_TABLE_NAME = "openwis_alarms";

   private static Logger LOG = LoggerFactory.getLogger(AlertServiceImpl.class);

   // -------------------------------------------------------------------------
   // Initialisation
   // -------------------------------------------------------------------------

   /**
    * Creates a new AlertServiceImpl.
    */
   public AlertServiceImpl() {
      // TODO: read logging properties from properties file
      //	   String configPath = "/opt/jboss/jboss-eap-5.1/jboss-as/server/default/deploy";
      //	   PropertyConfigurator.configure(configPath + "/log4j.properties");

	   // try to locate openwis-alerts logging
//	  try{
//		  File configFile = new File("openwis-alerts-logging.xml");
//		  if (configFile.isFile() && configFile.canRead()){
//			  DOMConfigurator.configureAndWatch(configFile.getAbsolutePath());
//		  }
//	  }
//	  catch(Exception e){
//
//	  }
//      Properties properties = new Properties();
//      properties.setProperty("log4j.rootLogger", "TRACE, A1");
//      properties.setProperty("log4j.appender.A1", "org.apache.log4j.RollingFileAppender");
//      properties.setProperty("log4j.appender.A1.file", "/tmp/AlertService.log");
//      properties.setProperty("log4j.appender.A1.maxFileSize", "1000000");
//      properties.setProperty("log4j.appender.A1.maxBackupIndex", "10");
//      properties.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
//      properties.setProperty("log4j.appender.A1.layout.ConversionPattern",
//            "%d{yyyy-MM-dd hh:mm:ss} %c{1} %-5p - %m%n");
//      PropertyConfigurator.configure(properties);
   }

   // -------------------------------------------------------------------------
   // Alarms - Resource
   // -------------------------------------------------------------------------

   /**
    * Gets the entityManager.
    *
    * @return the entityManager.
    */
   @WebMethod(exclude = true)
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * Sets the entityManager.
    *
    * @param entityManager the entityManager to set.
    */
   @WebMethod(exclude = true)
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   // -------------------------------------------------------------------------
   // Alarms - Get recent events
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   @Override
   public List<AlarmEvent> getFilteredEvents(
         @WebParam(name = "filterExp") final String filterExp,
         @WebParam(name = "index") final int index,
         @WebParam(name = "maxCount") final int maxCount) {
      return getFilteredEventsSorted(filterExp, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER, index, maxCount);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @SuppressWarnings("unchecked")
   public List<AlarmEvent> getFilteredEventsSorted(
         @WebParam(name = "filterExp") final String filterExp,
         @WebParam(name = "sortField") final String sortField,
         @WebParam(name = "sortOrder") final String sortOrder,
         @WebParam(name = "index") final int index,
         @WebParam(name = "maxCount") final int maxCount) {

      List<AlarmEvent> result = null;
      int count = maxCount;

      if (count <= 0) {
         count = 10;
      }

      if (filterExp != null && !filterExp.isEmpty()) {
         // build the SQL query (sorted)
         String sql = getQuery(filterExp, sortField, sortOrder, index, count);
         Query query = entityManager.createQuery(sql);
         query.setFirstResult(index);
         query.setMaxResults(count);

         result = query.getResultList();
      }
      else {
         result = getRecentEvents(null, null, index, count);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<AlarmEvent> getRecentEvents(
      @WebParam(name = "from") final Date from,
      @WebParam(name = "to") final Date to,
      @WebParam(name = "index") final int index,
      @WebParam(name = "maxCount") final int maxCount) {
      return getRecentEventsSorted(from, to, DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER, index, maxCount);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   @SuppressWarnings("unchecked")
   public List<AlarmEvent> getRecentEventsSorted(
         @WebParam(name = "from") final Date from,
         @WebParam(name = "to") final Date to,
         @WebParam(name = "sortField") final String sortField,
         @WebParam(name = "sortOrder") final String sortOrder,
         @WebParam(name = "index") final int index,
         @WebParam(name = "maxCount") final int maxCount) {

      int count = maxCount;

      if (count <= 0) {
         count = 10;
      }

      // check filter parameter and construct a a SQL token used for the WHERE clause
      String filterExp = null;
      if (from != null && to != null) {
         filterExp = DATABASE_TABLE_NAME + "." + DEFAULT_SORT_FIELD + " BETWEEN '" + from + "' AND '" + to + "'";
      }
      else if (from != null) {
         filterExp = DATABASE_TABLE_NAME + "." + DEFAULT_SORT_FIELD + " >= '" + from + "'";
      }
      else if (to != null) {
         filterExp = DATABASE_TABLE_NAME + "." + DEFAULT_SORT_FIELD + " <= '" + to + "'";
      }
      // build the SQL query (sorted)
      String sql = getQuery(filterExp, sortField, sortOrder, index, count);

      Query query = entityManager.createQuery(sql);
      query.setFirstResult(index);
      query.setMaxResults(count);

      List<AlarmEvent> result = query.getResultList();

      return result;
   }

   // -------------------------------------------------------------------------
   // Alarms - Raise events
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   @Override
   public void raiseError(
         @WebParam(name = "source") final String source,
         @WebParam(name = "location") final String location,
         @WebParam(name = "description") final String description) {
      raiseEvent(source, location, Level.ERROR.toString(), description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void raiseWarning(
         @WebParam(name = "source") final String source,
         @WebParam(name = "location") final String location,
         @WebParam(name = "description") final String description) {
      raiseEvent(source, location, Level.WARN.toString(), description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void raiseInformation(
         @WebParam(name = "source") final String source,
         @WebParam(name = "location") final String location,
         @WebParam(name = "description") final String description) {
      raiseEvent(source, location, Level.INFO.toString(), description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void raiseEvent(
         @WebParam(name = "source") final String source,
         @WebParam(name = "location") final String location,
         @WebParam(name = "severity") final String severity,
         @WebParam(name = "eventId") final String eventKey,
         @WebParam(name = "arguments") final Object... arguments) {

      // at least a valid message should be given
      if (eventKey == null || "".equals(eventKey.trim())) {
         return;
      }

      // create new alarm
      AlarmEvent alarm = new AlarmEvent();
      alarm.setDate(new Date());

      // Severity
      Level level = null;
      if (severity == null){
    	  level = Level.toLevel(getSeverityForKey(eventKey), Level.INFO);
      }
      if (level == null) {
         level = Level.toLevel(severity, Level.INFO);
      }
      alarm.setSeverity(level.toString());

      // Source
      if (source != null && !"".equals(source.trim())) {
         alarm.setSource(source.trim());
      }
      else {
         alarm.setSource(UNKNOWN_SOURCE);
      }

      // Location
      if (location != null && !"".equals(location.trim())) {
         alarm.setModule(location.trim());
      }
      else {
         alarm.setModule(UNKNOWN_LOCATION);
      }

      // Message: defaults to message pattern
      String description = eventKey;

      // lookup for message template corresponding to the event key.
      String template = getMessageTemplateForKey(eventKey);
      if (template != null) {
          if (arguments != null && arguments.length > 0) {
              // 1st chance: user message format pattern
              try {
                 description = MessageFormat.format(template, arguments);
              }
              catch (Exception fisrt) {
                 // 2nd chance: user message format pattern
                 try {
                    description = String.format(template, arguments);
                 }
                 catch (Exception last) {
                 }
              }
          }
      }

      alarm.setMessage(description);

      // Append message to regular log
      String message = String.format("[%s] %s: %s", alarm.getModule(), alarm.getSource(), alarm.getMessage());
      logMessage(level, message);

      // Create the alarm event entry in the database
      EntityManager entityMgr = getEntityManager();
      if (entityMgr != null) {
         entityMgr.persist(alarm);
      }
   }

   /** Don't use log4j directly, conflict with JBoss */
   private void logMessage(Level level, String message) {
      switch (level.toInt()) {
      case Level.WARN_INT:
         LOG.warn(message);
         break;
      case Level.ERROR_INT:
         LOG.error(message);
         break;
      default:
         LOG.info(message);
      }
   }

   private String getMessageTemplateForKey(String eventKey){
	   ResourceBundle bundle = ResourceBundle.getBundle("openwis-alerts");
	   String template = null;
	   try
	   {
	      template = bundle.getString(eventKey + ".text");
	   }
	   catch (Exception e) {
	      return null;
	   }
	   if (template != null) return template;
	   return null;
   }

   private String getSeverityForKey(String eventKey){
	   String template = null;
      try
      {
    	 ResourceBundle bundle = ResourceBundle.getBundle("openwis-alerts");
         template = bundle.getString(eventKey + ".level");
      }
      catch (Exception e) {
         return null;
      }
	   if (template != null) return template;
	   return null;
   }

   private String getQuery(final String filterExp,
                           final String sortColumn,
                           final String sortOrder,
                           final int index,
                           final int maxCount) {
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
      sql.append("' OFFSET '");
      sql.append(index);
      sql.append("'");

      return sql.toString();
   }

	@Override
	public long getFilteredEventsCount(
			@WebParam (name = "filterExp") String filterExp) {
		long recentEventsCount = 0;

		String sql = getCountQuery(filterExp);

		try {
			recentEventsCount = (Long) entityManager.createQuery(sql).getSingleResult();
		}
		catch (NoResultException e){
		}
		catch (NonUniqueResultException e){
		}

		return recentEventsCount;
	}

	@Override
	public long getRecentEventsCount(
			@WebParam (name = "from") Date from,
			@WebParam (name = "to") Date to) {
		long recentEventsCount = 0;

		String filterExp = null;
		if (from != null && to != null) {
			filterExp = DATABASE_TABLE_NAME + "." + DEFAULT_SORT_FIELD + " BETWEEN '" + from + "' AND '" + to + "'";
		}
		else if (from != null) {
			filterExp = DATABASE_TABLE_NAME + "." + DEFAULT_SORT_FIELD + " >= '" + from + "'";
		}
		else if (to != null) {
			filterExp = DATABASE_TABLE_NAME + "." + DEFAULT_SORT_FIELD + " <= '" + to + "'";
		}

		String sql = getCountQuery(filterExp);
		try {
			recentEventsCount = (Long) entityManager.createQuery(sql).getSingleResult();
		}
		catch (NoResultException e){
		}
		catch (NonUniqueResultException e){
		}

		return recentEventsCount;
	}

	private String getCountQuery(String filterExp) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(openwis_alarms) FROM AlarmEvent openwis_alarms");

		if (filterExp != null && !filterExp.isEmpty()) {
			sql.append(" WHERE ");
			sql.append(filterExp);
		}

		return sql.toString();
	}
}