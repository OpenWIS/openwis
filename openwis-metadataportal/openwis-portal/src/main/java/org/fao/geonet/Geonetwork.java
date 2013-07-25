//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jeeves.JeevesJCS;
import jeeves.interfaces.ApplicationHandler;
import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.Log;
import jeeves.utils.Util;
import jeeves.xlink.Processor;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.ThesaurusManager;
import org.fao.geonet.kernel.csw.CatalogConfiguration;
import org.fao.geonet.kernel.csw.CatalogDispatcher;
import org.fao.geonet.kernel.harvest.HarvestManager;
import org.fao.geonet.kernel.oaipmh.OaiPmhDispatcher;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.SearchManagerFactory;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.lib.ServerLib;
import org.fao.geonet.services.util.z3950.Repositories;
import org.fao.geonet.services.util.z3950.Server;
import org.fao.geonet.util.XslUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.MetadataServiceAlerts;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;
import org.openwis.metadataportal.kernel.deployment.OpenwisDeploymentsConfig;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.harvest.exec.HarvesterExecutorService;
import org.openwis.metadataportal.kernel.metadata.IMetadataManager;
import org.openwis.metadataportal.kernel.metadata.IProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.ITemplateManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.metadata.ProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.TemplateManager;
import org.openwis.metadataportal.model.availability.AvailabilityLevel;
import org.openwis.metadataportal.model.availability.DataServiceAvailability;
import org.openwis.metadataportal.model.availability.DeploymentAvailability;
import org.openwis.metadataportal.model.availability.MetadataServiceAvailability;
import org.openwis.metadataportal.model.availability.SecurityServiceAvailability;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.util.DateTimeUtils;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//=============================================================================

/** This is the main class. It handles http connections and inits the system
  */

public class Geonetwork implements ApplicationHandler {
   private Logger logger;

   private String path;

   private ISearchManager searchMan;

   private ThesaurusManager thesaurusMan;

   static final String IDS_ATTRIBUTE_NAME = "id";

   private boolean dbCreated;

   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
   
   private static final Hashtable<String, Boolean> servicesAvailability = new Hashtable<String, Boolean>();

   static boolean isAlarmAlreadyRaised;

   //---------------------------------------------------------------------------
   //---
   //--- GetContextName
   //---
   //---------------------------------------------------------------------------

   @Override
   public String getContextName() {
      return Geonet.CONTEXT_NAME;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Start
   //---
   //---------------------------------------------------------------------------

   /** Inits the engine, loading all needed data
     */

   @Override
   @SuppressWarnings("unchecked")
   public Object start(Element config, ServiceContext context) throws Exception {
      logger = context.getLogger();

      path = context.getAppPath();
      String baseURL = context.getBaseUrl();

      ServerLib sl = new ServerLib(path);
      String version = sl.getVersion();
      String subVersion = sl.getSubVersion();

      logger.info(MessageFormat.format("Initializing GeoNetwork {0}.{1} ...", version, subVersion));

      JeevesJCS.setConfigFilename(path + "WEB-INF/classes/cache.ccf");
      // force cache to be config'd so shutdown hook works correctly
      JeevesJCS.getInstance(Processor.XLINK_JCS);

      ServiceConfig handlerConfig = new ServiceConfig(config.getChildren());

      // --- Check current database and create database if an emty one is found
      Dbms dbms = initDatabase(context);

      //------------------------------------------------------------------------
      //--- initialize settings subsystem

      logger.info("  - Setting manager...");

      SettingManager settingMan = new SettingManager(dbms, context.getProviderManager());

      // --- Migrate database if an old one is found
      migrateDatabase(dbms, settingMan, version, subVersion);

      //------------------------------------------------------------------------
      //--- Initialize thesaurus

      logger.info("  - Thesaurus...");

      String thesauriDir = handlerConfig.getMandatoryValue(Geonet.Config.CODELIST_DIR);

      thesaurusMan = new ThesaurusManager(path, thesauriDir, context.isUser());

      //------------------------------------------------------------------------
      //--- initialize Z39.50

      logger.info("  - Z39.50...");

      boolean z3950Enable = settingMan.getValueAsBool("system/z3950/enable", false);
      ApplicationContext app_context = null;
      if (!z3950Enable) {
         logger.info("  - Z39.50: DISABLED");
      } else {
         String z3950port = settingMan.getValue("system/z3950/port");
         String host = settingMan.getValue("system/server/host");

         // null means not initialized

         // build Z3950 repositories file first from template
         if (Repositories.build(path, context)) {
            logger.info("     Repositories file built from template.");

            app_context = new ClassPathXmlApplicationContext(
                  handlerConfig.getMandatoryValue(Geonet.Config.JZKITCONFIG));

            // to have access to the GN context in spring-managed objects
            ContextContainer cc = (ContextContainer) app_context.getBean("ContextGateway");
            cc.setSrvctx(context);

            if (!z3950Enable)
               logger.info("     Server is Disabled.");
            else {
               logger.info("     Server is Enabled.");

               UserSession session = new UserSession();
               session.authenticate(null, "z39.50", "", "", "Guest", null);
               context.setUserSession(session);
               context.setIpAddress("127.0.0.1");
               Server.init(host, z3950port, path, context, app_context);
            }
         } else {
            logger.error("     Repositories file builder FAILED - Z3950 server disabled and Z3950 client services (remote search, harvesting) may not work.");
         }
      }

      //------------------------------------------------------------------------
      //--- initialize search and editing

      String htmlCacheDir = handlerConfig.getMandatoryValue(Geonet.Config.HTMLCACHE_DIR);
      String dataDir = path + handlerConfig.getMandatoryValue(Geonet.Config.DATA_DIR);

      logger.info("  - Search...");

      searchMan = SearchManagerFactory.createSearchManager(dbms, path, handlerConfig, new SettingInfo(
            settingMan));
      searchMan.startup();
      XslUtil.searchManager = searchMan;

      //------------------------------------------------------------------------
      //--- get edit params and initialize DataManager

      logger.info("  - Data manager...");

      File _htmlCacheDir = new File(htmlCacheDir);
      if (!_htmlCacheDir.isAbsolute()) {
         htmlCacheDir = path + htmlCacheDir;
      }
      DataManager dataMan = new DataManager(context, searchMan, dbms, settingMan, baseURL,
            htmlCacheDir, dataDir, path);

      String schemasDir = path + Geonet.Path.SCHEMAS;
      String saSchemas[] = new File(schemasDir).list();

      if (saSchemas == null)
         throw new Exception("Cannot scan schemas directory : " + schemasDir);
      else {
         for (int i = 0; i < saSchemas.length; i++)
            if (!saSchemas[i].equals("CVS") && !saSchemas[i].startsWith(".")) {
               logger.info("    Adding xml schema : " + saSchemas[i]);
               String schemaFile = schemasDir + saSchemas[i] + "/" + Geonet.File.SCHEMA;
               String suggestFile = schemasDir + saSchemas[i] + "/"
                     + Geonet.File.SCHEMA_SUGGESTIONS;
               String substitutesFile = schemasDir + saSchemas[i] + "/"
                     + Geonet.File.SCHEMA_SUBSTITUTES;

               dataMan.addSchema(saSchemas[i], schemaFile, suggestFile, substitutesFile);
            }
      }

      //------------------------------------------------------------------------
      //--- initialize harvesting subsystem
      logger.info("  - Harvest manager...");
      HarvestManager harvestMan = new HarvestManager(context, settingMan, dataMan);

      //------------------------------------------------------------------------
      //--- initialize catalogue services for the web

      logger.info("  - Catalogue services for the web...");

      CatalogConfiguration.loadCatalogConfig(path, Csw.CONFIG_FILE);
      CatalogDispatcher catalogDis = new CatalogDispatcher(handlerConfig, path);

      //------------------------------------------------------------------------
      //--- initialize catalog services for the web

      logger.info("  - Open Archive Initiative (OAI-PMH) server...");

      OaiPmhDispatcher oaipmhDis = new OaiPmhDispatcher(settingMan);

      //------------------------------------------------------------------------
      //--- return application context

      GeonetContext gnContext = new GeonetContext();

      gnContext.dataMan = dataMan;
      gnContext.searchMan = searchMan;
      gnContext.config = handlerConfig;
      gnContext.catalogDis = catalogDis;
      gnContext.settingMan = settingMan;
      gnContext.harvestMan = harvestMan;
      gnContext.thesaurusMan = thesaurusMan;
      gnContext.oaipmhDis = oaipmhDis;
      gnContext.app_context = app_context;

      logger.info("Site ID is : " + gnContext.getSiteId());

      // Creates a default site logo, only if the logo image doesn't exists
      // This can happen if the application has been updated with a new version preserving the database and
      // images/logos folder is not copied from old application
      createSiteLogo(gnContext.getSiteId());

      if (dbCreated) {
         // Create OpenWIS default templates if necessary
         logger.info("Adding OpenWIS default templates");
         String templateDirectoryPath = path + "/WEB-INF/classes/setup/templates";
         ITemplateManager templateManager = new TemplateManager(dbms, dataMan, searchMan);
         SiteSource source = new SiteSource(null, gnContext.getSiteId(), gnContext.getSiteName());
         templateManager.addDefaultTemplateFromLocalDirectory("iso19139", templateDirectoryPath,
               source);
      }

      // Synchronize groups with ldap
      synchronizeGroups(dbms, dataMan);

      //------------------------------------------------------------------------
      if (context.isAdmin()) {
         //--- Schedule recurrent harvesting tasks
         logger.info("Schedule recurrent harvesting tasks");
         HarvestingTaskManager htm = new HarvestingTaskManager(dbms);
         htm.scheduleAllRecurrentTasks(context);

         //--- Schedule stop gap metadata synchronization
         logger.info("Schedule Stop-Gap Metadata synchronization");
         IMetadataManager mdm = new MetadataManager(dbms, dataMan, settingMan, path);
         scheduleStopGapSyncho(settingMan, dbms, dataMan, mdm, gnContext);
         
         //--- Schedule Availability check
         logger.info("Schedule Availability check");
         scheduleAvailability(settingMan, dbms);

         //--- Schedule catalog size monitoring for alarm check
         logger.info("Schedule Catalog size monitoring");
         isAlarmAlreadyRaised = false;
         scheduleMetadataAlarms(dbms);

         //--- Schedule availability statistics
         logger.info("Schedule Availability statistics");
         new AvailabilityManager(dbms).scheduleAvailabilityStatistics(settingMan, searchMan,
               executor);
      }

      return gnContext;
   }

   /**
    * Attempt to synchronize with LDAP groups, in case of first creation or if no groups are found in DB.
    * @param dbms the dbms
    * @param dataMan the data manager
    */
   private void synchronizeGroups(Dbms dbms, DataManager dataMan) {
      GroupManager gm = new GroupManager(dbms);
      try {
         if (dbCreated || gm.getAllGroups().size() == 0) {
            logger.info("Synchronizing groups with LDAP");
            gm.synchronize(dataMan);
         }
      } catch (Exception e) {
         logger.error("!!! Unable to synchronize with LDAP (check your LDAP connection) !!!");
         logger.error(e.getMessage());
      }

   }

   /**
    * Schedule stop gap synchronization.
    *
    * @param settingMan the setting man
    * @param dbms the dbms
    * @param dataMan the data man
    * @param gnContext
    */
   private void scheduleStopGapSyncho(final SettingManager settingMan, final Dbms dbms,
         final DataManager dataMan, final IMetadataManager mdm, final GeonetContext gnContext) {
      try {
         Integer period = settingMan.getValueAsInt("system/stopGap/period");

         Runnable command = new Runnable() {
            /**
             * {@inheritDoc}
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               IProductMetadataManager pmm = new ProductMetadataManager();

               Date date = null;
               String lastSynchroDateKey = "system/stopGap/lastSynchro";
               String lastSynchro = settingMan.getValue(lastSynchroDateKey);
               try {
                  date = DateTimeUtils.parse(lastSynchro);
               } catch (Exception e) {
                  Log.warning(Geonet.ADMIN, "Invalid last stop gap synchronization date: '"
                        + lastSynchro + "'. Should synchronize all stop gap data");
               }

               try {
                  Date utcDate = DateTimeUtils.getUTCDate();
                  pmm.synchronizeStopGapMetadata(date, dbms, dataMan, mdm, gnContext);
                  settingMan.setValue(dbms, lastSynchroDateKey, DateTimeUtils.format(utcDate));
                  dbms.commit();
               } catch (Exception e) {
                  Log.error(Geonet.ADMIN, "Could not synchronize stop gap data", e);
               }
            }
         };
         executor.scheduleAtFixedRate(command, period, period, TimeUnit.SECONDS);
      } catch (Exception e) {
         Log.error(Geonet.ADMIN, "Could not configure the stop gap synchonisation", e);
      }
   }

   /**
    * Schedule availability.
    *
    * @param settingMan the setting man
    * @param dbms the dbms
    */
   private void scheduleAvailability(final SettingManager settingMan, final Dbms dbms) {
      try {
         Integer period = settingMan.getValueAsInt("system/availablility/period");

         Runnable command = new Runnable() {
            /**
             * {@inheritDoc}
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               Log.info(Geonet.ADMIN, "Availability Check");
               Collection<String> backups = OpenwisDeploymentsConfig.getBackUps();
               for (String backupName : backups) {
                  DeploymentManager deploymentManager = new DeploymentManager();
                  Deployment deployment = deploymentManager.getDeploymentByName(backupName);
                  Log.info(Geonet.ADMIN, "Checking availability of " + backupName);

                  boolean isServiceAvailable = true;
                  if (servicesAvailability.containsKey(backupName)) {
                     isServiceAvailable = Boolean.valueOf(servicesAvailability.get(backupName));
                  }

                  double remoteBackUpWarnRate = checkDeploymentAvailability(deployment);
                  double backupWarnRate = OpenwisDeploymentsConfig.getBackupWarnRate();

                  String report = MessageFormat.format(
                        "Availability rate for {0}: {1}%",
                        backupName, remoteBackUpWarnRate);
                  Log.info(Geonet.ADMIN, report);

                  // The rate of available function is inferior that the backup warn rate => the deployment is in error
                  if (backupWarnRate > remoteBackUpWarnRate) {
                     Log.warning(
                           Geonet.ADMIN,
                           MessageFormat
                           .format(
                                 "Below the backup warn rate ({0}%) => the deployment {1} is in error",
                                 backupWarnRate, backupName));
                     // Check that the state is changed
                     if (isServiceAvailable) {
                        //Send mail to admin
                        String[] admins = {deployment.getAdminMail(),
                              deploymentManager.getLocalDeployment().getAdminMail()};
                        sendMail(admins, settingMan, backupName, remoteBackUpWarnRate);

                        Log.debug(Geonet.ADMIN, "Store that the backup center " + backupName
                              + " is in Error");
                        //Store the state
                        servicesAvailability.put(backupName, false);
                     }
                  } else {
                     Log.debug(Geonet.ADMIN, "Store that the backup center " + backupName
                           + " is Available");
                     //Store the state
                     servicesAvailability.put(backupName, true);
                  }
               }
            }
         };
         executor.scheduleAtFixedRate(command, period, period, TimeUnit.SECONDS);
      } catch (Exception e) {
         Log.error(
               Geonet.ADMIN,
               "Could not configure the availability synchonisation. Check that 'system/availablility/period' exists in Settings",
               e);
      }
   }
   
   /**
    * Check availability of the given deployment.
    * @return the availability rate
    */
   private double checkDeploymentAvailability(Deployment deployment) {
      //Contact the proxy service of this center, with in params :
      // - service
      String service = "xml.availability.external.get";

      String deploymentURL = deployment.getUrl() + "srv/" + service;

      // Create an instance of HttpClient.
      HttpClient client = new HttpClient();

      // Create a method instance. Call the PROXY Service on the centre.
      GetMethod method = new GetMethod(deploymentURL);

      // Provide custom retry handler is necessary
      method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler());

      Element result = null;

      try {
         Log.debug(Geonet.ADMIN, "Execute availability backup method");
         // Execute the method.
         int statusCode = client.executeMethod(method);

         if (statusCode != HttpStatus.SC_OK) {
            Log.error(Geonet.ADMIN, "Availability check - Bad server response: " + method.getStatusLine());
            return 0;
         }

         // Read the response body.
         byte[] responseBody = method.getResponseBody();

         // Deal with the response.
         // Use caution: ensure correct character encoding and is not binary data
         String response = new String(responseBody);
         Log.debug(Geonet.ADMIN, response);

         Reader in = new StringReader(response);
         SAXBuilder builder = new SAXBuilder();
         Document doc = builder.build(in);

         result = doc.getRootElement();

         DeploymentAvailability remoteDeploymentAvailability = JeevesJsonWrapper.read(
               result.getText(), DeploymentAvailability.class);

         MetadataServiceAvailability mdtaServiceAvailability = remoteDeploymentAvailability
               .getMetadataServiceAvailability();
         DataServiceAvailability dataServiceAvailability = remoteDeploymentAvailability
               .getDataServiceAvailability();
         SecurityServiceAvailability securityServiceAvailability = remoteDeploymentAvailability
               .getSecurityServiceAvailability();

         int serviceUnavailable = 0;
         int numberOfService = 0;

         serviceUnavailable = checkServiceUnavailable("Harvesting", serviceUnavailable,
               mdtaServiceAvailability.getHarvesting().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Synchronization", serviceUnavailable,
               mdtaServiceAvailability.getSynchronization().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Indexing", serviceUnavailable,
               mdtaServiceAvailability.getIndexing().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("User Portal", serviceUnavailable,
               mdtaServiceAvailability.getUserPortal().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Dissemination Queue", serviceUnavailable,
               dataServiceAvailability.getDisseminationQueue().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Ingestion", serviceUnavailable,
               dataServiceAvailability.getIngestion().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Replication", serviceUnavailable,
               dataServiceAvailability.getReplicationProcess().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Subscription", serviceUnavailable,
               dataServiceAvailability.getSubscriptionQueue().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("Security Service", serviceUnavailable,
               securityServiceAvailability.getSecurityService().getLevel());
         numberOfService++;
         serviceUnavailable = checkServiceUnavailable("SSO Service", serviceUnavailable,
               securityServiceAvailability.getSsoService().getLevel());
         numberOfService++;

         double remoteBackUpWarnRate = (1 - ((double) serviceUnavailable / (double) numberOfService)) * 100;
         String report = MessageFormat.format("{0} / {1} service(s) DOWN", 
               serviceUnavailable, numberOfService);
         Log.info(Geonet.ADMIN, report);
         
         return remoteBackUpWarnRate;         
      } catch (Exception e) {
         Log.error(
               Geonet.ADMIN,
               "Could not check the availability synchonisation. Failed to access to 'xml.availability.external.get'",
               e);
         return 0;
      }
   }
   
   /**
    * Send Mail.
    * @param adminMail The admin mail.
    * @param sm The setting manager.
    */
   private void sendMail(String[] adminMails, SettingManager sm, String backUpName, double remoteBackUpWarnRate) {
      
      String host = sm.getValue("system/feedback/mailServer/host");
      String port = sm.getValue("system/feedback/mailServer/port");
      String from = sm.getValue("system/feedback/email");
      Log.debug(Geonet.SELF_REGISTER, "host : " + host + " port: " + port + " from : " + from + " to : " + adminMails);
      
      MailUtilities mail = new MailUtilities();

      String subject = OpenWISMessages.getString("BackUpAvailability.subject", null);
      String content = MessageFormat.format(
            OpenWISMessages.getString("BackUpAvailability.mailContent", null), backUpName,
            remoteBackUpWarnRate);
      
      boolean result = mail.sendMail(host, Integer.parseInt(port), subject, from, adminMails, content);
      if (!result) {
         Log.error(Geonet.ADMIN, "Send Mail Failed: the deployment is in error");
      } else {
         Log.info(Geonet.ADMIN, "Send Mail Successfull: the deployment is in error");
      }
   }
   
   /**
    * Check Service Unavailable.
    * @param serviceName The service name.
    * @param serviceUnavailable The number of service unavailable.
    * @param level The level
    * @return the number of service unavailable.
    */
   private int checkServiceUnavailable(String serviceName, int serviceUnavailable, AvailabilityLevel level) {
      if (level == AvailabilityLevel.DOWN) {
         serviceUnavailable++;
         Log.warning(Geonet.ADMIN, serviceName + " is DOWN");
      } else {
         Log.info(Geonet.ADMIN, serviceName + " is available");
      }
      return serviceUnavailable;
   }

   /**
    * Schedule alarms.
    * When the number of entries in the DAR catalogue exceeds a defined threshold
    *
    * @param dbms the dbms
    */
   private void scheduleMetadataAlarms(final Dbms dbms) {
      try {
         // Launch the run task every period definition
         Integer period = OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.CATALOG_SIZE_ALARM_PERIOD);
         // The limit number of entries in the DAR catalogue
         final Integer limit = OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.CATALOG_SIZE_ALARM_LIMIT);

         Runnable command = new Runnable() {
            /**
             * {@inheritDoc}
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               IMetadataManager mm = new MetadataManager(dbms);
               try {
                  Log.debug(Geonet.ADMIN, "Checking catalog size");
                  Integer nbMetadata = mm.getAllMetadata();
                  // Raise alarm once
                  if (nbMetadata > limit)
                  {
                     if (!isAlarmAlreadyRaised)
                     {
                        Log.warning(Geonet.ADMIN, "Catalog size (" + nbMetadata
                              + ") is beyond the limit (" + limit + ")");
                        AlertService alertService = ManagementServiceProvider.getAlertService();
                        if (alertService == null) {
                           Log.error(Geonet.ADMIN,
                                 "Could not get hold of the AlertService. No alert was passed!");
                           return;
                        }
                        String source = "catalogue-monitoring";
                        String location = "Catalogue";
                        String eventId = MetadataServiceAlerts.TOO_MANY_ENTRIES_IN_CATALOGUE.getKey();
                        List<Object> arguments = new ArrayList<Object>();
                        arguments.add(limit);
                        arguments.add(nbMetadata);
                        alertService.raiseEvent(source, location, null, eventId, arguments);
                        isAlarmAlreadyRaised = true;
                     }
                  }
                  else
                  {
                     isAlarmAlreadyRaised = false;
                  }
               } catch (Exception e) {
                  Log.error(Geonet.ADMIN, "Could not raise an alarm", e);
               }
            }
         };
         executor.scheduleAtFixedRate(command, period, period, TimeUnit.SECONDS);
      } catch (Exception e) {
         Log.error(Geonet.ADMIN, "Could not Schedule Availability check", e);
      }
   }

   /**
    * Check if current database is running same version as the web application.
    * If not, apply migration SQL script :
    *  resources/sql/migration/{version}-to-{version}-{dbtype}.sql.
    * eg. 2.4.3-to-2.5.0-default.sql
    *
    * @param dbms
    * @param settingMan
    * @param version
    * @param subVersion
    */
   private void migrateDatabase(Dbms dbms, SettingManager settingMan, String version,
         String subVersion) {
      logger.info("  - Migration ...");

      // Get db version and subversion
      String dbVersion = settingMan.getValue("system/platform/version");
      String dbSubVersion = settingMan.getValue("system/platform/subVersion");

      // Migrate db if needed
      logger.debug("      Webapp   version:" + version + " subversion:" + subVersion);
      logger.debug("      Database version:" + dbVersion + " subversion:" + dbSubVersion);

      if (version.equals(dbVersion)
      //&& subVersion.equals(dbSubVersion) Check only on version number
      ) {
         logger.info("      Webapp version = Database version, no migration task to apply.");
      } else {
         // Migrating from 2.0 to 2.5 could be done 2.0 -> 2.3 -> 2.4 -> 2.5
         String dbType = Lib.db.getDBType(dbms);
         logger.info("      Migrating from " + dbVersion + " to " + version + " (dbtype:" + dbType
               + ")...");
         String sqlMigrationScriptPath = path + "/WEB-INF/classes/setup/sql/migrate/" + dbVersion
               + "-to-" + version + "/" + dbType + ".sql";
         File sqlMigrationScript = new File(sqlMigrationScriptPath);
         if (sqlMigrationScript.exists()) {
            try {
               // Run the SQL migration
               logger.info("      Running SQL migration step ...");
               Lib.db.runSQL(dbms, sqlMigrationScript);

               // Refresh setting manager in case the migration task added some new settings.
               settingMan.refresh();

               // Update the logo
               String siteId = settingMan.getValue("system/site/siteId");
               initLogo(dbms, siteId);

               // TODO : Maybe a force rebuild index is required in such situation.
            } catch (Exception e) {
               logger.info("      Errors occurs during SQL migration task: "
                     + sqlMigrationScriptPath + " or when refreshing settings manager.");
               e.printStackTrace();
            }

            logger.info("      Successfull migration.\n"
                  + "      Catalogue administrator still need to update the catalogue\n"
                  + "      logo and data directory in order to complete the migration process.\n"
                  + "      Index rebuild is also recommended after migration.");

         } else {
            logger.info("      No migration task found between webapp and database version.\n"
                  + "      The system may be unstable or may failed to start if you try to run \n"
                  + "      the current GeoNetwork "
                  + version
                  + " with an older database (ie. "
                  + dbVersion
                  + "\n"
                  + "      ). Try to run the migration task manually on the current database\n"
                  + "      before starting the application or start with a new empty database.\n"
                  + "      Sample SQL scripts for migration could be found in WEB-INF/sql/migrate folder.\n");

         }

         // TODO : Maybe some migration stuff has to be done in Java ?
      }
   }

   /**
    * Database initialization. If no table in current database
    * create the GeoNetwork database. If an existing GeoNetwork database
    * exists, try to migrate the content.
    *
    * @param context
    * @return
    * @throws Exception
    */
   private Dbms initDatabase(ServiceContext context) throws Exception {
      Dbms dbms = null;
      try {
         dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      } catch (Exception e) {
         logger.info("    Failed to open database connection, Check config.xml db file configuration."
               + "Error is: " + e.getMessage());
      }

      String dbURL = dbms.getURL();
      logger.info("  - Database connection on " + dbURL + " ...");

      // Create db if empty
      if (!Lib.db.touch(dbms)) {
         logger.info("      " + dbURL + " is an empty database (Metadata table not found).");

         // Do we need to remove object before creating the database ?
         Lib.db.removeObjects(dbms, path);
         Lib.db.createSchema(dbms, path);
         dbms.commit();
         Lib.db.insertData(dbms, path);

         // Copy logo
         String uuid = UUID.randomUUID().toString();
         initLogo(dbms, uuid);

         dbCreated = true;
      } else {
         logger.info("      Found an existing GeoNetwork database.");
      }

      return dbms;
   }

   /**
    * Copy the default dummy logo to the logo folder based on uuid
    * @param dbms
    * @param nodeUuid
    * @throws FileNotFoundException
    * @throws IOException
    * @throws SQLException
    */
   private void initLogo(Dbms dbms, String nodeUuid) {
      createSiteLogo(nodeUuid);

      try {
         dbms.execute("UPDATE Settings SET value=? WHERE name='siteId'", nodeUuid);
      } catch (SQLException e) {
         logger.error("      Error when setting siteId values: " + e.getMessage());
      }
   }

   /**
    * Creates a default site logo, only if the logo image doesn't exists
    *
    * @param nodeUuid
    */
   private void createSiteLogo(String nodeUuid) {
      try {
         File logo = new File(path + "/images/logos/" + nodeUuid + ".gif");
         if (!logo.exists()) {
            FileInputStream is = new FileInputStream(path + "/images/logos/dummy.gif");
            FileOutputStream os = new FileOutputStream(path + "/images/logos/" + nodeUuid + ".gif");
            logger.info("      Setting catalogue logo for current node identified by: " + nodeUuid);
            BinaryFile.copy(is, os, true, true);
         }
      } catch (Exception e) {
         logger.error("      Error when setting the logo: " + e.getMessage());
      }
   }

   //---------------------------------------------------------------------------
   //---
   //--- Stop
   //---
   //---------------------------------------------------------------------------

   @Override
   public void stop() {
      logger.info("Stopping geonetwork...");

      //------------------------------------------------------------------------
      //--- end search

      logger.info("  - search...");

      try {
         searchMan.shutdown();
      } catch (Exception e) {
         logger.error("Raised exception while stopping search");
         logger.error("  Exception : " + e);
         logger.error("  Message   : " + e.getMessage());
         logger.error("  Stack     : " + Util.getStackTrace(e));
      }

      //------------------------------------------------------------------------
      //--- end Z39.50

      logger.info("  - Z39.50...");
      Server.end();
      
      // Stopping executors
      logger.info("  - executors...");
      executor.shutdownNow();
      HarvesterExecutorService.getInstance().shutdown();
      DataManager.shutdownExecutor();
   }

}

//=============================================================================

