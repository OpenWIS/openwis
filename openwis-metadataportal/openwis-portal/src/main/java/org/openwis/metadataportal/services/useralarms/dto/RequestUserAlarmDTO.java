package org.openwis.metadataportal.services.useralarms.dto;

import org.apache.commons.lang.StringUtils;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.UserAlarm;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

/**
 * A user alarm DTO which contains all the information needed to refer to a request.
 *
 * @author lmika
 *
 */
public class RequestUserAlarmDTO extends UserAlarmDTO {
   private long processedRequestId;
   private String requestId;
   private String urn;
   private String extractMode;
   private String downloadUrl;

   public RequestUserAlarmDTO() {
   }

   public RequestUserAlarmDTO(UserAlarm alarm, ProcessedRequest processedRequest) {
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

      this.processedRequestId = processedRequest.getId();
      this.requestId = String.format("%07d", processedRequest.getRequest().getId().longValue());
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

   public long getProcessedRequestId() {
      return processedRequestId;
   }

   public void setProcessedRequestId(long processedRequestId) {
      this.processedRequestId = processedRequestId;
   }

   public String getRequestId() {
      return requestId;
   }

   public void setRequestId(String requestId) {
      this.requestId = requestId;
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
