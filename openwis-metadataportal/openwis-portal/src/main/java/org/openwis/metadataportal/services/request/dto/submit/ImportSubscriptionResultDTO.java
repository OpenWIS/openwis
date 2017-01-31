package org.openwis.metadataportal.services.request.dto.submit;

public class ImportSubscriptionResultDTO {
   private boolean result;
   private String message;

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public boolean isResult() {
      return result;
   }

   public void setResult(boolean result) {
      this.result = result;
   }
}
