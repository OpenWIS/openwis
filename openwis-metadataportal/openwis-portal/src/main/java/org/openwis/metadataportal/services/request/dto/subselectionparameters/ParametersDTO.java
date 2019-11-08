/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.subselectionparameters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openwis.harness.subselectionparameters.AbstractParameter;
import org.openwis.harness.subselectionparameters.DatePeriodSelection;
import org.openwis.harness.subselectionparameters.DayPeriodSelection;
import org.openwis.harness.subselectionparameters.GeographicalAreaSelection;
import org.openwis.harness.subselectionparameters.MultipleSelection;
import org.openwis.harness.subselectionparameters.MultipleSelectionType;
import org.openwis.harness.subselectionparameters.Parameters;
import org.openwis.harness.subselectionparameters.SingleSelection;
import org.openwis.harness.subselectionparameters.SingleSelectionType;
import org.openwis.harness.subselectionparameters.SourceSelection;
import org.openwis.harness.subselectionparameters.TimePeriodSelection;
import org.openwis.harness.subselectionparameters.Value;

/**
 * A DTO to wrap all the sub selection parameters displayed during the request phase.
 */
public class ParametersDTO {

   /**
    * The code of the first parameter to display.
    */
   private String startParameter;

   /**
    * A list of parameters.
    */
   private List<ParameterDTO> parameters;

   /**
    * Converts a list of parameters to DTO objects.
    * @param parameters the parameters.
    * @return a list of DTO objects.
    */
   public static ParametersDTO parametersToDTO(Parameters parameters) {

      ParametersDTO paramsDTO = new ParametersDTO();
      if (parameters.getStartParameter() != null) {
         paramsDTO.setStartParameter(parameters.getStartParameter().getCode());
      }

      paramsDTO.setParameters(new ArrayList<ParameterDTO>());

      for (AbstractParameter ap : parameters.getParameters()) {
         paramsDTO.getParameters().add(abstractParameterToDTO(ap));
      }

      return paramsDTO;
   }

   /**
    * Convert from abstract parameter to Parameter DTO.
    * @param abstractParameter an abstract parameter.
    * @return a DTO object for this parameter.
    */
   private static ParameterDTO abstractParameterToDTO(AbstractParameter abstractParameter) {

      ParameterDTO paramDTO = new ParameterDTO();

      // Set type property
      String type = null;
      if (abstractParameter instanceof MultipleSelection) {
         MultipleSelectionType mst = ((MultipleSelection) abstractParameter).getType();
         if (mst != null) {
            type = mst.toString();
         }
      } else if (abstractParameter instanceof SingleSelection) {
         SingleSelectionType sst = ((SingleSelection) abstractParameter).getType();
         if (sst != null) {
            type = sst.toString();
         }
      }
      paramDTO.setType(type);

      // Set selectionType property
      paramDTO.setSelectionType(abstractParameter.getClass().getSimpleName());

      // Set nextParameter property
      if (abstractParameter.getNextParameter() != null) {
         paramDTO.setNextParameter(abstractParameter.getNextParameter().getCode());
      }

      // Set code property
      paramDTO.setCode(abstractParameter.getCode());

      // Set label property
      paramDTO.setLabel(abstractParameter.getLabel());

      // Set Values
      List<Value> values = null;
      if (abstractParameter instanceof MultipleSelection) {
         values = ((MultipleSelection) abstractParameter).getValues();
      } else if (abstractParameter instanceof SingleSelection) {
         values = ((SingleSelection) abstractParameter).getValues();
      } else if (abstractParameter instanceof SourceSelection) {
         values = ((SourceSelection) abstractParameter).getValues();
      }

      if (values != null) {
         paramDTO.setValues(new ArrayList<ValueDTO>());
         for (Value value : values) {
            paramDTO.getValues().add(valueToDTO(value));
         }
      }

      // Set geoConfig, defaultExtent, maxExtent for GeographicalAreaSelection
      if (abstractParameter instanceof GeographicalAreaSelection) {
         paramDTO.setGeoConfig(((GeographicalAreaSelection) abstractParameter).getGeoConfig());
         paramDTO.setGeoWKTSelection(((GeographicalAreaSelection) abstractParameter)
               .getGeoWKTSelection());
         paramDTO.setGeoWKTMaxExtent(((GeographicalAreaSelection) abstractParameter)
               .getGeoWKTMaxExtent());
         paramDTO.setGeoExtentType(((GeographicalAreaSelection) abstractParameter)
               .getGeoExtentType());
      }

      // Set properties for DatePeriodSelection
      if (abstractParameter instanceof DatePeriodSelection) {
         paramDTO
               .setPeriodMinExtent(((DatePeriodSelection) abstractParameter).getPeriodMinExtent());
         paramDTO
               .setPeriodMaxExtent(((DatePeriodSelection) abstractParameter).getPeriodMaxExtent());
         paramDTO.setFrom(((DatePeriodSelection) abstractParameter).getFrom());
         paramDTO.setTo(((DatePeriodSelection) abstractParameter).getTo());
         paramDTO.setExcludedDates(((DatePeriodSelection) abstractParameter).getExcludedDates());
      }

      // Set properties for DayPeriodSelection
      if (abstractParameter instanceof DayPeriodSelection) {
         paramDTO.setPeriodMinExtent(((DayPeriodSelection) abstractParameter).getPeriodMinExtent());
         paramDTO.setPeriodMaxExtent(((DayPeriodSelection) abstractParameter).getPeriodMaxExtent());
         paramDTO.setDate(((DayPeriodSelection) abstractParameter).getDate());
         paramDTO.setExcludedDates(((DayPeriodSelection) abstractParameter).getExcludedDates());
      }

      // Set properties for TimePeriodSelection
      if (abstractParameter instanceof TimePeriodSelection) {
         paramDTO
               .setPeriodMinExtent(((TimePeriodSelection) abstractParameter).getPeriodMinExtent());
         paramDTO
               .setPeriodMaxExtent(((TimePeriodSelection) abstractParameter).getPeriodMaxExtent());
         paramDTO.setFrom(((TimePeriodSelection) abstractParameter).getFrom());
         paramDTO.setTo(((TimePeriodSelection) abstractParameter).getTo());
      }

      return paramDTO;
   }

   /**
    * Converts a value to a DTO object.
    * @param value the value to convert.
    * @return the DTO object for this value.
    */
   private static ValueDTO valueToDTO(Value value) {

      ValueDTO valueDTO = new ValueDTO();

      // Set code property
      valueDTO.setCode(value.getCode());

      // Set id property
      valueDTO.setSelected(value.isSelected());

      // Set value property
      valueDTO.setValue(value.getValue());

      // Set availableFor property
      if (CollectionUtils.isNotEmpty(value.getAvailableFor())) {
         valueDTO.setAvailableFor(value.getAvailableFor());
      }

      return valueDTO;
   }

   /**
    * Gets the startParameter.
    * @return the startParameter.
    */
   public String getStartParameter() {
      return startParameter;
   }

   /**
    * Sets the startParameter.
    * @param startParameter the startParameter to set.
    */
   public void setStartParameter(String startParameter) {
      this.startParameter = startParameter;
   }

   /**
    * Gets the parameters.
    * @return the parameters.
    */
   public List<ParameterDTO> getParameters() {
      return parameters;
   }

   /**
    * Sets the parameters.
    * @param parameters the parameters to set.
    */
   public void setParameters(List<ParameterDTO> parameters) {
      this.parameters = parameters;
   }

}
