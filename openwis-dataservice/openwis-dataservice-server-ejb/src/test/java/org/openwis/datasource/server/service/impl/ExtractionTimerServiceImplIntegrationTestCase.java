/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.util.Calendar;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.timer.ExtractionTimerService;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.openwis.management.JndiManagementServiceBeans;
import org.openwis.management.ManagementServiceBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
public class ExtractionTimerServiceImplIntegrationTestCase extends ArquillianDBTestCase {

   /**
    * Comment for <code>TIME_ZONE</code>
    * @member: TIME_ZONE
    */
   private static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ExtractionTimerServiceImplIntegrationTestCase.class);

   /** The Constant METADATA_ID. */
   private static final String METADATA_ID = "FVXX01EGRR";

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "urn:x-wmo:md:int.wmo.wis::" + METADATA_ID;

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** now. */
   private final Calendar now = Calendar.getInstance(TIME_ZONE_UTC);

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The subscription srv. */
   @EJB
   private SubscriptionService subscriptionSrv;

   /** The processed request srv. */
   @EJB
   private ProcessedRequestService processedRequestSrv;

   /** The extraction timer srv. */
   @EJB
   private ExtractionTimerService extractionTimerSrv;

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
    * @throws NamingException 
    */
   @Before
   public void init() throws NamingException {
//      ManagementServiceBeans.setInstance(new JndiManagementServiceBeans(JndiManagementServiceBeans.LOCAL_JNDI_PREFIX));
      
      //FIXME: This insert must be remove as soon as DBUnit integration is performed
      ProductMetadata productMetadata = metadaSrv.getProductMetadataByUrn(URN_TEST);
      Long id = null;
      if (productMetadata == null) {
         productMetadata = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
         id = metadaSrv.createProductMetadata(productMetadata);
      }

      // Create product in cached
      createCachedProduct(URN_TEST, METADATA_ID, now, id);

      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * End.
    */
   @After
   public void end() {
      clearCachedProduct(METADATA_ID, now);
   }

   /**
    * Test extraction timer service.
    */
   @Test
   public void testExtractionTimerService() {
      Subscription subscription;
      ProcessedRequest pr;
      // Create Request
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      Long subId = subscriptionSrv.createSubscription(subscription, URN_TEST);
      subscription = subscriptionSrv.getFullSubscription(subId);
      pr = buildProcessedRequest(now.getTime());
      pr.setRequestResultStatus(RequestResultStatus.ONGOING_EXTRACTION);
      processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      pr = subscriptionSrv.findLastProcessedRequest(subId);
      Assert.assertNotNull(pr.getId());

      // start timer
      int interval = 250; // 250 ms
      try {
         extractionTimerSrv.start(interval);
         // Wait
         Thread.sleep((long) (interval * 1.5));
      } catch (InterruptedException e) {
         logger.warn("Outch!", e);
      }
      // TODO check with determinism Harness extraction
      //      pr = subscriptionSrv.findLastProcessedRequest(subId);
      //      Assert.assertNotNull(pr.getId());
      //      Assert.assertEquals(RequestResultStatus.EXTRACTED, pr.getRequestResultStatus());

      // end
      extractionTimerSrv.destroy();
   }
}
