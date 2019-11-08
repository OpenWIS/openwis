/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.exec;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.common.IMonitorable;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvesterThreadPoolExecutor extends ThreadPoolExecutor {

   private Map<Integer, Runnable> exec = new HashMap<Integer, Runnable>();

   public HarvesterThreadPoolExecutor(int poolSize) {
      super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
   }

   /**
    * Returns <code>true</code> if a harvesting task is currently running, <code>false</code> otherwise.
    * @param harvestingTask the harvesting task.
    * @return <code>true</code> if a harvesting task is currently running, <code>false</code> otherwise.
    */
   public boolean isRunning(Integer id) {
      return exec.containsKey(id);
   }

   /**
    * Monitor the harvester.
    * @param id the id of the harvesting task.
    * @return a monitorable object.
    */
   public IMonitorable monitor(Integer id) {
      Runnable r = exec.get(id);
      HarvesterRunnable harvester = (HarvesterRunnable) r;
      return harvester.monitor();
   }
   
   /**
    * Get the list of HarvesterRunnable currently running.
    */
   public Collection<HarvesterRunnable> getRunningTasks() {
      ArrayList<HarvesterRunnable> harvesterRunnables = new ArrayList<HarvesterRunnable>();
      for (Runnable r : exec.values()) {
         HarvesterRunnable harvester = (HarvesterRunnable) r;
         harvesterRunnables.add(harvester);
      }
      return harvesterRunnables;
   }

   /**
    * {@inheritDoc}
    * @see java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread, java.lang.Runnable)
    */
   @Override
   protected void beforeExecute(Thread t, Runnable r) {
      super.beforeExecute(t, r);

      //Add to the running queue.
      HarvesterRunnable harvester = (HarvesterRunnable) r;
      
      Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
            "Adding harvesting task {0} to the Harvesting ThreadPoolExecutor.", harvester.getHarvestingTask().getId()));

      exec.put(harvester.getHarvestingTask().getId(), r);
   }

   /**
    * {@inheritDoc}
    * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
    */
   @Override
   protected void afterExecute(Runnable r, Throwable t) {
      super.afterExecute(r, t);
      
      //Remove of the running queue.
      HarvesterRunnable harvester = (HarvesterRunnable) r;
      Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
            "Removing harvesting task {0} from the Harvesting ThreadPoolExecutor.", harvester.getHarvestingTask().getId()));

      exec.remove(harvester.getHarvestingTask().getId());
   }
}
