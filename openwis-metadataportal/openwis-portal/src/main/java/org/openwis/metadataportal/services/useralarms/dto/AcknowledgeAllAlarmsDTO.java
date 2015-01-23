package org.openwis.metadataportal.services.useralarms.dto;

public class AcknowledgeAllAlarmsDTO {
   private boolean subscription;

   public boolean isSubscription() {
      return subscription;
   }

   public void setSubscription(boolean subscription) {
      this.subscription = subscription;
   }
}
