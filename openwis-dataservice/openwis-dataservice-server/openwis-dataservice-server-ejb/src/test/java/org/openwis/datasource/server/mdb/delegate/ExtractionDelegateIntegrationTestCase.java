/**
 *
 */
package org.openwis.datasource.server.mdb.delegate;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.RequestService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.service.impl.ProcessedRequestServiceImplIntegrationTestCase;
import org.openwis.management.JndiManagementServiceBeans;
import org.openwis.management.ManagementServiceBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
//@Ignore
public class ExtractionDelegateIntegrationTestCase extends ArquillianDBTestCase {

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
   private final Calendar now = Calendar.getInstance(DateTimeUtils.UTC_TIME_ZONE);

   /** The extraction delegate. */
   @EJB
   private ExtractionDelegate extractionDelegate;

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The request srv. */
   @EJB
   private RequestService requestSrv;

   /** The subscription srv. */
   @EJB
   private SubscriptionService subscriptionSrv;

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
   public void init() throws Exception {
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
   
//   @Test
//   public void testAccessToProcessRequestSrv() {
//      processedRequestSrv.getAllProcessedRequestsByRequest(Long.valueOf(123), 0, 10, ProcessedRequestColumn.CREATION_DATE, SortDirection.ASC);
//   }

   /**
    * Test extract local.
    * 
    * XXX - For some reason, this test causes deadlocks in Wildfly 8.
    */
   @Test
   @Ignore
   public void testExtractLocal() {
      AdHoc adhoc;
      ProcessedRequest pr;
      // Create Request
      adhoc = buildAdHoc(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      adhoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      Long reqId = requestSrv.createRequest(adhoc, URN_TEST);
      Assert.assertNotNull(reqId);
      adhoc = requestSrv.getRequest(reqId);
      pr = processedRequestSrv.getProcessedRequestForAdhoc(reqId);
      Assert.assertNotNull(pr.getId());

      ProcessedRequestMessage pm = buildProcessedRequestMessage(pr.getId(), now);
      extractionDelegate.processMessage(pm);
      // FIXME use a determinism Harness LocalDataSource for better testing
      //      Assert.assertNotNull(pr);
      //      status = pr.getRequestResultStatus();
      //      Assert.assertFalse(RequestResultStatus.CREATED.equals(status));
      //      Assert.assertFalse(RequestResultStatus.INITIAL.equals(status));
      //      Assert.assertFalse(RequestResultStatus.FAILED.equals(status));
   }

   /**
    * Test extract global.
    */
   /*
      @Test
      public void testExtractGlobal() {

         Subscription subs;
         ProcessedRequest pr;
         // Create Subscription
         Value value = new Value();
         value.setValue(getHourInterval(now.getTime()));
         subs = buildOnProductArrivalSubscription(URN_TEST, ParameterCode.TIME_INTERVAL, value);
         subs.setExtractMode(ExtractMode.GLOBAL);
         Long reqId = subscriptionSrv.createSubscription(subs, URN_TEST);
         Assert.assertNotNull(reqId);
         subs = subscriptionSrv.getSubscription(reqId);
         pr = buildProcessedRequest(now.getTime());
         reqId = processedRequestSrv.addProcessedRequestToSubscription(subs, pr);
         pr = processedRequestSrv.getProcessedRequest(reqId);
         Assert.assertNotNull(pr);
         Assert.assertNotNull(pr.getId());

         ProcessedRequestMessage pm = buildProcessedRequestMessage(pr.getId(), now);

         Status s = extractionDelegate.processMessage(pm);
         Assert.assertNotNull(s);
      }
   */

   /**
    * Test extract invalid.
    */
   @Test
//   @Ignore
   public void testExtractInvalid() {
      try {
         ProcessedRequestMessage pm = buildProcessedRequestMessage(-1L, now);
         extractionDelegate.processMessage(pm);
         Assert.fail("Expect an EJBException");
      } catch (EJBException e) {
         logger.debug("Expected exception: " + e);
      }
   }

   /**
    * Builds the processed request message.
    *
    * @param prId the processed request id
    * @param date the date
    * @return the processed request message
    */
   private ProcessedRequestMessage buildProcessedRequestMessage(Long prId, Calendar date) {
      ProcessedRequestMessage result = new ProcessedRequestMessage();
      result.setId(prId);
      result.setProductDate(DateTimeUtils.formatUTC(date.getTime()));
      return result;
   }
}
