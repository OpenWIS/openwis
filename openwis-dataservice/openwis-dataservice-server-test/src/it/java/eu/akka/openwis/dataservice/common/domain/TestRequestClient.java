/**
 * 
 */
package eu.akka.openwis.dataservice.common.domain;

import java.util.Calendar;
import java.util.HashSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.RecurrentUpdateFrequency;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.ShoppingCartDissemination;
import org.openwis.dataservice.common.service.RequestService;

/**
 * @author n.guerrier
 * 
 */
public class TestRequestClient {

   /**
    * @throws java.lang.Exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      //
   }

   /**
    * @throws java.lang.Exception
    */
   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      //
   }

   @Test
   public void testCreateAdHoc() {
      try {
         Context context = new InitialContext();
         RequestService beanRemote = (RequestService) context
               .lookup("openwis-dataservice/RequestService/remote");
         AdHoc adHoc = new AdHoc();
         adHoc.setUser("USER_TESTBIS");
         adHoc.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);

         Dissemination diss = new ShoppingCartDissemination();
         adHoc.setPrimaryDissemination(diss);

         ProductMetadata productMetadata = new ProductMetadata();
         String urn = "URN" + Math.random();
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
         recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.DAY);
         recurrentUpdateFrequency.setRecurrentPeriod(1);
         productMetadata.setUpdateFrequency(recurrentUpdateFrequency);

         ProcessedRequest processedRequest = new ProcessedRequest();
         processedRequest.setSize(1234l);
         processedRequest.setCreationDate(Calendar.getInstance().getTime());
         //FIXME adHoc.setProcessedRequest(processedRequest);
         HashSet<Parameter> hashSet = new HashSet<Parameter>();
         Parameter parameter = new Parameter();
         Value value = new Value();
         value.setValue("VALUE");
         parameter.getValues().add(value);
         hashSet.add(parameter);
         adHoc.setParameters(hashSet);
         adHoc.setProductMetadata(productMetadata);

         Long id = beanRemote.createRequest(adHoc, urn);
         AdHoc existingRequest = beanRemote.getRequest(id);
//         FIXME existingRequest.getProcessedRequest().getSize();
         //         beanRemote.deleteRequest(id);

      } catch (NamingException e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testCreateSubscription() {
      //
   }

}
