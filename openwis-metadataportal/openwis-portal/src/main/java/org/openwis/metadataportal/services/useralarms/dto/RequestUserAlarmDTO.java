package org.openwis.metadataportal.services.useralarms.dto;

import org.openwis.dataservice.useralarms.UserAlarm;
import org.openwis.metadataportal.services.request.dto.follow.AdhocDTO;

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

   public RequestUserAlarmDTO(UserAlarm alarm, AdhocDTO processedRequest) {
      super(alarm);
      this.processedRequestId = processedRequest.getProcessedRequestDTO().getId();
      this.requestId = processedRequest.getRequestID();
      this.urn = processedRequest.getProductMetadataURN();
      this.extractMode = processedRequest.getExtractMode();
      this.downloadUrl = processedRequest.getProcessedRequestDTO().getUrl();
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
