package org.openwis.metadataportal.services.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openwis.management.alert.AlarmEvent;
import org.openwis.management.monitoring.ExchangedData;
import org.openwis.management.monitoring.UserDisseminationData;
import org.openwis.metadataportal.services.requestsStatistics.dto.DataExtractedDTO;

/**
 * Test data for statistic services . <P>
 * Explanation goes here. <P>
 *
 */
public class MockStatistics {

   private static final String[] severities = new String[] {"Debug", "Info", "Notice", "Warning",
         "Error", "Alarm", "Fatal", "Warning", "Warning", "Error"};

   public static List<AlarmEvent> getRecentEvents() {
      List<AlarmEvent> events = new ArrayList<AlarmEvent>();

      // current time
      GregorianCalendar calendar = new GregorianCalendar();

      for (long i = 0; i < severities.length; i++) {
         AlarmEvent event = new AlarmEvent();

         event.setId(i);
         event.setDate(getXmlCalendar(calendar));
         event.setModule("Component " + i);
         event.setSource("Process " + i);
         event.setSeverity(severities[(int) i]);
         event.setMessage("This is a resent " + event.getSeverity());

         events.add(event);
      }

      return events;
   }

   private static XMLGregorianCalendar getXmlCalendar(final GregorianCalendar calendar) {
      XMLGregorianCalendar xmlcal = null;

      try {
         calendar.add(Calendar.DAY_OF_YEAR, 1);
         xmlcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
      } catch (DatatypeConfigurationException e) {

      }
      return xmlcal;
   }

   public static List<UserDisseminationData> getDisseminatedStatistics() {
      List<UserDisseminationData> statistics = new ArrayList<UserDisseminationData>();

      // current time
      GregorianCalendar calendar = new GregorianCalendar();

      for (int i = 0; i < 4; i++) {
         UserDisseminationData data = new UserDisseminationData();
         data.setDate(getXmlCalendar(calendar));
         data.setSize((long) (i + 1) * 10 * 1024);
         data.setUserId("User" + (i + 1));
         statistics.add(data);
      }
      return statistics;
   }

   public static List<DataExtractedDTO> getDisseminatedExtractedStatistics() {
      List<DataExtractedDTO> statistics = new ArrayList<DataExtractedDTO>();
      // current time
      GregorianCalendar calendar = new GregorianCalendar();

      for (int i = 0; i < 4; i++) {
         DataExtractedDTO data = new DataExtractedDTO();
         data.setDate(calendar.getTime());
         data.setSize((long) (i + 1) * 1000 * 1024);
         data.setDissToolSize((long) (i + 1) * 1000 * 102 - i * 10);
         statistics.add(data);
      }

      return statistics;
   }
}
