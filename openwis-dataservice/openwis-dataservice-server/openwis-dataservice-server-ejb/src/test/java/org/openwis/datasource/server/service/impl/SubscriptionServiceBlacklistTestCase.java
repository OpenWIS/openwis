/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionBackup;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.SubscriptionService;
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
public class SubscriptionServiceBlacklistTestCase extends ArquillianDBTestCase {

   /** The logger. */
   private static Logger logger = LoggerFactory
         .getLogger(SubscriptionServiceBlacklistTestCase.class);

   /** The Constant VALUE. */
   public static final String VALUE = "VALUE";

   /** The Constant URN_TEST_A. */
   private static final String URN_TEST_A = "FVXX01AAAA";

   /** The Constant URN_TEST_B. */
   private static final String URN_TEST_B = "FVXX01BBBB";

   /** The Constant URN_TEST_C. */
   private static final String URN_TEST_C = "FVXX01CCCC";

   /** The Constant URN_TEST_D. */
   private static final String URN_TEST_D = "FVXX01DDDD";

   /** The Constant URN_TEST. */
   private static final String URN_TEST_PREFIX = "urn:x-wmo:md:int.wmo.wis::";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The subscription srv. */
   @EJB
   private SubscriptionService subscriptionSrv;

   /** The processed request srv. */
   @EJB
   private ProcessedRequestService processedRequestSrv;

   /** The CacheIndex service. */
   @EJB
   private CacheIndex cacheIndexService;

   /** The count. */
   private static long count = 101;

   /** The Constant GISC. */
   private static final String GISC = "gisc-in-failure";

   /** The Constant NOW. */
   private static final Calendar NOW = DateTimeUtils.getUTCCalendar();

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
      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * Creates the subscription.
    *
    * @param pm the pma
    * @param mode the mode
    * @param isOnProductArrival the is on product arrival
    * @return the subscription
    */
   private Subscription createSubscription(ProductMetadata pm, ExtractMode mode,
         boolean isOnProductArrival) {
      Value value = new Value();
      value.setValue(getHourInterval(NOW.getTime()));

      Subscription subscription;
      if (isOnProductArrival) {
         subscription = buildOnProductArrivalSubscription(USER_TEST,
               ParameterCode.DATE_TIME_INTERVAL, value);
      } else {
         subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL,
               value);
      }
      subscription.setExtractMode(mode);
      subscription.setState(SubscriptionState.SUSPENDED_BACKUP);

      SubscriptionBackup sb = new SubscriptionBackup();
      sb.setDeployment(GISC);
      sb.setSubscriptionId(count++);
      subscription.setSubscriptionBackup(sb);

      Long id = subscriptionSrv.createSubscription(subscription, pm.getUrn());
      return subscriptionSrv.getFullSubscription(id);
   }

   /**
    * Test blacklist.
    * @throws Throwable
    */
   @Test
   public void testBlacklist() throws Throwable {
      // Create ProductMetadata
      ProductMetadata pma = buildProductMetadata(URN_TEST_PREFIX + URN_TEST_A, DATA_POLICY_TEST);
      Long idPma = metadaSrv.createProductMetadata(pma);
      ProductMetadata pmb = buildProductMetadata(URN_TEST_PREFIX + URN_TEST_B, DATA_POLICY_TEST);
      Long idPmb = metadaSrv.createProductMetadata(pmb);
      ProductMetadata pmc = buildProductMetadata(URN_TEST_PREFIX + URN_TEST_C, DATA_POLICY_TEST);
      Long idPmc = metadaSrv.createProductMetadata(pmc);
      ProductMetadata pmd = buildProductMetadata(URN_TEST_PREFIX + URN_TEST_D, DATA_POLICY_TEST);
      Long idPmd = metadaSrv.createProductMetadata(pmd);

      // Create subscription A
      Subscription suba = createSubscription(pma, ExtractMode.GLOBAL, true);
      Subscription subb = createSubscription(pmb, ExtractMode.NOT_IN_LOCAL_CACHE, true);
      Subscription subc = createSubscription(pmc, ExtractMode.GLOBAL, false);
      Subscription subd = createSubscription(pmd, ExtractMode.GLOBAL, true);

      Calendar current;
      Calendar from = DateTimeUtils.getUTCCalendar();
      from.setTime(NOW.getTime());
      from.add(Calendar.HOUR, -3);

      // Create Product A
      int nbProductA = 0;
      current = (Calendar) from.clone();
      current.add(Calendar.MINUTE, 1);
      while (current.before(NOW)) {
         createCachedProduct(pma.getUrn(), URN_TEST_A, current, idPma);
         current.add(Calendar.MINUTE, 10);
         nbProductA++;
      }

      // Create Product B
      int nbProductB = 0;
      current = (Calendar) from.clone();
      current.add(Calendar.MINUTE, 2);
      while (current.before(NOW)) {
         createCachedProduct(pmb.getUrn(), URN_TEST_B, current, idPmb);
         current.add(Calendar.MINUTE, 30);
         nbProductB++;
      }

      // Create Product C
      int nbProductC = 0;
      current = (Calendar) from.clone();
      current.add(Calendar.MINUTE, 3);
      while (current.before(NOW)) {
         createCachedProduct(pmc.getUrn(), URN_TEST_C, current, idPmc);
         current.add(Calendar.MINUTE, 60);
         nbProductC++;
      }

      // No product D
      int nbProductD = 0;

      // Check subscription have not started
      List<LightProcessedRequestDTO> prs;

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(suba.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertTrue(prs.isEmpty());

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(subb.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertTrue(prs.isEmpty());

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(subc.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertTrue(prs.isEmpty());

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(subd.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertTrue(prs.isEmpty());

      // Set backup mode for the evil gisc
      subscriptionSrv.setBackup(GISC, true, DateTimeUtils.formatUTC(from.getTime()));

      Subscription sub;
      // check activated
      sub = subscriptionSrv.getFullSubscription(idPma);
      Assert.assertNotNull(sub);
      Assert.assertEquals(SubscriptionState.ACTIVE, sub.getState());

      // Waiting few times
      try {
         Thread.sleep(5 * 1000);// 5s
      } catch (InterruptedException e) {
         e.printStackTrace();
         throw e;
      }

      // re check subscription
      prs = processedRequestSrv.getAllProcessedRequestsByRequest(suba.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertEquals(nbProductA, prs.size());

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(subb.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertTrue(prs.isEmpty()); // not in cache => no processed request

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(subc.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertEquals(4, prs.size()); // check at from + {0, 1, 2, 3} => 4 processed request

      prs = processedRequestSrv.getAllProcessedRequestsByRequest(subd.getId(), 0, 20, null, null);
      Assert.assertNotNull(prs);
      Assert.assertEquals(nbProductD, prs.size());
   }
}
