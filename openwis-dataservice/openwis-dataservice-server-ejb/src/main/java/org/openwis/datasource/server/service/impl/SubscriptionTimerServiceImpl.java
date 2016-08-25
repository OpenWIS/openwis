package org.openwis.datasource.server.service.impl;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.timer.SubscriptionTimerService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.AlertService;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SubscriptionTimerServiceImpl. <P>
 * Explanation goes here. <P>
 */
@Remote(SubscriptionTimerService.class)
@Stateless(name = "SubscriptionTimerService")
public class SubscriptionTimerServiceImpl implements SubscriptionTimerService {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionTimerServiceImpl.class);

   /** The timer service. */
   @Resource
   private TimerService timerService;

   /** The subscription service. */
   @EJB
   private SubscriptionService subscriptionService;

   /** The entity manager. */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The ctrl service. */
   private ControlService ctrlService;

   /** The timer name. */
   public static final String NAME = "SUBSCRIPTION_RECURRENT_TIMER";

   /**
    * Destroy.
    *
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.DisseminationTimerService#destroy()
    */
   @SuppressWarnings("unchecked")
   @Override
   public void destroy() {
      Collection<Timer> timersCollection = timerService.getTimers();

      // Generics way
      // Cancel existing timers
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
      Calendar cal = Calendar.getInstance();
      timerService.createTimer(1000 * (60 - cal.get(Calendar.SECOND)), interval, NAME);
      logger.info("Timer: {} created with period = {}", NAME, Long.valueOf(interval));
   }

   /**
    * On timeout.
    *
    * @param timer the timer
    */
   @Timeout
   @Override
   public void onTimeout(Timer timer) {
      logger.debug("On timeout: {}", timer.getInfo());
      Calendar cal = DateTimeUtils.getUTCCalendar();
      ControlService controlService = getControlService();
      if (controlService != null
            && controlService.isServiceEnabled(ManagedServiceIdentifier.SUBSCRIPTION_SERVICE)) {
         processRecurrentSubscription(cal.getTime());
      } else {
         logger.info("Subscription service disable => does not process recurrent subscription");
      }
   }

   /**
    * Gets the control service.
    *
    * @return the control service
    */
   private ControlService getControlService() {
      if (ctrlService == null) {
         try {
            ctrlService = ManagementServiceBeans.getInstance().getControlService();
         } catch (NamingException e) {
            ctrlService = null;
         }
      }
      return ctrlService;
   }

   /**
    * Process all activated recurrent subscriptions against the reference date.
    *
    * @param date the date
    */
   @Override
   @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
   public void processRecurrentSubscription(Date date) {
      logger.debug("Process subscription at {}", date);
      try {
         // retrieve subscription
         Query query;
         query = entityManager.createNamedQuery("Subscription.FindRecurrentToProcess")
               .setParameter("date", date);

         @SuppressWarnings("unchecked")
         List<Subscription> subscriptions = query.getResultList();
         if (subscriptions != null && subscriptions.size() > 0) {
            logger.info("{} subscriptions to processed at {}", subscriptions.size(), date);
            subscriptionService.processRecurrentSubscriptions(subscriptions, date);
         } else {
            logger.debug("No subscription processed at {}", date);
         }
         entityManager.flush();
      } catch (Throwable t) {
         raiseAlarm(t);
      }
   }

   private void raiseAlarm(Throwable t) {
      String msg = MessageFormat.format("Cannot process recurrent subscription. Error: {0}", t);
      logger.error(msg, t);
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      alertService.raiseError("Data Service", "Subscription Timer", msg);
   }
}
