/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.SerialFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.harvest.exec.HarvesterExecutorService;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.metadataportal.model.harvest.HarvestingStatistics;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.harvest.HarvestingTaskRunMode;
import org.openwis.metadataportal.model.harvest.HarvestingTaskStatus;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.MetadataValidation;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvestingTaskManager extends AbstractManager {

   private DataManager dataManager;

   private static final String ERROR_SEPARATOR = "@@";

   private static final String INNER_ERROR_SEPARATOR = "##";

   private static final int ERROR_SIZE_LIMIT = 100;

   /** delay between each recurrent task to schedule at startup (in seconds) */
   private static final int SCHEDULE_DELAY_BETWEEN_TASKS = 30;

   /** initial delay before the first recurrent task scheduling at startup (in seconds) */
   private static final int INITIAL_DELAY_FOR_TASK_SCHEDULING = 30;
   
   /**
    * Default constructor.
    * Builds a HarvesterManager.
    * @param dbms
    */
   public HarvestingTaskManager(Dbms dbms) {
      super(dbms);
   }

   /**
    * Default constructor.
    * Builds a HarvesterManager.
    * @param dbms
    */
   public HarvestingTaskManager(Dbms dbms, DataManager dataManager) {
      super(dbms);
      this.dataManager = dataManager;
   }

   /**
    * Gets all harvesting tasks.
    * @return all harvesting tasks.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public SearchResultWrapper<HarvestingTask> getAllHarvestingTasks(
         SearchCriteriaWrapper<Boolean, String> searchCriteriaWrapper) throws Exception {
      SearchResultWrapper<HarvestingTask> wrapper = new SearchResultWrapper<HarvestingTask>();

      String isSynchro = BooleanUtils.toString(searchCriteriaWrapper.getCriteria(), "y", "n");

      //Count.
      String query = "SELECT COUNT(*) AS nb FROM HarvestingTask WHERE issynchronization = ?";
      List<Element> records = getDbms().select(query, isSynchro).getChildren();
      if (!records.isEmpty()) {
         wrapper.setTotal(Integer.parseInt(records.get(0).getChildText("nb")));
      }

      //Results.
      List<HarvestingTask> allTasks = new ArrayList<HarvestingTask>();
      StringBuffer sb = new StringBuffer();
      sb.append("SELECT HarvestingTask.*, HarvestingTaskResult.* ");
      sb.append("FROM HarvestingTask LEFT JOIN HarvestingTaskResult ON HarvestingTask.id = HarvestingTaskResult.harvestingTaskId ");
      sb.append("WHERE HarvestingTask.issynchronization = ? ");

      if (StringUtils.isNotBlank(searchCriteriaWrapper.getSort())
            && searchCriteriaWrapper.getDir() != null) {
         sb.append(" ORDER BY HarvestingTask.").append(searchCriteriaWrapper.getSort());
         sb.append(" ").append(searchCriteriaWrapper.getDir().toString());
      }

      if (searchCriteriaWrapper.getLimit() != null) {
         sb.append(" LIMIT ").append(searchCriteriaWrapper.getLimit());
      }

      if (searchCriteriaWrapper.getStart() != null) {
         sb.append(" OFFSET ").append(searchCriteriaWrapper.getStart());
      }

      records = getDbms().select(sb.toString(), isSynchro).getChildren();
      for (Element e : records) {
         allTasks.add(buildHarvestingTaskFromElement(e));
      }
      wrapper.setRows(allTasks);

      return wrapper;
   }

   /**
    * Attempts to delete a harvesting task.  First this method will attempt to reset the harvester, which includes removing
    * the metadata that was harvested from this task.  If the reset is successful, the harvester is remove and the method
    * returns <code>true</code>.  Otherwise, the method returns false.
    *
    * @param id
    *       The harvesting task id.
    * @returns
    *       <code>true</code> if the harvesting task was removed, <code>false</code> otherwise.
    * @throws Exception
    */
   public boolean deleteHarvestingTask(Integer id) throws Exception {
      boolean cleanSuccessful = resetHarvestingTask(id);

      if (! cleanSuccessful) {
         return false;
      }

      // Remove the operation allowed for this group.
      String queryTaskResult = "DELETE FROM HarvestingTaskResult WHERE harvestingTaskId=?";
      getDbms().execute(queryTaskResult, id);

      // Remove the groups descriptions.
      String queryConf = "DELETE FROM HarvestingTaskConfiguration WHERE harvestingTaskId=?";
      getDbms().execute(queryConf, id);

      // Remove the group.
      String queryTask = "DELETE FROM HarvestingTask WHERE id=?";
      getDbms().execute(queryTask, id);

      return true;
   }

   /**
    * Removes all metadata records associated with a harvesting or synchronisation task.
    *
    * @param id
    *       The harvesting/synchronisation task ID.
    * @return
    *       <code>true</code> if the clean was successful, <code>false</code> if some metadata records could not be removed.
    * @throws Exception
    */
   public boolean resetHarvestingTask(Integer id) throws Exception {
      boolean allMetadataRecordsRemoved = true;

      //Delete the associated metadata.
      List<Metadata> mds = new ArrayList<Metadata>(getAllMetadataByHarvestingTask(id));

      List<String> urns = Lists.transform(mds, new Function<Metadata, String>() {
         public String apply(Metadata from) {
            return from.getUrn();
         }
      });

      List<List<String>> mdPartitions = Lists.partition(urns, 100);
      for (List<String> partition : mdPartitions) {
         allMetadataRecordsRemoved = allMetadataRecordsRemoved && this.dataManager.deleteMetadataCollection(getDbms(), partition, false);
      }

      return allMetadataRecordsRemoved;
   }

   /**
    * Description goes here.
    * @param id
    * @param activate
    */
   public void setActivation(Integer id, HarvestingTaskStatus status) throws Exception {
      String queryActivate = "UPDATE HarvestingTask SET status = ? WHERE id=?";
      getDbms().execute(queryActivate, status.toString(), id);
   }

   /**
    * Description goes here.
    * @param id
    */
   public void updateLastRunDate(Integer id) throws Exception {
      String queryActivate = "UPDATE HarvestingTask SET lastRun = ? WHERE id=?";
      String date = getSdf().format(new Date(System.currentTimeMillis()));
      getDbms().execute(queryActivate, date, id);
   }

   /**
    * Description goes here.
    * @param id
    * @param result
    */
   public void updateHarvestingTaskResult(Integer id, MetadataAlignerResult result)
         throws Exception {
      String deleteOldResult = "DELETE FROM HarvestingTaskResult WHERE harvestingTaskId=?";
      getDbms().execute(deleteOldResult, id);

      StringBuffer insert = new StringBuffer();
      insert.append("INSERT INTO harvestingtaskresult(harvestingtaskresultid, dateresult, total, added, updated, unchanged, locallyremoved, unknownschema, fail, badformat, doesnotvalidate, ignored, unexpected, errors, harvestingtaskid) ");
      insert.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

      int resultId = SerialFactory.getSerial(getDbms(), "HarvestingTaskResult");
      String errors = null;
      // Just save the first ERROR_SIZE_LIMIT errors
      if (CollectionUtils.isNotEmpty(result.getErrors())) {
         if (result.getErrors().size() > ERROR_SIZE_LIMIT)
         {
            errors = Joiner.on(ERROR_SEPARATOR).join(result.getErrors().subList(0, ERROR_SIZE_LIMIT));
         }
         else
         {
            errors = Joiner.on(ERROR_SEPARATOR).join(result.getErrors());
         }
      }
      getDbms().execute(insert.toString(), resultId, getSdf().format(result.getDate()),
            result.getTotal(), result.getAdded(), result.getUpdated(), result.getUnchanged(),
            result.getLocallyRemoved(), result.getUnknownSchema(), BooleanUtils.toString(result.isFail(), "y", "n"),
            result.getBadFormat(), result.getDoesNotValidate(), result.getIgnored(), result.getUnexpected(), errors, id);
   }

   /**
    * Gets a harvesting task by its uuid.
    * @param uuid the uuid of the task.
    * @return a harvesting task by its uuid.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public HarvestingTask getHarvestingTaskByUuid(String uuid) throws Exception {
      //Count.
      String query = "SELECT * FROM HarvestingTask WHERE uuid = ?";
      List<Element> records = getDbms().select(query, uuid).getChildren();
      if (!records.isEmpty()) {
         return buildHarvestingTaskFromElement(records.get(0));
      }
      return null;
   }

   /**
    * Gets a harvesting task by its id.
    * @param id the id of the task.
    * @return a harvesting task by its id.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public HarvestingTask getHarvestingTaskById(Integer id, boolean loadConfiguration)
         throws Exception {
      //Count.
      String queryTask = "SELECT HarvestingTask.*, Categories.name AS categoryName FROM HarvestingTask, Categories WHERE HarvestingTask.categoryid = Categories.id AND HarvestingTask.id = ?";
      List<Element> recordTask = getDbms().select(queryTask, id).getChildren();
      HarvestingTask task = null;
      if (!recordTask.isEmpty()) {
         task = buildHarvestingTaskFromElement(recordTask.get(0));

         if (loadConfiguration) {
            String queryConfigTask = "SELECT * FROM HarvestingTaskConfiguration WHERE harvestingtaskid = ?";
            List<Element> recordsTaskConfig = getDbms().select(queryConfigTask, id).getChildren();
            for (Element e : recordsTaskConfig) {
               task.getConfiguration().put(e.getChildText("attr"), e.getChildText("val"));
            }
         }
      }

      return task;
   }
   
   /**
    * Find Harvesting tasks that reference this category
    * @param categoryId the category id
    * @return the list of harvesting tasks
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   public List<HarvestingTask> findHarvestingTaskByCategory(Integer categoryId) throws Exception {
      String queryTask = "SELECT id FROM HarvestingTask WHERE categoryid = ?";
      List<Element> records = getDbms().select(queryTask, categoryId).getChildren();
      ArrayList<HarvestingTask> tasks = new ArrayList<HarvestingTask>();
      for (Element e : records) {
         Integer harvestingTaskId = Integer.parseInt(e.getChildText("id"));
         HarvestingTask task = getHarvestingTaskById(harvestingTaskId, false);
         tasks.add(task);
      }
      return tasks;
   }

   /**
    * Creates an harvesting task.
    * @param task the task to create.
    * @return task id
    * @throws Exception if an error occurs.
    */
   public Integer createHarvestingTask(HarvestingTask task) throws Exception {
      task.setId(SerialFactory.getSerial(getDbms(), "HarvestingTask"));

      //Insert the harvesting task.
      StringBuffer sbHarvestingTask = new StringBuffer();
      sbHarvestingTask
            .append("INSERT INTO harvestingtask(id, uuid, name, harvestingtype, startingdate, validationmode, isrecurrent, recurrentperiod, lastrun, backup, status, issynchronization, isincremental, categoryid) ");
      sbHarvestingTask.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

      Integer recurrencePeriod = null;
      String backup = null;
      String lastRunDate = null;
     
      if (task.getRunMode().isRecurrent()) {
         recurrencePeriod = task.getRunMode().getRecurrencePeriod();
        
      }
      if (task.getBackup() != null) {
         backup = task.getBackup().getName();
      }
      if (task.getLastRun() != null) {
         lastRunDate = getSdf().format(task.getLastRun());
      }

      getDbms().execute(sbHarvestingTask.toString(), task.getId(), UUID.randomUUID().toString(),
            task.getName(), task.getType(),task.getRunMode().getStartingDate(), task.getValidationMode().toString(),
            BooleanUtils.toString(task.getRunMode().isRecurrent(), "y", "n"), recurrencePeriod,
            lastRunDate, backup, task.getStatus().toString(),
            BooleanUtils.toString(task.isSynchronizationTask(), "y", "n"),
            BooleanUtils.toString(task.isIncremental(), "y", "n"), task.getCategory().getId());

      //Insert the harvesting configuration.
      StringBuffer sbHarvestingTaskConf = new StringBuffer();
      sbHarvestingTaskConf
            .append("INSERT INTO harvestingtaskconfiguration(configurationid, attr, val, harvestingtaskid) ");
      sbHarvestingTaskConf.append("VALUES (?, ?, ?, ?);");
      for (String confKey : task.getConfiguration().keySet()) {
         int id = SerialFactory.getSerial(getDbms(), "HarvestingTaskConfiguration");
         getDbms().execute(sbHarvestingTaskConf.toString(), id, confKey,
               task.getConfiguration().get(confKey), task.getId());
      }
      
      return task.getId();
   }

   /**
    * Description goes here.
    * @param task
    * @throws Exception 
    */
   public void updateHarvestingTask(HarvestingTask task) throws Exception {
      //-- Update Task.
      StringBuffer queryUpdateTask = new StringBuffer();
      queryUpdateTask.append("UPDATE harvestingtask SET ");
      queryUpdateTask.append("name=?, startingdate=?, validationmode=?, isrecurrent=?, recurrentperiod=?, ");
      queryUpdateTask.append("backup=?, status=?, issynchronization=?, isincremental=?, categoryid=? ");
      queryUpdateTask.append("WHERE id = ?");

      Integer recurrencePeriod = null;
      String backup = null;
      

      if (task.getRunMode().isRecurrent()) {
         recurrencePeriod = task.getRunMode().getRecurrencePeriod();
      }
      if (task.getBackup() != null) {
         backup = task.getBackup().getName();
      }

      getDbms().execute(queryUpdateTask.toString(), task.getName(),
            task.getRunMode().getStartingDate(), 
            task.getValidationMode().toString(),
            BooleanUtils.toString(task.getRunMode().isRecurrent(), "y", "n"), recurrencePeriod,
            backup, task.getStatus().toString(),
            BooleanUtils.toString(task.isSynchronizationTask(), "y", "n"),
            BooleanUtils.toString(task.isIncremental(), "y", "n"), task.getCategory().getId(), task.getId());

      /** Update configuration.*/
      //Delete ALL.
      String queryDeleteTaskConfiguration = "DELETE FROM HarvestingTaskConfiguration WHERE harvestingTaskId=?";
      getDbms().execute(queryDeleteTaskConfiguration, task.getId());

      //Insert new configuration.
      StringBuffer sbHarvestingTaskConf = new StringBuffer();
      sbHarvestingTaskConf
            .append("INSERT INTO harvestingtaskconfiguration(configurationid, attr, val, harvestingtaskid) ");
      sbHarvestingTaskConf.append("VALUES (?, ?, ?, ?);");
      for (String confKey : task.getConfiguration().keySet()) {
         int id = SerialFactory.getSerial(getDbms(), "HarvestingTaskConfiguration");
         getDbms().execute(sbHarvestingTaskConf.toString(), id, confKey,
               task.getConfiguration().get(confKey), task.getId());
      }
   }

   @SuppressWarnings("unchecked")
   public Collection<Metadata> getAllMetadataByHarvestingTask(Integer id) throws Exception {
      Collection<Metadata> mds = new HashSet<Metadata>();

      String query = "SELECT id, uuid FROM Metadata WHERE harvestingTask = ?";
      List<Element> records = getDbms().select(query, id).getChildren();
      for (Element e : records) {
         Metadata md = new Metadata();
         md.setId(Integer.parseInt(e.getChildText("id")));
         md.setUrn(e.getChildText("uuid"));
         mds.add(md);
      }
      return mds;
   }

   /**
    * Gets a map of harvesting statistics. The key is <code>true</code> for synchronization tasks and <code>false</code> for harvesting tasks. 
    * @return a map of harvesting statistics. The key is <code>true</code> for synchronization tasks and <code>false</code> for harvesting tasks.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public Map<Boolean, HarvestingStatistics> getAllHarvestingStatistics(HarvestingTaskStatus status)
         throws Exception {
      Map<Boolean, HarvestingStatistics> stats = new HashMap<Boolean, HarvestingStatistics>();
      stats.put(Boolean.TRUE, new HarvestingStatistics());
      stats.put(Boolean.FALSE, new HarvestingStatistics());
      
      //Get active tasks.
      String queryTaskActiveCount = "select id, issynchronization, status from harvestingtask";
      List<Element> records = getDbms().select(queryTaskActiveCount).getChildren();
      for (Element e : records) {
         Boolean isSynchro = BooleanUtils.toBoolean(e.getChildText("issynchronization"), "y", "n");
         HarvestingStatistics hStat = stats.get(isSynchro);
         hStat.incTotal();
         boolean isActive = HarvestingTaskStatus.ACTIVE.name().equals(e.getChildText("status"));
         if (isActive) {
            hStat.incActive();
         }
      }
      
      // Task is in failure (among the active ones) if:
      // result contains at least one unexpected error
      String queryTaskFailureCount = "select count(task.*) AS nbFailureTasks, task.issynchronization from harvestingtask task";
      queryTaskFailureCount += " LEFT JOIN harvestingtaskresult result ON result.harvestingtaskid = task.id";
      queryTaskFailureCount += " WHERE ((result.total > 0 AND result.unexpected > 0) OR result.fail = 'y')";
      queryTaskFailureCount += " AND task.status = '" + HarvestingTaskStatus.ACTIVE.name() + "'";
      queryTaskFailureCount += " group by task.issynchronization";
      records = getDbms().select(queryTaskFailureCount).getChildren();
      for (Element e : records) {
         Boolean isSynchro = BooleanUtils.toBoolean(e.getChildText("issynchronization"), "y", "n");
         Integer nbFailureTasks = Integer.parseInt(e.getChildText("nbfailuretasks"));
         stats.get(isSynchro).setFailure(nbFailureTasks);
      }

      return stats;
   }

   /**
    * Runs the specified task id.
    * @param harvestingTaskId the id of the harvesting task to trigger.
    * @param context the service context.
    * @return <code>true</code> if the task has been scheduled, <code>false</code> if it was running.
    * @throws Exception if an error occurs.
    */
   public boolean run(Integer harvestingTaskId, ServiceContext context) throws Exception {
      return this.run(harvestingTaskId, context, false);
   }
   
   /**
    * Runs the specified task id.
    * @param harvestingTaskId the id of the harvesting task to trigger.
    * @param context the service context.
    * @return <code>true</code> if the task has been scheduled, <code>false</code> if it was running.
    * @throws Exception if an error occurs.
    */
   public boolean run(Integer harvestingTaskId, ServiceContext context, boolean runOnce) throws Exception {
      HarvesterExecutorService scheduler = HarvesterExecutorService.getInstance();
      if (!scheduler.isRunning(harvestingTaskId)) {
         HarvestingTask task = getHarvestingTaskById(harvestingTaskId, true);
         if (!runOnce) {
            scheduler.removeScheduledIfAny(harvestingTaskId);
         }
         scheduler.run(task, context, runOnce);
         return true;
      } else {
         return false;
      }
   }
   
   /**
    * Runs the specified task id.
    * @param harvestingTaskId the id of the harvesting task to trigger.
    * @param context the service context.
    * @return <code>true</code> if the task has been scheduled, <code>false</code> if it was running.
    * @throws Exception if an error occurs.
    */
   public boolean runOnce(Integer harvestingTaskId, ServiceContext context) throws Exception {
      return this.run(harvestingTaskId, context, true);
   }
   
   /**
    * Schedule all recurrent harvesting tasks (done at start-up).
    * 
    * @param context the service context
    * @throws Exception if an error occurs
    */
   @SuppressWarnings("unchecked")
   public void scheduleAllRecurrentTasks(final ServiceContext context) throws Exception {
      String query = "SELECT id, name FROM HarvestingTask WHERE isrecurrent = 'y' AND status='ACTIVE'";
      
      List<Element> records = getDbms().select(query).getChildren();
      
      // Schedule run of harvesting task with 30s of delay between each run 
      // to allow a smooth ramp-up
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      for (int i=0; i<records.size(); i++) {
         Element e = records.get(i);
         final Integer id = Integer.parseInt(e.getChildText("id"));
         final String name = e.getChildText("name");
         int delay = SCHEDULE_DELAY_BETWEEN_TASKS * i + INITIAL_DELAY_FOR_TASK_SCHEDULING;
         Log.info(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
               "Schedule harvesting task {0} ({1}) in {2}s.", name, 
               id, delay));
         
         executor.schedule(new Runnable() {
            @Override
            public void run() {
               try {
                  HarvestingTaskManager.this.run(id, context);
               } catch (Exception e) {
                  Log.error(Geonet.HARVESTER_EXECUTOR, "Cannot run harvesting task " + id, e);
               }
            }
         }, delay, TimeUnit.SECONDS);
      }
   }

   public boolean activate(Integer harvestingTaskId, ServiceContext context) throws Exception {
      setActivation(harvestingTaskId, HarvestingTaskStatus.ACTIVE);
      return run(harvestingTaskId, context);
   }
   
   public boolean suspend(Integer harvestingTaskId) throws Exception {
      HarvesterExecutorService scheduler = HarvesterExecutorService.getInstance();
      if (!scheduler.isRunning(harvestingTaskId)) {
         setActivation(harvestingTaskId, HarvestingTaskStatus.SUSPENDED);
         scheduler.removeScheduledIfAny(harvestingTaskId);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Runs the specified task id.
    * @param harvestingTaskId the id of the harvesting task to trigger.
    * @param context the service context.
    * @return <code>true</code> if the task has been scheduled, <code>false</code> if it was running.
    * @throws Exception if an error occurs.
    */
   public void runAll(Collection<Integer> ids, ServiceContext context) throws Exception {
      for (Integer id : ids) {
         run(id, context);
      }
   }

   /**
    * Runs the specified task id.
    * @param harvestingTaskId the id of the harvesting task to trigger.
    * @param context the service context.
    * @return <code>true</code> if the task has been scheduled, <code>false</code> if it was running.
    * @throws Exception if an error occurs.
    */
   public void suspendAll(Collection<Integer> ids) throws Exception {
      for (Integer id : ids) {
         suspend(id);
      }
   }

   /**
    * Runs the specified task id.
    * @param harvestingTaskId the id of the harvesting task to trigger.
    * @param context the service context.
    * @return <code>true</code> if the task has been scheduled, <code>false</code> if it was running.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public void switchBackupMode(boolean isSwitchedOn, String deploymentName, ServiceContext context)
         throws Exception {
      //Get concerned harvesting tasks ids.
      String query = "SELECT id FROM HarvestingTask WHERE backup like ?";
      List<Element> records = getDbms().select(query, deploymentName).getChildren();
      Collection<Integer> ids = new ArrayList<Integer>();
      for (Element e : records) {
         ids.add(Integer.parseInt(e.getChildText("id")));
      }

      if (ids.isEmpty()) {
         return;
      }

      String changeHarvestingStatus = "UPDATE HarvestingTask SET status = ? WHERE backup like ?";
      if (isSwitchedOn) {
         //Change status.
         getDbms().execute(changeHarvestingStatus, HarvestingTaskStatus.ACTIVE.toString(),
               deploymentName);
         
         //If activation, run them all.
         runAll(ids, context);
      } else {
         //If deactivation, stop them all.
         suspendAll(ids);

         //Change status.
         getDbms().execute(changeHarvestingStatus,
               HarvestingTaskStatus.SUSPENDED_BACKUP.toString(), deploymentName);
         
      }
   }

   //-------------------------------------------------- Private.

   /**
    * Builds a harvesting task from an element returned by the DBMS.
    * @param e the element.
    * @return a harvesting task object from an element returned by the DBMS.
    */
   private HarvestingTask buildHarvestingTaskFromElement(Element e) throws Exception {
      HarvestingTask harvestingTask = new HarvestingTask();
      harvestingTask.setId(Integer.parseInt(e.getChildText("id")));
      harvestingTask.setUuid(e.getChildText("uuid"));
      harvestingTask.setName(e.getChildText("name"));
      harvestingTask.setType(e.getChildText("harvestingtype"));
      harvestingTask
            .setValidationMode(MetadataValidation.valueOf(e.getChildText("validationmode")));

      HarvestingTaskRunMode runMode = new HarvestingTaskRunMode();
      runMode.setRecurrent(e.getChildText("isrecurrent").equals("y"));

      if (runMode.isRecurrent()) {
         int recurrentPeriod = Integer.parseInt(e.getChildText("recurrentperiod"));
         runMode.setRecurrentPeriod(recurrentPeriod);
         runMode.setRecurrencePeriod(recurrentPeriod);
         runMode.setRecurrentScale(e.getChildText("recurrentscale"));
         if (StringUtils.isNotBlank(e.getChildText("startingdate"))) {
            runMode.setStartingDate(e.getChildText("startingdate"));
         }
      }
      harvestingTask.setRunMode(runMode);
      if (StringUtils.isNotBlank(e.getChildText("lastrun"))) {
         harvestingTask.setLastRun(getSdf().parse(e.getChildText("lastrun")));
      }
      if (StringUtils.isNotBlank(e.getChildText("backup"))) {
         harvestingTask.setBackup(new Deployment(e.getChildText("backup")));
      }
      harvestingTask.setStatus(HarvestingTaskStatus.valueOf(e.getChildText("status")));
      harvestingTask.setSynchronizationTask(e.getChildText("issynchronization").equals("y"));
      harvestingTask.setIncremental(e.getChildText("isincremental").equals("y"));
      if (StringUtils.isNotBlank(e.getChildText("harvestingtaskresultid"))) {
         harvestingTask.setLastResult(buildHarvestingTaskResultFromElement(e));
      }
      harvestingTask.setCategory(new Category());
      harvestingTask.getCategory().setId(Integer.parseInt(e.getChildText("categoryid")));
      harvestingTask.getCategory().setName(e.getChildText("categoryname"));
      return harvestingTask;
   }

   /**
    * Builds a harvesting task result from an element returned by the DBMS.
    * @param e the element.
    * @return a harvesting task result object from an element returned by the DBMS.
    */
   private MetadataAlignerResult buildHarvestingTaskResultFromElement(Element e) throws Exception {
      MetadataAlignerResult harvestingTaskResult = new MetadataAlignerResult();
      harvestingTaskResult.setDate(getSdf().parse(e.getChildText("dateresult")));
      harvestingTaskResult.setTotal(Integer.parseInt(e.getChildText("total")));
      harvestingTaskResult.setAdded(Integer.parseInt(e.getChildText("added")));
      harvestingTaskResult.setUpdated(Integer.parseInt(e.getChildText("updated")));
      harvestingTaskResult.setUnchanged(Integer.parseInt(e.getChildText("unchanged")));
      harvestingTaskResult.setLocallyRemoved(Integer.parseInt(e.getChildText("locallyremoved")));
      harvestingTaskResult.setUnknownSchema(Integer.parseInt(e.getChildText("unknownschema")));
      harvestingTaskResult.setFail(e.getChildText("fail").equals("y"));
      harvestingTaskResult.setBadFormat(Integer.parseInt(e.getChildText("badformat")));
      harvestingTaskResult.setDoesNotValidate(Integer.parseInt(e.getChildText("doesnotvalidate")));
      harvestingTaskResult.setIgnored(Integer.parseInt(e.getChildText("ignored")));
      harvestingTaskResult.setUnexpected(Integer.parseInt(e.getChildText("unexpected")));
      harvestingTaskResult.setErrors(buildImportResultErrors(e.getChildText("errors")));
      return harvestingTaskResult;
   }

   private List<MetadataAlignerError> buildImportResultErrors(String errors) throws Exception {
      List<MetadataAlignerError> resultList = new ArrayList<MetadataAlignerError>();
      if (StringUtils.isBlank(errors)) {
         return resultList;
      }
      for (String errorStr : Splitter.on(ERROR_SEPARATOR).split(errors)) {
         List<String> maeSplitter = Lists.newArrayList(Splitter.on(INNER_ERROR_SEPARATOR).split(
               errorStr));
         String urn = maeSplitter.get(0);
         if ("null".equals(urn))
         {
            urn = "";
         }
         String msg = maeSplitter.get(1);
         resultList.add(new MetadataAlignerError(urn, msg));
      }
      return resultList;
   }

   /**
    * Update task configuration
    * 
    * @param taskId the task ID
    * @param key the key
    * @param value the value
    * @throws SQLException 
    */
   @SuppressWarnings("unchecked")
   public void updateTaskConfiguration(Integer taskId, String key, String value)
         throws SQLException {

      String queryConfigTask = "SELECT configurationid FROM HarvestingTaskConfiguration WHERE harvestingtaskid = ? AND attr = ?";
      List<Element> recordsTaskConfig = getDbms().select(queryConfigTask, taskId, key)
            .getChildren();
      if (!recordsTaskConfig.isEmpty()) {
         // Update new configuration
         String updateQuery = "UPDATE harvestingtaskconfiguration SET val= ? WHERE configurationid = ?";
         getDbms().execute(updateQuery, value,
               new Integer(recordsTaskConfig.get(0).getChildText("configurationid")));
      } else {
         //Insert new configuration.
         StringBuffer sbHarvestingTaskConf = new StringBuffer();
         sbHarvestingTaskConf
               .append("INSERT INTO harvestingtaskconfiguration(configurationid, attr, val, harvestingtaskid) ");
         sbHarvestingTaskConf.append("VALUES (?, ?, ?, ?);");

         int id = SerialFactory.getSerial(getDbms(), "HarvestingTaskConfiguration");
         getDbms().execute(sbHarvestingTaskConf.toString(), id, key, value, taskId);
      }
   }
   
   @SuppressWarnings("unchecked")
   public void suspendAllCurrentHarvestingTasks() throws Exception {
      //Get concerned harvesting tasks ids.
      String query = "SELECT id FROM HarvestingTask WHERE isrecurrent = 'y' AND status='ACTIVE' AND issynchronization = 'n'";
      List<Element> records = getDbms().select(query).getChildren();
      Collection<Integer> ids = new ArrayList<Integer>();
      for (Element e : records) {
         ids.add(Integer.parseInt(e.getChildText("id")));
      }
      if (ids.isEmpty()) {
         return;
      }
      suspendAll(ids);
   }
   
   @SuppressWarnings("unchecked")
   public void startAllCurrentHarvestingTasks(ServiceContext context) throws Exception {
      //Get concerned harvesting tasks ids.
      String query = "SELECT id FROM HarvestingTask WHERE isrecurrent = 'y' AND status='SUSPENDED' AND issynchronization = 'n'";
      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         int id = Integer.parseInt(e.getChildText("id"));
         activate(id, context);
      }
   }
   
   @SuppressWarnings("unchecked")
   public void suspendAllCurrentSynchroTasks() throws Exception {
      //Get concerned harvesting tasks ids.
      String query = "SELECT id FROM HarvestingTask WHERE isrecurrent = 'y' AND status='ACTIVE' AND issynchronization = 'y'";
      List<Element> records = getDbms().select(query).getChildren();
      Collection<Integer> ids = new ArrayList<Integer>();
      for (Element e : records) {
         ids.add(Integer.parseInt(e.getChildText("id")));
      }
      if (ids.isEmpty()) {
         return;
      }
      suspendAll(ids);
   }
 
   @SuppressWarnings("unchecked")
   public void startAllCurrentSynchroTasks(ServiceContext context) throws Exception {
      //Get concerned harvesting tasks ids.
      String query = "SELECT id FROM HarvestingTask WHERE isrecurrent = 'y' AND status='SUSPENDED' AND issynchronization = 'y'";
      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         int id = Integer.parseInt(e.getChildText("id"));
         activate(id, context);
      }
   }
   
   
   @SuppressWarnings("unchecked")
   public String getLastRunDate(Integer taskId) throws Exception {
	   String lastRunDate = "";
	   String query = "SELECT lastrun FROM HarvestingTask WHERE id = ?";
	   List<Element> records = getDbms().select(query, taskId).getChildren();
	   if (records.size() > 0) {
		   lastRunDate = records.get(0).getChildText("lastrun");
	   }
	   return lastRunDate;
   }
   /*
   @SuppressWarnings("unchecked")
   public void updateLastReport(Integer taskId, String reportName) throws Exception {
	   String queryTaskReport = "SELECT id FROM HarvestingTask WHERE id = ?";
	   List<Element> recordsTaskReport = getDbms().select(queryTaskReport, taskId).getChildren();
	   if (!recordsTaskReport.isEmpty()) {
		   //Update last report name for the current harvesting task identifier
		   String updateQuery = "UPDATE HarvestingTask SET report = ? WHERE id = ?";
		   getDbms().execute(updateQuery, reportName, new Integer(recordsTaskReport.get(0).getChildText("id")));
		   
	   } 
   }
   
   @SuppressWarnings("unchecked")
   public String getLastReportName(Integer taskId) throws Exception {
	   String lastReportFileName = "";
	   String query = "SELECT report FROM HarvestingTask WHERE id = ?";
	   List<Element> records = getDbms().select(query, taskId).getChildren();
	   if (records.size() > 0) {
		   lastReportFileName = records.get(0).getChildText("report");
	   }
	   return lastReportFileName;
   }
   */
}

