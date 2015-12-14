package org.openwis.dataservice.util;

import java.util.regex.Pattern;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.JndiUtils;

public enum GTScategory {
	GLOBAL,LOCAL;
	
	public static Pattern essentialPattern;
	public static Pattern additionalPattern;
//	static {
//	   String essentialRegexp = JndiUtils.getString(ConfigurationInfo.GTS_CATEGORY_ESSENTIAL_REGEXP);
//	   if (essentialRegexp == null) {
//	      // default pattern
//	      essentialRegexp = "WMO\\s*Essential";
//	   }
//      String additionalRegexp = JndiUtils.getString(ConfigurationInfo.GTS_CATEGORY_ADDITIONAL_REGEXP);
//      if (additionalRegexp == null) {
//         // default pattern
//         additionalRegexp = "WMO\\s*Additional";
//      }
//      
//      essentialPattern = Pattern.compile(essentialRegexp);
//	   additionalPattern = Pattern.compile(additionalRegexp);
//	}
//	
	
	public static GTScategory getGTSCategoryFromString(String categoryString){
	   if ((essentialPattern == null) || (additionalPattern == null)) {
	      precompilePatterns();
	   }
	   
	   return  ((essentialPattern.matcher(categoryString).matches() || additionalPattern.matcher(categoryString).matches()) ? GLOBAL : LOCAL);
	}
	
	private static void precompilePatterns() {
      String essentialRegexp = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.GTS_CATEGORY_ESSENTIAL_REGEXP);
      if (essentialRegexp == null) {
         // default pattern
         essentialRegexp = "WMO\\s*Essential";
      }
      String additionalRegexp = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.GTS_CATEGORY_ADDITIONAL_REGEXP);
      if (additionalRegexp == null) {
         // default pattern
         additionalRegexp = "WMO\\s*Additional";
      }
      
      essentialPattern = Pattern.compile(essentialRegexp);
      additionalPattern = Pattern.compile(additionalRegexp);	   
	}
}