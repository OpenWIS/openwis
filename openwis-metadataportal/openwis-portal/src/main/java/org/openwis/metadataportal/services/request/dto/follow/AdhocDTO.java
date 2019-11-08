/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.util.ISODate;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.Parameter;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.RequestResultStatus;
import org.openwis.dataservice.Value;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.common.utils.Utils;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;

import com.google.common.base.Joiner;

/**
 * The Follow my request DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class AdhocDTO extends AbstractRequestDTO {

   /**
    * The processed request.
    */
   private ProcessedRequestDTO processedRequestDTO;

   /**
    * Gets the processedRequestDTO.
    * @return the processedRequestDTO.
    */
   public ProcessedRequestDTO getProcessedRequestDTO() {
      return processedRequestDTO;
   }

   /**
    * Sets the processedRequestDTO.
    * @param processedRequestDTO the processedRequestDTO to set.
    */
   public void setProcessedRequestDTO(ProcessedRequestDTO processedRequestDTO) {
      this.processedRequestDTO = processedRequestDTO;
   }

   /**
    * The utility method to convert Adhoc objects to DTO.
    * @param adhocs the adhocs to convert.
    * @return the DTO for displaying the page.
    */
   public static List<AdhocDTO> adhocProcessedRequestsToDTO(List<ProcessedRequest> processedRequests) {
      List<AdhocDTO> dtos = new ArrayList<AdhocDTO>();
      for (ProcessedRequest pr : processedRequests) {
         dtos.add(adhocProcessedRequestToDTO(pr));
      }
      return dtos;
   }

   /**
    * The utility method to convert Adhoc objects to DTO.
    * @param adhocs the adhocs to convert.
    * @return the DTO for displaying the page.
    */
   @SuppressWarnings("unchecked")
   public static AdhocDTO adhocProcessedRequestToDTO(ProcessedRequest pr) {
      String stagingPostUrl = OpenwisMetadataPortalConfig
            .getString(ConfigurationConstants.URL_STAGING_POST);

      AdhocDTO dto = new AdhocDTO();

      //Simple attributes.
      dto.setProductMetadataTitle(pr.getRequest().getProductMetadata().getTitle());
      dto.setProductMetadataURN(pr.getRequest().getProductMetadata().getUrn());
      dto.setRequestID(Utils.formatRequestID(pr.getRequest().getId()));
      dto.setUserName(pr.getRequest().getUser());
      dto.setRequestType(pr.getRequest().getRequestType());
      dto.setDeployment(new DeploymentManager().getLocalDeployment());
      if(pr.getRequest().getExtractMode().equals(ExtractMode.GLOBAL)) {
         dto.setExtractMode("CACHE");
      } else {
         dto.setExtractMode(pr.getRequest().getProductMetadata().getLocalDataSource());
      }

      //SSP
      for (Parameter param : pr.getRequest().getParameters()) {
         Collection<String> values = CollectionUtils.collect(param.getValues(), new Transformer() {
            @Override
            public Object transform(Object input) {
               return ((Value) input).getValue();
            }
         });
         dto.getSsp().add(new SubSelectionParametersDTO(param.getCode(), values));
      }

      //Dissemination.
      if(pr.getRequest().getPrimaryDissemination() != null) {
         dto.setPrimaryDissemination(DisseminationDTO.disseminationToDTO(pr.getRequest().getPrimaryDissemination()));
      }
      
      if(pr.getRequest().getSecondaryDissemination() != null) {
         dto.setSecondaryDissemination(DisseminationDTO.disseminationToDTO(pr.getRequest().getSecondaryDissemination()));
      }

      //Processed request.
      dto.setProcessedRequestDTO(new ProcessedRequestDTO());
      dto.getProcessedRequestDTO().setId(pr.getId());
      if (pr.getCreationDate() != null) {
         dto.getProcessedRequestDTO().setCreationDate(
               new ISODate(pr.getCreationDate().toGregorianCalendar().getTimeInMillis()).toString() + "Z");
      }

      if (pr.getSubmittedDisseminationDate() != null) {
         dto.getProcessedRequestDTO().setSubmittedDisseminationDate(
               new ISODate(pr.getCreationDate().toGregorianCalendar().getTimeInMillis()).toString() + "Z");
      }

      if (pr.getCompletedDate() != null) {
         dto.getProcessedRequestDTO().setCompletedDate(
               new ISODate(pr.getCreationDate().toGregorianCalendar().getTimeInMillis()).toString() + "Z");
      }

      //Download URL + Size.
      if (StringUtils.isNotEmpty(pr.getUri())) {
         dto.getProcessedRequestDTO().setUrl(stagingPostUrl + pr.getUri());
      }
      dto.getProcessedRequestDTO().setSize(pr.getSize());

      //Status of the request
      if (pr.getRequestResultStatus() != null) {
         if (pr.getRequestResultStatus().equals(RequestResultStatus.FAILED)) {
            dto.getProcessedRequestDTO().setStatus(StatusDTO.FAILED);
         } else if (pr.getRequestResultStatus().equals(RequestResultStatus.DISSEMINATED)) {
            dto.getProcessedRequestDTO().setStatus(StatusDTO.COMPLETE);
         } else {
            dto.getProcessedRequestDTO().setStatus(StatusDTO.IN_PROGRESS);
         }
      }

      return dto;
   }
}
