package org.openwis.factorytests.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;

public class MassMetadataCreation {

   private StringBuilder templateContent;

   private String xmlContent;

   private void loadTemplate() throws Exception {
      String file = getClass().getClassLoader().getResource("performance/md-template.xml")
            .getFile();
      BufferedReader reader = new BufferedReader(new FileReader(file));
      templateContent = new StringBuilder(3000);
      String line = reader.readLine();
      while (line != null) {
         templateContent.append(line + "\n");
         line = reader.readLine();
      }
   }

   private void setValue(String var, String value) {
      xmlContent = xmlContent.replace("${" + var + "}", value);
   }

   private String createUrn(int i) {
      return "urn:x-wmo:md:int.wmo.wis::" + i;
   }

   private void createTemplates(int nb) throws Exception {
      File outputDir = new File("results/md");
      outputDir.mkdirs();

      loadTemplate();
      for (int i = 0; i < nb; i++) {
         xmlContent = templateContent.toString();
         if (i % 100 == 0) {
            System.out.print(".");
         }
         setValue("urn", createUrn(i));
         setValue("author", "author_" + i);
         setValue("organisation", "organisation_" + i);
         setValue("title", "title_" + i);
         setValue("abstract", "abstract_" + i);
         int priority = i % 5;
         setValue("priority", "GTS Priority " + priority);
         FileWriter out = new FileWriter(new File(outputDir, "md-" + i + ".xml"));
         out.append(xmlContent);
         out.flush();
         out.close();
      }
   }

   /**
    * Description goes here.
    * @param args
    */
   public static void main(String[] args) {
      try {
         //System.out.println(System.getProperties());
         //new MassMetadataCreation().createTemplates(2);
         String regexp = "^urn:x-wmo:md:int.wmo.wis::\\D\\D\\D\\D\\d\\d\\D\\D\\D\\D$";
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::AWIO20FMEE"));
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::BCEU83LFRO"));
         
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis2::AWIO20FMEE"));
         System.out.println(Pattern.matches(regexp, "AWIO20FMEE"));
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::AWIO20FMEE__"));
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::AWIO20FMEE22"));
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::2WIO20FMEE"));
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::AWIO202MEE"));
         System.out.println(Pattern.matches(regexp, "urn:x-wmo:md:int.wmo.wis::AWIOA0FMEE"));
         System.out.println(Pattern.matches(regexp, "urnx-wmo:md:int.wmo.wis::AWIO20FMEE"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
