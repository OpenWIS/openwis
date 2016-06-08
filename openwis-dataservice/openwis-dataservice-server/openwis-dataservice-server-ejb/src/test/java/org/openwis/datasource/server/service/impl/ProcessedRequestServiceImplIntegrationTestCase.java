/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Request;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.RequestService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.openwis.management.JndiManagementServiceBeans;
import org.openwis.management.ManagementServiceBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
public class ProcessedRequestServiceImplIntegrationTestCase extends ArquillianDBTestCase {

   /**
    * Comment for <code>TIME_ZONE</code>
    * @member: TIME_ZONE
    */
   private static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

   /** Comment for <code>EXPECTED_EXCEPTION</code>. @member: EXPECTED_EXCEPTION */
   private static final String EXPECTED_EXCEPTION = "Expected Exception: ";

   /** Comment for <code>SHOULD_RAISE_AN_EJB_EXCEPTION</code>. @member: SHOULD_RAISE_AN_EJB_EXCEPTION */
   private static final String SHOULD_RAISE_AN_EJB_EXCEPTION = "Should raise an EJBException!";

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory
         .getLogger(ProcessedRequestServiceImplIntegrationTestCase.class);

   /** The Constant METADATA_ID. */
   private static final String METADATA_ID = "FVXX01EGRR";

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "urn:x-wmo:md:int.wmo.wis::" + METADATA_ID;

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** now. */
   private final static Calendar now;

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The subscription srv. */
   @EJB
   private SubscriptionService subscriptionSrv;

   /** The request srv. */
   @EJB
   private RequestService requestSrv;

   /** The processed request srv. */
   @EJB
   private ProcessedRequestService processedRequestSrv;

   /** The CacheIndex service. */
   @EJB
   private CacheIndex cacheIndexService;

   private static boolean created = false;

   /**
    * {@inheritDoc}
    * @see org.openwis.datasource.server.ArquillianDBTestCase#getCachedIndex()
    */
   @Override
   protected CacheIndex getCachedIndex() {
      return cacheIndexService;
   }

   static {
      now = Calendar.getInstance(TIME_ZONE_UTC);
      now.set(Calendar.MINUTE, -31);
   }

   /**
    * Instantiates a new processed request service impl test case.
    */
   public ProcessedRequestServiceImplIntegrationTestCase() {
      super();
   }

   /**
    * Clear data folder on startup.
    */
   @BeforeClass
   public static void clearData() {
      File dataDir = new File("data");
      if (dataDir.exists()) {
         dataDir.delete();
      }
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
      if (!created) {
         createCachedProduct(URN_TEST, METADATA_ID, now, id);
         created = true;
      }

      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * End.
    */
   @AfterClass
   public static void end() {
      ArquillianDBTestCase.clearCachedProduct(METADATA_ID, now);
   }

   /**
    * Test extract subscription recurrent global.
    */
   /*
      @Test
      public void testExtractSubscriptionOnEventGlobal() {
         Subscription sub;
         Collection<LightProcessedRequestDTO> requests;
         ProcessedRequest pr;
         LightProcessedRequestDTO lightPr;
         RequestResultStatus status;
         Long subId;
         Long prId;
         // Create subscription
         Value value = new Value();
         value.setValue(getHourInterval(now.getTime()));
         sub = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL, value);
         sub.setExtractMode(ExtractMode.GLOBAL);
         subId = subscriptionSrv.createSubscription(sub, URN_TEST);
         Assert.assertNotNull(subId);

         // Check Empty subscription
         sub = subscriptionSrv.getFullSubscription(subId);
         Assert.assertNotNull(sub);
         requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
         Assert.assertNotNull(requests);
         Assert.assertTrue(requests.isEmpty());

         // add subscription request
         pr = buildProcessedRequest(now.getTime());
         prId = processedRequestSrv.addProcessedRequestToSubscription(sub, pr);
         Assert.assertNotNull(prId);
         sub = subscriptionSrv.getFullSubscription(subId);
         Assert.assertNotNull(sub);
         requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
         Assert.assertNotNull(requests);
         Assert.assertEquals(1, requests.size());
         lightPr = requests.iterator().next();
         Assert.assertEquals(prId, lightPr.getId());
         Assert.assertEquals(RequestResultStatus.CREATED, lightPr.getRequestResultStatus());

         // Extract
         //      processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

         // Check new status
         sub = subscriptionSrv.getFullSubscription(subId);
         Assert.assertNotNull(sub);
         requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
         Assert.assertNotNull(requests);
         Assert.assertEquals(1, requests.size());
         lightPr = requests.iterator().next();
         Assert.assertEquals(prId, lightPr.getId());
         status = lightPr.getRequestResultStatus();
         Assert.assertFalse(RequestResultStatus.CREATED.equals(status));
         Assert.assertFalse(RequestResultStatus.INITIAL.equals(status));
         Assert.assertFalse(RequestResultStatus.FAILED.equals(status));
      }
   */
   
   /**
    * Test create subscription processed request.
    */
   @Test
   public void testCreateSubscriptionProcessedRequest() {
      Subscription subscription;
      Collection<LightProcessedRequestDTO> requests;
      Long subId;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.TIME_INTERVAL,
            value);
      subId = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty request
      subscription = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(subscription);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 100, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      ProcessedRequest pr;
      LightProcessedRequestDTO lightPr;

      // Add a processedRequest
      pr = buildProcessedRequest(now.getTime());
      Long prId = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId);

      // Check subscription updated
      subscription = subscriptionSrv.getFullSubscription(subId);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
   }

   /**
    * Test create subscription processed request.
    */
   @Test
   public void testCreateRequestProcessedRequest() {
      AdHoc adHoc;
      ProcessedRequest pr;
      Long reqId;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      adHoc = buildAdHoc(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check Empty request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
   }

   /**
    * Test clear processed request staging post.
    */
   @Test
   public void testClearProcessedRequestStagingPost() {
      AdHoc adHoc;
      ProcessedRequest pr;
      Long reqId;
      String uri;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      adHoc = buildAdHoc(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check Empty request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
      uri = pr.getUri();
      Assert.assertNotNull(uri);

      // clear
      processedRequestSrv.clearProcessedRequestStagingPost();
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
      uri = pr.getUri();
      Assert.assertNull(uri);
   }

   /**
    * Test clear processed request staging post by uri.
    */
   @Test
   public void testClearProcessedRequestStagingPostByUri() {
      AdHoc adHoc;
      ProcessedRequest pr;
      Long reqId;
      String uri;
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      adHoc = buildAdHoc(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check Empty request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
      uri = pr.getUri();
      Assert.assertNotNull(uri);

      // clear
      processedRequestSrv.clearProcessedRequestStagingPostByUri(uri);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNull(pr);
   }

   /**
    * Test monitor extraction.
    */
   @Test
   public void testMonitorExtraction() {
      AdHoc adHoc;

      RequestResultStatus status;
      // Create request
      adHoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      adHoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      Long adhocId = requestSrv.createRequest(adHoc, URN_TEST);

      ProcessedRequest pr = processedRequestSrv.getProcessedRequestForAdhoc(adhocId);
      // Monitor
      processedRequestSrv.monitorExtraction(pr.getId());

      // Check
      pr = processedRequestSrv.getProcessedRequest(pr.getId());
      Assert.assertNotNull(pr);
      status = pr.getRequestResultStatus();
      assertNotNull(status);
      Assert.assertFalse(RequestResultStatus.ONGOING_EXTRACTION.equals(status));
   }

   /**
    * Test delete.
    */
   @Test
   public void testDelete() {
      AdHoc adHoc;

      // Create request
      adHoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      adHoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      Long adhocId = requestSrv.createRequest(adHoc, URN_TEST);

      ProcessedRequest pr = processedRequestSrv.getProcessedRequestForAdhoc(adhocId);
      Long id = pr.getId();

      processedRequestSrv.deleteProcessedRequests(Collections.singletonList(id));

   }

   /**
    * Test extract request local.
    */
   @Test
   public void testExtractRequestLocal() {
      AdHoc adHoc;
      ProcessedRequest pr;
      RequestResultStatus status;
      Long reqId;
      // Create request
      adHoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      adHoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      Parameter dateParameter = new Parameter();
      dateParameter.setCode("parameter.date.period");
      Value value = new Value();
      value.setValue("2010-12-13/2010-12-15");
      dateParameter.getValues().add(value);
      adHoc.getParameters().add(dateParameter);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check initial request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
      status = pr.getRequestResultStatus();
      Assert.assertEquals(RequestResultStatus.CREATED, status);

      // Extract
      processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

      // Check new status
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
      status = pr.getRequestResultStatus();
      Assert.assertFalse(RequestResultStatus.CREATED.equals(status));
      Assert.assertFalse(RequestResultStatus.INITIAL.equals(status));

      if (RequestResultStatus.ONGOING_EXTRACTION.equals(status)) {
         waitFor(1 * 1000); // 1 second
         boolean extraction = processedRequestSrv.monitorExtraction(reqId);
         Assert.assertTrue(extraction);
      }
   }

   /**
    * Wait for.
    *
    * @param timeout the timeout
    */
   private void waitFor(int timeout) {
      try {
         Thread.sleep(timeout);
      } catch (InterruptedException e) {
         logger.warn("", e);
      }
   }

   /**
    * Test extract request global.
    */
   /*  @Test
     public void testExtractRequestGlobal() {
        AdHoc adHoc;
        ProcessedRequest pr;
        RequestResultStatus status;
        Long reqId;
        // Create request
        Value value = new Value();
        value.setValue(getHourInterval(now.getTime()));
        adHoc = buildAdHoc(USER_TEST, ParameterCode.TIME_INTERVAL, value);
        adHoc.setExtractMode(ExtractMode.GLOBAL);
        reqId = requestSrv.createRequest(adHoc, URN_TEST);
        Assert.assertNotNull(reqId);

        // Check Empty request
        adHoc = requestSrv.getRequest(reqId);
        Assert.assertNotNull(adHoc);
        pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
        Assert.assertNotNull(pr);
        status = pr.getRequestResultStatus();
        Assert.assertEquals(RequestResultStatus.CREATED, status);

        // Extract
        processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

        // Check new status
        adHoc = requestSrv.getRequest(reqId);
        Assert.assertNotNull(adHoc);
        pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
        Assert.assertNotNull(pr);
        status = pr.getRequestResultStatus();
        Assert.assertFalse(RequestResultStatus.CREATED.equals(status));
        Assert.assertFalse(RequestResultStatus.INITIAL.equals(status));
        Assert.assertFalse(RequestResultStatus.FAILED.equals(status));
     }
   */
   
   /**
    * Test extract with invalid date.
    */
   @Test
   public void testExtractWithInvalidDate() {
      AdHoc adHoc;
      ProcessedRequest pr;
      Long reqId;
      // Create request
      adHoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      adHoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check Empty request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);

      // Extract
      Status status = processedRequestSrv.extract(pr, "<AnInvalidDate>", null);
      Assert.assertEquals(Status.ERROR, status);
   }

   /**
    * Test extract with no processed request.
    */
   @Test
   public void testExtractWithNoProcessedRequest() {
      // Extract with no processed request
      try {
         processedRequestSrv.extract(null, DateTimeUtils.formatUTC(now.getTime()), null);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e);
      }
   }

   /**
    * Test extract subscription local.
    */
   @Test
   public void testExtractSubscriptionLocal() {
      Subscription sub;
      Collection<LightProcessedRequestDTO> requests;
      ProcessedRequest pr;
      LightProcessedRequestDTO lightPr;
      RequestResultStatus status;
      Long subId;
      Long prId;
      // Create subscription
      sub = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      sub.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      subId = subscriptionSrv.createSubscription(sub, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty subscription
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      // add subscription request
      pr = buildProcessedRequest(now.getTime());
      prId = processedRequestSrv.addProcessedRequestToSubscription(sub, pr);
      Assert.assertNotNull(prId);
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      Assert.assertEquals(RequestResultStatus.CREATED, lightPr.getRequestResultStatus());

      // Extract
      processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

      // Check new status
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      status = lightPr.getRequestResultStatus();
      Assert.assertFalse(RequestResultStatus.CREATED.equals(status));
      Assert.assertFalse(RequestResultStatus.INITIAL.equals(status));
      // FIXME use a determinism Harness LocalDataSource for better testing
      //      Assert.assertFalse(RequestResultStatus.FAILED.equals(status));
   }

   /**
    * Test extract subscription recurrent global.
    */
   /*
      @Test
      public void testExtractSubscriptionRecurrentGlobal() {
         Subscription sub;
         Collection<LightProcessedRequestDTO> requests;
         ProcessedRequest pr;
         LightProcessedRequestDTO lightPr;
         RequestResultStatus status;
         Long subId;
         Long prId;
         // Create subscription
         Value value = new Value();
         value.setValue(getHourInterval(now.getTime()));
         sub = buildRecurrentSubscription(USER_TEST, ParameterCode.TIME_INTERVAL, value);
         sub.setExtractMode(ExtractMode.GLOBAL);
         subId = subscriptionSrv.createSubscription(sub, URN_TEST);
         Assert.assertNotNull(subId);

         // Check Empty subscription
         sub = subscriptionSrv.getFullSubscription(subId);
         Assert.assertNotNull(sub);
         requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
         Assert.assertNotNull(requests);
         Assert.assertTrue(requests.isEmpty());

         // add subscription request
         pr = buildProcessedRequest(now.getTime());
         prId = processedRequestSrv.addProcessedRequestToSubscription(sub, pr);
         Assert.assertNotNull(prId);
         sub = subscriptionSrv.getFullSubscription(subId);
         Assert.assertNotNull(sub);
         requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
         Assert.assertNotNull(requests);
         Assert.assertEquals(1, requests.size());
         lightPr = requests.iterator().next();
         Assert.assertEquals(prId, lightPr.getId());
         Assert.assertEquals(RequestResultStatus.CREATED, lightPr.getRequestResultStatus());

         // Extract
         processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

         // Check new status
         sub = subscriptionSrv.getFullSubscription(subId);
         Assert.assertNotNull(sub);
         requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
         Assert.assertNotNull(requests);
         Assert.assertEquals(1, requests.size());
         lightPr = requests.iterator().next();
         Assert.assertEquals(prId, lightPr.getId());
         status = lightPr.getRequestResultStatus();
         Assert.assertFalse(RequestResultStatus.CREATED.equals(status));
         Assert.assertFalse(RequestResultStatus.INITIAL.equals(status));
         Assert.assertFalse(RequestResultStatus.FAILED.equals(status));
      }
   */

   /**
    * Test extract invalid subscription recurrent global.
    */
   /*
   @Test
   public void testExtractInvalidSubscriptionRecurrentGlobal() {
      Subscription sub;
      Collection<LightProcessedRequestDTO> requests;
      ProcessedRequest pr;
      LightProcessedRequestDTO lightPr;
      Long subId;
      Long prId;
      // Create subscription
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      sub = buildRecurrentSubscription(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      sub.setExtractMode(ExtractMode.GLOBAL);
      sub.setValid(false);
      subId = subscriptionSrv.createSubscription(sub, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty subscription
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      // add subscription request
      pr = buildProcessedRequest(now.getTime());
      prId = processedRequestSrv.addProcessedRequestToSubscription(sub, pr);
      Assert.assertNotNull(prId);
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      Assert.assertEquals(RequestResultStatus.CREATED, lightPr.getRequestResultStatus());

      // Extract
      processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

      // Check new status
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
   .getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      RequestResultStatus status = lightPr.getRequestResultStatus();
      Assert.assertEquals(RequestResultStatus.FAILED, status);
   }
   */
   
   /**
    * Test extract suspended subscription recurrent global.
    */
   @Test
   public void testExtractSuspendedSubscriptionRecurrentGlobal() {
      Subscription sub;
      Collection<LightProcessedRequestDTO> requests;
      ProcessedRequest pr;
      LightProcessedRequestDTO lightPr;
      Long subId;
      Long prId;
      // Create subscription
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      sub = buildRecurrentSubscription(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      sub.setExtractMode(ExtractMode.GLOBAL);
      sub.setState(SubscriptionState.SUSPENDED);
      subId = subscriptionSrv.createSubscription(sub, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty subscription
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      // add subscription request
      pr = buildProcessedRequest(now.getTime());
      prId = processedRequestSrv.addProcessedRequestToSubscription(sub, pr);
      Assert.assertNotNull(prId);
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      Assert.assertEquals(RequestResultStatus.CREATED, lightPr.getRequestResultStatus());

      // Extract
      processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

      // Check new status
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      RequestResultStatus status = lightPr.getRequestResultStatus();
      Assert.assertEquals(RequestResultStatus.FAILED, status);
   }

   /**
    * Test extract suspended backup subscription recurrent global.
    */
   @Test
   public void testExtractSuspendedBackupSubscriptionRecurrentGlobal() {
      Subscription sub;
      Collection<LightProcessedRequestDTO> requests;
      ProcessedRequest pr;
      LightProcessedRequestDTO lightPr;
      Long subId;
      Long prId;
      // Create subscription
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      sub = buildRecurrentSubscription(USER_TEST, ParameterCode.TIME_INTERVAL, value);
      sub.setExtractMode(ExtractMode.GLOBAL);
      sub.setState(SubscriptionState.SUSPENDED_BACKUP);
      subId = subscriptionSrv.createSubscription(sub, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty subscription
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      // add subscription request
      pr = buildProcessedRequest(now.getTime());
      prId = processedRequestSrv.addProcessedRequestToSubscription(sub, pr);
      Assert.assertNotNull(prId);
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      Assert.assertEquals(RequestResultStatus.CREATED, lightPr.getRequestResultStatus());

      // Extract
      processedRequestSrv.extract(pr, DateTimeUtils.formatUTC(now.getTime()), null);

      // Check new status
      sub = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(sub);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(1, requests.size());
      lightPr = requests.iterator().next();
      Assert.assertEquals(prId, lightPr.getId());
      RequestResultStatus status = lightPr.getRequestResultStatus();
      Assert.assertEquals(RequestResultStatus.FAILED, status);
   }

   /**
    * Test delete processed requests.
    */
   @Test
   public void testDeleteProcessedRequestsForAdHoc() {
      AdHoc adHoc;
      ProcessedRequest pr;
      Long reqId;

      // Test adHoc
      adHoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check not Empty request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);

      // Delete request
      processedRequestSrv.deleteProcessedRequestsByRequest(adHoc.getId());
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNull(pr);
   }

   /**
    * Test delete processed requests.
    */
   @Test
   public void testDeleteProcessedRequestsForSubscription() {
      Subscription subscription;
      Collection<LightProcessedRequestDTO> requests;
      Long subId;
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subId = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty request
      subscription = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(subscription);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 100, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      ProcessedRequest pr;

      // Add two processedRequest
      pr = buildProcessedRequest(now.getTime());
      Long prId = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId);

      pr = buildProcessedRequest(now.getTime());
      prId = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId);

      // Check subscription updated
      subscription = subscriptionSrv.getFullSubscription(subId);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(2, requests.size());

      // Delete
      processedRequestSrv.deleteProcessedRequestsByRequest(subId);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());
   }

   /**
    * Test delete processed requests with AdHoc.
    */
   @Test
   public void testDeleteProcessedRequestsWithAdHocWithAdHoc() {
      AdHoc adHoc;
      ProcessedRequest pr;
      Long reqId;
      Long prId;

      // Test adHoc
      adHoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      reqId = requestSrv.createRequest(adHoc, URN_TEST);
      Assert.assertNotNull(reqId);

      // Check not Empty request
      adHoc = requestSrv.getRequest(reqId);
      Assert.assertNotNull(adHoc);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr);
      prId = pr.getId();
      Assert.assertNotNull(prId);

      // Delete processed request
      processedRequestSrv.deleteProcessedRequestWithAdHoc(prId);
      pr = processedRequestSrv.getProcessedRequest(prId);
      Assert.assertNull(pr);
      Request requset = requestSrv.getRequest(reqId);
      Assert.assertNull(requset);
   }

   /**
    * Test delete processed requests with ad hoc with subscription.
    */
   @Test
   public void testDeleteProcessedRequestsWithAdHocWithSubscription() {
      Subscription subscription;
      Collection<LightProcessedRequestDTO> requests;
      Long subId;
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subId = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(subId);

      // Check Empty request
      subscription = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(subscription);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertTrue(requests.isEmpty());

      ProcessedRequest pr;

      // Add two processedRequest
      pr = buildProcessedRequest(now.getTime());
      Long prId = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId);

      pr = buildProcessedRequest(now.getTime());
      prId = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId);

      // Check subscription updated
      subscription = subscriptionSrv.getFullSubscription(subId);
      requests = processedRequestSrv
.getAllProcessedRequestsByRequest(subId, 0, 500, null, null);
      Assert.assertNotNull(requests);
      Assert.assertEquals(2, requests.size());

      // Delete processed request
      processedRequestSrv.deleteProcessedRequestWithAdHoc(prId);
      pr = processedRequestSrv.getProcessedRequest(prId);
      Assert.assertNull(pr);
      Request request = subscriptionSrv.getSubscription(subId);
      Assert.assertNotNull(request);
   }
}
