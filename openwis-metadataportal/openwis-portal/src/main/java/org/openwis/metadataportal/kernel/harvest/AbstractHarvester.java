/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.openwis.metadataportal.kernel.common.IMonitorable;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public abstract class AbstractHarvester implements IMonitorable {

   private ServiceContext context;

   private Dbms dbms;

   /**
    * Default constructor.
    * Builds a AbstractHarvester.
    * @param context
    */
   public AbstractHarvester(ServiceContext context, Dbms dbms) {
      super();
      this.context = context;
      this.dbms = dbms;
   }

   /**
    * Gets the context.
    * @return the context.
    */
   public ServiceContext getContext() {
      return context;
   }

   /**
    * Gets the dbms.
    * @return the dbms.
    */
   public Dbms getDbms() {
      return dbms;
   }

   /**
    * Description goes here.
    * @param task
    * @return
    * @throws Exception 
    */
   public abstract MetadataAlignerResult harvest(HarvestingTask task) throws Exception;

}
