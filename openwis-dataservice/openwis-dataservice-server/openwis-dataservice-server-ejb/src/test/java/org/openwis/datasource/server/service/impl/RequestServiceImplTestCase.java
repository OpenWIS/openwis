package org.openwis.datasource.server.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.hibernate.LazyInitializationException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.RequestService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RequestServiceImplTestCase. <P>
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
@Ignore
public class RequestServiceImplTestCase extends ArquillianDBTestCase {

   /** The Constant EXPECTED_EXCEPTION. */
   private static final String EXPECTED_EXCEPTION = "Expected Exception: ";

   /** The Constant SHOULD_RAISE_AN_EJB_EXCEPTION. */
   private static final String SHOULD_RAISE_AN_EJB_EXCEPTION = "Should raise an EJBException!";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionServiceImplTestCase.class);

   /** The Constant VALUE. */
   public static final String VALUE = "VALUE";

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "URN_TEST";

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** The request srv. */
   @EJB
   private RequestService requestSrv;

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

   /** The processed request srv. */
   @EJB
   private ProcessedRequestService processedRequestSrv;

   /** The CacheIndex service. */
   @EJB
   private CacheIndex cacheIndexService;

   /** The blacklist service. */
   @EJB
   private BlacklistService blacklistService;

   /** The now. */
   private final Calendar now = DateTimeUtils.getUTCCalendar();

   /**
    * {@inheritDoc}
    * @see org.openwis.datasource.server.ArquillianDBTestCase#getCachedIndex()
    */
   @Override
   protected CacheIndex getCachedIndex() {
      return cacheIndexService;
   }

   /** The ad hoc. */
   private AdHoc adHoc;

   /**
    * Initialize attributes.
    */
   @Before
   public void initAttr() {
      // Initialize adHoc attribute
      Value value = new Value();
      value.setValue(getHourInterval(now.getTime()));
      adHoc = buildAdHoc(USER_TEST, ParameterCode.TIME_INTERVAL, value);

      //FIXME: This insert must be remove as soon as DBUnit integration is performed
      ProductMetadata productMetadata = metadaSrv.getProductMetadataByUrn(URN_TEST);
      if (productMetadata == null) {
         productMetadata = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);
      }

      //FIXME: This delete must be remove as soon as DBUnit integration is performed
      Collection<String> users = new ArrayList<String>();
      users.add(USER_TEST);
      List<ProcessedRequest> requests = requestSrv.getRequestsByUsers(users, 0, 5, null, null);
      for (ProcessedRequest pr : requests) {
         requestSrv.deleteRequest(pr.getRequest().getId());
      }
      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * Test create request should fail for unknown urn metadata.
    */
   @Test
   public void testCreateRequestShouldFailForUnknownUrnMetadata() {
      // Create Request
      try {
         requestSrv.createRequest(adHoc, "URN_TEST_UNKNWON");
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create request should fail for blacklisted user.
    */
   @Test
   public void testCreateRequestShouldFailForBlacklistedUser() {
      // Create Request
      try {
         String user = "bl-user";
         blacklistService.setUserBlacklisted(user, true);

         Value value = new Value();
         value.setValue(getHourInterval(now.getTime()));
         AdHoc req = buildAdHoc(user, ParameterCode.TIME_INTERVAL, value);

         requestSrv.createRequest(req, URN_TEST);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create request should fail for empty urn metadata.
    */
   @Test
   public void testCreateRequestShouldFailForEmptyUrnMetadata() {
      // Create Request
      try {
         requestSrv.createRequest(adHoc, "");
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create request should fail for null urn metadata.
    */
   @Test
   public void testCreateRequestShouldFailForNullUrnMetadata() {
      // Create Request
      try {
         requestSrv.createRequest(adHoc, null);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create request should fail for ad hoc with not null id.
    */
   @Test
   public void testCreateRequestShouldFailForAdHocWithNotNullId() {
      Long id = createRequestHelper();
      AdHoc adHocOld = requestSrv.getRequest(id);
      try {
         requestSrv.createRequest(adHocOld, URN_TEST);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create request.
    */
   @Test
   public void testCreateRequest() {
      try {
         Long id = requestSrv.createRequest(adHoc, URN_TEST);
         Assert.assertNotNull(id);

         ProcessedRequest pr = processedRequestSrv.getProcessedRequestForAdhoc(id);
         Assert.assertNotNull(pr);
         Assert.assertNotNull(pr.getUri());
      } catch (EJBException e) {
         e.printStackTrace();
         Assert.fail("URN_TEST should be present in the initial database");
      }
   }

   /**
    * Test get request.
    */
   @Test
   public void testGetRequest() {
      // Create request
      Long id = createRequestHelper();

      AdHoc req = requestSrv.getRequest(id);
      Assert.assertNotNull(req);
      Assert.assertEquals(USER_TEST, req.getUser());

      // Should fail with LazyException
      try {
         req.getProductMetadata().getUrn();
         Assert.fail();
      } catch (LazyInitializationException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test delete request.
    */
   @Test
   public void testDeleteRequest() {

      // Create request
      Long id = createRequestHelper();

      // try to delete it
      try {
         requestSrv.deleteRequest(id);
      } catch (EJBException e) {
         Assert.fail("AdHoc request #" + id + " should be present in the database");
      }

      // Assert request is no more in the system
      AdHoc adHocNew = requestSrv.getRequest(id);
      Assert.assertNull(adHocNew);
   }

   /**
    * Test get last processed request.
    */
   @Test
   public void testGetLastProcessedRequest() {

      //create request
      Long id = createRequestHelper();

      ProcessedRequest pr = processedRequestSrv.getProcessedRequestForAdhoc(id);
      pr.setCompletedDate(new Date());
      processedRequestSrv.updateProcessedRequest(pr);

      List<ProcessedRequest> requests = requestSrv.getLastProcessedRequest(USER_TEST, 0);
      Assert.assertEquals(0, requests.size());

      requests = requestSrv.getLastProcessedRequest(USER_TEST, 100);
      Assert.assertEquals(1, requests.size());
      Assert.assertEquals(id, requests.get(0).getId());

      requests = requestSrv.getLastProcessedRequest(USER_TEST, 10000);
      Assert.assertEquals(1, requests.size());

      requests = requestSrv.getLastProcessedRequest(USER_TEST + " NOT IN DB", 10);
      Assert.assertEquals(0, requests.size());
   }

   /**
    * Test get requests by users.
    */
   @Test
   public void testGetRequestsByUsers() {

      // Create request
      Long id = createRequestHelper();

      Collection<String> users = new ArrayList<String>();
      users.add(USER_TEST);
      users.add("OTHER USER");
      users.add("");

      List<ProcessedRequest> requests = requestSrv.getRequestsByUsers(users, 0, 5, null, null);
      Assert.assertEquals(1, requests.size());
      Assert.assertEquals(id, requests.get(0).getId());

      users.remove(USER_TEST);
      requests = requestSrv.getRequestsByUsers(users, 0, 5, null, null);
      Assert.assertEquals(0, requests.size());

      users = new ArrayList<String>();
      requests = requestSrv.getRequestsByUsers(users, 0, 5, null, null);
      Assert.assertEquals(0, requests.size());
   }

   /**
    * Test remove request.
    */
   @Test
   public void testRemoveRequest() {
      String user1 = "UserOne";
      String user2 = "UserTwo";
      AdHoc adhoc;

      // Create Request for user1
      adhoc = buildAdHoc(user1, null);
      requestSrv.createRequest(adhoc, URN_TEST);

      // Create Request for user2
      adhoc = buildAdHoc(user2, null);
      requestSrv.createRequest(adhoc, URN_TEST);

      int count;
      // Check
      count = requestSrv.getRequestsByUsersCount(Collections.singleton(user1));
      Assert.assertEquals(1, count);

      count = requestSrv.getRequestsByUsersCount(Collections.singleton(user2));
      Assert.assertEquals(1, count);

      int deleted;
      // Remove all user1 request
      deleted = requestSrv.deleteRequestByUser(user1);
      Assert.assertEquals(deleted, 1);
      count = requestSrv.getRequestsByUsersCount(Collections.singleton(user1));
      Assert.assertEquals(0, count);

      count = requestSrv.getRequestsByUsersCount(Collections.singleton(user2));
      Assert.assertEquals(1, count);

      // Remove all user2 request
      deleted = requestSrv.deleteRequestByUser(user2);
      Assert.assertEquals(deleted, 1);
      count = requestSrv.getRequestsByUsersCount(Collections.singleton(user1));
      Assert.assertEquals(0, count);

      count = requestSrv.getRequestsByUsersCount(Collections.singleton(user2));
      Assert.assertEquals(0, count);

      // Remove all user1 request
      deleted = requestSrv.deleteRequestByUser(user1);
      Assert.assertEquals(deleted, 0);
   }

   /**
    * Creates the request helper.
    *
    * @return the long
    */
   private Long createRequestHelper() {
      Long id = null;
      try {
         id = requestSrv.createRequest(adHoc, URN_TEST);
         Assert.assertNotNull(id);

      } catch (EJBException e) {
         e.printStackTrace();
         Assert.fail(e.getMessage());
      }
      return id;
   }

}
