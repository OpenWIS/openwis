package org.openwis.dataservice.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Tests for {@link DisseminationUtils}
 */
public class DisseminationUtilsTest {

   @Test
   public void testParseFileName() {
      assertNull(DisseminationUtils.parseFileName(null));
      assertEquals("", DisseminationUtils.parseFileName(""));
      assertEquals("test.jpg", DisseminationUtils.parseFileName("test.jpg"));
      assertEquals("test#.jpg", DisseminationUtils.parseFileName("test#.jpg"));
      assertEquals("test#test.jpg", DisseminationUtils.parseFileName("test#test.jpg"));
      assertEquals("test#", DisseminationUtils.parseFileName("test#"));
      assertEquals("test##", DisseminationUtils.parseFileName("test##"));
      assertEquals("test##.jpg", DisseminationUtils.parseFileName("test##.jpg"));
      assertEquals("test#test#test.jpg", DisseminationUtils.parseFileName("test#test#test.jpg"));
      SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
      format.setTimeZone(TimeZone.getTimeZone("UTC"));
      assertEquals("test" + format.format(new Date()) + "test.jpg",
            DisseminationUtils.parseFileName("test#yyyy.MM.dd HH:mm#test.jpg"));
   }

}
