/**
 *
 */
package org.openwis.datasource.server.mdb.delegate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;
import org.openwis.management.JndiManagementServiceBeans;
import org.openwis.management.ManagementServiceBeans;

/**
 * The Class SubscriptionDelegateTestCase. <P>
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
public class SubscriptionDelegateIntegrationTestCase extends ArquillianDBTestCase {

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "URN_TEST";

   /** The Constant URN_TEST. */
   private static final String URN_TEST_0 = "URN_TEST_0";

   /** The Constant URN_TEST_1. */
   private static final String URN_TEST_1 = "URN_TEST_1";

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** The subscription delegate. */
   @EJB
   private SubscriptionDelegate subscriptionDelegate;

   /** The subscription srv. */
   @EJB
   private SubscriptionService subscriptionSrv;

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The processed request srv. */
   @EJB
   private ProcessedRequestService processedRequestSrv;

   /** The CacheIndex service. */
   @EJB
   private CacheIndex cacheIndexService;

   /** The now. */
   private final Calendar now = DateTimeUtils.getUTCCalendar();

   /**
    * Gets the cached index.
    *
    * @return the cached index
    * {@inheritDoc}
    * @see org.openwis.datasource.server.ArquillianDBTestCase#getCachedIndex()
    */
   @Override
   protected CacheIndex getCachedIndex() {
      return cacheIndexService;
   }

   /**
    * Initialize.
    * Description goes here.
    */
   @Before
   public void initialize() throws Exception {
//      ManagementServiceBeans.setInstance(new JndiManagementServiceBeans(JndiManagementServiceBeans.LOCAL_JNDI_PREFIX));
      
      // Initialize subscription attribute
      //FIXME: This insert must be remove as soon as DBUnit integration is performed
      ProductMetadata productMetadata = metadaSrv.getProductMetadataByUrn(URN_TEST);
      if (productMetadata == null) {
         productMetadata = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);

         productMetadata = buildProductMetadata(URN_TEST_0, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);

         productMetadata = buildProductMetadata(URN_TEST_1, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);
      }

      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * Test subscription on product arrival.
    * 
    * XXX - Currently non-deterministic.  Need to fix.
    */
   @Test
   @Ignore()
   public void testSubscriptionOnProductArrival() {
      Subscription subscription;
      Date lastEvtDate;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL,
            value);

      // Create the subscription
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
      Assert.assertNotNull(id);

      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(URN_TEST, Calendar.getInstance().getTime());
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertEquals(1, processedRequests.size());
      ProcessedRequest processedRequest = processedRequests.iterator().next();

      // check processedRequest
      Assert.assertNotNull(processedRequest.getUri());
      Assert.assertEquals(RequestResultStatus.CREATED, processedRequest.getRequestResultStatus());

      // Check subscription
      subscription = subscriptionSrv.getFullSubscription(id);
      Collection<LightProcessedRequestDTO> requests = processedRequestSrv
            .getAllProcessedRequestsByRequest(id, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      LightProcessedRequestDTO pr = requests.iterator().next();
      Assert.assertEquals(processedRequest.getId(), pr.getId());

      lastEvtDate = subscription.getLastEventDate();
      // last event date is updated after extraction for on product arrival
      Assert.assertNull(lastEvtDate);
   }

   /**
    * Test invalid subscription on product arrival.
    */
   @Test
   @InSequence(2)
   public void testInvalidSubscriptionOnProductArrival() {
      Subscription subscription;
      ProductMetadata productMetadata;
      String urn;

      Date lastEvtDate;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL,
            value);
      subscription.setValid(false);

      // Create Product Metadata
      urn = "PM-InvalidSubscription";
      productMetadata = buildProductMetadata(urn, DATA_POLICY_TEST);
      metadaSrv.createProductMetadata(productMetadata);

      // Create the subscription
      Long id = subscriptionSrv.createSubscription(subscription, urn);
      subscription = subscriptionSrv.getSubscription(id);
      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
      Assert.assertNotNull(id);

      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(urn, Calendar.getInstance().getTime());
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertEquals(0, processedRequests.size());

      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
   }

   /**
    * Test suspended subscription on product arrival.
    */
   @Test
   @InSequence(3)
   public void testSuspendedSubscriptionOnProductArrival() {
      Subscription subscription;
      ProductMetadata productMetadata;
      String urn;

      Date lastEvtDate;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL,
            value);
      subscription.setState(SubscriptionState.SUSPENDED);

      // Create Product Metadata
      urn = "PM-SuspendedSubscription";
      productMetadata = buildProductMetadata(urn, DATA_POLICY_TEST);
      metadaSrv.createProductMetadata(productMetadata);

      // Create the subscription
      Long id = subscriptionSrv.createSubscription(subscription, urn);
      subscription = subscriptionSrv.getSubscription(id);
      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
      Assert.assertNotNull(id);

      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(urn, Calendar.getInstance().getTime());
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertEquals(0, processedRequests.size());

      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
   }

   /**
    * Test suspended backup subscription on product arrival.
    */
   @Test
   @InSequence(4)
   public void testSuspendedBackupSubscriptionOnProductArrival() {
      Subscription subscription;
      ProductMetadata productMetadata;
      String urn;

      Date lastEvtDate;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL,
            value);
      subscription.setState(SubscriptionState.SUSPENDED_BACKUP);

      // Create Product Metadata
      urn = "PM-SuspendedBackupSubscription";
      productMetadata = buildProductMetadata(urn, DATA_POLICY_TEST);
      metadaSrv.createProductMetadata(productMetadata);

      // Create the subscription
      Long id = subscriptionSrv.createSubscription(subscription, urn);
      subscription = subscriptionSrv.getSubscription(id);
      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
      Assert.assertNotNull(id);

      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(urn, Calendar.getInstance().getTime());
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertEquals(0, processedRequests.size());

      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
   }

   /**
    * Test subscription recurrent.
    */
   @Test
   @InSequence(5)
   public void testSubscriptionRecurrent() {
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      Subscription subscription = buildRecurrentSubscription(USER_TEST,
            ParameterCode.TIME_INTERVAL, value);
      Date lastEvtDate;

      // Create the subscription
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST_1);
      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
      Assert.assertNotNull(id);

      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(URN_TEST_1, Calendar.getInstance()
            .getTime());
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertEquals(0, processedRequests.size());

      lastEvtDate = subscription.getLastEventDate();
      Assert.assertNull(lastEvtDate);
   }

   /**
    * Test on product arrival with no subscription.
    */
   @Test
   @InSequence(6)
   public void testOnProductArrivalWithNoSubscription() {
      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(URN_TEST_0, Calendar.getInstance()
            .getTime());
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertTrue(processedRequests.isEmpty());
   }

   /**
    * Test on product arrival with invalid date.
    */
   @Test
   @InSequence(7)
   public void testOnProductArrivalWithInvalidDate() {
      Subscription subscription = buildOnProductArrivalSubscription(USER_TEST, null);
      // Create the subscription
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      // Send an Incoming product
      IncomingDSMessage message = buildIncomingDSMessage(URN_TEST, Calendar.getInstance().getTime());
      message.setProductDate("<AnInvalidDate>");
      Collection<ProcessedRequest> processedRequests = subscriptionDelegate.processSubscriptions(message);
      Assert.assertNotNull(processedRequests);
      Assert.assertTrue(processedRequests.isEmpty());
   }

   /**
    * Create an Incoming DS Message.
    *
    * @param urn the urn
    * @param date the date
    * @return the message
    */
   private IncomingDSMessage buildIncomingDSMessage(String urn, Date date) {
      IncomingDSMessage message = new IncomingDSMessage();
      message.setProductDate(DateTimeUtils.formatUTC(date));
      message.setMetadataURNs(Collections.singletonList(urn));
      return message;
   }

}
