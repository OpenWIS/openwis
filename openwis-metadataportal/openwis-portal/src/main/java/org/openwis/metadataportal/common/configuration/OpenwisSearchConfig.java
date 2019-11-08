package org.openwis.metadataportal.common.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.collections.ListUtils;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OpenwisSearchConfig {
   public static final String WEIGHT_TITLE = "weight.title";

   public static final String WEIGHT_ABSTRACT = "weight.abstract";

   public static final String WEIGHT_KEYWORDS = "weight.keywords";
   
   public static final String STOPWORDS = "stopwords";

   private static int titleWeight;

   private static int abstractWeight;

   private static int keywordsWeight;

   private static List<String> stopWords;

   /**
    * The resource bundle.
    */
   private static ResourceBundle resourceBundle;

   static {
      resourceBundle = ResourceBundle.getBundle("openwis-search");
      titleWeight = getInt(WEIGHT_TITLE);
      abstractWeight = getInt(WEIGHT_ABSTRACT);
      keywordsWeight = getInt(WEIGHT_KEYWORDS);
      stopWords = Arrays.asList(resourceBundle.getString(STOPWORDS).split(","));
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
   
   public static boolean isInStopWords(String word) {
      return stopWords.contains(word.toLowerCase());
   }
}
