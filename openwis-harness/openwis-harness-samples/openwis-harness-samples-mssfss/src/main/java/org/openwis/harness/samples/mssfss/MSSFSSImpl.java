package org.openwis.harness.samples.mssfss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.openwis.harness.mssfss.ChangeRouting;
import org.openwis.harness.mssfss.ChangeRoutingResponse;
import org.openwis.harness.mssfss.CreateRouting;
import org.openwis.harness.mssfss.CreateRoutingResponse;
import org.openwis.harness.mssfss.DeleteRouting;
import org.openwis.harness.mssfss.DeleteRoutingResponse;
import org.openwis.harness.mssfss.Frequency;
import org.openwis.harness.mssfss.GetRecentEventsForARouting;
import org.openwis.harness.mssfss.GetRecentEventsForARoutingResponse;
import org.openwis.harness.mssfss.ListRouting;
import org.openwis.harness.mssfss.ListRoutingResponse;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.harness.mssfss.Parameter;
import org.openwis.harness.mssfss.RecurrentFrequency;
import org.openwis.harness.mssfss.RecurrentScaleType;
import org.openwis.harness.mssfss.RequestStatus;
import org.openwis.harness.mssfss.Routing;
import org.openwis.harness.mssfss.RoutingEvent;
import org.openwis.harness.mssfss.RoutingSortColumn;
import org.openwis.harness.mssfss.RoutingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(targetNamespace = "http://mssfss.harness.openwis.org/", name = "MSSFSS", portName = "MSSFSSImplPort", serviceName = "MSSFSSImplService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class MSSFSSImpl implements MSSFSS {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(MSSFSSImpl.class);

   /** The Constant DATE_PATTERN. */
   private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   /** The Constant UTC. */
   private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

   /** The date format local. */
   private static DateFormat dateFormat;

   /**
    * Gets the date format.
    *
    * @return the date format
    */
   public synchronized DateFormat getDateFormat() {
      if (dateFormat == null) {
         dateFormat = new SimpleDateFormat(DATE_PATTERN);
         dateFormat.setTimeZone(UTC);
      }
      return dateFormat;
   }

   @Override
   public List<String> getChannelsForUser(String user) {
      if ("non-mssfss-user".equals(user)) {
         logger.info("Empty channels for non-mssfss-user");
         return Arrays.asList();
      }
      List<String> channels = Arrays.asList("channel1-" + user, "channel2-" + user, "channel3-"
            + user);
      logger.info("Channels for user " + user + ": " + channels);
      return channels;
   }

   private String getFrequencyStr(Frequency frequency) {
      String freqStr = "" + frequency;
      if (frequency instanceof RecurrentFrequency) {
         RecurrentFrequency recFreq = (RecurrentFrequency) frequency;
         freqStr += " / start=" + recFreq.getNextDate() + ", every="
               + recFreq.getReccurencePeriod() + " " + recFreq.getRecurrentScale();
      }
      return freqStr;
   }

   private String getSubselectionParamsStr(List<Parameter> params) {
      String paramStr = "[";
      for (Parameter parameter : params) {
         paramStr += parameter.getCode() + ": " + parameter.getValues();
      }
      paramStr += "]";
      return paramStr;
   }

   @Override
   public ChangeRoutingResponse changeRouting(ChangeRouting parameters) {
      logger.info("Changing routing - channel=" + parameters.getChannel() + ", requestId="
            + parameters.getIdRequest() + ", frequency="
            + getFrequencyStr(parameters.getFrequency()) + ", subselectionParams="
            + getSubselectionParamsStr(parameters.getSubSelectionParams()));

      ChangeRoutingResponse resp = new ChangeRoutingResponse();
      if (parameters.getIdRequest().equals("-1")) {
         resp.setResultStatus(RequestStatus.UNKNOWN_REQUEST);
         resp.setMessage("Unknown request");
      } else {
         resp.setResultStatus(RequestStatus.REQUEST_PENDING);
      }

      return resp;
   }

   @Override
   public ListRoutingResponse listRouting(ListRouting parameters) {
      logger.info("List routing - user=" + parameters.getUser() + ", page=" + parameters.getPage()
            + ", pageSize=" + parameters.getPageSize() + ", sortColumn="
            + parameters.getSortColumn().toString());

      ListRoutingResponse resp = new ListRoutingResponse();
      resp.setNumberOfResults(100);
      ArrayList<Routing> routings = new ArrayList<Routing>();
      for (int i = 0; i < 100; i++) {
         routings.add(createRouting(parameters.getUser(), i));
      }

      // paged results
      final RoutingSortColumn sortColumn = parameters.getSortColumn();
      final boolean reverse = parameters.isRevert();
      if (sortColumn != null) {
         Collections.sort(routings, new Comparator<Routing>() {
            @Override
            public int compare(Routing o1, Routing o2) {
               int result = 0;
               if (RoutingSortColumn.CHANNEL.equals(sortColumn)) {
                  result = o1.getChannel().compareTo(o2.getChannel());
               } else if (RoutingSortColumn.ID.equals(sortColumn)) {
                  result = o1.getId().compareTo(o2.getId());
               } else if (RoutingSortColumn.CREATION_DATE.equals(sortColumn)) {
                  result = o1.getCreationDate().compareTo(o2.getCreationDate());
               } else if (RoutingSortColumn.METADATA_URN.equals(sortColumn)) {
                  result = o1.getMdURN().compareTo(o2.getMdURN());
               } else if (RoutingSortColumn.STATE.equals(sortColumn)) {
                  result = o1.getState().compareTo(o2.getState());
               } else if (RoutingSortColumn.LAST_EVENT_DATE.equals(sortColumn)) {
                  result = o1.getLastEventDate().compareTo(o2.getLastEventDate());
               }
               if (reverse) {
                  result = -result;
               }
               return result;
            }
         });
      }

      int fromIndex = parameters.getPage() * parameters.getPageSize();
      int toIndex = fromIndex + parameters.getPageSize();
      resp.getRoutings().addAll(routings.subList(fromIndex, toIndex));

      return resp;
   }

   private Routing createRouting(String user, int i) {
      Calendar cal = Calendar.getInstance(UTC);
      cal.add(Calendar.MINUTE, -i);
      String now = getDateFormat().format(cal.getTime());

      Routing routing = new Routing();
      routing.setChannel("channel-" + String.valueOf(i % 10));
      routing.setId("routing-" + i);
      routing.setCreationDate(now);
      RecurrentFrequency rf = new RecurrentFrequency();
      rf.setNextDate(now);
      rf.setRecurrentScale(RecurrentScaleType.DAY);
      rf.setReccurencePeriod(1);
      routing.setFrequency(rf);
      routing.setLastEventDate(now);
      routing.setMdURN("myurn-" + i);
      if (i % 5 == 0) {
         routing.setState(RoutingState.PENDING);
      } else {
         routing.setState(RoutingState.ACTIVE);
      }
      routing.setUser(user);
      Parameter param = new Parameter();
      param.setCode("parameter.time.interval");
      param.getValues().add("08:00Z/09:00Z");
      routing.getSubSelectionParams().add(param);
      return routing;
   }

   @Override
   public GetRecentEventsForARoutingResponse getRecentEventsForARouting(
         GetRecentEventsForARouting parameters) {
      logger.info("Get events for routing - idRequest=" + parameters.getIdRequest() + ", page="
            + parameters.getPage() + ", pageSize=" + parameters.getPageSize() + ", sortColumn="
            + parameters.getSortColumn());
      Calendar cal = Calendar.getInstance(UTC);

      GetRecentEventsForARoutingResponse resp = new GetRecentEventsForARoutingResponse();
      resp.setNumberOfResults(100);
      ArrayList<RoutingEvent> events = new ArrayList<RoutingEvent>();
      for (int i = 0; i < 100; i++) {
         RoutingEvent event = new RoutingEvent();
         cal.add(Calendar.MINUTE, -1);
         String now = getDateFormat().format(cal.getTime());
         event.setCreationDate(now);
         if (i % 5 == 0) {
            event.setMessage("Failed");
         } else {
            event.setMessage("Delivered");
         }
         events.add(event);
      }

      resp.setRouting(createRouting("myuser", 0));

      // paged results
      final String sortColumn = parameters.getSortColumn();
      final boolean reverse = parameters.isRevert();
      if (sortColumn != null) {
         Collections.sort(events, new Comparator<RoutingEvent>() {
            @Override
            public int compare(RoutingEvent o1, RoutingEvent o2) {
               int result = 0;
               if ("message".equalsIgnoreCase(sortColumn)) {
                  result = o1.getMessage().compareTo(o2.getMessage());
               } else if ("creationdate".equalsIgnoreCase(sortColumn)) {
                  result = o1.getCreationDate().compareTo(o2.getCreationDate());
               }
               if (reverse) {
                  result = -result;
               }
               return result;
            }
         });
      }

      int fromIndex = parameters.getPage() * parameters.getPageSize();
      int toIndex = fromIndex + parameters.getPageSize();
      resp.getRoutingEvents().addAll(events.subList(fromIndex, toIndex));

      return resp;
   }

   @Override
   public DeleteRoutingResponse deleteRouting(DeleteRouting parameters) {
      logger.info("Delete routing - requestId=" + parameters.getIdRequest());

      DeleteRoutingResponse resp = new DeleteRoutingResponse();
      if (parameters.getIdRequest().equals("-1")) {
         resp.setResultStatus(RequestStatus.UNKNOWN_REQUEST);
         resp.setMessage("Unknown request");
      } else {
         resp.setResultStatus(RequestStatus.REQUEST_PENDING);
      }

      return resp;
   }

   @Override
   public CreateRoutingResponse createRouting(CreateRouting parameters) {
      logger.info("Create routing - channel=" + parameters.getChannel() + ", requestId="
            + getFrequencyStr(parameters.getFrequency()) + ", subselectionParams="
            + getSubselectionParamsStr(parameters.getSubSelectionParams()));

      CreateRoutingResponse resp = new CreateRoutingResponse();
      if (parameters.getChannel().equals("error-channel")) {
         resp.setStatus(RequestStatus.REQUEST_REJECTED);
         resp.setMessage("Wrong channel");
      } else {
         resp.setStatus(RequestStatus.REQUEST_PENDING);
         resp.setIdRequest("00001");
      }

      return resp;
   }

}