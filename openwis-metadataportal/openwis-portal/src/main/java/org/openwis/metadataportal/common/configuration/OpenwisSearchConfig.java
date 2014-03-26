package org.openwis.metadataportal.common.configuration;

import java.util.ResourceBundle;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OpenwisSearchConfig {
   public static final String WEIGHT_TITLE = "weight.title";

   public static final String WEIGHT_ABSTRACT = "weight.abstract";

   public static final String WEIGHT_KEYWORDS = "weight.keywords";

   private static int titleWeight;

   private static int abstractWeight;

   private static int keywordsWeight;

   /**
    * The resource bundle.
    */
   private static ResourceBundle resourceBundle;

   static {
      resourceBundle = ResourceBundle.getBundle("openwis-search");
      titleWeight = getInt(WEIGHT_TITLE);
      abstractWeight = getInt(WEIGHT_ABSTRACT);
      keywordsWeight = getInt(WEIGHT_KEYWORDS);
   }

   /**
    * Default constructor.
    * Builds a OpenwisSearchConfig.
    */
   private OpenwisSearchConfig() {
      super();
   }

   public static int getInt(String key) {
      return Integer.parseInt(resourceBundle.getString(key));
   }

   /**
    * Returns the title weight.
    * @return title weight
    */
   public static int getTitleWeight() {
      return titleWeight;
   }

   public static void setTitleWeight(int titleWeight) {
      OpenwisSearchConfig.titleWeight = titleWeight;
   }

   public static int getAbstractWeight() {
      return abstractWeight;
   }

   public static void setAbstractWeight(int abstractWeight) {
      OpenwisSearchConfig.abstractWeight = abstractWeight;
   }

   public static int getKeywordsWeight() {
      return keywordsWeight;
   }

   public static void setKeywordsWeight(int keywordsWeight) {
      OpenwisSearchConfig.keywordsWeight = keywordsWeight;
   }

   public static void reset() {
      titleWeight = getInt(WEIGHT_TITLE);
      abstractWeight = getInt(WEIGHT_ABSTRACT);
      keywordsWeight = getInt(WEIGHT_KEYWORDS);

   }
}
