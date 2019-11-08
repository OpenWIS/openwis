package org.openwis.metadataportal.services.catalog;

import org.openwis.management.monitoring.ExchangedDataStatistics;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * Helper to update the catalog statistics.
 */
public class CatalogStatUpdateHelper {

   public static final String CREATED = "Created";
   public static final String INSERTED = "Inserted";
   public static final String HARVESTED = "Harvested - ";
   public static final String SYNCHRONIZED = "Synchronized - ";

   /**
    * Update catalog stats on Create.
    */
   public static void updateStatOnCreate(int nbMd, int totalVolume) {
      updateStat(CREATED, nbMd, totalVolume);
   }

   /**
    * Update catalog stats on Insert.
    */
   public static void updateStatOnInsert(int nbMd, int totalVolume) {
      updateStat(INSERTED, nbMd, totalVolume);
   }

   /**
    * Update catalog stats on Insert.
    */
   public static void updateStatOnHarvesting(HarvestingTask harvestingTask, int nbMd, int totalVolume) {
      String source;
      if (harvestingTask.isSynchronizationTask()) {
         source = SYNCHRONIZED;
      } else {
         source = HARVESTED;
      }
      source += harvestingTask.getName();
      updateStat(source, nbMd, totalVolume);
   }

   private static void updateStat(String source, int nbMd, int totalVolume) {
      if (nbMd == 0) {
         return;
      }

      ExchangedDataStatistics statServ = ManagementServiceProvider.getExchangedDataStatistics();

      String date = DateTimeUtils.format(DateTimeUtils.getUTCDate());

      statServ.updateExchangedData(date, source, nbMd, totalVolume);
   }


}
