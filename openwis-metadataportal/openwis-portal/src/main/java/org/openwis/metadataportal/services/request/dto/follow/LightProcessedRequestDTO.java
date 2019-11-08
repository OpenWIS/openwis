package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.List;

import org.fao.geonet.util.ISODate;

/**
 * <p>Java class for lightProcessedRequestDTO complex type.
 */

public class LightProcessedRequestDTO extends org.openwis.dataservice.LightProcessedRequestDTO {

   /**
    * This method takes a list of subscriptions and returns a list of DTO objects.
    * @param subscriptions the subscriptions.
    * @return a list of DTO objects.
    */
   public static List<LightProcessedRequestDTO> toUTC(List<org.openwis.dataservice.LightProcessedRequestDTO> lightProcessedRequestDTOList) {
      List<LightProcessedRequestDTO> dtos = new ArrayList<LightProcessedRequestDTO>();
      for (org.openwis.dataservice.LightProcessedRequestDTO lightProcessedRequestDTO : lightProcessedRequestDTOList) {
         LightProcessedRequestDTO dto = new LightProcessedRequestDTO();
         dto.setCompletedDate(lightProcessedRequestDTO.getCompletedDate());
         dto.setCreationDate(lightProcessedRequestDTO.getCreationDate());
         dto.setMessage(lightProcessedRequestDTO.getMessage());
         dto.setRequestResultStatus(lightProcessedRequestDTO.getRequestResultStatus());
         dto.setSize(lightProcessedRequestDTO.getSize());
         dto.setSubmittedDisseminationDate(lightProcessedRequestDTO.getSubmittedDisseminationDate());
         dto.setUri(lightProcessedRequestDTO.getUri());
         dto.setId(lightProcessedRequestDTO.getId());
         dtos.add(dto);
      }
      return dtos;
   }

    /**
     * Gets the completedDate as UTC String.
     * @return the completedDate.
     */
    public String getCompletedDateUtc() {
       String completedDateUtc = null;
       if (getCompletedDate() != null)
       {
          completedDateUtc = new ISODate(getCompletedDate().toGregorianCalendar()
                .getTimeInMillis()).toString()
                + "Z";
       }
       return completedDateUtc;
    }

    /**
     * Gets the creationDate as UTC String.
     * @return the creationDate.
     */
    public String getCreationDateUtc() {
       String creationDateUtc = null;
       if (getCreationDate() != null)
       {
          creationDateUtc = new ISODate(getCreationDate().toGregorianCalendar()
                .getTimeInMillis()).toString()
                + "Z";
       }
       return creationDateUtc;
    }

    /**
     * Gets the submittedDisseminationDate as UTC String.
     * @return the submittedDisseminationDate.
     */
    public String getSubmittedDisseminationDateUtc() {      
       String submittedDisseminationDate = null;
       if (getSubmittedDisseminationDate() != null)
       {
          submittedDisseminationDate = new ISODate(getSubmittedDisseminationDate().toGregorianCalendar()
                .getTimeInMillis()).toString()
                + "Z";
       }
       return submittedDisseminationDate;
    }

}
