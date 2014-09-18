/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.exec;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.common.IMonitorable;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvesterExecutorService {

   private static HarvesterExecutorService instance = new HarvesterExecutorService();

   private HarvesterThreadPoolExecutor pool = new HarvesterThreadPoolExecutor(20);

   private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(10);

   private Map<Integer, ScheduledFuture<?>> scheduledFutures = new HashMap<Integer, ScheduledFuture<?>>();

   /**
    * Default constructor.
    * Builds a HarvesterExecutorService.
    */
   private HarvesterExecutorService() {
      super();
   }

   /**
    * Returns <code>true</code> if the task is running.
    * @param task the task.
    * @return <code>true</code> if the task is running.
    */
   public boolean isRunning(Integer id) {
      return pool.isRunning(id);
   }

   /**
    * Returns <code>true</code> if the task is running.
    * @param task the task.
    * @return <code>true</code> if the task is running.
    */
   public IMonitorable monitor(Integer id) {
      return pool.monitor(id);
   }

   /**
    * Removes the thread from the scheduled list if any.
    * @param id the id of the task.
    * @throws Exception if an error occurs.
    */
   public void removeScheduledIfAny(Integer id) throws Exception {
      ScheduledFuture<?> future = scheduledFutures.get(id);
      if (future != null) {
         future.cancel(false);
         scheduledFutures.remove(id);
      }
   }

   /**
    * Run the given harvesting task.
    * @param task the harvesting task.
    * @param harvestingTaskManager 
    */
   public void run(HarvestingTask task, ServiceContext context, boolean runOnce) {
      if (!task.getRunMode().isRecurrent() || runOnce) {
         pool.execute(new HarvesterRunnable(task, context));
      } else {
         if (scheduledFutures.get(task.getId()) != null) {
            Log.warning(Geonet.HARVESTER_EXECUTOR, "The task " + task.getId()
                  + " has already been scheduled, cancelling the current one");
            scheduledFutures.get(task.getId()).cancel(true);
         }
         
         long initialDelay = 0;
         if (task.getRunMode().getStartingDate() != null){
            long startingDate;
            try {
               startingDate = DateTimeUtils.parse(task.getRunMode().getStartingDate()).getTime();
               long now = System.currentTimeMillis();
               
               if ( startingDate > now ){
                  initialDelay=startingDate-now;
               } else {
                  long delta  = now - startingDate;
                  long recurrenceMS = task.getRunMode().getRecurrencePeriod()*1000;
                  initialDelay = recurrenceMS - (delta % recurrenceMS);
               }
            } catch (ParseException e) {
               
            }
         }
         // the scheduler takes seconds as unit
         initialDelay = initialDelay / 1000; 
         
         Log.info(Geonet.HARVESTER_EXECUTOR, "The task " + task.getName() + " (" + task.getId()
               + ")" + " will be scheduled in " + initialDelay + " seconds");
         ScheduledFuture<?> scheduledFuture = scheduledPool.scheduleAtFixedRate(
               new HarvesterScheduledRunnable(pool, task, context), initialDelay, task.getRunMode()
                     .getRecurrencePeriod(), TimeUnit.SECONDS);
         scheduledFutures.put(task.getId(), scheduledFuture);
      }
   }
   
   /**
    * Get the ids of harvesting tasks that have been scheduled.
    */
   public Collection<Integer> getScheduledTaskIds() {
      return scheduledFutures.keySet();
   }
   
   /**
    * Get the list of HarvesterRunnable currently running.
    */
   public Collection<HarvesterRunnable> getRunningTasks() {
      return pool.getRunningTasks();
   }
   
   /**
    * Get the tasks in the execution pool.
    */
   public Collection<HarvesterRunnable> getExecutionPoolQueue() {
      Collection<HarvesterRunnable> tasksInPool = new ArrayList<HarvesterRunnable>();
      for (Runnable runnable : pool.getQueue()) {
         HarvesterRunnable harvesterRunnable = (HarvesterRunnable) runnable;
         tasksInPool.add(harvesterRunnable);
      }
      return tasksInPool;
   }
   
   /**
    * Shutdown the harvesting executor service.
    */
   public void shutdown() {
      pool.shutdownNow();
      scheduledPool.shutdownNow();
   }
   
   /**
    * Gets the unique instance of the executor service.
    * @return the unique instance of the executor service.
    */
   public static HarvesterExecutorService getInstance() {
      return instance;
   }

}
