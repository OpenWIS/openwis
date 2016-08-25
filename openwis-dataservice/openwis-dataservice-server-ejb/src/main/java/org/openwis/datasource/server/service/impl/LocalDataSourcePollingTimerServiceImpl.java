package org.openwis.datasource.server.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.common.service.LocalDataSourceExtractService;
import org.openwis.dataservice.common.timer.LocalDataSourcePollingTimerService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.openwis.management.config.PropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SubscriptionTimerServiceImpl. <P>
 * Explanation goes here. <P>
 */
@Remote(LocalDataSourcePollingTimerService.class)
@Stateless(name = "LocalDataSourcePollingTimerService")
public class LocalDataSourcePollingTimerServiceImpl extends ProductArrivalHandler implements
      LocalDataSourcePollingTimerService {

   /** The logger. */
   private static Logger logger = LoggerFactory
         .getLogger(LocalDataSourcePollingTimerServiceImpl.class);

   /** The timer name. */
   private static final String NAME = "LOCALDATASOURCE_POLLING_TIMER";

   /** The timer service. */
   @Resource
   private TimerService timerService;

   /** The local data source extract service. */
   @EJB
   private LocalDataSourceExtractService localDataSourceExtractSrv;

   /** The local data source polling. */
   private List<String> localDataSourcePolling;

   /** The interval. */
   private long interval;

   /**
    * Instantiates a new local data source polling timer service impl.
    */
   public LocalDataSourcePollingTimerServiceImpl() {
      super();
   }

   /**
    * Destroy.
    *
    *
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.DisseminationTimerService#destroy()
    */
   @Override
   public void destroy() {
      @SuppressWarnings("unchecked")
      Collection<Timer> timersCollection = timerService.getTimers();
      // Generics way
      for (Timer timer : timersCollection) {
         if (timer.getInfo().equals(NAME)) {
            timer.cancel();
            logger.info("Timer: {} has been removed.", NAME);
         }
      }
   }

   /**
    * Start.
    *
    * @param interval the interval
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.DisseminationTimerService#start(long)
    */
   @Override
   public void start(long interval) {
      destroy();

      // Configure timer
      this.interval = interval;
      timerService.createTimer(interval, interval, NAME);
      logger.info("Timer created with period = {}", Long.valueOf(interval));
   }

   /**
    * On timeout.
    *
    * @param timer the timer
    */
   @Timeout
   @Override
   public void onTimeout(Timer timer) {
      // Update for next
      Calendar date = DateTimeUtils.getUTCCalendar();
      date.add(Calendar.MILLISECOND, (int) (0 - interval));
      logger.info("LocalDataSource Polling since: {}", date.getTime());
      processPolling(date.getTime());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.LocalDataSourcePollingTimerService#processPolling(java.util.Date)
    */
   @Override
   public void processPolling(Date date) {
      for (String ldsName : getLocalDataSourcePolling()) {
         this.processPolling(ldsName, date);
      }
   }

   /**
    * Gets the local data source polling.
    *
    * @return the local data source polling
    */
   public synchronized List<String> getLocalDataSourcePolling() {
      if (localDataSourcePolling == null) {
         localDataSourcePolling = new ArrayList<String>();
         // Retrieve LocalDataSource for polling
         String ldsPolling;
//         InitialContext ctx;
//         try {
//            ctx = new InitialContext();
//            Properties properties = (Properties) ctx
//                  .lookup(DataServiceConfiguration.LOCA_DATA_SOURCE_CONFIGURATION_LOCATION);
            Properties properties = new PropertySource(PropertySource.LOCAL_DATA_SOURCE).getProperties();
            for (String ldsName : localDataSourceExtractSrv.getAllLocalDataSourceRef()) {
               ldsPolling = properties.getProperty(ldsName
                     + DataServiceConfiguration.LOCAL_DATA_SOURCE_POLLING_ENDS);
               if (Boolean.valueOf(ldsPolling)) {
                  localDataSourcePolling.add(ldsName);
               }
            }
//         } catch (NamingException e1) {
//            logger.error("Can not retrieve the JNDI context.", e1);
//         }

      }
      return localDataSourcePolling;
   }

   /**
    * Process polling.
    *
    * @param ldsName the LocalDataSource name
    * @param date the date
    */
   private void processPolling(String ldsName, Date date) {
      String timestamp = DateTimeUtils.formatUTC(date);
      List<String> products = localDataSourceExtractSrv.getAvailability(ldsName, timestamp);
      String productDate;
      for (String urn : products) {
         productDate = DateTimeUtils.formatUTC(date);
         sendProductArrival(productDate, urn);
      }
   }
}
