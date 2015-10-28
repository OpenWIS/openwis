package org.openwis.dataservice.cache;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.extraction.ExtractFromCache;
import org.openwis.datasource.server.ArquillianDBTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtractFromCacheTestCase. <P>
 * Explanation goes here. <P>
 */
@RunWith(Arquillian.class)
@Ignore
public class ExtractFromCacheTestCase extends ArquillianDBTestCase {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ExtractFromCacheTestCase.class);

   /** The extract from cache instance. */
   @EJB
   private ExtractFromCache efc;

   /** The cache index. */
   @EJB
   private CacheIndex cacheIndex;

   /** The metadata service. */
   @EJB
   private ProductMetadataService metadataSrv;

   /** The created files. */
   private final Set<File> createdFiles;

   /**
    * Instantiates a new extract from cache test case.
    */
   public ExtractFromCacheTestCase() {
      super();
      createdFiles = new HashSet<File>();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.datasource.server.ArquillianDBTestCase#getCachedIndex()
    */
   @Override
   protected CacheIndex getCachedIndex() {
      return cacheIndex;
   }

   /**
    * Clear files.
    */
   @After
   public void clearFiles() {
      logger.info("Clear created files: {}", createdFiles);
      for (File file : createdFiles) {
         try {
            if (file.delete()) {
               logger.debug("File {} deleted", file);
            }
         } catch (Exception e) {
            logger.warn("Could not delete the file {}", file);
         }
      }
   }

   /**
    * Test simple extraction.
    * Create a metadata and a product metadata
    * Try a simple extraction of with a time interval matching the product
    *
    * @throws Exception the exception
    */
   @Test
   public void testSimpleExtraction() throws Exception {
      String metadataId = "FVXX02EGRR";
      String urn = "urn:x-wmo:md:int.wmo.wis::" + metadataId;
      Calendar now = DateTimeUtils.getUTCCalendar();

      // Create a metadata
      ProductMetadata metadata = buildProductMetadata(urn, "dp-test");
      Long mdId = metadataSrv.createProductMetadata(metadata);

      // Create a product for this metadata
      createCachedProduct(urn, metadataId, now, mdId);

      // Extraction
      Value value = new Value();
      int hour = now.get(Calendar.HOUR_OF_DAY);
      if (hour == 23) {
         hour = 0;
      }
      value.setValue(MessageFormat.format("{0,number,00}:00Z/{1,number,00}:00Z", hour, (hour + 1)));

      Parameter param = new Parameter();
      param.setCode("aCode");
      param.setValues(Collections.singleton(value));

      List<Parameter> params = Collections.singletonList(param);
      Long prId = 1L;
      String uri = Long.toHexString(System.currentTimeMillis());
      MessageStatus extract = efc.extract("", urn, params, prId, uri, null);

      // Checks
      Assert.assertNotNull(extract);
      Assert.assertEquals(Status.EXTRACTED, extract.getStatus());
   }

   /**
    * Test simple extraction with no result.
    * Create a metadata and a product metadata
    * Try a simple extraction of with a time interval matching the product
    *
    * @throws Exception the exception
    */
   @Test
   public void testSimpleEmptyExtraction() throws Exception {
      String metadataId = "FVXX03EGRR";
      String urn = "urn:x-wmo:md:int.wmo.wis::" + metadataId;
      Calendar now = DateTimeUtils.getUTCCalendar();

      // Create a metadata
      ProductMetadata metadata = buildProductMetadata(urn, "dp-0");
      Long mdId = metadataSrv.createProductMetadata(metadata);

      // Create a product for this metadata
      createCachedProduct(urn, metadataId, now, mdId);

      // Extraction
      Value value = new Value();
      int hour = now.get(Calendar.HOUR_OF_DAY);
      if (hour >= 22) {
         hour = 0;
      }
      value.setValue(MessageFormat.format("{0,number,00}:00Z/{1,number,00}:00Z", (hour + 1),
            (hour + 2)));

      Parameter param = new Parameter();
      param.setCode("aCode");
      param.setValues(Collections.singleton(value));

      List<Parameter> params = Collections.singletonList(param);
      Long prId = 1L;
      String uri = File.createTempFile(metadataId, null).getAbsolutePath();
      MessageStatus extract = efc.extract("", urn, params, prId, uri, null);

      // Checks
      Assert.assertNotNull(extract);
      Assert.assertEquals(Status.NO_RESULT_FOUND, extract.getStatus());
   }

}
