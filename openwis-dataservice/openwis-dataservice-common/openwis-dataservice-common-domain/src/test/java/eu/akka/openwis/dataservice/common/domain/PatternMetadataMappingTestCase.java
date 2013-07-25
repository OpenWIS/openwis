/**
 *
 */
package eu.akka.openwis.dataservice.common.domain;

import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.cache.PatternMetadataMapping;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class PatternMetadataMappingTestCase extends AbstractTestCase {

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
    *
    * @throws Exception the exception
    */
   @Test
   public void testPatterMapping() throws Exception {
      try {
         PatternMetadataMapping pmm = new PatternMetadataMapping();
         pmm.setPattern("a pattern");
         em.persist(pmm);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }
}
