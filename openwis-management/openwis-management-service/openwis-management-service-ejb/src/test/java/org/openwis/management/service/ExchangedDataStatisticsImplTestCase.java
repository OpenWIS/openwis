package org.openwis.management.service;

import java.util.Calendar;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.management.entity.ExchangedData;
import org.openwis.management.service.bean.ExchangedDataResult;
import org.openwis.management.utils.DateTimeUtils;

/**
 * The Class DisseminatedDataStatisticsImplTestCase.
 */
@RunWith(Arquillian.class)
@Ignore
public class ExchangedDataStatisticsImplTestCase extends ManagementServiceTest {

   /** The statistics. */
   @EJB
   private ExchangedDataStatistics statisticsService;

   /**
    * Initialize the test.
    */
   @Before
   public void init() {
      // Initialize
   }

   /**
    * Test update user dissemination statistics.
    */
   @Test
   public void testUpdateExtractedData() {
      String source = "Source";
      long size;
      int nb;
      // Insert
      size = 10;
      nb = 1;
      ExchangedData data;

      // nb: 1, vol: 10
      statisticsService.updateExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source, nb, size);
      data = statisticsService.getExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source);
      Assert.assertNotNull(data);
      Assert.assertEquals(1, data.getNbMetadata().intValue());
      Assert.assertEquals(10, data.getTotalSize().intValue());

      // nb: 2, vol: 20
      statisticsService.updateExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source, nb, size);
      data = statisticsService.getExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source);
      Assert.assertNotNull(data);
      Assert.assertEquals(2, data.getNbMetadata().intValue());
      Assert.assertEquals(20, data.getTotalSize().intValue());
   }

   /**
    * Test get exchanged data.
    */
   @Test
   public void testGetExchangedData() {
      String source = "Source-2";
      ExchangedData data;

      data = statisticsService.getExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source);
      Assert.assertNull(data);

      statisticsService.updateExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source, 1, 10);
      data = statisticsService.getExchangedData(DateTimeUtils.format(DateTimeUtils.getUTCDate()),
            source);
      Assert.assertNotNull(data);
      Assert.assertEquals(1, data.getNbMetadata().intValue());
      Assert.assertEquals(10, data.getTotalSize().intValue());
   }

   /**
    * Test get exchanged data in interval by source.
    */
   @Test
   public void testGetExchangedDataInIntervalBySource() {
      String[] sources = {"src-6", "src-7", "Plop"};
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -2);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
         for (String src : sources) {
            statisticsService.updateExchangedData(DateTimeUtils.format(current), src, 1, 10);
         }
         current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      ExchangedDataResult result = statisticsService.getExchangedDataInIntervalBySources("src-", 0,
            20, null, null);
      Assert.assertNotNull(result);
      Assert.assertEquals(days * (sources.length - 1), result.getCount());
      Assert.assertEquals(days * (sources.length - 1), result.getList().size());
   }

   /**
    * Test get exchanged data in interval for all sources.
    */
   @Test
   public void testGetExchangedDataInIntervalForAllSources() {

      String[] sources = {"Source-3", "Source-4"};
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -1);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
         for (String src : sources) {
            statisticsService.updateExchangedData(DateTimeUtils.format(current), src, 1, 10);
         }
         current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      String from = DateTimeUtils.format(cal);
      String to = DateTimeUtils.format(current);

      ExchangedDataResult result = statisticsService.getExchangedDataInIntervalForAllSources(from,
            to, 0, 20, null, null);
      Assert.assertNotNull(result);
      Assert.assertEquals(days * sources.length, result.getCount());
      Assert.assertEquals(days * sources.length, result.getList().size());
   }

   /**
    * Test get total exchanged data in interval.
    */
   @Test
   public void testGetTotalExchangedDataInInterval() {
      String source = "Source-5";
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -3);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
         statisticsService.updateExchangedData(DateTimeUtils.format(current), source, 1, 10);
         current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      ExchangedData data = statisticsService.getTotalExchangedDataInInterval(
            DateTimeUtils.format(cal), DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(1 * days, data.getNbMetadata().intValue());
      Assert.assertEquals(10 * days, data.getTotalSize().intValue());
   }
}
