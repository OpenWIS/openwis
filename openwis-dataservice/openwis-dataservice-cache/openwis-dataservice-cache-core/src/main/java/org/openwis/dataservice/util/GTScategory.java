package org.openwis.dataservice.util;

import java.util.regex.Pattern;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.ConfigServiceFacade;

public enum GTScategory {
   GLOBAL, LOCAL;

   public static Pattern essentialPattern;

   public static Pattern additionalPattern;

   public static String GLOBAL_EXCHANGE = "GlobalExchange";
   
   /**
    * Precompile the regular expressions.
    */
   private static synchronized void precompileIfNecessary() {
      if ((essentialPattern == null) || (additionalPattern == null)) {
         ConfigServiceFacade configService = ConfigServiceFacade.getInstance();
         
         String essentialRegexp = configService.getString(ConfigurationInfo.GTS_CATEGORY_ESSENTIAL_REGEXP);
         if (essentialRegexp == null) {
            // default pattern
            essentialRegexp = "WMO\\s*Essential";
         }
         String additionalRegexp = configService
               .getString(ConfigurationInfo.GTS_CATEGORY_ADDITIONAL_REGEXP);
         if (additionalRegexp == null) {
            // default pattern
            additionalRegexp = "WMO\\s*Additional";
         }
   
         essentialPattern = Pattern.compile(essentialRegexp);
         additionalPattern = Pattern.compile(additionalRegexp);
      }
   }

   public static GTScategory getGTSCategoryFromString(String categoryString){		
      precompileIfNecessary();
      
	   return  ((essentialPattern.matcher(categoryString).matches() 
	         || additionalPattern.matcher(categoryString).matches() 
	         || GLOBAL_EXCHANGE.equalsIgnoreCase(categoryString)) ? GLOBAL : LOCAL);
	}
}