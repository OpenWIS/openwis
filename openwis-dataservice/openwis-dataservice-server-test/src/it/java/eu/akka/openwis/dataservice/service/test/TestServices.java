/**
 * 
 */
package eu.akka.openwis.dataservice.service.test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.RecurrentUpdateFrequency;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.ShoppingCartDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.RecurrentFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.CacheExtraService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.RequestService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class TestServices {

   static RequestService rsrv = null;

   static SubscriptionService ssrv = null;

   static ProductMetadataService msrv = null;

   static CacheExtraService csrv = null;

   //static String urn = "URN" + Math.random();
   static String urn = "urn:x-wmo:md:int.wmo.wis::ZSMCBABJ";

   /**
    * @throws java.lang.Exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      ServiceProvider.getDefaultContext();
      System.out.println("Context set up OK !");
      rsrv = ServiceProvider.getRequestSrv();
      ssrv = ServiceProvider.getSubscriptionSrv();
      msrv = ServiceProvider.getMetadataSrv();
      //csrv = ServiceProvider.getCacheSrv();
      System.out.println("Services retrieved ");

   }

   /**
    * @throws java.lang.Exception
    */
   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      System.out.println("End of test");
   }

   @Test
   public void testNewSubscription() throws InterruptedException {
      System.out.println("Create Subscription ...");
      Subscription subs = new Subscription();
      subs.setActive(Boolean.TRUE);
      subs.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      // Dissemination
      ShoppingCartDissemination shoppingCartDissemination = new ShoppingCartDissemination();
      subs.setPrimaryDissemination(shoppingCartDissemination);
      // Frequency
      RecurrentFrequency recurrentFrequency = new RecurrentFrequency();
      recurrentFrequency.setReccurencePeriod(Integer.valueOf(1));
      recurrentFrequency.setReccurentScale(RecurrentScale.HOUR);
      subs.setFrequency(recurrentFrequency);
      subs.setStartingDate(Calendar.getInstance().getTime());
      subs.setUser("USER_TEST");
      // ProductMetaData
      ProductMetadata productMetadata = new ProductMetadata();
      //String urn2 = "URN" + Math.random();
      productMetadata.setUrn(urn);
      productMetadata.setDataPolicy("");
      productMetadata.setFed(true);
      productMetadata.setFncPattern("");
      productMetadata.setGtsCategory("");
      productMetadata.setIngested(true);
      productMetadata.setLocalDataSource("localDataSource1");
      productMetadata.setOriginator("");
      productMetadata.setOverridenDataPolicy("");
      productMetadata.setOverridenFncPattern("");
      productMetadata.setOverridenPriority(0);
      productMetadata.setPriority(0);
      productMetadata.setProcess("");
      productMetadata.setTitle("");
      RecurrentUpdateFrequency recurrentUpdateFrequency = new RecurrentUpdateFrequency();
      recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.HOUR);
      recurrentUpdateFrequency.setRecurrentPeriod(1);
      productMetadata.setUpdateFrequency(recurrentUpdateFrequency);
      msrv.createProductMetadata(productMetadata);

      // Parameters
      HashSet<Parameter> parameters = new HashSet<Parameter>();
      Parameter parameter = new Parameter();
      Value value = new Value();
      value.setValue("VALUE");
      parameter.getValues().add(value);
      parameters.add(parameter);
      subs.setParameters(parameters);

      System.out.print("Create new Subscription ...");
      Long subId = ssrv.createSubscription(subs, urn);
      System.out.println("id = " + subId);

   }

   @Test
   public void TestSubscriptionGeneratedRequest() throws InterruptedException {
      // Client JMS
      Subscription subscription = ssrv.getFullSubscription(Long.valueOf(2L));
      System.out.println(subscription.getStartingDate());
      IncomingDSMessage incomingDSMessage = new IncomingDSMessage();
      incomingDSMessage.setMetadataURNs(Arrays.asList(subscription.getProductMetadata().getUrn()));
      incomingDSMessage.setProductDate(DateTimeUtils.formatUTC(Calendar.getInstance().getTime()));
      try {
         StringWriter sw = new StringWriter();
         Serializer.serialize(incomingDSMessage, sw);
         SimpleQueueSender.sendMessage("queue/IncomingDataQueue", sw.toString());
         //         Thread.sleep(5000);
         //         incomingDSMessage.setProductDate(Calendar.getInstance().getTime());
         //         sw = new StringWriter();
         //         Serializer.serialize(incomingDSMessage, sw);
         //         SimpleQueueSender.sendMessage("queue/IncomingDataQueue", sw.toString());
      } catch (JAXBException e) {
         Assert.fail(e.getMessage());
      }
   }

   @Test
   public void testNewAdhoc() throws InterruptedException {
      System.out.println("Create Request ...");
      // AdHoc
      AdHoc adHoc = new AdHoc();
      adHoc.setUser("USER_TEST");
      adHoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);
      // Dissemination
      ShoppingCartDissemination shoppingCartDissemination = new ShoppingCartDissemination();
      adHoc.setPrimaryDissemination(shoppingCartDissemination);

      // ProductMetaData
      ProductMetadata productMetadata = new ProductMetadata();
      String urn2 = "URN" + Math.random();
      productMetadata.setUrn(urn2);
      productMetadata.setDataPolicy("");
      productMetadata.setFed(true);
      productMetadata.setFncPattern("");
      productMetadata.setGtsCategory("");
      productMetadata.setIngested(true);
      productMetadata.setLocalDataSource("localDataSource1");
      productMetadata.setOriginator("");
      productMetadata.setOverridenDataPolicy("");
      productMetadata.setOverridenFncPattern("");
      productMetadata.setOverridenPriority(0);
      productMetadata.setPriority(0);
      productMetadata.setProcess("");
      productMetadata.setTitle("");
      RecurrentUpdateFrequency recurrentUpdateFrequency = new RecurrentUpdateFrequency();
      recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.HOUR);
      recurrentUpdateFrequency.setRecurrentPeriod(1);
      productMetadata.setUpdateFrequency(recurrentUpdateFrequency);

      //productMetadata = msrv.getProductMetadataByUrn(urn2);
      // Parameters
      HashSet<Parameter> hashSet = new HashSet<Parameter>();
      Parameter parameter = new Parameter();
      Value value = new Value();
      value.setValue("VALUE");
      parameter.getValues().add(value);
      hashSet.add(parameter);
      adHoc.setParameters(hashSet);

      Long id = rsrv.createRequest(adHoc, urn2);
      // Test the state change
      Thread.sleep(15000);
      AdHoc existingRequest = rsrv.getRequest(id);
      //TODO Fix processed request.
//      Assert
//            .assertTrue(existingRequest.getProcessedRequest().getRequestResultStatus() == RequestResultStatus.DISSEMINATED);
   }

   @Test
   public void testCallJMS() throws InterruptedException {
      // Client JMS
      IncomingDSMessage incomingDSMessage = new IncomingDSMessage();
      incomingDSMessage.setMetadataURNs(Arrays.asList("0.22363095024816637"));
      incomingDSMessage.setProductDate(DateTimeUtils.formatUTC(Calendar.getInstance().getTime()));
      try {
         StringWriter sw = new StringWriter();
         Serializer.serialize(incomingDSMessage, sw);
         SimpleQueueSender.sendMessage("queue/IncomingDataQueue", sw.toString());
         Thread.sleep(5000);
         incomingDSMessage
               .setProductDate(DateTimeUtils.formatUTC(Calendar.getInstance().getTime()));
         sw = new StringWriter();
         Serializer.serialize(incomingDSMessage, sw);
         SimpleQueueSender.sendMessage("queue/IncomingDataQueue", sw.toString());
      } catch (JAXBException e) {
         Assert.fail(e.getMessage());
      }
   }

   @Test
   public void TestGetRequest() {
      System.out.println("Retrieve Request ...");
      AdHoc retrievedRequest = rsrv.getRequest(Long.valueOf(1l));
      System.out.println("Received Request = " + retrievedRequest);
   }

   @Test
   public void TestLastRequest() {
      ProcessedRequest pr = ssrv.findLastProcessedRequest(Long.valueOf(2l));
      System.out.println(pr);
   }

   @Test
   public void TestProductMetadata() {
      // ProductMetaData
      ProductMetadata productMetadata = new ProductMetadata();
      productMetadata.setUrn(urn);
      productMetadata.setDataPolicy("");
      productMetadata.setFed(true);
      productMetadata.setFncPattern("");
      productMetadata.setGtsCategory("");
      productMetadata.setIngested(true);
      productMetadata.setLocalDataSource("localDataSource1");
      productMetadata.setOriginator("");
      productMetadata.setOverridenDataPolicy("");
      productMetadata.setOverridenFncPattern("");
      productMetadata.setOverridenPriority(0);
      productMetadata.setPriority(0);
      productMetadata.setProcess("");
      productMetadata.setTitle("");
      RecurrentUpdateFrequency recurrentUpdateFrequency = new RecurrentUpdateFrequency();
      recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.HOUR);
      recurrentUpdateFrequency.setRecurrentPeriod(1);
      productMetadata.setUpdateFrequency(recurrentUpdateFrequency);

      Long id = msrv.createProductMetadata(productMetadata);

      ProductMetadata productMetadata2 = msrv.getProductMetadataByUrn(urn);
      Assert.assertEquals(id, productMetadata2.getId());

   }

   @Test
   public void TestRemoveAdHocRequest() {
      System.out.println("Retrieve Request ...");
      AdHoc retrievedRequest = rsrv.getRequest(Long.valueOf(1l));
      System.out.println("Received Request = " + retrievedRequest);
      rsrv.deleteRequest(retrievedRequest.getId());
   }

   @Test
   public void TestRemoveSubscription() {
      System.out.println("Retrieve Request ...");
      Subscription subscription = ssrv.getSubscription(Long.valueOf(23l));
      System.out.println("Received Subscription = " + subscription);
      ssrv.deleteSubscription(subscription.getId());
   }

   @Test
   public void TestLastRequests() {
      System.out.println("Retrieve Last Request ...");
      List<ProcessedRequest> last = rsrv.getLastProcessedRequest("USER_TEST", 3);
      System.out.println("Received Request = " + last);

   }

}
