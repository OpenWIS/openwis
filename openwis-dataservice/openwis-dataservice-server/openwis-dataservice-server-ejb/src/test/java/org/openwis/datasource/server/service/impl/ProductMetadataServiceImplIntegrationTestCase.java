package org.openwis.datasource.server.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.naming.NamingException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.entity.cache.PatternMetadataMapping;
import org.openwis.dataservice.common.domain.entity.enumeration.ProductMetadataColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.openwis.management.JndiManagementServiceBeans;
import org.openwis.management.ManagementServiceBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RequestServiceImplTestCase. <P>
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
public class ProductMetadataServiceImplIntegrationTestCase extends ArquillianDBTestCase {

   /** Comment for <code>EXPECTED_EXCEPTION</code>. @member: EXPECTED_EXCEPTION */
   private static final String EXPECTED_EXCEPTION = "Expected Exception: ";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionServiceImplIntegrationTestCase.class);

   /** Comment for <code>_11</code>.
    * @member: _11 */
   private static final int MINIMAL_SIZE = 11;

   /** The Constant URN_TEST. */
   private static final String URN_TEST = "URN_TEST";

   /** The Constant URN_TEST. */
   private static final String URN_TEST_PATTERN = "URN_TEST_{0}";

   /** The Constant DATA_POLICY_TEST. */
   private static final String DATA_POLICY_TEST = "dp-test";

   /** The metada srv. */
   @EJB
   private ProductMetadataService metadaSrv;

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

   /** Initialize the test. 
    * @throws NamingException */
   @Before
   public void init() throws NamingException {
//      ManagementServiceBeans.setInstance(new JndiManagementServiceBeans(JndiManagementServiceBeans.LOCAL_JNDI_PREFIX));
      
      //FIXME: This insert must be remove as soon as DBUnit integration is performed
      ProductMetadata productMetadata = metadaSrv.getProductMetadataByUrn(URN_TEST);
      if (productMetadata == null) {
         productMetadata = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(productMetadata);
         for (int i = 0; i < 10; i++) {
            productMetadata = buildProductMetadata(MessageFormat.format(URN_TEST_PATTERN, i),
                  DATA_POLICY_TEST);
            productMetadata
                  .setOverridenFncPattern("^urn:x-wmo:md:int\\.wmo\\.wis::.*[A-Z]{4}\\d\\dEGRR$");
            metadaSrv.createProductMetadata(productMetadata);
         }
      }
   }

   /**
    * Test get product metadata for unknown URN.
    */
   @Test
   public void testGetProductMetadataForUnknownUrn() {
      ProductMetadata pm;
      pm = metadaSrv.getProductMetadataByUrn("UnknownURN_0");
      Assert.assertNull(pm);
   }

   /**
    * Test get product metadata for null URN.
    */
   @Test
   public void testGetProductMetadataForNullUrn() {
      try {
         metadaSrv.getProductMetadataByUrn(null);
         Assert.fail("Should raise an invalid arg exception");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get product metadata for empty URN.
    */
   @Test
   public void testGetProductMetadataForEmptyUrn() {
      ProductMetadata pm;
      pm = metadaSrv.getProductMetadataByUrn("");
      Assert.assertNull(pm);
   }

   /**
    * Test get product metadata for unknown URN.
    */
   @Test
   public void testGetProductMetadataForValidUrn() {
      ProductMetadata pm;
      pm = metadaSrv.getProductMetadataByUrn(MessageFormat.format(URN_TEST_PATTERN, 0));
      Assert.assertNotNull(pm);
   }

   /**
    * Test get all product metadata with invalid page number.
    */
   @Test
   public void testGetAllProductMetadataWithInvalidPageNumber() {
      try {
         metadaSrv.getAllProductsMetadata(-1, 2, ProductMetadataColumn.URN, SortDirection.DESC);
         Assert.fail("Should raise InvalidArgumentException!");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get all product metadata with invalid page size.
    */
   @Test
   public void testGetAllProductMetadataWithInvalidPageSize() {
      try {
         metadaSrv.getAllProductsMetadata(0, 0, ProductMetadataColumn.URN, SortDirection.DESC);
         Assert.fail("Should raise InvalidArgumentException  !");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get all product metadata.
    * 
    * XXX - Currently non-deterministic.  Need to fix.
    */
   @Test
   @Ignore
   public void testGetAllProductMetadata() {
      List<ProductMetadata> pms;

      // Check default parameter
      pms = metadaSrv.getAllProductsMetadata(0, MINIMAL_SIZE, ProductMetadataColumn.URN,
            SortDirection.ASC);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.size() >= MINIMAL_SIZE);

      Assert.assertEquals(URN_TEST, pms.get(0).getUrn());
      for (int i = 1; i < MINIMAL_SIZE; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i - 1)), pms.get(i).getUrn());
      }

      // Check DESC
      pms = metadaSrv.getAllProductsMetadata(0, 200, ProductMetadataColumn.URN, SortDirection.DESC);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.size() > 10);

      Collections.reverse(pms);
      Assert.assertEquals(URN_TEST, pms.get(0).getUrn());
      for (int i = 1; i < MINIMAL_SIZE; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i - 1)), pms.get(i).getUrn());
      }

      // Test Paging
      int pageSize = 3;
      int page = 0;
      pms = metadaSrv.getAllProductsMetadata(page * pageSize, pageSize, null, null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(pageSize, pms.size());
      Assert.assertEquals(URN_TEST, pms.get(0).getUrn());
      for (int i = 1; i < pageSize; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i - 1)), pms.get(i).getUrn());
      }

      page = 1;
      pms = metadaSrv.getAllProductsMetadata(page * pageSize, pageSize, null, null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(pageSize, pms.size());
      for (int i = 0; i < pageSize; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i + (page * pageSize - 1))),
               pms.get(i).getUrn());
      }

      page = 2;
      pms = metadaSrv.getAllProductsMetadata(page * pageSize, pageSize, null, null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(pageSize, pms.size());
      for (int i = 0; i < pageSize; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i + (page * pageSize - 1))),
               pms.get(i).getUrn());
      }
   }

   /**
    * Test get products metadata by urns with invalid page number.
    */
   @Test
   public void testGetProductsMetadataByUrnsWithInvalidPageNumber() {
      List<String> urns = new ArrayList<String>();
      for (int i = 0; i < (MINIMAL_SIZE - 1); i++) {
         urns.add(MessageFormat.format(URN_TEST_PATTERN, i));
      }

      try {
         metadaSrv.getProductsMetadataByUrns(urns, -1, MINIMAL_SIZE, ProductMetadataColumn.URN,
               SortDirection.DESC);
         Assert.fail("Should raise InvalidArgumentException ! ");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get products metadata by urns with invalid page size.
    */
   @Test
   public void testGetProductsMetadataByUrnsWithInvalidPageSize() {
      List<String> urns = new ArrayList<String>();
      for (int i = 0; i < (MINIMAL_SIZE - 1); i++) {
         urns.add(MessageFormat.format(URN_TEST_PATTERN, i));
      }

      try {
         metadaSrv.getProductsMetadataByUrns(urns, 0, 0, ProductMetadataColumn.URN,
               SortDirection.DESC);
         Assert.fail("Should raise InvalidArgumentException !");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get products metadata by urns with empty urns.
    */
   @Test
   public void testGetProductsMetadataByUrnsWithEmptyUrns() {
      List<ProductMetadata> pms;
      List<String> urns = new ArrayList<String>();

      // Check default parameter
      pms = metadaSrv.getProductsMetadataByUrns(urns, 0, MINIMAL_SIZE, ProductMetadataColumn.URN,
            SortDirection.DESC);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.isEmpty());
   }

   /**
    * Test get products metadata by urns with null urns.
    */
   @Test
   public void testGetProductsMetadataByUrnsWithNullUrns() {
      List<ProductMetadata> pms;
      List<String> urns = new ArrayList<String>();

      // Check default parameter
      pms = metadaSrv.getProductsMetadataByUrns(urns, 0, MINIMAL_SIZE, ProductMetadataColumn.URN,
            SortDirection.DESC);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.isEmpty());
   }

   /**
    * Test get products metadata by urns.
    */
   @Test
   public void testGetProductsMetadataByUrns() {
      List<ProductMetadata> pms;
      List<String> urns = new ArrayList<String>();
      int size = 10;
      for (int i = 0; i < size; i++) {
         urns.add(MessageFormat.format(URN_TEST_PATTERN, i));
      }

      // Check default parameter
      pms = metadaSrv.getProductsMetadataByUrns(urns, 0, MINIMAL_SIZE, ProductMetadataColumn.URN,
            SortDirection.ASC);
      Assert.assertNotNull(pms);
      Assert.assertEquals(size, pms.size());

      for (int i = 0; i < size; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, i), pms.get(i).getUrn());
      }

      // Check DESC
      pms = metadaSrv.getProductsMetadataByUrns(urns, 0, 200, ProductMetadataColumn.URN,
            SortDirection.DESC);
      Assert.assertNotNull(pms);
      Assert.assertEquals(size, pms.size());

      Collections.reverse(pms);
      for (int i = 0; i < size; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, i), pms.get(i).getUrn());
      }

      // Test Paging
      int pageSize = 3;
      int page = 0;
      pms = metadaSrv.getProductsMetadataByUrns(urns, page * pageSize, pageSize, null, null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(pageSize, pms.size());
      for (int i = 0; i < pageSize; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, i), pms.get(i).getUrn());
      }

      page = 1;
      pms = metadaSrv.getProductsMetadataByUrns(urns, page * pageSize, pageSize, null, null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(pageSize, pms.size());
      for (int i = 0; i < pageSize; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i + page * pageSize)), pms
               .get(i).getUrn());
      }

      page = 2;
      pms = metadaSrv.getProductsMetadataByUrns(urns, page * pageSize, pageSize, null, null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(pageSize, pms.size());
      for (int i = 0; i < pageSize; i++) {
         Assert.assertEquals(MessageFormat.format(URN_TEST_PATTERN, (i + page * pageSize)), pms
               .get(i).getUrn());
      }
   }

   /**
    * Test get products metadata count.
    */
   @Test
   public void testGetProductsMetadataCount() {
      int count = metadaSrv.getProductsMetadataCount();
      Assert.assertTrue(count >= MINIMAL_SIZE);
   }

   /**
    * Test create product metadata for valid URN.
    */
   @Test
   public void testCreateProductMetadataForValidUrn() {
      ProductMetadata pm;
      pm = buildProductMetadata(MessageFormat.format(URN_TEST_PATTERN, System.nanoTime()),
            DATA_POLICY_TEST);
      Long id = metadaSrv.createProductMetadata(pm);
      Assert.assertNotNull(id);
   }

   /**
    * Test create product metadata for invalid URN.
    */
   @Test
   public void testCreateProductMetadataForInvalidUrn() {
      ProductMetadata pm;
      try {
         pm = buildProductMetadata(URN_TEST, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(pm);
         Assert.fail("Should raise an exception !");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create product metadata for a null URN.
    */
   @Test
   public void testCreateProductMetadataForANullUrn() {
      ProductMetadata pm;
      try {
         pm = buildProductMetadata(null, DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(pm);
         Assert.fail("Should raise an exception!");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test create product metadata for an empty URN.
    */
   @Test
   public void testCreateProductMetadataForAnEmptyUrn() {
      ProductMetadata pm;
      try {
         pm = buildProductMetadata("", DATA_POLICY_TEST);
         metadaSrv.createProductMetadata(pm);
         Assert.fail("Should raise an exception");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test get product metadata by id.
    */
   @Test
   public void testGetProductMetadataById() {
      ProductMetadata pm;
      String urn = MessageFormat.format(URN_TEST_PATTERN, System.nanoTime());
      pm = buildProductMetadata(urn, DATA_POLICY_TEST);
      Long id = metadaSrv.createProductMetadata(pm);
      Assert.assertNotNull(id);

      ProductMetadata pm2 = metadaSrv.getProductMetadataById(id);
      Assert.assertNotNull(pm2);
      Assert.assertEquals(urn, pm2.getUrn());
   }

   /**
    * Test delete product metadata for unknown URN.
    */
   @Test
   public void testDeleteProductMetadataForUnknownUrn() throws Exception {
      try {
         metadaSrv.deleteProductMetadataByURN("UnknownURN_1");
      } catch (EJBException e) {
         Assert.fail(e.getMessage());
      }
   }

   /**
    * Test delete product metadata for null URN.
    */
   @Test
   public void testDeleteProductMetadataForNullUrn() throws Exception {
      try {
         metadaSrv.deleteProductMetadataByURN(null);
         Assert.fail("Should raise an IllegalArgumentException");
      } catch (EJBException e) {
         logger.info(EXPECTED_EXCEPTION + e.toString());
      }
   }

   /**
    * Test delete product metadata for empty URN.
    */
   @Test
   public void testDeleteProductMetadataForEmptyUrn() throws Exception {
      try {
         metadaSrv.deleteProductMetadataByURN("UnknownURN");
      } catch (EJBException e) {
         Assert.fail(e.getMessage());
      }
   }

   /**
    * Test get product metadata for unknown URN.
    */
   @Test
   public void testDeleteProductMetadataForValidUrn() throws Exception {
      ProductMetadata pm;
      String urn = MessageFormat.format(URN_TEST_PATTERN, System.nanoTime());
      // Create a product metadata
      pm = buildProductMetadata(urn, DATA_POLICY_TEST);
      Long id = metadaSrv.createProductMetadata(pm);
      Assert.assertNotNull(id);

      // Delete it
      metadaSrv.deleteProductMetadataByURN(urn);
      pm = metadaSrv.getProductMetadataByUrn(urn);
      Assert.assertNull(pm);
   }

   /**
    * Test delete product metadata for unknown id.
    */
   @Test
   public void testDeleteProductMetadataForUnknownId() throws Exception {
      try {
         metadaSrv.deleteProductMetadata(-1L);
      } catch (EJBException e) {
         Assert.fail(e.getMessage());
      }
   }

   /**
    * Test delete product metadata for valid id.
    */
   @Test
   public void testDeleteProductMetadataForValidId() throws Exception {
      ProductMetadata pm;
      String urn = MessageFormat.format(URN_TEST_PATTERN, System.nanoTime());
      // Create a product metadata
      pm = buildProductMetadata(urn, DATA_POLICY_TEST);
      Long id = metadaSrv.createProductMetadata(pm);
      Assert.assertNotNull(id);

      // Delete it
      metadaSrv.deleteProductMetadata(id);
      pm = metadaSrv.getProductMetadataByUrn(urn);
      Assert.assertNull(pm);
   }

   /**
    * Test update product.
    */
   @Test
   public void testUpdateProduct() {
      ProductMetadata pm;
      String urn = MessageFormat.format(URN_TEST_PATTERN, System.nanoTime());
      // Create a product metadata
      pm = buildProductMetadata(urn, DATA_POLICY_TEST);
      Long id = metadaSrv.createProductMetadata(pm);
      Assert.assertNotNull(id);
      pm = metadaSrv.getProductMetadataByUrn(urn);

      // Update it
      String dataPolicy = "DP";
      int priority = 2;
      pm.setDataPolicy(dataPolicy);
      pm.setPriority(priority);

      metadaSrv.updateProductMetadata(pm);
      pm = metadaSrv.getProductMetadataByUrn(urn);
      Assert.assertNotNull(pm);

      Assert.assertEquals(dataPolicy, pm.getDataPolicy());
      Assert.assertEquals(priority, (int) pm.getPriority());
   }

   /**
    * Test get all pattern metadata mapping.
    */
   @Test
   public void testGetAllPatternMetadataMapping() {
      List<PatternMetadataMapping> lst = metadaSrv.getAllPatternMetadataMapping();
      Assert.assertNotNull(lst);
      Assert.assertFalse(lst.isEmpty());
      for (PatternMetadataMapping pmm : lst) {
         Assert.assertNotNull(pmm);
         Assert.assertNotNull(pmm.getCompiledPattern());
         Assert.assertNotNull(pmm.getProductMetadata());
      }
   }

   /**
    * Test get last stop gap metadata.
    */
   @Test
   public void testGetLastStopGapMetadata() {
      List<ProductMetadata> pms;
      ProductMetadata pm;
      Long id;
      Date date = DateTimeUtils.getUTCCalendar().getTime();
      String utcDate = DateTimeUtils.formatUTC(date);

      // check empty
      pms = metadaSrv.getLastStopGapMetadata(utcDate);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.isEmpty());

      // Add a normal product metadata
      pm = buildProductMetadata("NotAStopGap", DATA_POLICY_TEST);
      metadaSrv.createProductMetadata(pm);

      // check empty
      pms = metadaSrv.getLastStopGapMetadata(utcDate);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.isEmpty());

      // add a stop gap metadata
      id = metadaSrv.createStopGapMetadata("TTAAII", "test", 0);

      // check not empty
      pms = metadaSrv.getLastStopGapMetadata(utcDate);
      Assert.assertNotNull(pms);
      Assert.assertEquals(1, pms.size());
      Assert.assertEquals(id, pms.get(0).getId());

      // check with a new date
      try {
         Thread.sleep(2000);// waiting 2s
      } catch (InterruptedException e) {
         // Looser !
         e.printStackTrace();
      }
      Date date2 = DateTimeUtils.getUTCCalendar().getTime();
      String utcDate2 = DateTimeUtils.formatUTC(date2);

      // check empty
      pms = metadaSrv.getLastStopGapMetadata(utcDate2);
      Assert.assertNotNull(pms);
      Assert.assertTrue(pms.isEmpty());

      // add a stop gap metadata
      id = metadaSrv.createStopGapMetadata("TTAAII2", "test", 0);

      // check not empty
      pms = metadaSrv.getLastStopGapMetadata(utcDate2);
      Assert.assertNotNull(pms);
      Assert.assertEquals(1, pms.size());
      Assert.assertEquals(id, pms.get(0).getId());

      // check all
      pms = metadaSrv.getLastStopGapMetadata(null);
      Assert.assertNotNull(pms);
      Assert.assertEquals(2, pms.size());
   }
}
