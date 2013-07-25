package org.openwis.metadataportal.services.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openwis.management.control.IngestionFilter;
import org.openwis.management.control.FeedingFilter;
import org.openwis.management.control.ReplicationFilter;

public class MockConfigurationFilters {

   public static List<IngestionFilter> getIngestionFilters() {
      List<IngestionFilter> filters = new ArrayList<IngestionFilter>();
      
      for (int i = 0; i < 3; i++) {
         IngestionFilter filter = new IngestionFilter();
         filter.setRegex("^urn:x-wmo:md:int\\.wmo\\.wis::[A-Z]{" + i + "}\\d\\dLFPW$");
         filter.setDescription("Test filter " + (i + 1));
         filters.add(filter);
      }      
      return filters;
   }

   public static List<FeedingFilter> getFeedingFilters() {
      List<FeedingFilter> filters = new ArrayList<FeedingFilter>();
      
      for (int i = 0; i < 1; i++) {
         FeedingFilter filter = new FeedingFilter();
         filter.setRegex("^urn:x-wmo:md:int\\.wmo\\.wis::{" + i + "}\\.*");
         filter.setDescription("Test filter " + (i + 1));
         filters.add(filter);
      }      
      return filters;
   }

   public static List<ReplicationFilter> getReplicationFilters() {
      List<ReplicationFilter> filters = new ArrayList<ReplicationFilter>();
      
      GregorianCalendar calendar = new GregorianCalendar();
      
      for (int i = 0; i < 2; i++) {
         ReplicationFilter filter = new ReplicationFilter();
         filter.setActive(false);
         filter.setType("OpenWIS");
         filter.setUptime(getXmlCalendar(calendar));
         filter.setSource(i == 0 ? "gisc.kma.go.kr" : "gisc.metaoffice.gov.uk");
         filter.setRegex("^urn:x-wmo:md:int\\.wmo\\.wis::[A-Z]{" + i + "}\\d\\dLFPW$");
         filter.setDescription("Test filter " + (i + 1));
         filters.add(filter);
      }
      return filters;
   }
   
   private static XMLGregorianCalendar getXmlCalendar(final GregorianCalendar calendar) {
      XMLGregorianCalendar xmlcal = null;
      
      try {
         calendar.add(Calendar.DAY_OF_YEAR, 1);
         xmlcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
      }
      catch (DatatypeConfigurationException e) {
         
      }
      return xmlcal;
   }
   
}
