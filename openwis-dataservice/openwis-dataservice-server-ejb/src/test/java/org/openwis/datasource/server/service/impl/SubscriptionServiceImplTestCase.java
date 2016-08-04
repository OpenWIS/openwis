/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.bean.DataPolicyOperations;
import org.openwis.dataservice.common.domain.bean.Operation;
import org.openwis.dataservice.common.domain.bean.UserDataPolicyOperations;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.MailAttachmentMode;
import org.openwis.dataservice.common.domain.entity.enumeration.MailDispatchMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.DisseminationZipMode;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MSSFSSChannel;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MSSFSSDissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MailDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.PublicDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.Frequency;
import org.openwis.dataservice.common.domain.entity.subscription.RecurrentFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionBackup;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.service.BlacklistService;
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
public class SubscriptionServiceImplTestCase extends ArquillianDBTestCase {

   /** Comment for <code>EXPECTED_EXCEPTION</code>. @member: EXPECTED_EXCEPTION */
   private static final String EXPECTED_EXCEPTION = "Expected Exception: ";

   /** Comment for <code>SHOULD_RAISE_AN_EJB_EXCEPTION</code>. @member: SHOULD_RAISE_AN_EJB_EXCEPTION */
   private static final String SHOULD_RAISE_AN_EJB_EXCEPTION = "Should raise an EJBException!";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionServiceImplTestCase.class);

   /** The Constant VALUE. */
   public static final String VALUE = "VALUE";

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "URN_TEST";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** The Constant URN_TEST. */
   private static final String URN_TEST_0 = "URN_TEST_0";

   /** The Constant USER_TEST. */
   private static final String USER_TEST = "USER_TEST";

   /** The Constant USER_TEST. */
   private static final String USER_TEST_PATTERN = "USER_TEST-{0}";

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

   /** The blacklist service. */
   @EJB
   private BlacklistService blacklistService;

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

         productMetadata = buildProductMetadata(URN_TEST_0, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);
      }

      // clear User staging post
      clearStagingPost(USER_TEST);
   }

   /**
    * Test get subscription with id.
    */
   @Test
   public void testGetSubscriptionWithId() {
      Subscription subscription;
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      // Get subscription
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
   }

   /**
    * Test get subscription with invalid id.
    */
   @Test
   public void testGetSubscriptionWithInvalidId() {
      Subscription subscription;

      // Test unknown id
      subscription = subscriptionSrv.getSubscription(-1L);
      Assert.assertNull(subscription);

      // Test null id
      try {
         subscriptionSrv.getSubscription(null);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get full subscription with id.
    */
   @Test
   public void testGetFullSubscriptionWithId() {
      Subscription subscription;
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      // Get full subscription
      subscription = subscriptionSrv.getFullSubscription(id);
      Assert.assertNotNull(subscription);
      // Check that all requests are loaded
      Collection<LightProcessedRequestDTO> requests = processedRequestSrv
            .getAllProcessedRequestsByRequest(id, 0, 500, null, null);
      Assert.assertTrue(requests.isEmpty());
   }

   /**
    * Test full get subscription with invalid id.
    */
   @Test
   public void testFullGetSubscriptionWithInvalidId() {
      // Test unknown id
      try {
         subscriptionSrv.getFullSubscription(-1L);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }

      // Test null id
      try {
         subscriptionSrv.getFullSubscription(null);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create subscription.
    */
   @Test
   public void testCreateSubscription() {
      Subscription subscription;
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setBackup(true);
      SubscriptionBackup sb = new SubscriptionBackup();
      sb.setSubscriptionId(41L);
      sb.setDeployment("GISC-A");
      subscription.setSubscriptionBackup(sb);

      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
   }

   /**
    * Test create subscription with empty urn.
    */
   @Test
   public void testCreateSubscriptionWithEmptyUrn() {
      Subscription subscription;
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);

      try {
         subscriptionSrv.createSubscription(subscription, "");
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create subscription with blacklisted user.
    */
   @Test
   public void testCreateSubscriptionWithBlacklistedUser() {
      Subscription subscription;

      try {
         String user = "bl-user";
         blacklistService.setUserBlacklisted(user, true);

         subscription = buildRecurrentSubscription(user, ParameterCode.DATE_TIME_INTERVAL);
         subscriptionSrv.createSubscription(subscription, URN_TEST);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create subscription with null urn.
    */
   @Test
   public void testCreateSubscriptionWithNullUrn() {
      Subscription subscription;
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);

      try {
         subscriptionSrv.createSubscription(subscription, null);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create subscription empty.
    */
   @Test
   public void testCreateSubscriptionEmpty() {
      try {
         subscriptionSrv.createSubscription(null, URN_TEST);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create subscription with invalid urn.
    */
   @Test
   public void testCreateSubscriptionWithInvalidUrn() {
      Subscription subscription;
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);

      try {
         subscriptionSrv.createSubscription(subscription, URN_TEST + System.nanoTime());
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test delete subscription.
    */
   @Test
   public void testDeleteSubscription() {
      Subscription subscription;
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      // delete subscription
      subscriptionSrv.deleteSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNull(subscription);
   }

   /**
    * Test delete subscription with invalid id.
    */
   @Test
   public void testDeleteSubsrciptionWithInvalidId() {

      try {
         subscriptionSrv.deleteSubscription(-1L);
         Assert.fail(SHOULD_RAISE_AN_EJB_EXCEPTION);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test update subscription.
    */
   @Test
   public void testUpdateSubscription() {
      Subscription subscription;
      // Insert a subscription
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Update
      Date date = Calendar.getInstance().getTime();
      subscription.setStartingDate(date);

      subscription = subscriptionSrv.updateSubscription(subscription);
      Assert.assertEquals(date, subscription.getStartingDate());
   }

   /**
    * Test update subscription config.
    */
   @Test
   public void testUpdateSubscriptionConfig() {
      Subscription subscription;
      // Insert a subscription
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      Long id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // New Primary dissemination
      PublicDissemination newPrimaryDissemination;
      newPrimaryDissemination = new PublicDissemination();
      newPrimaryDissemination.setZipMode(DisseminationZipMode.ZIPPED);
      MailDiffusion diffusion = new MailDiffusion();
      newPrimaryDissemination.setDiffusion(diffusion);
      diffusion.setAddress("public@openwis.org");
      diffusion.setMailAttachmentMode(MailAttachmentMode.AS_ATTACHMENT);
      diffusion.setFileName("Product.zip");
      diffusion.setMailDispatchMode(MailDispatchMode.TO);
      diffusion.setSubject("[OpenWIS] Product for Subscription: " + id);
      diffusion.setHeaderLine("Header");

      // New Secondary Dissemination
      MSSFSSDissemination newSecondaryDissemination = new MSSFSSDissemination();
      newSecondaryDissemination.setZipMode(DisseminationZipMode.WMO_FTP);
      MSSFSSChannel channel = new MSSFSSChannel();
      newSecondaryDissemination.setChannel(channel);
      channel.setChannel("RadioOne");

      // New Parameters
      String productId = "XXX-Test_ProductId";
      Set<Parameter> newParams = new HashSet<Parameter>();
      Parameter param = new Parameter();
      newParams.add(param);
      Value value = new Value();
      param.setValues(Collections.singleton(value));
      param.setCode(ParameterCode.PRODUCT_ID);
      value.setValue(productId);

      // New Frequency
      RecurrentFrequency newFrequency = new RecurrentFrequency();
      newFrequency.setReccurentScale(RecurrentScale.HOUR);
      newFrequency.setReccurencePeriod(1);

      // New Date
      GregorianCalendar cDate = (GregorianCalendar) DateTimeUtils.getUTCCalendar();
      cDate.setTime(new Date());

      // Update the subscription config
      subscriptionSrv.updateSubscriptionConfig(id, newParams, newPrimaryDissemination,
            newSecondaryDissemination, newFrequency, DateTimeUtils.formatUTC(new Date()));

      // Check subscription
      subscription = subscriptionSrv.getFullSubscription(id);
      Assert.assertNotNull(subscription);
      // Check parameters
      Set<Parameter> parameters = subscription.getParameters();
      Assert.assertNotNull(parameters);
      Assert.assertEquals(1, parameters.size());
      Parameter p = parameters.iterator().next();
      Assert.assertEquals(ParameterCode.PRODUCT_ID, p.getCode());
      Assert.assertNotNull(p.getValues());
      Assert.assertEquals(1, p.getValues().size());
      Value v = p.getValues().iterator().next();
      Assert.assertEquals(productId, v.getValue());

      // Check Frequency
      Frequency frequency = subscription.getFrequency();
      Assert.assertNotNull(frequency);
      Assert.assertEquals(RecurrentFrequency.class, frequency.getClass());
      RecurrentFrequency f = (RecurrentFrequency) frequency;
      Assert.assertEquals(RecurrentScale.HOUR, f.getReccurentScale());
      Assert.assertEquals(1, (int) f.getReccurencePeriod());

      // Check primary dissemination
      Dissemination primDiss = subscription.getPrimaryDissemination();
      Assert.assertNotNull(primDiss);
      Assert.assertEquals(PublicDissemination.class, primDiss.getClass());
      PublicDissemination pubDiss = (PublicDissemination) primDiss;
      Assert.assertEquals(DisseminationZipMode.ZIPPED, pubDiss.getZipMode());
      Assert.assertEquals(MailDiffusion.class, pubDiss.getDiffusion().getClass());

      // Check secondary dissemination
      Dissemination secDiss = subscription.getSecondaryDissemination();
      Assert.assertNotNull(secDiss);
      Assert.assertEquals(DisseminationZipMode.WMO_FTP, secDiss.getZipMode());
      Assert.assertEquals(MSSFSSDissemination.class, secDiss.getClass());
      MSSFSSChannel c = ((MSSFSSDissemination) secDiss).getChannel();
      Assert.assertNotNull(c);
      Assert.assertEquals("RadioOne", c.getChannel());
   }

   /**
    * Test get subscription with users.
    */
   @Test
   public void testGetSubscriptionWithUsers() {
      Subscription subscription;
      Long id;
      String user1 = MessageFormat.format(USER_TEST_PATTERN, System.nanoTime());
      String user2 = MessageFormat.format(USER_TEST_PATTERN, System.nanoTime());

      // Create subscription for user1
      subscription = buildOnProductArrivalSubscription(user1, ParameterCode.DATE_TIME_INTERVAL);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = buildRecurrentSubscription(user1, ParameterCode.DATE_TIME_INTERVAL);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      // Create subscription for user2
      subscription = buildOnProductArrivalSubscription(user2, ParameterCode.DATE_TIME_INTERVAL);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = buildRecurrentSubscription(user2, ParameterCode.DATE_TIME_INTERVAL);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);

      List<Subscription> subscriptions;
      List<String> users;
      // Find for user1
      users = Arrays.asList(user1);
      subscriptions = subscriptionSrv.getSubscriptionsByUsers(users, 0, 5, null, null);
      Assert.assertNotNull(subscriptions);
      Assert.assertEquals(2, subscriptions.size());

      // Find for user2
      users = Arrays.asList(user2);
      subscriptions = subscriptionSrv.getSubscriptionsByUsers(users, 0, 5, null, null);
      Assert.assertNotNull(subscriptions);
      Assert.assertEquals(2, subscriptions.size());

      // Find for user1 & user2
      users = Arrays.asList(user1, user2);
      subscriptions = subscriptionSrv.getSubscriptionsByUsers(users, 0, 5, null, null);
      Assert.assertNotNull(subscriptions);
      Assert.assertEquals(4, subscriptions.size());
   }

   /**
    * Test get subscription with no user.
    */
   @Test
   public void testGetSubscriptionWithNoUser() {
      List<String> users;

      // Find for empty
      try {
         users = Arrays.asList();
         subscriptionSrv.getSubscriptionsByUsers(users, 0, 5, null, null);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }

      // Find for null
      try {
         users = null;
         subscriptionSrv.getSubscriptionsByUsers(users, 0, 5, null, null);
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test find last processed request.
    */
   @Test
   public void testFindLastProcessedRequest() {
      Subscription subscription;
      Long subId;
      subscription = buildOnProductArrivalSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subId = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(subId);
      subscription = subscriptionSrv.getFullSubscription(subId);
      Assert.assertNotNull(subscription);

      ProcessedRequest pr;
      // Check initial state
      pr = subscriptionSrv.findLastProcessedRequest(subId);
      Assert.assertNull(pr);

      Calendar date;
      Long prId1;
      Long prId2;
      // Add a processedRequest
      date = Calendar.getInstance();
      pr = buildProcessedRequest(date.getTime());
      prId1 = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId1);

      // Add another processedRequest
      date = Calendar.getInstance();
      pr = buildProcessedRequest(date.getTime());
      prId2 = processedRequestSrv.addProcessedRequestToSubscription(subscription, pr);
      Assert.assertNotNull(prId2);

      // Get last ProcessedRequest
      pr = subscriptionSrv.findLastProcessedRequest(subId);
      Assert.assertNotNull(pr);
      Assert.assertEquals(prId2, pr.getId());
   }

   /**
    * Test resume subscription.
    */
   @Test
   public void testResumeSubscription() {
      Subscription subscription;
      Long id;

      // Create active subscription
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setState(SubscriptionState.ACTIVE);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Test resume
      subscriptionSrv.resumeSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);

      Assert.assertNotNull(subscription);
      Assert.assertEquals(SubscriptionState.ACTIVE, subscription.getState());

      // Create suspended as backup subscription
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setState(SubscriptionState.SUSPENDED_BACKUP);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Test resume
      subscriptionSrv.resumeSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);

      Assert.assertNotNull(subscription);
      Assert.assertEquals(SubscriptionState.SUSPENDED_BACKUP, subscription.getState());

      // Create suspended subscription
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setState(SubscriptionState.SUSPENDED);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Test resume
      subscriptionSrv.resumeSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);

      Assert.assertNotNull(subscription);
      Assert.assertEquals(SubscriptionState.ACTIVE, subscription.getState());

   }

   /**
    * Test suspend subscription.
    */
   @Test
   public void testSuspendSubscription() {
      Subscription subscription;
      Long id;
      // Create active Subscription
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setState(SubscriptionState.ACTIVE);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Test resume
      subscriptionSrv.suspendSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);

      Assert.assertNotNull(subscription);
      Assert.assertEquals(SubscriptionState.SUSPENDED, subscription.getState());

      // Create suspended Subscription
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setState(SubscriptionState.SUSPENDED_BACKUP);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Test resume
      subscriptionSrv.suspendSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);

      Assert.assertNotNull(subscription);
      Assert.assertEquals(SubscriptionState.SUSPENDED_BACKUP, subscription.getState());

      // Create suspended Subscription
      subscription = buildRecurrentSubscription(USER_TEST, ParameterCode.DATE_TIME_INTERVAL);
      subscription.setState(SubscriptionState.SUSPENDED);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);

      // Test resume
      subscriptionSrv.suspendSubscription(id);
      subscription = subscriptionSrv.getSubscription(id);

      Assert.assertNotNull(subscription);
      Assert.assertEquals(SubscriptionState.SUSPENDED, subscription.getState());
   }

   /**
    * Test check user subscription.
    */
   @Test
   public void testCheckUserSubscription() {
      Subscription subscription;
      Long id;
      String user = USER_TEST;
      // Create subscription
      subscription = buildRecurrentSubscription(user, ParameterCode.DATE_TIME_INTERVAL);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
      Assert.assertTrue(subscription.isValid());

      DataPolicyOperations dpo;

      // Check
      dpo = new DataPolicyOperations();
      dpo.setDataPolicy(DATA_POLICY_TEST);
      Set<Operation> operations = new HashSet<Operation>();
      dpo.setOperations(operations);

      // Check disable
      subscriptionSrv.checkUserSubscription(user, Collections.singleton(dpo));
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
      Assert.assertFalse(subscription.isValid());

      // Check enable
      dpo = new DataPolicyOperations();
      dpo.setDataPolicy(DATA_POLICY_TEST);
      operations.addAll(Arrays.asList(Operation.values()));
      dpo.setOperations(operations);
      subscriptionSrv.checkUserSubscription(user, Collections.singleton(dpo));
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
      Assert.assertTrue(subscription.isValid());
   }

   /**
    * Test check users subscription.
    */
   @Test
   public void testCheckUsersSubscription() {
      Subscription subscription;
      Long id;
      String user = USER_TEST;

      // Create subscription
      subscription = buildRecurrentSubscription(user, ParameterCode.DATE_TIME_INTERVAL);
      id = subscriptionSrv.createSubscription(subscription, URN_TEST);
      Assert.assertNotNull(id);
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
      Assert.assertTrue(subscription.isValid());

      UserDataPolicyOperations udpo;
      DataPolicyOperations dpo;

      // Check disable
      udpo = new UserDataPolicyOperations();
      udpo.setUser(user);
      dpo = new DataPolicyOperations();
      udpo.setDataPolicyOperations(Collections.singleton(dpo));
      dpo.setDataPolicy(DATA_POLICY_TEST);
      Set<Operation> operations = new HashSet<Operation>();
      dpo.setOperations(operations);

      subscriptionSrv.checkUsersSubscription(Collections.singleton(udpo));
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
      Assert.assertFalse(subscription.isValid());

      // Check enable
      udpo = new UserDataPolicyOperations();
      udpo.setUser(user);
      dpo = new DataPolicyOperations();
      udpo.setDataPolicyOperations(Collections.singleton(dpo));
      dpo.setDataPolicy(DATA_POLICY_TEST);
      operations.addAll(Arrays.asList(Operation.values()));
      dpo.setOperations(operations);

      subscriptionSrv.checkUsersSubscription(Collections.singleton(udpo));
      subscription = subscriptionSrv.getSubscription(id);
      Assert.assertNotNull(subscription);
      Assert.assertTrue(subscription.isValid());
   }
}
