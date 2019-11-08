/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.subselectionparameters;

import java.util.List;

import org.openwis.harness.subselectionparameters.GeoConfig;
import org.openwis.harness.subselectionparameters.GeoExtentType;

/**
 * A DTO for the parameter line. <P>
 */
public class ParameterDTO {

   /**
    * The selection type.
    */
   private String selectionType;

   /**
    * The type (specific to selection type).
    */
   private String type;

   /**
    * A list of values.
    */
   private List<ValueDTO> values;

   /**
    * The code.
    */
   private String code;

   /**
    * The label.
    */
   private String label;

   /**
    * The code for the next parameter.
    */
   private String nextParameter;

   /**
    * The geographical selection configuration.
    */
   private GeoConfig geoConfig;

   /**
    * The geographical default extent in WKT format.
    */
   private String geoWKTSelection;

   /**
    * The geographical max extent in WKT format.
    */
   private String geoWKTMaxExtent;

   /**
    * The geographical extent type.
    */
   private GeoExtentType geoExtentType;

   /**
    * The min extent for the period.
    */
   private String periodMinExtent;

   /**
    * The max extent for the period.
    */
   private String periodMaxExtent;

   /**
    * The Date or Time default min value.
    */
   private String from;

   /**
    * The Date or Time default max value.
    */
   private String to;

   /**
    * The excludes dates.
    */
   private List<String> excludedDates;

   /**
    * The date.
    */
   private String date;

   /**
    * Gets the type.
    * @return the type.
    */
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * @param type the type to set.
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * Gets the selectionType.
    * @return the selectionType.
    */
   public String getSelectionType() {
      return selectionType;
   }

   /**
    * Sets the selectionType.
    * @param selectionType the selectionType to set.
    */
   public void setSelectionType(String selectionType) {
      this.selectionType = selectionType;
   }

   /**
    * Gets the values.
    * @return the values.
    */
   public List<ValueDTO> getValues() {
      return values;
   }

   /**
    * Sets the values.
    * @param values the values to set.
    */
   public void setValues(List<ValueDTO> values) {
      this.values = values;
   }

   /**
    * Gets the code.
    * @return the code.
    */
   public String getCode() {
      return code;
   }

   /**
    * Sets the code.
    * @param code the code to set.
    */
   public void setCode(String code) {
      this.code = code;
   }

   /**
    * Gets the label.
    * @return the label.
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the label.
    * @param label the label to set.
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * Gets the nextParameter.
    * @return the nextParameter.
    */
   public String getNextParameter() {
      return nextParameter;
   }

   /**
    * Sets the nextParameter.
    * @param nextParameter the nextParameter to set.
    */
   public void setNextParameter(String nextParameter) {
      this.nextParameter = nextParameter;
   }

   /**
    * Gets the geoConfig.
    * @return the geoConfig.
    */
   public GeoConfig getGeoConfig() {
      return geoConfig;
   }

   /**
    * Sets the geoConfig.
    * @param geoConfig the geoConfig to set.
    */
   public void setGeoConfig(GeoConfig geoConfig) {
      this.geoConfig = geoConfig;
   }

   /**
    * Gets the geoWKTSelection.
    * @return the geoWKTSelection.
    */
   public String getGeoWKTSelection() {
      return geoWKTSelection;
   }

   /**
    * Sets the geoWKTSelection.
    * @param geoWKTSelection the geoWKTSelection to set.
    */
   public void setGeoWKTSelection(String geoWKTSelection) {
      this.geoWKTSelection = geoWKTSelection;
   }

   /**
    * Gets the geoWKTMaxExtent.
    * @return the geoWKTMaxExtent.
    */
   public String getGeoWKTMaxExtent() {
      return geoWKTMaxExtent;
   }

   /**
    * Sets the geoWKTMaxExtent.
    * @param geoWKTMaxExtent the geoWKTMaxExtent to set.
    */
   public void setGeoWKTMaxExtent(String geoWKTMaxExtent) {
      this.geoWKTMaxExtent = geoWKTMaxExtent;
   }

   /**
    * Gets the geoExtentType.
    * @return the geoExtentType.
    */
   public GeoExtentType getGeoExtentType() {
      return geoExtentType;
   }

   /**
    * Sets the geoExtentType.
    * @param geoExtentType the geoExtentType to set.
    */
   public void setGeoExtentType(GeoExtentType geoExtentType) {
      this.geoExtentType = geoExtentType;
   }

   /**
    * Gets the periodMinExtent.
    * @return the periodMinExtent.
    */
   public String getPeriodMinExtent() {
      return periodMinExtent;
   }

   /**
    * Sets the periodMinExtent.
    * @param periodMinExtent the periodMinExtent to set.
    */
   public void setPeriodMinExtent(String periodMinExtent) {
      this.periodMinExtent = periodMinExtent;
   }

   /**
    * Gets the periodMaxExtent.
    * @return the periodMaxExtent.
    */
   public String getPeriodMaxExtent() {
      return periodMaxExtent;
   }

   /**
    * Sets the periodMaxExtent.
    * @param periodMaxExtent the periodMaxExtent to set.
    */
   public void setPeriodMaxExtent(String periodMaxExtent) {
      this.periodMaxExtent = periodMaxExtent;
   }

   /**
    * Gets the from.
    * @return the from.
    */
   public String getFrom() {
      return from;
   }

   /**
    * Sets the from.
    * @param from the from to set.
    */
   public void setFrom(String from) {
      this.from = from;
   }

   /**
    * Gets the to.
    * @return the to.
    */
   public String getTo() {
      return to;
   }

   /**
    * Sets the to.
    * @param to the to to set.
    */
   public void setTo(String to) {
      this.to = to;
   }

   /**
    * Gets the excludedDates.
    * @return the excludedDates.
    */
   public List<String> getExcludedDates() {
      return excludedDates;
   }

   /**
    * Sets the excludedDates.
    * @param excludedDates the excludedDates to set.
    */
   public void setExcludedDates(List<String> excludedDates) {
      this.excludedDates = excludedDates;
   }

   /**
    * Gets the date.
    * @return the date.
    */
   public String getDate() {
      return date;
   }

   /**
    * Sets the date.
    * @param date the date to set.
    */
   public void setDate(String date) {
      this.date = date;
   }

}
