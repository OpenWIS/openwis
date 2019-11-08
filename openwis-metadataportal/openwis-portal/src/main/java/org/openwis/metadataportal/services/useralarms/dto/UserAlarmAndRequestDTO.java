package org.openwis.metadataportal.services.useralarms.dto;

import org.apache.commons.lang.StringUtils;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.UserAlarm;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

/**
 * A user alarm DTO which also contains select information about the request the alarm is for.
 *
 * @author lmika
 *
 */
public class UserAlarmAndRequestDTO extends UserAlarmDTO {
   private String urn;
   private String extractMode;
   private String downloadUrl;

   public UserAlarmAndRequestDTO() {
   }

   public UserAlarmAndRequestDTO(UserAlarm alarm, ProcessedRequest processedRequest) {
      super(alarm);
      if (processedRequest != null) {
         initialiseFromProcessedRequest(processedRequest);
      }
   }

   /**
    * Initialise the internal fields from the processed request.
    */
   private void initialiseFromProcessedRequest(ProcessedRequest processedRequest) {
      String stagingPostUrl = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.URL_STAGING_POST);

      this.urn = processedRequest.getRequest().getProductMetadata().getUrn();

      if(processedRequest.getRequest().getExtractMode().equals(ExtractMode.GLOBAL)) {
         this.extractMode = "CACHE";
      } else {
         this.extractMode = processedRequest.getRequest().getProductMetadata().getLocalDataSource();
      }

      if (StringUtils.isNotEmpty(processedRequest.getUri())) {
         this.downloadUrl = stagingPostUrl + processedRequest.getUri();
      } else {
         this.downloadUrl = "";
      }
   }

   public String getUrn() {
      return urn;
   }

   public void setUrn(String urn) {
      this.urn = urn;
   }

   public String getExtractMode() {
      return extractMode;
   }

   public void setExtractMode(String extractMode) {
      this.extractMode = extractMode;
   }

   public String getDownloadUrl() {
      return downloadUrl;
   }

   public void setDownloadUrl(String downloadUrl) {
      this.downloadUrl = downloadUrl;
   }
}
