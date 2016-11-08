/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.subscription.RecurrentFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.timer.SubscriptionTimerService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SubscriptionServiceImplTestCase. <P>
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
@Ignore
public class SubscriptionTimerServiceImplIntegrationTestCase extends ArquillianDBTestCase {

   /** The logger. */
   private static Logger logger = LoggerFactory
         .getLogger(SubscriptionTimerServiceImplIntegrationTestCase.class);

   /** The Constant METADATA_ID. */
   private static final String METADATA_ID = "FVXX01EGRR";

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "urn:x-wmo:md:int.wmo.wis::" + METADATA_ID;

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** now. */
   private final Calendar now = DateTimeUtils.getUTCCalendar();

   /** The metada server. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The subscription server. */
   @EJB
   private SubscriptionService subscriptionSrv;

   /** The subscription timer service. */
   @EJB
   private SubscriptionTimerService subscriptionTimerSrv;

   /** The processed request srv. */
   @EJB
   private ProcessedRequestService processedRequestSrv;

   /** The CacheIndex service. */
   @EJB
   private CacheIndex cacheIndexService;

   /**
    * {@inheritDoc}
    * @see org.openwis.datasource.server.ArquillianDBTestCase#getCachedIndex()
    */
   @Override
   protected CacheIndex getCachedIndex() {
      return cacheIndexService;
   }

   /**
    * Initialize the test.
    */
   @Before
   public void init() {
      //FIXME: This insert must be remove as soon as DBUnit integration is performed
      ProductMetadata productMetadata = metadaSrv.getProductMetadataByUrn(URN_TEST);
      if (productMetadata == null) {
         productMetadata = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);
      }
      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * Test subscription timer with on arrival.
    */
   @Test
   public void testSubscriptionTimerWithOnArrival() {
      logger.trace("testSubscriptionTimerWithOnArrival");
      Subscription subscription;
      Long id;

      // Build data
      Calendar cal = Calendar.getInstance();
      cal.setTime(now.getTime());
      cal.add(Calendar.DAY_OF_MONTH, -1);
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL,
            value);
      subscription.setStartingDate(cal.getTime());
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);

      // launch timer
      cal.add(Calendar.HOUR, 1);
      subscriptionTimerSrv.processRecurrentSubscription(cal.getTime());

      // Check
      subscription = subscriptionSrv.getFullSubscription(id);
      Collection<LightProcessedRequestDTO> requests = processedRequestSrv
            .getAllProcessedRequestsByRequest(id, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());
   }

   /**
    * Test subscription timer with on arrival.
    */
   @Test
   public void testSubscriptionTimerRecurrent() {
      logger.trace("testSubscriptionTimerRecurrent");
      Subscription subscription;
      RecurrentFrequency rFreq;
      Long subId;
      Date nextDate;

      Calendar cal = Calendar.getInstance();
      cal.setTime(now.getTime());
      cal.add(Calendar.DAY_OF_MONTH, -1);
      // Build data
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      rFreq = (RecurrentFrequency) subscription.getFrequency();
      rFreq.setReccurencePeriod(1);
      rFreq.setReccurentScale(RecurrentScale.HOUR);
      subscription.setStartingDate(cal.getTime());
      subId = subscriptionSrv.createSubscription(subscription, URN_TEST);

      // Check next date
      subscription = subscriptionSrv.getSubscription(subId);
      rFreq = (RecurrentFrequency) subscription.getFrequency();
      nextDate = rFreq.getNextDate();
      Assert.assertNotNull(nextDate);

      // launch timer
      cal.add(Calendar.MINUTE, -30);
      subscriptionTimerSrv.processRecurrentSubscription(cal.getTime());

      // Check
      subscription = subscriptionSrv.getFullSubscription(subId);
      Collection<LightProcessedRequestDTO> requests = processedRequestSrv
            .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());
      rFreq = (RecurrentFrequency) subscription.getFrequency();
      Assert.assertEquals(nextDate, rFreq.getNextDate());

      // launch timer
      cal.add(Calendar.HOUR, 1);
      subscriptionTimerSrv.processRecurrentSubscription(cal.getTime());

      // Check
      subscription = subscriptionSrv.getFullSubscription(subId);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      rFreq = (RecurrentFrequency) subscription.getFrequency();
      Assert.assertTrue(nextDate.before(rFreq.getNextDate()));
      nextDate = rFreq.getNextDate();

      // re launch timer
      cal.add(Calendar.MINUTE, 10);
      subscriptionTimerSrv.processRecurrentSubscription(cal.getTime());

      // Check
      subscription = subscriptionSrv.getFullSubscription(subId);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      rFreq = (RecurrentFrequency) subscription.getFrequency();
      Assert.assertEquals(nextDate, rFreq.getNextDate());
   }
}
