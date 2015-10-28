package org.openwis.dataservice.config;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class SystemPropertyFileResourceResoverTest {
   
   private SystemPropertyFileResourceResolver resolver = new SystemPropertyFileResourceResolver();

   @Test
   public void testRelativePaths() throws Exception {
      System.setProperty("testmapping/mapping1", "examplePropertyFile.properties");
      System.setProperty("testmapping/anotherMapping1", "properties/in/a/directory.properties");
      System.setProperty("java:funkyMapping.properties", "../tmp/morerelative.properties");
      
      URL url1 = resolver.resolveFromMappedName("testmapping/mapping1");
      URL url2 = resolver.resolveFromMappedName("testmapping/anotherMapping1");
      URL url3 = resolver.resolveFromMappedName("java:funkyMapping.properties");
      
      Assert.assertEquals(new File(FileUtils.getUserDirectory(), "examplePropertyFile.properties").toURI().toURL(), url1);
      Assert.assertEquals(new File(FileUtils.getUserDirectory(), "properties/in/a/directory.properties").toURI().toURL(), url2);
      Assert.assertEquals(new File(FileUtils.getUserDirectory(), "../tmp/morerelative.properties").toURI().toURL(), url3);
   }
   
   @Test
   public void testAbsolutePaths() throws Exception {
      System.setProperty("java:env/something", "/home/openwis/somewhere.properties");
      System.setProperty("java:global/property/file", "/properties/in/a/directory.properties");
      System.setProperty("foo/bar/baz", "/tmp/morerelative.properties");
      
      URL url1 = resolver.resolveFromMappedName("java:env/something");
      URL url2 = resolver.resolveFromMappedName("java:global/property/file");
      URL url3 = resolver.resolveFromMappedName("foo/bar/baz");
      
      Assert.assertEquals(new File("/home/openwis/somewhere.properties").toURI().toURL(), url1);
      Assert.assertEquals(new File("/properties/in/a/directory.properties").toURI().toURL(), url2);
      Assert.assertEquals(new File("/tmp/morerelative.properties").toURI().toURL(), url3);
   }
   
   @Test
   public void testNoSetting() throws Exception {
      URL url1 = resolver.resolveFromMappedName("missing1");
      URL url2 = resolver.resolveFromMappedName("java:property/is/not/set");
      URL url3 = resolver.resolveFromMappedName("java:global/name/missing");

      Assert.assertNull(url1);
      Assert.assertNull(url2);
      Assert.assertNull(url3);
   }
}
