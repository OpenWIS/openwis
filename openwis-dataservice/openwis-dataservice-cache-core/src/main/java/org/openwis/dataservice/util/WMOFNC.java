package org.openwis.dataservice.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//WMO-FNC
//pflag_productidentifier_oflag_originator_yyyymmddhhmmss[_freeformat].type[.compression]
//pflag = T --> productidentifier = TTAAii
//pflag = A --> productidentifier = TTAAiiCCCCddhhmm[BBB]
//pflag = W or Z --> ...
//oflag = C
//originator = OOOO
//freeformat ("-",",","A-Z","a-z","0-9")
//"," only in freeformat
//"-" omit
//type (met,tif,gif,png,ps,mpg,jpg,txt,htm,bin,doc,wpd)
//compression (Z,zip,gz,bz2)

/*
 * This class holds all the necessary information about a WMO FNC type file.
 */
public class WMOFNC {

	private String pflag = null;
	private String productidentifier = null;
		private String dateTypeDesignator = null; // TT
		private String geographicalTypeDesignator = null; // AA
		private String levelDesignator = null; // ii

		private String locationIndicator = null; // CCCC
		private String referenceTime = null; // ddhhmm
		private String amendments = null; // BBB
	private String oflag = null;
	private String originator = null;
	private String dateStamp = null;
	private String freeformat = null;
	private String type = null;
	private String compression = null;

	private static final String DATETIME_FMT = "yyyyMMddHHmmss";
	private static final int DATE_STAMP_LENGTH = 14;
	private static final String WMO_METADATA_URN_PREFIX = "urn:x-wmo:md:int.wmo.wis::";

	public WMOFNC(){
	}


	public String getPflag() {
		return pflag;
	}


	public void setPflag(String pflag) {
		this.pflag = pflag;
	}


	public String getProductidentifier() {
		return productidentifier;
	}


	public void setProductidentifier(String productidentifier) {
		this.productidentifier = productidentifier;
	}


	public String getDateTypeDesignator() {
		return dateTypeDesignator;
	}


	public void setDateTypeDesignator(String dateTypeDesignator) {
		this.dateTypeDesignator = dateTypeDesignator;
	}


	public String getGeographicalTypeDesignator() {
		return geographicalTypeDesignator;
	}


	public void setGeographicalTypeDesignator(String geographicalTypeDesignator) {
		this.geographicalTypeDesignator = geographicalTypeDesignator;
	}


	public String getLevelDesignator() {
		return levelDesignator;
	}


	public void setLevelDesignator(String levelDesignator) {
		this.levelDesignator = levelDesignator;
	}


	public String getLocationIndicator() {
		return locationIndicator;
	}


	public void setLocationIndicator(String locationIndicator) {
		this.locationIndicator = locationIndicator;
	}


	public String getReferenceTime() {
		return referenceTime;
	}


	public void setReferenceTime(String referenceTime) {
		this.referenceTime = referenceTime;
	}


	public String getAmendments() {
		return amendments;
	}


	public void setAmendments(String amendments) {
		this.amendments = amendments;
	}


	public String getOflag() {
		return oflag;
	}


	public void setOflag(String oflag) {
		this.oflag = oflag;
	}


	public String getOriginator() {
		return originator;
	}


	public void setOriginator(String originator) {
		this.originator = originator;
	}


	public String getDateStamp() {
		return dateStamp;
	}


	public void setDateStamp(String dateStamp) {
		this.dateStamp = dateStamp;
	}


	public String getFreeformat() {
		return freeformat;
	}


	public void setFreeformat(String freeformat) {
		this.freeformat = freeformat;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCompression() {
		return compression;
	}


	public void setCompression(String compression) {
		this.compression = compression;
	}

	public String getFileName() {
		char DOT = '.';
		char UNDERSCORE = '_';
		StringBuilder stringBuilder = new StringBuilder(pflag);
		String filename = stringBuilder.append(UNDERSCORE).append(productidentifier).append(UNDERSCORE).append(oflag).append(UNDERSCORE).append(originator).append(UNDERSCORE).append(dateStamp).toString();
		filename = filename + ((freeformat != null)? (UNDERSCORE + freeformat) : "") + DOT + type;
		filename = filename + ((compression != null)? (DOT + compression) : "");
		return filename;
	}

	@Override
	public String toString(){
		return getFileName();
	}

	public boolean hasAllMandatoryFields(){
		boolean mandatory = pflag != null && productidentifier != null && oflag != null && originator != null && dateStamp != null && type != null;

		if ("A".equals(pflag)) return mandatory && dateTypeDesignator != null && geographicalTypeDesignator != null && levelDesignator != null
			&& locationIndicator != null && referenceTime != null;

		if ("T".equals(pflag)) return mandatory && dateTypeDesignator != null && geographicalTypeDesignator != null && levelDesignator != null;
		return mandatory;
	}

	public String[] getMandatoryFields(){
		if ("A".equals(pflag)) return new String[] {pflag,dateTypeDesignator,geographicalTypeDesignator,levelDesignator,locationIndicator,referenceTime,oflag,originator,dateStamp,type};
		if ("T".equals(pflag)) return new String[] {pflag,dateTypeDesignator,geographicalTypeDesignator,levelDesignator,oflag,originator,dateStamp,type};
		return new String[] {pflag,productidentifier,oflag,originator,dateStamp,type};
	}

	public boolean hasNonMandatoryFields(){
		if ("A".equals(pflag)) return amendments != null || freeformat != null || compression != null;
		return freeformat != null || compression != null;
	}

	public final String[] getNonMandatoryFields() {
		if ("A".equals(pflag)) return new String[] {amendments,freeformat,compression};
		return new String[] {freeformat,compression};
	}

	public String getAHL(){
		if ("A".equals(pflag)){
			return dateTypeDesignator + geographicalTypeDesignator + levelDesignator + " " + locationIndicator + " " + referenceTime + ((amendments != null)? " " + amendments : "");
		}
		return null;
	}

	public Date getProductDate() {
		return parseDate(getDateStamp());
	}

	public static Date parseDate(final String value) {
	      // check for null
	      if (value == null) {
	         return null;
	      }

	      // check for length
	      String source = value.trim();
	      if (source.length() != DATE_STAMP_LENGTH){
	    	  return null;
	      }

	      SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FMT);
	      formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

	      if (source.indexOf('-') >= 0) {
	    	  source = handleUnspecifiedDate(source, formatter);
	      }

	      try {
	         Date date = formatter.parse(source);
	         return date;
	      }
	      catch (Exception e) {
	         return null;
	      }
	   }

	private static String handleUnspecifiedDate(String source, SimpleDateFormat formatter) {
		char[] sourceCharArray = source.toCharArray();
		String currentDateString = formatter.format(new Date(System.currentTimeMillis()));
	    char[] currentDateCharArray = currentDateString.toCharArray();

		for (int i = 0; i < DATE_STAMP_LENGTH; i++){
	    	char sourceChar = sourceCharArray[i];
	    	char currentDateChar = currentDateCharArray[i];

	    	if (sourceChar == '-'){
	    		sourceCharArray[i] = currentDateChar;
	    	}
	    }

		source = String.valueOf(sourceCharArray);

		Date now = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		int year = Integer.valueOf(source.substring(0, 4));
		int month = Integer.valueOf(source.substring(4, 6)) - 1;
		int day = Integer.valueOf(source.substring(6, 8));
		int hour = Integer.valueOf(source.substring(8, 10));
		int minute = Integer.valueOf(source.substring(10, 12));
		int second = Integer.valueOf(source.substring(12, source.length()));

		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentYear = calendar.get(Calendar.YEAR);
		if (day > currentDay && month >= currentMonth && year >= currentYear){
			if (currentMonth == 0){
				calendar.set(Calendar.MONTH, 11);
				calendar.set(Calendar.YEAR, currentYear - 1);
			} else {
				calendar.set(Calendar.MONTH, currentMonth - 1);
			}
		} else {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
		}

		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return formatter.format(calendar.getTime());
	}

	public String getMetadata() {
		if ("A".equals(pflag)) return String.format("%s%s%s%s",getDateTypeDesignator(), getGeographicalTypeDesignator(), getLevelDesignator(), getLocationIndicator());
		if ("T".equals(pflag)) return String.format("%s%s%s%s",getDateTypeDesignator(), getGeographicalTypeDesignator(), getLevelDesignator(), getOriginator());
		return "";
	}

	public String getMetadataURN() {
		return WMO_METADATA_URN_PREFIX.concat(getMetadata());
	}
}