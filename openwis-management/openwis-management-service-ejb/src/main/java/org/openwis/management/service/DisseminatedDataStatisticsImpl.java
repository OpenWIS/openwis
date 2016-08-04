/**
 *
 */
package org.openwis.management.service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.openwis.management.entity.SortDirection;
import org.openwis.management.entity.UserDisseminatedData;
import org.openwis.management.entity.UserDisseminatedDataColumn;
import org.openwis.management.service.bean.UserDisseminatedDataResult;
import org.openwis.management.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the management interface to monitor the volume of disseminated data.
 */
@WebService(name = "DisseminatedDataStatistics", portName = "DisseminatedDataStatisticsPort", serviceName = "DisseminatedDataStatistics", targetNamespace = "http://monitoring.management.openwis.org/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(DisseminatedDataStatistics.class)
@Stateless(name = "DisseminatedDataStatistics")
public class DisseminatedDataStatisticsImpl implements DisseminatedDataStatistics {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(DisseminatedDataStatisticsImpl.class);

   /** The entity manager. */
   @PersistenceContext
   private EntityManager em;

   // -------------------------------------------------------------------------
   // Update Methods
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.DisseminatedDataStatistics#updateUserDisseminatedByToolData(java.lang.String, java.lang.String, int, long)
    */
   @Override
   public void updateUserDisseminatedByToolData(@WebParam(name = "userId") final String userId,
         @WebParam(name = "date") final String date, @WebParam(name = "nbFiles") final int nbFiles,
         @WebParam(name = "totalSize") final long totalSize) {
      // check arguments
      if (nbFiles < 0) {
         throw new IllegalArgumentException("The number of file should been ≥0");
      }
      if (totalSize < 0) {
         throw new IllegalArgumentException("The size should been ≥0");
      }
      try {
         if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                  "Updating disseminated data statistics: %s, volume = %s, nbFiles: %s", date,
                  totalSize, nbFiles));
         }
         // create new entity
         UserDisseminatedData data = getUserDisseminatedData(userId, date);
         if (data == null) {
            Calendar cal = getCalendarFromDate(date);

            data = new UserDisseminatedData();
            data.setUserId(userId);
            data.setDate(cal.getTime());
            data.setDissToolNbFiles(nbFiles);
            data.setDissToolSize(totalSize);
            data.setSize(0L);
            data.setNbFiles(0);
            em.persist(data);
            // flush to make fails asap for multi-thread safety (mdb may be retried)
            // unique constraint checked on db
            em.flush();
         } else {
            Query updateQuery = em.createNativeQuery("update openwis_disseminated_data "
                  + "set diss_tool_size = diss_tool_size + ?1, diss_tool_nb_files = diss_tool_nb_files + ?2  where user_disseminated_data_id = ?3");
            updateQuery.setParameter(1, totalSize);
            updateQuery.setParameter(2, nbFiles);
            updateQuery.setParameter(3, data.getId());
            updateQuery.executeUpdate();
         }
      } catch (Exception e) {
         // report exception
         logger.error(String.format(
               "Failed to update disseminated data statistics: %s, volume = %s, nFiles: %s", date,
               totalSize, nbFiles), e);
         throw new RuntimeException(e);
      }
   }

   @Override
   public void updateUserExtractedData(@WebParam(name = "userId") final String userId,
         @WebParam(name = "date") final String date, @WebParam(name = "nbFiles") final int nbFiles,
         @WebParam(name = "totalSize") final long totalSize) {
      // check arguments
      if (nbFiles < 0) {
         throw new IllegalArgumentException("The number of file should been ≥0");
      }
      if (totalSize < 0) {
         throw new IllegalArgumentException("The size should been ≥0");
      }
      try {
         if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                  "Updating extracted data statistics: %s, volume = %s, nbFiles: %s", date,
                  totalSize, nbFiles));
         }
         // create new entity
         UserDisseminatedData data = getUserDisseminatedData(userId, date);
         if (data == null) {
            Calendar cal = getCalendarFromDate(date);

            data = new UserDisseminatedData();
            data.setUserId(userId);
            data.setDate(cal.getTime());
            data.setSize(totalSize);
            data.setNbFiles(nbFiles);
            data.setDissToolSize(0L);
            data.setDissToolNbFiles(0);
            em.persist(data);
            // flush to make fails asap for multi-thread safety (mdb may be retried)
            // unique constraint checked on db
            em.flush();
         } else {
            Query updateQuery = em.createNativeQuery("update openwis_disseminated_data "
                  + "set size = size + ?1, nb_files = nb_files + ?2  where user_disseminated_data_id = ?3");
            updateQuery.setParameter(1, totalSize);
            updateQuery.setParameter(2, nbFiles);
            updateQuery.setParameter(3, data.getId());
            updateQuery.executeUpdate();
         }
      } catch (Exception e) {
         // report exception
         throw new RuntimeException(String.format(
               "Failed to update extracted data statistics: %s, volume = %s, nFiles: %s", date,
               totalSize, nbFiles), e);
      }
   }

   // -------------------------------------------------------------------------
   // Report Methods
   // -------------------------------------------------------------------------

   /**
    * Retrieves the overall volume of data disseminated per day and per user.
    *
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of statistical records.
    */
   @Override
   public List<UserDisseminatedData> getDisseminatedDataStatistics(
         @WebParam(name = "maxItemsCount") final int maxItemsCount) {
      return getUsersDisseminatedDataByUser("", 0, maxItemsCount, null, null).getList();
   }

   /**
    * Retrieve the overall volume of data disseminated per day for a given user.
    *
    * @param userId the user id.
    * @param date the date.
    * @return the user disseminated data.
    */
   @Override
   public UserDisseminatedData getUserDisseminatedData(
         @WebParam(name = "userId") final String userId, @WebParam(name = "date") final String date) {
      UserDisseminatedData result = null;
      if (userId != null && date != null) {
         Calendar cal;
         try {
            cal = getCalendarFromDate(date);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Date should be formated with the '"
                  + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
         }

         // Create query
         Query query = em.createNamedQuery("UserDisseminatedData.getByUserAndDate");
         query.setParameter("user", userId);
         query.setParameter("date", cal.getTime());

         // retrieve data
         try {
            result = (UserDisseminatedData) query.getSingleResult();
         } catch (NoResultException e) {
            logger.info("No disseminataion found for user {} at {}", new Object[] {userId, date});
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.DisseminatedDataStatistics#getUsersDisseminatedDataByUser(java.lang.String, int, int, org.openwis.management.entity.UserDisseminatedDataColumn, org.openwis.management.entity.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public UserDisseminatedDataResult getUsersDisseminatedDataByUser(
         @WebParam(name = "user") String user, @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") UserDisseminatedDataColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {
      UserDisseminatedDataResult result = null;
      List<UserDisseminatedData> list = null;
      logger.debug("Get UserDisseminatedData");

      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0!");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }

      // Default column is URN
      UserDisseminatedDataColumn col;
      if (column != null) {
         col = column;
      } else {
         col = UserDisseminatedDataColumn.DATE;
      }

      // Default direction is Descending
      SortDirection dir;
      if (sortDirection != null) {
         dir = sortDirection;
      } else {
         dir = SortDirection.ASC;
      }

      // Create query
      String q = MessageFormat
            .format(
                  "SELECT udd FROM UserDisseminatedData udd WHERE udd.userId LIKE :user ORDER BY udd.{0} {1}",
                  col.getAttribute(), dir);
      Query query = em.createQuery(q);
      query.setParameter("user", user + "%");
      query.setFirstResult(firstResult);
      query.setMaxResults(maxResults);

      // Process query
      try {
         list = query.getResultList();
      } catch (NoResultException e) {
         list = Collections.emptyList();
      }

      // Count
      query = em.createNamedQuery("UserDisseminatedData.countByUser");
      query.setParameter("user", user + "%");

      int count = ((Number) query.getSingleResult()).intValue();

      // Build result
      result = new UserDisseminatedDataResult();
      result.setList(list);
      result.setCount(count);

      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.DisseminatedDataStatistics#getUsersDisseminatedData(java.lang.String, java.lang.String, int, int, org.openwis.management.entity.UserDisseminatedDataColumn, org.openwis.management.entity.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<UserDisseminatedData> getUsersDisseminatedData(
         @WebParam(name = "users") Set<String> users, @WebParam(name = "date") String date) {
      List<UserDisseminatedData> result = null;
      logger.debug("Get UserDisseminatedData");
      if (users != null) {
         try {
            Calendar cal = getCalendarFromDate(date);

            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            for (String user : users) {
               if (isFirst) {
                  isFirst = false;
               } else {
                  sb.append(", ");
               }
               sb.append('\'');
               sb.append(StringEscapeUtils.escapeSql(user));
               sb.append('\'');
            }

            // Create query
            String q = MessageFormat
                  .format(
                        "SELECT udd FROM UserDisseminatedData udd WHERE udd.date = :date AND udd.userId IN ({0})",
                        sb);
            Query query = em.createQuery(q);
            query.setParameter("date", cal.getTime());

            // Process query
            try {
               result = query.getResultList();
            } catch (NoResultException e) {
               result = Collections.emptyList();
            }

         } catch (ParseException pe) {
            throw new IllegalArgumentException("Date should be formated with the '"
                  + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
         }
      }
      return result;
   }

   /**
    * Retrieve the overall volume of data disseminated per day for a given user
    * applying a filter from a given date to a given date.
    *
    * @param userId the user id.
    * @param from the from date.
    * @param to the to date.
    * @return the user disseminated data.
    */
   @Override
   public UserDisseminatedData getUserDisseminatedDataInInterval(
         @WebParam(name = "userId") final String userId,
         @WebParam(name = "from") final String from, @WebParam(name = "to") final String to) {
      UserDisseminatedData result = null;

      try {
         Calendar calFrom = DateTimeUtils.getUTCCalendar();
         calFrom.setTime(DateTimeUtils.parse(from));
         Calendar calTo = DateTimeUtils.getUTCCalendar();
         calTo.setTime(DateTimeUtils.parse(to));
         
         // Check
         if (calFrom.after(calTo)) {
            throw new IllegalArgumentException(MessageFormat.format(
                  "The FROM date ({0}) should be before the TO ({1})", from, to));
         }

         result = new UserDisseminatedData();
         result.setUserId(userId);

         Query query = em.createNamedQuery("UserDisseminatedData.getByUserBetweenDate");

         query.setParameter("user", userId);
         query.setParameter("from", calFrom.getTime());
         query.setParameter("to", calTo.getTime());

         // retrieve data
         try {
            Object[] res = (Object[]) query.getSingleResult();
            result.setNbFiles(((Long) res[0]).intValue());
            result.setSize((Long) res[1]);
            result.setDissToolNbFiles(((Long) res[2]).intValue());
            result.setDissToolSize((Long) res[3]);
         } catch (NoResultException e) {
            logger.info("No disseminataion found for users between {} and {}", from, to);
         }

      } catch (ParseException pe) {
         throw new IllegalArgumentException("Date should be formated with the '"
               + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
      }
      return result;
   }

   /**
    * Retrieve the overall volume of data disseminated per day and per user.
    *
    * @param date the date.
    * @return the user disseminated data.
    */
   @Override
   public UserDisseminatedData getDisseminatedData(@WebParam(name = "date") final String date) {
      UserDisseminatedData result = null;
      try {
         Calendar cal = getCalendarFromDate(date);

         // Query
         Query query = em.createNamedQuery("DisseminatedData.getByDate");
         query.setParameter("date", cal.getTime());

         result = new UserDisseminatedData();
         result.setDate(cal.getTime());
         // retrieve data
         try {
            Object[] res = (Object[]) query.getSingleResult();
            result.setNbFiles(((Long) res[0]).intValue());
            result.setSize((Long) res[1]);
            result.setDissToolNbFiles(((Long) res[2]).intValue());
            result.setDissToolSize((Long) res[3]);
         } catch (NoResultException e) {
            logger.info("No disseminataion found for users at {}", date);
         }
      } catch (ParseException pe) {
         throw new IllegalArgumentException("Date should be formated with the '"
               + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
      }

      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.DisseminatedDataStatistics#getDisseminatedDataInInterval(java.lang.String, java.lang.String, int, int, org.openwis.management.entity.UserDisseminatedDataColumn, org.openwis.management.entity.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public UserDisseminatedDataResult getDisseminatedDataInInterval(String from, String to,
         int firstResult, int maxResults, UserDisseminatedDataColumn column,
         SortDirection sortDirection) {
      UserDisseminatedDataResult result = null;
      List<UserDisseminatedData> list = null;
      logger.debug("Get getDisseminatedDataInInterval");

      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0!");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }

      // Default column is URN
      UserDisseminatedDataColumn col;
      if (column != null) {
         col = column;
      } else {
         col = UserDisseminatedDataColumn.DATE;
      }

      // Default direction is Descending
      SortDirection dir;
      if (sortDirection != null) {
         dir = sortDirection;
      } else {
         dir = SortDirection.DESC;
      }

      Calendar calFrom = null;
      Calendar calTo = null;
      try {
         String betweenConstraints = "";
         if (from != null && to != null) {
            calFrom = DateTimeUtils.getUTCCalendar();
            calFrom.setTime(DateTimeUtils.parse(from));
            calTo = DateTimeUtils.getUTCCalendar();
            calTo.setTime(DateTimeUtils.parse(to));
            // Check
            if (calFrom.after(calTo)) {
               throw new IllegalArgumentException(MessageFormat.format(
                     "The FROM date ({0}) should be before the TO ({1})", from, to));
            }
            betweenConstraints = "WHERE uds.date BETWEEN :from AND :to"; 
         }

         // Create query
         String q = MessageFormat
               .format(
                     "SELECT SUM(uds.nbFiles) AS nbFiles, SUM(uds.size) AS totalSize, SUM(uds.dissToolNbFiles) AS dissToolNbFiles, SUM(uds.dissToolSize) AS dissToolSize, uds.date FROM UserDisseminatedData uds "
                     + betweenConstraints + " GROUP BY uds.date ORDER BY uds.{0} {1}",
                     col.getAttribute(), dir);
         Query query = em.createQuery(q);
         if (from != null && to != null) {
            query.setParameter("from", calFrom.getTime());
            query.setParameter("to", calTo.getTime());
         }

         query.setFirstResult(firstResult);
         query.setMaxResults(maxResults);

         // Process query
         try {
            UserDisseminatedData data;

            list = new ArrayList<UserDisseminatedData>();
            for (Object[] res : (List<Object[]>) query.getResultList()) {
               data = new UserDisseminatedData();
               data.setNbFiles(((Long) res[0]).intValue());
               data.setSize((Long) res[1]);
               data.setDissToolNbFiles(((Long) res[2]).intValue());
               data.setDissToolSize((Long) res[3]);
               data.setDate((Date) res[4]);
               list.add(data);
            }

         } catch (NoResultException e) {
            list = Collections.emptyList();
         }

         // Count
         q = "SELECT COUNT(DISTINCT uds.date) FROM UserDisseminatedData uds " + betweenConstraints;
         query = em.createQuery(q);
         if (from != null && to != null) {
            query.setParameter("from", calFrom.getTime());
            query.setParameter("to", calTo.getTime());
         }
         
         int count = ((Number) query.getSingleResult()).intValue();

         // Build result
         result = new UserDisseminatedDataResult();
         result.setList(list);
         result.setCount(count);
      } catch (ParseException pe) {
         throw new IllegalArgumentException("Date should be formated with the '"
               + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
      }

      return result;

   }

   /**
    * Gets the calendar from date.
    * Reset Hour, minutes, ... fields.
    *
    * @param date the date
    * @return the calendar from date
    * @throws ParseException the parse exception
    */
   private Calendar getCalendarFromDate(String date) throws ParseException {
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.setTime(DateTimeUtils.parse(date));
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return cal;
   }
}
