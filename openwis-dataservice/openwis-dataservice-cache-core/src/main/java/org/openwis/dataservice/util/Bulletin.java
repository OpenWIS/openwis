package org.openwis.dataservice.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Bulletin {

	// AHL
	private String dateTypeDesignator = ""; // TT
	private String geographicalTypeDesignator = ""; // AA
	private String levelDesignator = ""; // ii
	private String locationIndicator = ""; // CCCC
	private String referenceTime = ""; // ddhhmm
	private String amendments = null; // BBB

	private byte[] data = null;

	// originator from packed file
	private String originator = "";

	private String type = "";


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public Bulletin(){

	}

	public String getAHL(){
		StringBuilder stringBuilder = new StringBuilder(dateTypeDesignator);
		String ahl = stringBuilder.append(geographicalTypeDesignator).append(levelDesignator).append(locationIndicator).append(referenceTime).toString();
		if (amendments != null) ahl = ahl + amendments;
		return ahl;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/*
	 * Returns the WMOFNC filename that can be derived from the bulletins fields.
	 */
	public String getWMOFNCFileName(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		int day = Integer.valueOf(referenceTime.substring(0, 2));
		int hour = Integer.valueOf(referenceTime.substring(2, 4));
		int minute = Integer.valueOf(referenceTime.substring(4, 6));

		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		if (day > currentDay){
			int currentMonth = calendar.get(Calendar.MONTH);
			if (currentMonth == 0){
				calendar.set(Calendar.MONTH, 11);
				int currentYear = calendar.get(Calendar.YEAR);
				calendar.set(Calendar.YEAR, currentYear - 1);
			} else {
				calendar.set(Calendar.MONTH, currentMonth - 1);
			}
		}

		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);

		String dateString = dateFormat.format(calendar.getTime());

		// TODO should the checksum be part of the filename (see issues of SSD-32 in PMDB)
		StringBuilder stringBuilder = new StringBuilder("A_");
		return stringBuilder.append(getAHL()).append("_C_").append(originator).append("_").append(dateString).append(".").append(type).toString();
	}

	public String getMetadataURN(){
		return "urn:x-wmo:md:int.wmo.wis::".concat(String.format("%s%s%s%s",getDateTypeDesignator(), getGeographicalTypeDesignator(), getLevelDesignator(), getLocationIndicator()));
	}
}