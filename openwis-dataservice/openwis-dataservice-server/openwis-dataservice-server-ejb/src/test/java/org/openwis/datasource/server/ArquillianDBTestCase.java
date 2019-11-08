package org.openwis.datasource.server;

import java.io.File;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFactory;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.cache.CacheIndexImpl;
import org.openwis.dataservice.common.domain.entity.blacklist.BlacklistInfo;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.cache.PatternMetadataMapping;
import org.openwis.dataservice.common.domain.entity.enumeration.BlacklistInfoColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.RecurrentUpdateFrequency;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.ShoppingCartDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.EventBasedFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Frequency;
import org.openwis.dataservice.common.domain.entity.subscription.RecurrentFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.hash.HashUtils;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.dataservice.extraction.ExtractFromCacheImpl;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.dataservice.util.GTScategory;
import org.openwis.dataservice.util.GlobalDataCollectionUtils;
import org.openwis.dataservice.util.WMOFNC;
import org.openwis.datasource.server.dao.JpaDao;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.mdb.delegate.ExtractionDelegate;
import org.openwis.datasource.server.mdb.delegate.impl.ExtractionDelegateImpl;
import org.openwis.datasource.server.mdb.delegate.impl.SubscriptionDelegateImpl;
import org.openwis.datasource.server.mocks.MockedFeederEjb;
import org.openwis.datasource.server.service.impl.BlacklistServiceImpl;
import org.openwis.datasource.server.service.impl.ProcessedRequestServiceImplIntegrationTestCase;
import org.openwis.datasource.server.service.impl.RequestServiceImpl;
import org.openwis.datasource.server.service.impl.UserAlarmManagerImpl;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.openwis.datasource.server.utils.QueueUtils;
import org.openwis.harness.dissemination.Diffusion;
import org.openwis.management.service.DisseminatedDataStatisticsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ArquillianDBTestCase. <P>
 * Explanation goes here. <P>
 */
public abstract class ArquillianDBTestCase extends DatabaseTestCase {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ArquillianDBTestCase.class);

   /** The Constant JDBC_HSQLDB_URL. */
   private static final String JDBC_HSQLDB_URL = "jdbc:hsqldb:mem";
   
   /**
    * Creates the deployment.
    *
    * @return the archive
    */
   @Deployment
   public static Archive<?> createDeployment() {
      File dataFolder = new File("data");
      if (dataFolder.exists() && dataFolder.isDirectory() && dataFolder.canWrite()) {
         dataFolder.delete();
      }
  
      PomEquippedResolveStage mavenResolver = Maven.resolver().loadPomFromFile("pom.xml");
      //PomEquippedResolveStage mavenResolver = Maven.resolver().loadPomFromFile("pom.xml");
      //MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
//            .loadMetadataFromPom("pom.xml");
      
//      mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-domain")
//         .using(new AcceptScopesStrategy(ScopeType.TEST))

      WebArchive archive = ShrinkWrap
            //.create(JavaArchive.class)
            .create(WebArchive.class)
            .addAsResource("log4j.properties")
            // .addAsResource("jndi.properties")
            .addAsResource("openwis-dataservice-cache.properties")
            .addAsResource("conf-dataservice.properties")
            .addAsResource("ws-localdatasources.properties")
            .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
            // Add a class from each package requested.
            // WARNING : Don't include MDB since "maxSession" and "DLQMaxResent" are not valid on OpenEJB
            .addPackages(true, JpaDao.class.getPackage(),
                  SubscriptionDelegateImpl.class.getPackage(),
                  ProcessedRequestMessage.class.getPackage(), BlacklistInfo.class.getPackage(),
                  ExtractFromCacheImpl.class.getPackage(), CacheIndex.class.getPackage(),
                  CacheIndexImpl.class.getPackage(), CachedFile.class.getPackage(),
                  ProcessedRequestServiceImplIntegrationTestCase.class.getPackage(),
                  PatternMetadataMapping.class.getPackage(), QueueUtils.class.getPackage(),
                  BlacklistService.class.getPackage(),
                  BlacklistInfoColumn.class.getPackage(),
                  BlacklistServiceImpl.class.getPackage(),
                  ExtractionDelegate.class.getPackage(),
                  ExtractionDelegateImpl.class.getPackage(),
                  ExtractFromCacheImpl.class.getPackage(),
//                  DisseminatedDataStatisticsImpl.class.getPackage(),
                  UserAlarmManagerImpl.class.getPackage(), UserAlarm.class.getPackage(),
                  ArquillianDBTestCase.class.getPackage(),
                  MockedFeederEjb.class.getPackage(),
                  RequestServiceImpl.class.getPackage(),
                  GlobalDataCollectionUtils.class.getPackage(),
                  
                  Diffusion.class.getPackage()
            )
            //.addAsManifestResource("test-persistence.xml", " /WEB-INF/classes/persistence.xml")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsResource("org/openwis/management/JndiManagementServiceBeans.properties", "org/openwis/management/JndiManagementServiceBeans.properties")
            .addAsLibraries(mavenResolver.resolve("io.openwis.harness:openwis-harness-localdatasource").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("io.openwis.management.service:openwis-management-service-common").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("io.openwis.management:openwis-management-client").withTransitivity().asFile())
            //.addAsLibraries(mavenResolver.resolve("io.openwis.management.service:openwis-management-service-ejb").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-domain").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("io.openwis.dataservice.cache:openwis-dataservice-cache-core").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-timer:ejb:?").withTransitivity().asFile())
//            .addAsLibraries(mavenResolver.resolve("io.openwis.harness:openwis-harness-dissemination").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("org.apache.commons:commons-lang3").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("commons-collections:commons-collections").withTransitivity().asFile())
            .addAsLibraries(mavenResolver.resolve("org.dbunit:dbunit").withTransitivity().asFile())
//            .addAsLibraries(mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-domain").withTransitivity().asFile())
            //.merge(mavenResolver.resolve("io.openwis.management.service:openwis-management-service-common").withTransitivity().asSingleFile())
            //.addAsDirectories(filesToFilenames(mavenResolver.resolve("io.openwis.management.service:openwis-management-service-common").withTransitivity().asFile()))
            ;
      

      // Need to remove persistence.xml from the management service ejb
      File serviceEjb = mavenResolver.resolve("io.openwis.management.service:openwis-management-service-ejb").withoutTransitivity().asSingleFile();
      JavaArchive serviceEjbArchive = ShrinkWrap.createFromZipFile(JavaArchive.class, serviceEjb);
      serviceEjbArchive.delete("/META-INF/persistence.xml");
      
      archive.addAsLibrary(serviceEjbArchive);
      
//      JavaArchive mgmtEjb = new ArchiveFactory().createFromZipFile(JavaArchive.class, serviceEjb)
//         .delete("/META-INF/persistence.xml")
//         .getAsType(Java)
      
      
//      JavaArchive domainArchive = ShrinkWrap.create(ZipImporter.class)
//            .importFrom(mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-domain").withoutTransitivity().asSingleFile())
//            .as(JavaArchive.class)
//            ;
      
      // TEMP: Export
      File testExport = new File("/home/accounts/lmika/tmp/domain.zip");
      if (testExport.exists()) {
         testExport.delete();
      }
      archive.as(ZipExporter.class).exportTo(testExport);
      
      //mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-domain").withTransitivity().
        //    .addAsDirectories(filesToFilenames(mavenResolver.resolve("io.openwis.management.service:openwis-management-service-common").withTransitivity().asFile()));
            //.merge(mavenResolver.resolve("").withTransitivity().asSingleFile())

      //List<> mavenResolver.resolve("io.openwis.dataservice.common:openwis-dataservice-common-domain").withTransitivity().asList(JavaArchive.class);

//      logger.info("Deployment/n{}", archive.toString(true));
      return archive;
   }
   
   /*
   private static String[] filesToFilenames(File[] files) {
      String[] filenames = new String[files.length];
      
      for (int i = 0; i < files.length; i++) {
         filenames[i] = files[i].getAbsolutePath();
      }
      
      return filenames;
   }
   */

   /**
    * Put database in the initial state
    */
   public void initDB() {
      try {
         DatabaseOperation.INSERT.execute(getConnection(), getDataSet());
      } catch (Exception e) {
         logger.error("Could not initialize the DB", e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.dbunit.DatabaseTestCase#getConnection()
    */
   @Override
   protected IDatabaseConnection getConnection() throws Exception {
      Class<?> driverClass = Class.forName("org.hsqldb.jdbcDriver");
      driverClass.getClass();
      Connection jdbcConnection = DriverManager.getConnection(JDBC_HSQLDB_URL, "sa", "");
      return new DatabaseConnection(jdbcConnection);
   }

   /**
    * Creates the cached product.
    *
    * @param metadataId the metadata id
    * @param date the date
    */
   protected void createCachedProduct(String urn, final String metadataId, final Calendar date,
         final Long id) {
      File urnTestFile = getCachedFile(metadataId, date);
      logger.info("Create the test URN file {}", urnTestFile);
      try {
         urnTestFile.createNewFile();
         CacheIndex cacheIndex = getCachedIndex();
         //         CacheIndex cacheIndex = MockCacheIndex.getCacheIndex();
         if (cacheIndex != null) {
            FileInfo fileInfo = createNewFileInfo(urnTestFile, date.getTime());
            fileInfo.setMetadataURNList(Collections.singletonList(urn));
            fileInfo.addMetadataId(id);
            //            int numberOfChecksumBytes = 100;
            //            String checksum = ChecksumCalculator.calculateChecksumOnFile(urnTestFile, numberOfChecksumBytes);
            //            fileInfo.setChecksum(checksum);
            //            fileInfo.setNumberOfChecksumBytes(numberOfChecksumBytes);
            fileInfo.setPriority(Integer.valueOf(1));
            long fileSize = urnTestFile.length();
            fileInfo.setSize(fileSize);
            fileInfo.setGtsCategory(GTScategory.GLOBAL);

            cacheIndex.addCacheIndexEntry(fileInfo);
            //            Thread.sleep(5000);
         }
      } catch (Exception e) {
         logger.error("Could not create the test URN file", e);
      }
   }

   /**
    * Gets the cached index.
    *
    * @return the cached index
    */
   protected abstract CacheIndex getCachedIndex();

   /**
    * Creates the new file.
    *
    * @param file the file
    * @param productDate the product date
    * @return the file info
    */
   private FileInfo createNewFileInfo(File file, Date productDate) {
      FileInfo fileInfo = new FileInfo();
      fileInfo.setFileURL(file.getAbsolutePath());

      WMOFNC wmofnc = GlobalDataCollectionUtils.parseFileName(file.getName());
      if (wmofnc != null) {
         fileInfo.setProductFilename(file.getName());
         fileInfo.setReceivedFromGTS(true);
         Date insertionDate = new Date(System.currentTimeMillis());
         fileInfo.setProductDate(productDate);
         fileInfo.setInsertionDate(insertionDate);
      }
      return fileInfo;
   }

   /**
    * Gets the cached file.
    *
    * @param metadataId the metadata id
    * @param date the date
    * @return the cached file
    */
   private static File getCachedFile(final String metadataId, final Calendar date) {
      // Configure Cache
      String cacheUrl = ResourceBundle.getBundle("openwis-dataservice-cache").getString(
            "cache.dir.cache");

      cacheUrl = cacheUrl.concat(File.separator).concat("test");
      new File(cacheUrl).mkdirs();

      SimpleDateFormat sdf1 = new SimpleDateFormat("FFHHmm", Locale.ENGLISH);
      sdf1.setTimeZone(DateTimeUtils.UTC_TIME_ZONE);
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
      sdf2.setTimeZone(DateTimeUtils.UTC_TIME_ZONE);

      // update cache
      String dataFileName = MessageFormat.format("A_{0}{1}_C_{2}_{3}.txt", metadataId,
            sdf1.format(date.getTime()), metadataId.substring(metadataId.length() - 4),
            sdf2.format(date.getTime()));
      return new File(cacheUrl, dataFileName);
   }

   /**
    * Clear cached product.
    *
    * @param metadataId the metadata id
    * @param date the date
    */
   protected static void clearCachedProduct(final String metadataId, final Calendar date) {
      File urnTestFile = getCachedFile(metadataId, date);
      urnTestFile.delete();
   }

   /**
    * Clear user staging post.
    *
    * @param users the users
    */
   protected void clearStagingPost(final String... users) {
      // Clear user staging post
      String rootStagingPost = JndiUtils.getString(DataServiceConfiguration.STAGING_POST_URI_KEY);
      String userFolder;
      for (String user : users) {
         try {
            userFolder = HashUtils.getMD5Digest(user);
            File file = new File(rootStagingPost, userFolder);
            if (file.exists() && file.isDirectory()) {
               if (file.delete()) {
                  logger.info("Clear {} staging post", user);
               } else {
                  logger.warn("Cannot clear {} staging post", user);
               }
            }
         } catch (NoSuchAlgorithmException e) {
            logger.error("Could not clear user staging post", e);
         }
      }
   }

   /**
    * Builds the product metadata.
    *
    * @param urn the urn
    * @return the product metadata
    */
   protected ProductMetadata buildProductMetadata(final String urn, final String dataPolicy) {
      ProductMetadata productMetadata = new ProductMetadata();
      productMetadata.setUrn(urn);
      productMetadata.setDataPolicy(dataPolicy);
      productMetadata.setFed(true);
      productMetadata.setFncPattern("");
      productMetadata.setGtsCategory("");
      productMetadata.setIngested(true);
      productMetadata.setLocalDataSource("localDataSource1");
      productMetadata.setOriginator("");
      productMetadata.setOverridenDataPolicy(null);
      productMetadata.setOverridenPriority(0);
      productMetadata.setPriority(0);
      productMetadata.setProcess("");
      productMetadata.setTitle("");
      productMetadata.setFileExtension("");
      RecurrentUpdateFrequency recurrentUpdateFrequency = new RecurrentUpdateFrequency();
      recurrentUpdateFrequency.setRecurrentScale(RecurrentScale.HOUR);
      recurrentUpdateFrequency.setRecurrentPeriod(1);
      productMetadata.setUpdateFrequency(recurrentUpdateFrequency);

      return productMetadata;
   }

   /**
    * {@inheritDoc}
    * @see org.dbunit.DatabaseTestCase#getDataSet()
    */
   @Override
   protected IDataSet getDataSet() throws Exception {
      InputStream resourceAsStream = getClass().getResourceAsStream(
            "/dataset/requestService/initial_db.xml");
      return new FlatXmlDataSetBuilder().build(resourceAsStream);
   }

   /**
    * Builds the on product arrival subscription.
    *
    * @param user the user
    * @return the subscription
    */
   protected Subscription buildOnProductArrivalSubscription(final String user, String code,
         Value... values) {
      Subscription result;

      // Initialize subscription attribute
      result = new Subscription();
      result.setUser(user);
      result.setEmail(user + "@openwis.org");

      result.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);

      // OnProductArrival, started yesterday
      Frequency onProductArrival = new EventBasedFrequency();
      result.setFrequency(onProductArrival);

      Calendar yesterday = Calendar.getInstance();
      yesterday.add(Calendar.DAY_OF_MONTH, -1);
      result.setStartingDate(yesterday.getTime());

      // Use staging post
      ShoppingCartDissemination shoppingCartDissemination = new ShoppingCartDissemination();
      result.setPrimaryDissemination(shoppingCartDissemination);

      // Parameters
      Set<Parameter> hashSet = new LinkedHashSet<Parameter>();
      if (values != null) {
         Parameter parameter = new Parameter();
         parameter.setCode(code);
         hashSet.add(parameter);
         for (Value value : values) {
            parameter.getValues().add(value);
         }
      }
      result.setParameters(hashSet);

      return result;
   }

   /**
    * Builds the recurrent subscription.
    *
    * @param user the user
    * @return the subscription
    */
   protected Subscription buildRecurrentSubscription(final String user, String code,
         Value... values) {
      Subscription result;

      // Initialize subscription attribute
      result = new Subscription();
      result.setUser(user);
      result.setEmail(user + "@openwis.org");
      result.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);

      // recurrent, started yesterday
      RecurrentFrequency frequency = new RecurrentFrequency();
      frequency.setReccurentScale(RecurrentScale.HOUR);
      frequency.setReccurencePeriod(1);
      result.setFrequency(frequency);

      Calendar yesterday = Calendar.getInstance();
      yesterday.add(Calendar.DAY_OF_MONTH, -1);
      result.setStartingDate(yesterday.getTime());

      // Use staging post
      ShoppingCartDissemination shoppingCartDissemination = new ShoppingCartDissemination();
      result.setPrimaryDissemination(shoppingCartDissemination);

      // Parameters
      Set<Parameter> hashSet = new LinkedHashSet<Parameter>();
      if (values != null) {
         Parameter parameter = new Parameter();
         parameter.setCode(code);
         hashSet.add(parameter);
         for (Value value : values) {
            parameter.getValues().add(value);
         }
      }
      result.setParameters(hashSet);

      return result;
   }

   /**
    * Builds the addHoc.
    *
    * @param user the user
    * @param code
    * @return the adHoc
    */
   protected AdHoc buildAdHoc(final String user, String code, Value... values) {
      AdHoc result = new AdHoc();
      result.setUser(user);
      result.setEmail(user + "@openwis.org");
      result.setExtractMode(ExtractMode.NOT_IN_LOCAL_CACHE);

      ShoppingCartDissemination shoppingCartDissemination = new ShoppingCartDissemination();
      result.setPrimaryDissemination(shoppingCartDissemination);

      // Parameters
      HashSet<Parameter> hashSet = new HashSet<Parameter>();
      if (values != null) {
         Parameter parameter = new Parameter();
         parameter.setCode(code);
         hashSet.add(parameter);
         for (Value value : values) {
            parameter.getValues().add(value);
         }
      }
      result.setParameters(hashSet);

      return result;
   }

   /**
    * Builds the processed request.
    *
    * @param date the date
    * @return the processed request
    */
   protected ProcessedRequest buildProcessedRequest(final Date date) {
      ProcessedRequest result = new ProcessedRequest();
      result.setCreationDate(date);
      result.setRequestResultStatus(RequestResultStatus.CREATED);
      result.setSize(1);
      result.setUri("URI_TEST");
      result.setVersion(1L);
      return result;
   }

   /**
    * Gets the interval.
    *
    * @param date the date
    * @return the interval
    */
   public final String getHourInterval(Date date) {
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.setTime(date);
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      return MessageFormat.format("{0,number,00}:00Z/{1,number,00}:00Z", hour, (hour + 1));
   }

}
