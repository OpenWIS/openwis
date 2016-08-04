/**
 *
 */
package eu.akka.openwis.dataservice.common.domain;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Diffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MailDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.PublicDissemination;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class AdhocTestCase extends AbstractTestCase {


   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.AbstractTestCase#getRelativeDataSet()
    */
   @Override
   public String getRelativeDataSet() {
      return "/dataset/adhoc/adhocs.xml";
   }

   /**
    * Test find adhoc id.
    */
   @Test
   public void testFindAdhocId() {

      AdHoc existingAdhoc = em.find(AdHoc.class, Long.valueOf(20));
      Assert.assertNotNull(existingAdhoc);

      ProcessedRequest processedRequest = em.find(ProcessedRequest.class, Long.valueOf(10));
      Assert.assertEquals(processedRequest.getSize(), 1234l);

      // DISEMINATION
      Dissemination existingPrimaryDissemination = existingAdhoc.getPrimaryDissemination();
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
      Set<Parameter> exisitingParameters = existingAdhoc.getParameters();
      Assert.assertNotNull(exisitingParameters);
      Assert.assertTrue(exisitingParameters.size() == 1);
      Parameter existingParameter = exisitingParameters.iterator().next();
      Assert.assertNotNull(existingParameter);
      Assert.assertTrue(existingParameter.getValues().size() == 4);

      // USER
      String user = existingAdhoc.getUser();
      Assert.assertNotNull(user);
   }
}
