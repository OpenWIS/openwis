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

import org.openwis.management.entity.IngestedData;
import org.openwis.management.entity.IngestedDataColumn;
import org.openwis.management.entity.SortDirection;
import org.openwis.management.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the management interface to monitor the volume of ingested data.
 */
@WebService(name = "IngestedDataStatistics", portName = "IngestedDataStatisticsPort", serviceName = "IngestedDataStatistics", targetNamespace = "http://monitoring.management.openwis.org/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(IngestedDataStatistics.class)
@Stateless(name = "IngestedDataStatistics")
public class IngestedDataStatisticsImpl implements IngestedDataStatistics {

   /** The logger */
   private static final Logger LOG = LoggerFactory.getLogger(IngestedDataStatisticsImpl.class);


   /**
    * The entity manager.
    */
   @PersistenceContext
   private EntityManager entityManager;

   /**
    * Gets the entityManager.
    * @return the entityManager.
    */
   private final EntityManager getEntityManager() {
      return entityManager;
   }

   // -------------------------------------------------------------------------
   // Update Statistics
   // -------------------------------------------------------------------------

   /**
    * Add a volume of ingested data for a given date.
    *
    * @param date the date.
    * @param size the data size.
    */
   @Override
   public void updateIngestedData(@WebParam(name = "date") final String date,
         @WebParam(name = "size") final long size) {

      // check arguments
      EntityManager em = getEntityManager();

      if (checkArguments(em, date, size)) {
         try {
            // check if entity is already existing
            IngestedData data = getIngestedData(date);

            if (data == null) {
               // Create new entity
               Calendar cal = getCalendarFromDate(date);

               data = new IngestedData();
               data.setDate(cal.getTime());
               data.setSize(size);

               em.persist(data);
               // flush to make fails asap for multi-thread safety (mdb may be retried)
               // unique constraint checked on db
               em.flush();
            } else {
               Query updateQuery = em.createNativeQuery("update openwis_ingested_data "
                     + "set size = size + ?1 where ingested_data_id = ?2");
               updateQuery.setParameter(1, size);
               updateQuery.setParameter(2, data.getId());
               updateQuery.executeUpdate();
            }

            // trace...
            if (LOG.isDebugEnabled()) {
               LOG.debug(String.format("Updating ingested data statistics: %s, volume = %s", date,
                     size));
            }
         } catch (ParseException pe) {
             throw new IllegalArgumentException("Date should be formated with the '"
                     + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
         } catch (Exception e) {
            // report exception
            throw new RuntimeException(String.format(
                  "Failed to update ingested data statistics: %s, volume = %s",
                  date, size), e);
         }
      } else {
         LOG.error("updateIngestedData: invalid arguments provided");
      }
   }
   

   // -------------------------------------------------------------------------
   // Query Report
   // -------------------------------------------------------------------------

   /**
    * Retrieve the overall volume of data ingested per day.
    *
    * @param date the date.
    * @return the ingested data.
    */
   @Override
   public IngestedData getIngestedData(@WebParam(name = "date") final String date) {

      IngestedData result = null;

      EntityManager em = getEntityManager();

      if (checkArguments(em, date)) {
         Calendar cal;
         try {
             cal = getCalendarFromDate(date);
         } catch (ParseException pe) {
             throw new IllegalArgumentException("Date should be formated with the '"
                   + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
         }

         // Create query
         String q = "SELECT ingested_data FROM IngestedData ingested_data WHERE ingested_data.date = :date";
         Query query = em.createQuery(q);
         query.setParameter("date", cal.getTime());          

         // Retrieve data
         try {
            result = (IngestedData) query.getSingleResult();
         } catch (NoResultException e) {
            LOG.info("No ingested data found at {}", new Object[] {cal.getTime()});
         }
      } else {
         LOG.error("getIngestedData: invalid arguments provided");
      }

      return result;
   }

   /**
    * Retrieve the overall volume of data ingested applying a filter
    * from a given date to a given date.
    *
    * @param from the from date.
    * @param to the to date.
    * @return the ingested data.
    */
   @SuppressWarnings("unchecked")
   @Override
   public  IngestedData getIngestedDataInInterval(@WebParam(name = "from") final String from,
         @WebParam(name = "to") final String to) {

      IngestedData result = null;

      EntityManager em = getEntityManager();

      if (checkArguments(em, from, to)) {
         Calendar calFrom;
         Calendar calTo;
         try {
        	 calFrom = getCalendarFromDate(from);
        	 calTo = getCalendarFromDate(to);
         } catch (ParseException pe) {
        	 throw new IllegalArgumentException("Date should be formated with the '"
                   + DateTimeUtils.DATE_TIME_PATTERN + "' pattern", pe);
         }
         // Check
         if (calFrom.after(calTo)) {
            throw new IllegalArgumentException(MessageFormat.format(
                  "The FROM date ({0}) should be before the TO ({1})", from, to));
         }

         result = new IngestedData();

         // Create query
         String q = "SELECT ingested_data FROM IngestedData ingested_data WHERE ingested_data.date BETWEEN :from AND :to";

         Query query = em.createQuery(q);
         query.setParameter("from", calFrom.getTime());
         query.setParameter("to", calTo.getTime());

         // Retrieve data
         long size = 0L;
         for (IngestedData data : (List<IngestedData>) query.getResultList()) {
            size += data.getSize();
         }

         result.setSize(size);
      } else {
         LOG.error("getIngestedDataInInterval: invalid arguments provided");
      }

      return result;
   }

   /**
    * Retrieves the overall volume of data ingested per day.
    *
    * @param firstResult the starting index
    * @param maxItemsCount specifies the maximum number of items to return
    * @param column the column to sort
    * @param dir the sort direction
    * @return the list of statistical records.
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<IngestedData> getIngestedDataStatistics(
		 @WebParam(name = "firstResult") final int firstResult,
         @WebParam(name = "maxItemsCount") final int maxItemsCount,
         @WebParam(name = "column") IngestedDataColumn column,
         @WebParam(name = "dir") SortDirection sortDirection) {
	   
      List<IngestedData> result = null;

      EntityManager em = getEntityManager();

      if (checkArguments(em, maxItemsCount)) {

    	  // Check arguments
          if (firstResult < 0) {
             throw new IllegalArgumentException("FirstResult must be >= 0!");
          }
          if (maxItemsCount <= 0) {
             throw new IllegalArgumentException("MaxItemsCount must be > 0!");
          }

          // Default column is DATE
          IngestedDataColumn col;
          if (column != null) {
             col = column;
          } else {
             col = IngestedDataColumn.DATE;
          }

          // Default direction is Descending
          SortDirection dir;
          if (sortDirection != null) {
             dir = sortDirection;
          } else {
             dir = SortDirection.DESC;
          }
    	  
          // Create query
          String q = MessageFormat
          	.format("SELECT ingested_data FROM IngestedData ingested_data ORDER BY ingested_data.{0} {1}",
          			col.getAttribute(), dir);

    	  Query query = em.createQuery(q);
          query.setFirstResult(firstResult);
          query.setMaxResults(maxItemsCount);
    	  
          // Process query
          try {
             result = query.getResultList();
          } catch (NoResultException e) {
             result = Collections.emptyList();
          }
      } else {
         LOG.error("getIngestedDataStatistics: invalid arguments provided");
      }

      return result;
   }

   // -------------------------------------------------------------------------
   // Utilities
   // -------------------------------------------------------------------------

   /**
    * Checks if a list of arguments is not null and not empty. Each argument
    * in the list is checked if it is not null and, in case of a Number,
    * if it is >= 0.
    * @param args the list of arguments
    * @return true if arguments pass the check, false otherwise
    */
   private static final boolean checkArguments(final Object... args) {
      // no args
      if (args == null || args.length == 0) {
         return false;
      }
      // assume every thing is ok
      boolean valid = true;

      for (Object argIt : args) {
         // not null
         if (argIt == null) {
            valid = false;
            break;
         }

         if (argIt instanceof Number) {
            Number number = (Number) argIt;
            if (number.longValue() < 0) {
               valid = false;
               break;
            }
         }
      }
      return valid;
   }

   /**
    * Gets a Calendar from a Date, resetting the calendar time to 0
    *
    * @param date the date
    * @return the calendar from date with the calendar time set to 0
    * @throws ParseException the parse exception
    */
   private Calendar getCalendarFromDate(String date) throws ParseException {
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.setTime(DateTimeUtils.parse(date));
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return cal;
   }
}
