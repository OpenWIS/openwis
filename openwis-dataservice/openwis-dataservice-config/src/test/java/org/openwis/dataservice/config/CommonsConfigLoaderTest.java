package org.openwis.dataservice.config;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

public class CommonsConfigLoaderTest {
   
   private CommonsConfigLoader configLoader = new CommonsConfigLoader();

   @Test
   public void testLoadConfig() throws Exception {
      Properties test1 = configLoader.loadConfig(getClass().getResource("/test1.properties"));
      
      Assert.assertEquals("This is a test property", test1.get("test.property"));
      Assert.assertEquals("Lists, should, not, be, supported", test1.get("test2"));
      Assert.assertEquals("But " + System.getenv("HOME") + " placeholders should be", test1.get("test3.value"));
   }
}
