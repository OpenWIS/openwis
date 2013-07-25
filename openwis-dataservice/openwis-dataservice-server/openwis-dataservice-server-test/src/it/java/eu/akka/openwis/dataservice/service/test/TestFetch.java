/**
 * 
 */
package eu.akka.openwis.dataservice.service.test;

import javax.naming.NamingException;

import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.SubscriptionService;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class TestFetch {

   /**
    * Description goes here.
    * @param args
    * @throws NamingException 
    */
   public static void main(String[] args) throws NamingException {
      ServiceProvider.getDefaultContext();
      SubscriptionService ssrv = ServiceProvider.getSubscriptionSrv();

      //      eu.akka.openwis.dataservice.common.domain.entity.subscription.Subscription subscription = new Subscription();
      //      subscription.setActive(Boolean.TRUE);
      //      subscription.setRequests(new LinkedHashSet<Request>());
      //      subscription.setParameters(new LinkedHashSet<Parameter>());
      //
      //      Request request = new Request();
      //      request.setCreationDate(new Date());
      //      request.setUpdateDate(new Date());
      //      request.setSize(10);
      //      request.setRequestResultStatus(RequestResultStatus.CREATED);
      //      request.setExtractMode(ExtractMode.GLOBAL);
      //      subscription.getRequests().add(request);
      //
      //      request = new Request();
      //      request.setCreationDate(new Date());
      //      request.setUpdateDate(new Date());
      //      request.setSize(20);
      //      request.setRequestResultStatus(RequestResultStatus.CREATED);
      //      request.setExtractMode(ExtractMode.GLOBAL);
      //      subscription.getRequests().add(request);
      //
      //      Value v1 = new Value();
      //      v1.setValue("First Value");
      //
      //      Parameter p = new Parameter();
      //      p.addValue(v1);
      //      subscription.getParameters().add(p);
      //
      //      p = new Parameter();
      //      p.addValue(v1);
      //      subscription.getParameters().add(p);
      //
      //      System.out.print("Save Subscription ...");
      //      ssrv.createSubscription(subscription);
      //
      //      System.out.println("done");
      System.out.println("Get Subscription");
      Subscription retrieved = ssrv.getFullSubscription(Long.valueOf(1l));
      //Subscription retrieved = ssrv.getSubscription(2l);
      System.out.println(retrieved);
//      System.out.println("Requests = " + retrieved.getProcessedRequests());
      System.out.println("Parameters = " + retrieved.getParameters());
   }

}
