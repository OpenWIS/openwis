/**
 * 
 */
package eu.akka.openwis.dataservice.common.domain;

import java.util.List;

import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ProcessedRequestTestCase extends AbstractTestCase {

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.AbstractTestCase#getRelativeDataSet()
    */
   @Override
   public String getRelativeDataSet() {
      return "/dataset/processed_request/processed_request.xml";
   }

   /**
    * Test name query processed request.
    */
   @SuppressWarnings("unchecked")
   @Test
   public void testNameQueryProcessedRequest() {
      Query findQuery2 = em.createNamedQuery("ProcessedRequest.FindByRequestResult").setParameter(
            "requestresult", RequestResultStatus.DISSEMINATED);
      List<ProcessedRequest> results = findQuery2.getResultList();
      Assert.assertNotNull(results);
      Assert.assertTrue(results.size() == 2);
   }

}
