/**
 *
 */
package org.openwis.management.service;

import java.util.Date;
import java.util.List;

import org.openwis.management.entity.AlarmEvent;

/**
 * Defines the alert interface of the management service allowing to report
 * persistent system events.
 * <p>
 * Alarms are logged in one or several log file(s) and can be visualised by an
 * authenticated and authorised user form the administrator portal.
 */
public interface AlertService {

   // -------------------------------------------------------------------------
   // Alarms - Get recent events
   // -------------------------------------------------------------------------

   /**
    * Retrieves the list of recent system alarms matching the given filter expression.
    *
    * @param filterExp the filter expression
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of recent alarms event matching the selection criteria.
    */
   List<AlarmEvent> getFilteredEvents(String filterExp, int index, int maxItemsCount);

   /**
    * Retrieves the list of recent system alarms matching the given filter expression.
    *
    * @param filterExp the filter expression
    * @param maxItemsCount specifies the maximum number of items to return
    * @param sortField specifies the sorting field
    * @param sortField specifies the sorting field
    * @param sortOrder specifies the sort order
    * @param sortOrder specifies the sort order
    * @return the list of recent alarms event matching the selection criteria.
    */
   List<AlarmEvent> getFilteredEventsSorted(String filterExp, String sortField, String sortOrder, int index, int maxItemsCount);

   /**
    *
    * @param filterExp the filter expression
    * @return the number of recent alarms event matching the selection criteria.
    */
   long getFilteredEventsCount(String filterExp);

   /**
    * Retrieves the list of recent system alarms.
    *
    * @param from specifies the start time
    * @param to specifies the end time
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of recent alarms event matching the selection criteria.
    */
   List<AlarmEvent> getRecentEvents(Date from, Date to, int index, int maxItemsCount);

   /**
    * Retrieves the list of recent system alarms.
    *
    * @param from specifies the start time
    * @param to specifies the end time
    * @param sortField specifies the sorting field
    * @param sortOrder specifies the sort order
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of recent alarms event matching the selection criteria.
    */
   List<AlarmEvent> getRecentEventsSorted(Date from, Date to, String sortField, String sortOrder, int index, int maxItemsCount);

   /**
    *
    * @param from specifies the start time
    * @param to specifies the end time
    * @return the number of recent alarms event matching the selection criteria.
    */
   long getRecentEventsCount(Date from, Date to);

   // -------------------------------------------------------------------------
   // Alarms - Raise events
   // -------------------------------------------------------------------------

   /**
    * Raise a event as an error.
    *
    * @param source identifies the object on which the event initially occurred
    * @param location identifies the component (process) at which the event initially occurred
    * @param description text describing the event
    */
   void raiseError(String source, String location, String description);

   /**
    * Raise a event as a warning.
    *
    * @param source identifies the object on which the event initially occurred
    * @param location identifies the component (process) at which the event initially occurred
    * @param description text describing the event
    */
   void raiseWarning(String source, String location, String description);

   /**
    * Raise a event as an information.
    *
    * @param source identifies the object on which the event initially occurred
    * @param location identifies the component (process) at which the event initially occurred
    * @param description text describing the event
    */
   void raiseInformation(String source, String location, String description);

   /**
    * Raise a event using the specified format string and arguments.
    *
    * @param source identifies the object on which the event initially occurred
    * @param location identifies the component (process) at which the event
    *           initially occurred
    * @param severity specifies the severity of the event; one of TRACE, INFO,
    *           WARNING, ERROR
    * @param eventId the identifier of the pre-defined event
    * @param eventArguments arguments referenced by the format specifiers
    */
   void raiseEvent(String source, String location,
                   String severity, String eventId, Object... eventArguments);
}
