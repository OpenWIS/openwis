/**
 * 
 */
package org.openwis.dataservice.utils.test;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.dataservice.common.hash.HashUtils;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class TestHashUtils {

   /** The Constant CHECK_STRING. */
   private static final String CHECK_STRING = "data-service";

   /** The Constant HASHED_CHECK_STRING. */
   private static final String HASHED_CHECK_STRING = "4295eeabce45a46ac3e22b6ec8d35096";

   /**
    * Description goes here.
    *
    * @throws Exception the exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      //
   }

   /**
    * Description goes here.
    *
    * @throws Exception the exception
    */
   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      //
   }

   /**
    * Description goes here.
    *
    * @throws Exception the exception
    */
   @Before
   public void setUp() throws Exception {
      //
   }

   /**
    * Description goes here.
    *
    * @throws Exception the exception
    */
   @After
   public void tearDown() throws Exception {
      //
   }

   /**
    * Test get m d5 digest.
    *
    * @throws NoSuchAlgorithmException the no such algorithm exception
    */
   @Test
   public void testGetMD5Digest() throws NoSuchAlgorithmException {
      String hash1 = HashUtils.getMD5Digest(CHECK_STRING);
      String hash2 = HashUtils.getTextMD5Digest(CHECK_STRING);
      Assert.assertEquals(hash1, hash2);
      Assert.assertEquals(HASHED_CHECK_STRING, hash1);
   }

   /**
    * Test first zero get m d5 digest.
    *
    * @throws NoSuchAlgorithmException the no such algorithm exception
    */
   @Test
   public void testFirstZeroGetMD5Digest() throws NoSuchAlgorithmException {
      Assert.assertEquals("08f8e0260c64418510cefb2b06eee5cd", HashUtils.getMD5Digest("bbb"));
   }

}
