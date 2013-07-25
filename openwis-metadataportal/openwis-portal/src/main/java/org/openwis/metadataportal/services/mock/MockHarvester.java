/**
 * 
 */
package org.openwis.metadataportal.services.mock;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MockHarvester extends AbstractHarvester {

   /**
    * Default constructor.
    * Builds a MockHarvester.
    * @param context
    * @param dbms
    */
   public MockHarvester(ServiceContext context, Dbms dbms) {
      super(context, dbms);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.common.IMonitorable#getProcessed()
    */
   @Override
   public int getProcessed() {
      return 0;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.common.IMonitorable#getTotal()
    */
   @Override
   public int getTotal() {
      return 0;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.harvest.AbstractHarvester#harvest(org.openwis.metadataportal.model.harvest.HarvestingTask)
    */
   @Override
   public MetadataAlignerResult harvest(HarvestingTask task) throws Exception {
      System.out.println("==> Beginning Mock Harvester:" + task.getId());
      
      Thread.sleep(45000);
      
      System.out.println("==> End Mock Harvester:" + task.getId());
      
      return null;
   }

}
