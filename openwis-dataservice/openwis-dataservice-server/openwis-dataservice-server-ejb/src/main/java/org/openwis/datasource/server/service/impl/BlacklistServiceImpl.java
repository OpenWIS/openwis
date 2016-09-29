package org.openwis.datasource.server.service.impl;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openwis.dataservice.common.domain.bean.BlacklistInfoResult;
import org.openwis.dataservice.common.domain.entity.blacklist.BlacklistInfo;
import org.openwis.dataservice.common.domain.entity.enumeration.BlacklistInfoColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.BlacklistStatus;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.service.MailSender;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.StatisticsMessage;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.entity.UserDisseminatedData;
import org.openwis.management.service.DisseminatedDataStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BlacklistService implementation.
 */
@WebService(targetNamespace = "http://dataservice.openwis.org/", name = "BlacklistService", portName = "BlacklistServicePort", serviceName = "BlacklistService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Local(BlacklistService.class)
@Stateless(name = "BlacklistService")
public class BlacklistServiceImpl implements BlacklistService {

   /** The logger. */
   private final Logger logger = LoggerFactory.getLogger(BlacklistServiceImpl.class);

   /** The entity manager. */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The mail sender. */
   @EJB
   private MailSender mailSender;
   
   /**
    * injection ConnectionFactory
    */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory cf;

   /**
    * injection queue
    */
   @Resource(mappedName = "java:/queue/StatisticsQueue")
   private Queue queue;

   /** The data statistics. */
   private DisseminatedDataStatistics dataStatistics;

   /**
    * Instantiates a new blacklist service implementation.
    */
   public BlacklistServiceImpl() {
      super();
   }

   /**
    * Gets the data statistics.
    *
    * @return the data statistics
    */
   private DisseminatedDataStatistics getDataStatistics() {
      if (dataStatistics == null) {
         try {
            dataStatistics = ManagementServiceBeans.getInstance().getDisseminatedDataStatistics();
         } catch (NamingException e) {
            dataStatistics = null;
         }
      }
      
      return dataStatistics;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#checkAndUpdateDisseminatedData(java.lang.String, java.util.Date, int, long)
    */
   @Override
   public boolean checkAndUpdateDisseminatedData(@WebParam(name = "user") String user,
		   @WebParam(name = "email") String email,
         @WebParam(name = "date") String date, @WebParam(name = "nbFiles") int nbFiles,
         @WebParam(name = "totalSize") long totalSize) {
      BlacklistInfo bli = getUserBlackListInfo(user, true);

      UserDisseminatedData data = getDataStatistics().getUserDisseminatedData(user, date);

      // Send update statistics message
      StatisticsMessage message = new StatisticsMessage();
      message.setUserId(user);
      message.setDate(date);
      message.setNbFiles(nbFiles);
      message.setTotalSize(totalSize);
      message.setCommand(StatisticsMessage.CMD_UPDATE_USER_EXTRACTED_DATA);
      sendStatisticsUpdate(message);
      
      // as the statistics are updated asynchronously, the blacklisting check
      // is done with last available values in db + current ones
      if (data == null) {
         data = new UserDisseminatedData();
         data.setUserId(user);
         data.setSize(totalSize);
         data.setNbFiles(nbFiles);
         data.setDissToolSize(0L);
         data.setDissToolNbFiles(0);
      } else {
         data.setSize(data.getSize() + totalSize);
         data.setNbFiles(data.getNbFiles() + nbFiles);
      }

      return checkBlacklistStatus(user,email, data, bli);
   }

   /**
    * Send statistics update message to the dedicated JMS queue.
    *
    * @param statisticsMessage the statistics message
    */
   private void sendStatisticsUpdate(StatisticsMessage statisticsMessage) {
      Connection connection = null;
      try {
         // Create queue connection
         // Create a JMS Connection
         connection = cf.createConnection();
         // Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);

         // Create XML message
         StringWriter sw;
         sw = new StringWriter();
         Serializer.serialize(statisticsMessage, sw);
         String textMessage = sw.toString();
         TextMessage messageToSend = session.createTextMessage(textMessage);
         // Send message in the request queue
         messageProducer.send(messageToSend);

      } catch (Throwable t) {
         logger.error("Unable to create message for the statistics queue", t);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (JMSException jme) {
               logger.error("Unable to properly close connection to the queue", jme);
            }
         }
      }
   }
   
   /**
    * Check blacklist status.
    *
    * @param user the user
    * @param email the email
    * @param data the data
    * @param bli the blacklist info
    * @return true, if successful
    */
   private boolean checkBlacklistStatus(String user, String email, UserDisseminatedData data,
         BlacklistInfo bli) {
      ResourceBundle bundle = ResourceBundle.getBundle("openwis-blacklist");
      String from = JndiUtils.getString(DataServiceConfiguration.MAIL_FROM);
      String title = null;
      String body = null;
      // Check files numbers
      boolean isValid = true;
      if (data.getNbFiles() >= bli.getNbDisseminationBlacklistThreshold()) {
         isValid = false;
         bli.setStatus(BlacklistStatus.BLACKLISTED_BY_NUMBER_OF_DISSEMINATIONS);
         updateUserBlackListInfo(bli);
         title = bundle.getString("msg.user.blacklisted.number.subject");
         body = MessageFormat.format(bundle.getString("msg.user.blacklisted.number.content"),
               data.getNbFiles(), bli.getNbDisseminationBlacklistThreshold());
      } else if (data.getNbFiles() >= bli.getNbDisseminationWarnThreshold()) {
         title = bundle.getString("msg.user.warn.number.subject");
         body = MessageFormat.format(bundle.getString("msg.user.warn.number.content"),
               data.getNbFiles(), bli.getNbDisseminationBlacklistThreshold());
      }

      // Check Volume
      if (isValid) {
         if (data.getSize() >= bli.getVolDisseminationBlacklistThreshold()) {
            bli.setStatus(BlacklistStatus.BLACKLISTED_BY_VOLUME_OF_DISSEMINATIONS);
            updateUserBlackListInfo(bli);
            title = bundle.getString("msg.user.blacklisted.size.subject");
            body = MessageFormat.format(bundle.getString("msg.user.blacklisted.size.content"),
                  data.getSize(), bli.getVolDisseminationBlacklistThreshold());
         } else if (data.getSize() >= bli.getVolDisseminationWarnThreshold()) {
            title = bundle.getString("msg.user.warn.size.subject");
            body = MessageFormat.format(bundle.getString("msg.user.warn.size.content"),
                  data.getSize(), bli.getVolDisseminationBlacklistThreshold());
         }
      }
      // Send mail
      if (title != null) {
         mailSender.sendMail(from, email, title, body);
      }
      return isValid;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#isUserBlacklisted(java.lang.String)
    */
   @Override
   public boolean isUserBlacklisted(@WebParam(name = "user") String user) {
      List<BlacklistStatus> notBlacklisted = Arrays.asList(
            BlacklistStatus.NOT_BLACKLISTED_BY_ADMIN, BlacklistStatus.NOT_BLACKLISTED);
      BlacklistInfo blInfo = getUserBlackListInfoIfExists(user);
      return !(blInfo == null || notBlacklisted.contains(blInfo.getStatus()));
   }

   /**
    * Sets the user blacklisted.
    *
    * @param user the new user blacklisted
    * @param blacklisted the blacklisted status
    */
   @Override
   public void setUserBlacklisted(@WebParam(name = "user") String user,
         @WebParam(name = "blacklisted") boolean blacklisted) {
      if (user != null) {
         BlacklistInfo ubli = getUserBlackListInfoIfExists(user);
         if (ubli == null) {
            ubli = new BlacklistInfo();
            ubli.setUser(user);
         }
         if (blacklisted) {
            ubli.setStatus(BlacklistStatus.BLACKLISTED_BY_ADMIN);
         } else {
            ubli.setStatus(BlacklistStatus.NOT_BLACKLISTED_BY_ADMIN);
         }
         updateUserBlackListInfo(ubli);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#updateUserBlackListInfo(org.openwis.dataservice.common.domain.entity.blacklist.BlacklistInfo)
    */
   @Override
   public BlacklistInfo updateUserBlackListInfo(
         @WebParam(name = "blacklistInfo") BlacklistInfo blacklistInfo) {
      if (blacklistInfo != null) {
         if (blacklistInfo.getId() == null) {
            // Create
            entityManager.persist(blacklistInfo);
            logger.info("Blacklist Threshold created: {}", blacklistInfo);
         } else {
            entityManager.merge(blacklistInfo);
            logger.info("Blacklist Threshold updated: {}", blacklistInfo);
         }
      }
      return blacklistInfo;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#getUserBlackListInfo(java.lang.String)
    */
   @Override
   public BlacklistInfo getUserBlackListInfoIfExists(@WebParam(name = "user") String user) {
      BlacklistInfo result = null;

      Query query = entityManager.createNamedQuery("BlacklistInfo.FindByUser");
      query.setParameter("user", user);
      try {
         result = (BlacklistInfo) query.getSingleResult();
      } catch (NoResultException e) {
         logger.info("No blacklist information found for user: {}", user);
         // Build default result
         result = new BlacklistInfo();
         result.setUser(user);
         result.setStatus(BlacklistStatus.NOT_BLACKLISTED);
         result.setNbDisseminationWarnThreshold(JndiUtils
               .getLong(DataServiceConfiguration.BLACKLIST_DEFAULT_NB_WARN));
         result.setNbDisseminationBlacklistThreshold(JndiUtils
               .getLong(DataServiceConfiguration.BLACKLIST_DEFAULT_NB_BLACKLIST));
         result.setVolDisseminationWarnThreshold(JndiUtils
               .getLong(DataServiceConfiguration.BLACKLIST_DEFAULT_VOL_WARN));
         result.setVolDisseminationBlacklistThreshold(JndiUtils
               .getLong(DataServiceConfiguration.BLACKLIST_DEFAULT_VOL_BLACKLIST));

      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#getUserBlackListInfo(java.lang.String, boolean)
    */
   @Override
   public BlacklistInfo getUserBlackListInfo(String user, boolean create) {
      BlacklistInfo result = getUserBlackListInfoIfExists(user);
      if (create && result.getId() == null) {
         entityManager.persist(result);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#getUsersBlackListInfo(int, int, org.openwis.dataservice.common.domain.entity.enumeration.BlacklistInfoColumn, org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public BlacklistInfoResult getUsersBlackListInfo(
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") BlacklistInfoColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {
      BlacklistInfoResult result = null;
      List<BlacklistInfo> list;
      logger.debug("Get BlacklistInfo");
      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0 !");
      }
      // Default column is User
      BlacklistInfoColumn col;
      if (column != null) {
         col = column;
      } else {
         col = BlacklistInfoColumn.USER;
      }

      // Default direction is Descending
      SortDirection dir;
      if (sortDirection != null) {
         dir = sortDirection;
      } else {
         dir = SortDirection.ASC;
      }

      // Create query
      String q = MessageFormat.format("SELECT bi FROM BlacklistInfo bi ORDER BY bi.{0} {1}",
            col.getAttribute(), dir);
      Query query = entityManager.createQuery(q);
      query.setFirstResult(firstResult);
      query.setMaxResults(maxResults);

      // Process query
      try {
         list = query.getResultList();
      } catch (NoResultException e) {
         list = Collections.emptyList();
      }

      // Count
      query = entityManager.createNamedQuery("BlacklistInfo.count");
      int count = ((Number) query.getSingleResult()).intValue();

      // Build result
      result = new BlacklistInfoResult();
      result.setList(list);
      result.setCount(count);

      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.BlacklistService#getUsersBlackListInfo(java.lang.String, int, int, org.openwis.dataservice.common.domain.entity.enumeration.BlacklistInfoColumn, org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public BlacklistInfoResult getUsersBlackListInfoByUser(
         @WebParam(name = "startWith") String startWith,
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") BlacklistInfoColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {
      BlacklistInfoResult result = null;
      List<BlacklistInfo> list;
      logger.debug("Get BlacklistInfo");
      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0 !");
      }
      // Default column is User
      BlacklistInfoColumn col;
      if (column != null) {
         col = column;
      } else {
         col = BlacklistInfoColumn.USER;
      }

      // Default direction is Descending
      SortDirection dir;
      if (sortDirection != null) {
         dir = sortDirection;
      } else {
         dir = SortDirection.ASC;
      }

      // Create query
      String q = MessageFormat.format(
            "SELECT bi FROM BlacklistInfo bi WHERE bi.user like ''{0}%'' ORDER BY bi.{1} {2}",
            startWith, col.getAttribute(), dir);
      Query query = entityManager.createQuery(q);
      query.setFirstResult(firstResult);
      query.setMaxResults(maxResults);

      // Process query
      try {
         list = query.getResultList();
      } catch (NoResultException e) {
         list = Collections.emptyList();
      }

      // Count
      query = entityManager.createNamedQuery("BlacklistInfo.countByUser");
      query.setParameter("user", startWith + "%");
      int count = ((Number) query.getSingleResult()).intValue();

      // Build result
      result = new BlacklistInfoResult();
      result.setList(list);
      result.setCount(count);

      return result;
   }
}
