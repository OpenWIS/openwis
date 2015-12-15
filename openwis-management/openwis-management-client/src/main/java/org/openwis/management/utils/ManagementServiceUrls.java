package org.openwis.management.utils;

/**
 * Provides the WSDL Urls the management services are bound to.
 */
public interface ManagementServiceUrls {

   public String getAlertServiceWsdl();
   
   public String getControlServiceWsdl();
   
   public String getDisseminatedDataStatisticsWsdl();
   
   public String getExchangedDataStatisticsWsdl();
   
   public String getReplicatedDataStatisticsWsdl();
   
   public String getIgestedDataStatisticsWsdl();
}
