/**
 * 
 */
package eu.akka.openwis.dataservice.common.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Diffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MailDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.PublicDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SubscriptionTestCase extends AbstractTestCase {

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.AbstractTestCase#getRelativeDataSet()
    */
   @Override
   public String getRelativeDataSet() {
      return "/dataset/subscription/subscription.xml";
   }

   /**
    * Test subscription id.
    */
   @Test
   public void testSubscriptionId() {

      Subscription subscription = em.find(Subscription.class, Long.valueOf(20));
      Assert.assertNotNull(subscription);

      // DISEMINATION
      Dissemination existingPrimaryDissemination = subscription.getPrimaryDissemination();
      Assert.assertTrue(existingPrimaryDissemination instanceof PublicDissemination);
      Assert.assertNotNull(((PublicDissemination) existingPrimaryDissemination).getDiffusion());
      Diffusion existingDiffusion = ((PublicDissemination) existingPrimaryDissemination)
            .getDiffusion();
      Assert.assertTrue(existingDiffusion instanceof MailDiffusion);
      Assert.assertNotNull(((MailDiffusion) existingDiffusion).getAddress());
      Assert.assertNotNull(((MailDiffusion) existingDiffusion).getFileName());
      Assert.assertNotNull(((MailDiffusion) existingDiffusion).getHeaderLine());
      Assert.assertNotNull(((MailDiffusion) existingDiffusion).getMailAttachmentMode());

      // PRODUCT_METADATA

      // PARAMETERS
      Set<Parameter> exisitingParameters = subscription.getParameters();
      Assert.assertNotNull(exisitingParameters);
      Assert.assertTrue(exisitingParameters.size() == 1);
      Parameter existingParameter = exisitingParameters.iterator().next();
      Assert.assertNotNull(existingParameter);
      Assert.assertTrue(existingParameter.getValues().size() == 4);

      // USER
      String user = subscription.getUser();
      Assert.assertNotNull(user);
   }

   /** Test subscription recurrent. */
   @SuppressWarnings("unchecked")
   @Test
   public void testSubscriptionRecurrent() {
      Calendar now = Calendar.getInstance();
      Date date = now.getTime();
      Query findQuery2 = em.createNamedQuery("Subscription.FindRecurrentToProcess").setParameter(
            "date", date);
      List<Subscription> results = findQuery2.getResultList();
      Assert.assertFalse(results.isEmpty());
      Subscription sub = results.get(0);
      Assert.assertNotNull(sub);
   }
}
