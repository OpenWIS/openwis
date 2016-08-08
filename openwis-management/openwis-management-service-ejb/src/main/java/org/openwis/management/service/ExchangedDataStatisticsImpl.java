/**
 *
 */
package org.openwis.management.service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openwis.management.entity.ExchangedData;
import org.openwis.management.entity.ExchangedDataColumn;
import org.openwis.management.entity.SortDirection;
import org.openwis.management.service.bean.ExchangedDataResult;
import org.openwis.management.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the management interface to monitor the volume of extracted data.
 */
@WebService(name = "ExchangedDataStatistics", portName = "ExchangedDataStatisticsPort", serviceName = "ExchangedDataStatistics", targetNamespace = "http://monitoring.management.openwis.org/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(ExchangedDataStatistics.class)
@Stateless(name = "ExchangedDataStatistics")
public class ExchangedDataStatisticsImpl implements ExchangedDataStatistics {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ExchangedDataStatisticsImpl.class);

   /** The entity manager. */
   @PersistenceContext
   private EntityManager em;

   // -------------------------------------------------------------------------
   // Update Statistics
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.ExchangedDataStatistics#updateExchangedData(java.lang.String, long, java.lang.String, long)
    */
   @Override
   public void updateExchangedData(@WebParam(name = "date") String date,
         @WebParam(name = "source") String source, @WebParam(name = "nbMetadata") long nbMetadata,
         @WebParam(name = "totalSize") long totalSize) {
      // check arguments
      if (nbMetadata < 0) {
         throw new IllegalArgumentException("The number of metadata should been ≥0");
      }
      if (totalSize < 0) {
         throw new IllegalArgumentException("The size should been ≥0");
      }
      try {
         if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                  "Updating extracted data statistics: %s, source= %s, volume = %s, nbFiles: %s",
                  source, date, totalSize, nbMetadata));
         }
         // create new entity
         ExchangedData data = getExchangedData(date, source);
         if (data == null) {
            Calendar cal = getCalendarFromDate(date);

            data = new ExchangedData();
            data.setNbMetadata(nbMetadata);
            data.setDate(cal.getTime());
            data.setTotalSize(totalSize);
            data.setSource(source);
            em.persist(data);
         } else {
            data.setTotalSize(data.getTotalSize() + totalSize);
            data.setNbMetadata(data.getNbMetadata() + nbMetadata);
            em.merge(data);
         }
      } catch (Exception e) {
         // report exception
         logger.error(
               String.format(
                     "Failed to update exchanged data statistics: %s, source= %s, volume = %s, nFiles: %s",
                     date, source, totalSize, nbMetadata), e);
      }
   }

   // -------------------------------------------------------------------------
   // Query Reports
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.ExchangedDataStatistics#getExchangedData(java.lang.String, java.lang.String)
    */
   @Override
   public ExchangedData getExchangedData(@WebParam(name = "date") String date,
         @WebParam(name = "source") String source) {
      ExchangedData result = null;
      if (source != null && date != null) {
         Calendar cal;
         try {
            cal = getCalendarFromDate(date);
         } catch (ParseException pe) {
            throw new IllegalArgumentException("Date should be formated with the '"
                  + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
         }

         // Create query
         Query query = em.createNamedQuery("ExchangedData.getBySourceAndDate");
         query.setParameter("src", source);
         query.setParameter("date", cal.getTime());

         // retrieve data
         try {
            result = (ExchangedData) query.getSingleResult();
         } catch (NoResultException e) {
            logger.info("No ExchangedData found for user {} at {}", new Object[] {source, date});
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.ExchangedDataStatistics#getExchangedDataInIntervalBySource(java.lang.String, java.lang.String, int, int, org.openwis.management.entity.ExchangedDataColumn, org.openwis.management.entity.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public ExchangedDataResult getExchangedDataInIntervalForAllSources(
         @WebParam(name = "from") String from, @WebParam(name = "to") String to,
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxCount") int maxResults,
         @WebParam(name = "column") ExchangedDataColumn column,
         @WebParam(name = "dir") SortDirection sortDirection) {
      ExchangedDataResult result = null;
      List<ExchangedData> list = null;
      logger.debug("Get ExchangedData");

      try {
         Calendar calFrom = getCalendarFromDate(from);
         Calendar calTo = getCalendarFromDate(to);
         // Check
         if (calFrom.after(calTo)) {
            throw new IllegalArgumentException(MessageFormat.format(
                  "The FROM date ({0}) should be before the TO ({1})", from, to));
         }
         // Check arguments
         if (firstResult < 0) {
            throw new IllegalArgumentException("FirstResult must be ≥ 0!");
         }
         if (maxResults <= 0) {
            throw new IllegalArgumentException("MaxResults must be > 0!");
         }

         // Default column is URN
         ExchangedDataColumn col;
         if (column != null) {
            col = column;
         } else {
            col = ExchangedDataColumn.DATE;
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
                     "SELECT ed FROM ExchangedData ed WHERE ed.date BETWEEN :from AND :to ORDER BY ed.{0} {1}",
                     col.getAttribute(), dir);
         Query query = em.createQuery(q);
         query.setParameter("from", calFrom.getTime());
         query.setParameter("to", calTo.getTime());
         query.setFirstResult(firstResult);
         query.setMaxResults(maxResults);

         // Process query
         try {
            list = query.getResultList();
         } catch (NoResultException e) {
            list = Collections.emptyList();
         }

         // Count
         query = em.createNamedQuery("ExchangedData.countInIntervalForAllSources");
         query.setParameter("from", calFrom.getTime());
         query.setParameter("to", calTo.getTime());

         int count = ((Number) query.getSingleResult()).intValue();

         // Build result
         result = new ExchangedDataResult();
         result.setList(list);
         result.setCount(count);

      } catch (ParseException pe) {
         throw new IllegalArgumentException("Date should be formated with the '"
               + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
      }
      return result;
   }


   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.ExchangedDataStatistics#getExchangedDataInIntervalBySource(java.lang.String, java.lang.String, java.lang.String, int, int, org.openwis.management.entity.ExchangedDataColumn, org.openwis.management.entity.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public ExchangedDataResult getExchangedDataInIntervalBySources(
         @WebParam(name = "source") String source, @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxCount") int maxResults,
         @WebParam(name = "column") ExchangedDataColumn column,
         @WebParam(name = "dir") SortDirection sortDirection) {
      ExchangedDataResult result = null;
      List<ExchangedData> list = null;
      logger.debug("Get ExchangedData");

      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0!");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }

      // Default column is URN
      ExchangedDataColumn col;
      if (column != null) {
         col = column;
      } else {
         col = ExchangedDataColumn.DATE;
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
            "SELECT ed FROM ExchangedData ed WHERE ed.source LIKE :source ORDER BY ed.{0} {1}",
            col.getAttribute(), dir);
      Query query = em.createQuery(q);
      query.setParameter("source", source + "%");
      query.setFirstResult(firstResult);
      query.setMaxResults(maxResults);

      // Process query
      try {
         list = query.getResultList();
      } catch (NoResultException e) {
         list = Collections.emptyList();
      }

      // Count
      query = em.createNamedQuery("ExchangedData.countBySources");
      query.setParameter("source", source + "%");
      int count = ((Number) query.getSingleResult()).intValue();

      // Build result
      result = new ExchangedDataResult();
      result.setList(list);
      result.setCount(count);

      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.management.service.ExchangedDataStatistics#getTotalExchangedDataInInterval(java.lang.String, java.lang.String)
    */
   @Override
   public ExchangedData getTotalExchangedDataInInterval(@WebParam(name = "from") String from,
         @WebParam(name = "to") String to) {

      ExchangedData result = null;
      try {
         Calendar calFrom = getCalendarFromDate(from);
         Calendar calTo = getCalendarFromDate(to);
         // Check
         if (calFrom.after(calTo)) {
            throw new IllegalArgumentException(MessageFormat.format(
                  "The FROM date ({0}) should be before the TO ({1})", from, to));
         }

         result = new ExchangedData();

         Query query = em.createNamedQuery("ExchangedData.getBetweenDate");

         query.setParameter("from", calFrom.getTime());
         query.setParameter("to", calTo.getTime());

         // retrieve data
         try {
            Object[] res = (Object[]) query.getSingleResult();

            result.setNbMetadata((Long) res[0]);
            result.setTotalSize((Long) res[1]);

         } catch (NoResultException e) {
            logger.info("No ExchangedDate found between {} and {}", from, to);
         }

      } catch (ParseException pe) {
         throw new IllegalArgumentException("Date should be formated with the '"
               + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
      }
      return result;
   }

   /**
    * Retrieves the overall volume of data disseminated and extracted per day.
    *
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of statistical records.
    */
   @Override
   public List<ExchangedData> getExchangedDataStatistics(
         @WebParam(name = "maxItemsCount") final int maxItemsCount) {
      return getExchangedDataInIntervalBySources("%", 0, maxItemsCount, null, null).getList();
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
