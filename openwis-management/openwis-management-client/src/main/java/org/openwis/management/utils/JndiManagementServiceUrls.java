package org.openwis.management.utils;

public class JndiManagementServiceUrls implements ManagementServiceUrls {

   private static final String MANAGEMENT_ALERTSERVICE_WSDL = "openwis.management.alertservice.wsdl";
   private static final String MANAGEMENT_CONTROLSERVICE_WSDL = "openwis.management.controlservice.wsdl";
   private static final String MANAGEMENT_DISSEMINATEDDATA_STATISTICS_WSDL = "openwis.management.disseminateddatastatistics.wsdl";
   private static final String MANAGEMENT_EXCHANGEDDATA_STATISTICS_WSDL = "openwis.management.exchangeddatastatistics.wsdl";
   private static final String MANAGEMENT_REPLICATEDDATA_STATISTICS_WSDL = "openwis.management.replicateddatastatistics.wsdl";
   private static final String MANAGEMENT_INGESTEDDATA_STATISTICS_WSDL = "openwis.management.ingesteddatastatistics.wsdl";

   @Override
   public String getAlertServiceWsdl() {
      return JndiUtils.getString(MANAGEMENT_ALERTSERVICE_WSDL);
   }

   @Override
   public String getControlServiceWsdl() {
      return JndiUtils.getString(MANAGEMENT_CONTROLSERVICE_WSDL);
   }

   @Override
   public String getDisseminatedDataStatisticsWsdl() {
      return JndiUtils.getString(MANAGEMENT_DISSEMINATEDDATA_STATISTICS_WSDL);
   }

   @Override
   public String getExchangedDataStatisticsWsdl() {
      return JndiUtils.getString(MANAGEMENT_EXCHANGEDDATA_STATISTICS_WSDL);
   }

   @Override
   public String getReplicatedDataStatisticsWsdl() {
      return JndiUtils.getString(MANAGEMENT_REPLICATEDDATA_STATISTICS_WSDL);
   }

   @Override
   public String getIgestedDataStatisticsWsdl() {
      return JndiUtils.getString(MANAGEMENT_INGESTEDDATA_STATISTICS_WSDL);
   }
}
