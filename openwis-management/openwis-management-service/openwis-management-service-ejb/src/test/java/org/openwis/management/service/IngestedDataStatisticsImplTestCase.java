package org.openwis.management.service;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.management.entity.IngestedData;
import org.openwis.management.utils.DateTimeUtils;

/**
 * The Class IngestedDataStatisticsImplTestCase.
 */
@RunWith(Arquillian.class)
@Ignore
public class IngestedDataStatisticsImplTestCase extends ManagementServiceTest {

   /** The statistics. */
   @EJB
   private IngestedDataStatistics statisticsService;

   /**
    * Initialize the test.
    */
   @Before
   public void init() {
      // Initialize
   }

   /**
    * Test update ingested data statistics.
    */
   @Test
   public void testUpdateIngestedData() {
      long size;
      // Insert
      size = 10;
      IngestedData data;

      // vol: 10
      statisticsService.updateIngestedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()), size);
      data = statisticsService.getIngestedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()));
      
      Assert.assertNotNull(data);
      Assert.assertEquals(10, data.getSize().intValue());

      // vol: 20
      statisticsService.updateIngestedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()), size);
      data = statisticsService.getIngestedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()));

      Assert.assertNotNull(data);
      Assert.assertEquals(20, data.getSize().intValue());
   }

   /**
    * Test get ingested data.
    */
   @Test
   public void testGetIngestedData() {
	  Calendar cal = DateTimeUtils.getUTCCalendar();
	  cal.add(Calendar.MONTH, -1);
	  Calendar current = DateTimeUtils.getUTCCalendar();
	  current.setTimeInMillis(cal.getTimeInMillis());
 
	  IngestedData data;

	  data = statisticsService.getIngestedData(DateTimeUtils.format(current));
      Assert.assertNull(data);

      statisticsService.updateIngestedData(DateTimeUtils.format(current), 10);
      data = statisticsService.getIngestedData(DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(10, data.getSize().intValue());
   }

   /**
    * Test get ingested data in interval.
    */
   @Test
   public void testGetIngestedDataInInterval() {
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -1);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
    	  statisticsService.updateIngestedData(DateTimeUtils.format(current), 10);
    	  current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      IngestedData data = statisticsService.getIngestedDataInInterval(
            DateTimeUtils.format(cal), DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(days * 10, data.getSize().intValue());
   }

   /**
    * Test get ingested data statistics.
    */
   @Test
   public void testGetIngestedDataStatistics() {
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -1);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
    	  statisticsService.updateIngestedData(DateTimeUtils.format(current), 10);
    	  current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      List<IngestedData> list = statisticsService.getIngestedDataStatistics(0,5,null,null);
      Assert.assertNotNull(list);
      Assert.assertEquals(days, list.size());
   }
}
