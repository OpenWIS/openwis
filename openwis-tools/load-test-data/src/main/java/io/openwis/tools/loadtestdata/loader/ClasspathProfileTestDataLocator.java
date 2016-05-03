package io.openwis.tools.loadtestdata.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Loads resources from the classpath.
 */
public class ClasspathProfileTestDataLocator implements TestDataLocator {
   
   private static final Pattern METADATA_RESOURCE_PATTERN = Pattern.compile(".*\\.xml");
   
   private final String profile;

   public ClasspathProfileTestDataLocator(String profile) {
      super();
      this.profile = profile;
   }

   @Override
   public List<URL> findMetadata() {
      String packageName = String.format("testdata.%s.metadata", profile);
      
      System.err.println("Package: " + packageName);
      Set<String> metadataResources = new Reflections(ClasspathHelper.forPackage(packageName), 
            new FilterBuilder().includePackage(packageName),
            new ResourcesScanner()
      ).getResources(METADATA_RESOURCE_PATTERN);
      
      return new ArrayList<URL>(Lists.transform(new ArrayList<String>(metadataResources), new Function<String, URL>() {
         @Override
         public URL apply(String resourceName) {
            System.err.println("Found metadata: " + resourceName);
            return getClass().getResource("/" + resourceName);
         }
      }));
   }
}
