package org.openwis.dataservice.gts.collection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.tools.ant.util.FileUtils;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.cache.CacheManager;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.cache.PatternMetadataMapping;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.dataservice.util.ChecksumCalculator;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.dataservice.util.GTScategory;
import org.openwis.dataservice.util.GlobalDataCollectionUtils;
import org.openwis.dataservice.util.WMOFNC;
import org.openwis.dataservice.util.WMOFTP;
import org.openwis.management.alert.AlertService;
import org.openwis.management.service.ControlService;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection MDB: 
 * - receive message from splitting process and from collectiontimer for replicated files
 * - for each incoming file and perform collection in cache
 */
@MessageDriven(messageListenerInterface = MessageListener.class, activationConfig = {
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/CollectionQueue"),
      @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")})
public class CollectionMDB implements MessageListener, ConfigurationInfo {

   // Logging tool
   private final static Logger LOG = LoggerFactory.getLogger(CollectionMDB.class);

   // Message Driven Context.
   @Resource
   private MessageDrivenContext mdc;

   @EJB
   private CacheManager cacheManager;

   // Initial context
   InitialContext context;

   private String sourceDirectory;

   private String workingDirectory;
   
   private ControlService controlService;
   private String tempDirectoryPath;
   private String replicationWorkingDirectory;
   private String[] includePatterns;
   private String[] excludePatterns;

   private long collectionPeriod = 0;
   private long collectionInitialDelay = 0;

   private final String productDateFormatString = "yyyyMMddHHmmss";

   private int maxNumberOfIncludedFiles = 0;
   private int numberOfChecksumBytes = 0;

   private int numberOfDeletedFiles = 0;

   private boolean checkForOriginator;
   private List<Pattern> originatorFilterPatternList;
   private List<String> originatorFilterStringList;

   private String suffixFilename = null;
   
   private static HashMap<Pattern,ProductMetadata> cachedFncPatternMap;
   private static long lastFncPatternMapUpdate = 0;
   /** Time before re-creating the FNC Pattern map */
   private static final int FNC_PATTERN_CACHE_TIMEOUT = 60000;
   
   @EJB
   private ProductMetadataService pmds;
   
   @PersistenceContext
   private EntityManager entityManager;

   
   /**
    * Create a Hashmap mapping a compiled fncPattern from the ProductMetadata table to it's associated ProductMetadata object.
    */
   private static synchronized HashMap<Pattern,ProductMetadata> createFNCPatternMap(ProductMetadataService pmds){
      // Check if we have to reload the fnc map
      long now = System.currentTimeMillis();
      long delta = now - lastFncPatternMapUpdate;
      if (cachedFncPatternMap == null || lastFncPatternMapUpdate == 0
            || delta > FNC_PATTERN_CACHE_TIMEOUT) {
         HashMap<Pattern,ProductMetadata> fncPatternMap = new HashMap<Pattern,ProductMetadata>();
         List<PatternMetadataMapping> patternMetadataMapping = pmds.getAllPatternMetadataMapping();
         long t1 = System.currentTimeMillis();
         for (PatternMetadataMapping mapping : patternMetadataMapping){
            fncPatternMap.put(mapping.getCompiledPattern(), mapping.getProductMetadata());
         }
         long t2 = System.currentTimeMillis();
         cachedFncPatternMap = fncPatternMap;
         lastFncPatternMapUpdate = now;
         //if (LOG.isDebugEnabled()) {
            LOG.info("FNC Pattern Map created: total time=" + (t2-now) + ", getAllPattern=" + 
                  (t1 - now) + ", map creation=" + (t2-t1));
         //}
      }
      return cachedFncPatternMap;
   }

   // -------------------------------------------------------------------------
   // Message listener implementation
   // -------------------------------------------------------------------------
   @Override
   public void onMessage(Message message) {
      try {
         TextMessage messageReceived = (TextMessage) message;
         LOG.debug("Received Collection message:  {}", messageReceived.getText());

         // parse received message
         File sortedFile = new File(messageReceived.getText());

         if (!sortedFile.exists()) {
            LOG.error("File does not exist: " + sortedFile);
            return;
         }

         // If a suffix has been added to the file in case of duplicates bulletin filenames, retrieve it or null otherwise
         suffixFilename = message.getStringProperty(WMOFTP.WMOFTP_SUFFIX);

         FileInfo fileInfo = new FileInfo();
         String fileNamePath = sortedFile.getPath().replace('\\', '/').replace(getSourceDirectory(), "");

         // Remove suffix for logical work to be done on the filename. The real filename with the suffix will just be used
         // to physically access the file.
         String fileNamePathWithoutSuffix = null;
         if (suffixFilename != null) {
            fileNamePathWithoutSuffix = fileNamePath.replace(suffixFilename, "");
         } else {
            fileNamePathWithoutSuffix = fileNamePath;
         }

         fileInfo.setFileURLwithSuffix(getSourceDirectory() + fileNamePath);
         fileInfo.setFileURL(getSourceDirectory() + fileNamePathWithoutSuffix);

         // Parse filename without suffix in order to get WMOFNC informations
         WMOFNC wmofnc = GlobalDataCollectionUtils.parseFileName(fileNamePathWithoutSuffix
               .substring(fileNamePathWithoutSuffix.lastIndexOf('/') + 1));

         if (wmofnc == null) {
            handleUnparsableFile(getSourceDirectory(),fileNamePath);
            return;
         }

         // File which represent the bulletin filename with the suffix
         File fileWithSuffix = new File(getSourceDirectory(), fileNamePath);

         // Remove leading "-" from the suffix to get the checksum
         String checksum = null;

         if (suffixFilename != null) {
            // Retrieve the checksum from the filename suffix for bulletins coming as packed files
            checksum = suffixFilename.substring(1, suffixFilename.length());
         } else {
            // Caculate the checksum for unpacked received bulletins
            checksum = ChecksumCalculator.calculateChecksumOnFile(fileWithSuffix,
                  getNumberOfChecksumBytes());
         }

         LOG.debug("+++ checksum:" + checksum);
         fileInfo.setChecksum(checksum);
         fileInfo.setNumberOfChecksumBytes(getNumberOfChecksumBytes());

         fileInfo.setSize(fileWithSuffix.length());

         HashMap<Pattern,ProductMetadata> fncPatternMap = createFNCPatternMap(pmds);
         
         if (isValidForIngestion(fileInfo,wmofnc,fncPatternMap)){
            if (!isDuplicate(fileInfo, wmofnc)) {
               Map<String,Boolean> validationMap = ProductValidatorManager.validateAll(fileInfo);
               if (!isCorrupted(validationMap)){
                  LOG.debug("Product passed all Validators.");
                  collectNewFile(fileInfo,wmofnc);
               } else {
                  handleCorruptedFile(fileInfo, validationMap);
               }
            } else {
               handleDuplicate(fileNamePath);
            }
         } else {
            handleInvalidFile(fileInfo, fileNamePath, checksum);
         }

         fileInfo = null;
         wmofnc = null;

      } catch (Throwable t) {
         LOG.error("Unexpected error while processing Collection message", t);
         mdc.setRollbackOnly();
      }
   }

   public String getWorkingDirectory() {
      if (workingDirectory == null) {
         workingDirectory = JndiUtils.getString(HARNESS_WORKING_DIRECTORY_KEY);
      }
      return workingDirectory;
   }
   
   private ControlService getControlService() {
      if (controlService == null) {
         try {
            InitialContext context = new InitialContext();
            controlService = (ControlService) context
                  .lookup("openwis-management-service/ControlService/remote");
         } catch (NamingException e) {
            controlService = null;
         }
      }
      return controlService;
   }


   private void handleCorruptedFile(FileInfo fileInfo, Map<String, Boolean> validationMap) {
      File corruptedFile = new File(fileInfo.getFileURLwithSuffix());

      if (corruptedFile == null || !corruptedFile.exists()){
         LOG.error("The corrupted file " + corruptedFile.getPath() + " does not exist.");
         return;
      }

      String filename = corruptedFile.getName();
      String checksum = fileInfo.getChecksum();
      String cause = getCause(validationMap);

      raiseCorruptedDataAlert(filename, cause);

      LOG.warn("+++ File " + filename + " is corrupted and will therefore be moved to the temporary directory.");
      String path = getTempDirectory() + "/corrupted/" + filename + "_" + checksum;
      File targetFile = new File(path,filename);
      try {
         getFileUtils().rename(corruptedFile, targetFile);
      }
      catch (IOException e) {
         LOG.error("Could not rename " + corruptedFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
      }

      if (!(fileInfo.getFileURL()).contains(getReplicationWorkingDirectory())){
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(fileInfo.getFileURL(), getSourceDirectory());
      } else {
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(fileInfo.getFileURL(), getReplicationWorkingDirectory());
      }

      increaseNumberOfDeletedFiles();
   }

   private String getCause(Map<String, Boolean> validationMap) {
      String cause = "The following Product Validators have flagged the product as corrupted: ";

      for (String validatorName : validationMap.keySet()){
         boolean valid = validationMap.get(validatorName).booleanValue();
         if (!valid) cause = cause + validatorName + ", ";
      }

      return cause;
   }

   private boolean isCorrupted(Map<String,Boolean> validationMap) {
      boolean isCorrupted = false;

      for (Boolean validBoolean : validationMap.values()){
         boolean valid = validBoolean.booleanValue();
         if (!valid) isCorrupted = true;
      }

      return isCorrupted;
   }

   public void handleUnparsableFile(String sourceDirectory, String fileNamePath){
      raiseUnparsableFileAlert(fileNamePath);
      LOG.error("+++ Could not parse file {}. Moving into temporary directory.",fileNamePath);

      File unparsedFile = new File(sourceDirectory,fileNamePath);
      if (unparsedFile == null || !unparsedFile.exists()){
         LOG.error("The unparsable file " + unparsedFile.getPath() + " does not exist.");
         return;
      }

      if (cacheManager != null){
         cacheManager.archiveFileToTemporaryDirectory(unparsedFile,false);
      } else {
         LOG.error("Could not archive file " + sourceDirectory + "/" + fileNamePath + " to the temporary direcroty.");
      }
      String relativePath = fileNamePath.substring(0, fileNamePath.lastIndexOf('/'));
      if (!(sourceDirectory + relativePath).contains(getReplicationWorkingDirectory())){
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(sourceDirectory + relativePath, sourceDirectory);
      } else {
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(sourceDirectory + relativePath, getReplicationWorkingDirectory());
      }

      increaseNumberOfDeletedFiles();
   }

   private void raiseUnparsableFileAlert(String filename){
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null){
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-CollectionTimerServiceImpl";
      String location = "Ingestion";
      String severity = "ERROR";
      String eventId = DataServiceAlerts.UNPARSABLE_FILE.getKey();

      List<Object> arguments = new ArrayList<Object>();
      arguments.add(source);
      arguments.add(filename);

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   private void raiseCorruptedDataAlert(String filename, String cause){
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null){
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-CollectionTimerServiceImpl";
      String location = "Ingestion";
      String severity = "ERROR";
      String eventId = DataServiceAlerts.CORRUPTED_DATA_RECEIVED.getKey();

      List<Object> arguments = new ArrayList<Object>();
      arguments.add(source);
      arguments.add(filename);
      arguments.add(cause);

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   private void handleDuplicate(String fileNamePath){
      String filename = fileNamePath.substring(fileNamePath.lastIndexOf('/') + 1);
      String relativePath = fileNamePath.substring(0, fileNamePath.lastIndexOf('/'));
      File duplicateFile = new File(getSourceDirectory() + relativePath,filename);

      if (duplicateFile == null || !duplicateFile.exists()){
         LOG.error("The duplicate file " + duplicateFile.getPath() + " does not exist.");
         return;
      }

      LOG.info("+++ Duplicate file " + fileNamePath + " has been removed.");
      duplicateFile.delete();
      if (!(getSourceDirectory() + relativePath).contains(getReplicationWorkingDirectory())){
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(getSourceDirectory() + relativePath, getSourceDirectory());
      } else {
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(getSourceDirectory() + relativePath, getReplicationWorkingDirectory());
      }
      increaseNumberOfDeletedFiles();
   }

   private void handleInvalidFile(FileInfo fileInfo, String fileNamePath, String checksum){
      String filename = fileNamePath.substring(fileNamePath.lastIndexOf('/') + 1);
      String relativePath = fileNamePath.substring(0, fileNamePath.lastIndexOf('/'));
      File invalidFile = new File(getSourceDirectory() + relativePath,filename);

      if (invalidFile == null || !invalidFile.exists()){
         LOG.error("The invalid file " + invalidFile.getPath() + " does not exist.");
         return;
      }

      if (fileInfo.getMetadataURNList().isEmpty()){
         // handle invalid files without associated (stopgap-)metadata
         LOG.info("+++ File " + fileNamePath + " was invalid (due to lack of associated metadata URNs) for ingestion and will be moved to the temporary directory.");
         String path = getTempDirectory() + "/noMetadata";
         File targetFile = new File(path,filename);
         try {
            getFileUtils().rename(invalidFile, targetFile);
         }
         catch (IOException e) {
            LOG.error("Could not rename " + invalidFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
         }
      } else {
         // handle LOCAL Data or data filtered out due to ingestion filters
         LOG.info("+++ File " + filename + " was invalid for ingestion and and will be moved to the temporary directory.");
         String path = getTempDirectory() + "/invalid";
         File targetFile = new File(path,filename);
         try {
            getFileUtils().rename(invalidFile, targetFile);
         }
         catch (IOException e) {
            LOG.error("Could not rename " + invalidFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
         }
      }

      if (!(getSourceDirectory() + relativePath).contains(getReplicationWorkingDirectory())){
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(getSourceDirectory() + relativePath, getSourceDirectory());
      } else {
         GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(getSourceDirectory() + relativePath, getReplicationWorkingDirectory());
      }
      increaseNumberOfDeletedFiles();
   }

   public FileUtils getFileUtils(){
      return FileUtils.getFileUtils();
   }

   private void handleFileWithoutMetadata(final WMOFNC wmofnc, final FileInfo fileInfo){
      LOG.warn("+++ No metadata was found for file: {}", wmofnc.getFileName());
      // received item without metadata
      String T1T2 = wmofnc.getDateTypeDesignator();
      char T1 ='0';
      if (T1T2 != null) T1 = T1T2.charAt(0);
      // see: SSD-45
      boolean isPrioOne = (T1 == 'W' || T1 == 'B');
      boolean isPrioTwo = ((T1 == 'I' || T1 == 'K' || T1 == 'U' || T1 == 'S') && !("SY".equals(T1T2)));
      if (isPrioOne || isPrioTwo){
         int priority = isPrioOne ? 1 : 2;
         createStopGapMetadata(wmofnc,fileInfo,priority);
         raiseNoMetadataAlert(wmofnc.getFileName(), fileInfo.getSize());
      }
   }

   /**
    * Collect the metadata associated with the wmofnc file using the data available in the fncPatternMap and put the collected date in the fileInfo.
    */
   private void collectAssociatedMetadata(HashMap<Pattern,ProductMetadata> fncPatternMap, final WMOFNC wmofnc, final FileInfo fileInfo){
      if ("A".equals(wmofnc.getPflag()) || "T".equals(wmofnc.getPflag())){
         // add single metadataURN (for pflag=A-files only)
         String metadataURN = wmofnc.getMetadataURN();
         LOG.debug("+++ metadataURN:" + metadataURN);

         if (metadataURN != null && !"".equals(metadataURN)){
            Query query = entityManager.createQuery("SELECT pm FROM ProductMetadata pm where pm.urn = '" + metadataURN + "'");
            ProductMetadata matchingMetadata = null;
            try{
               matchingMetadata = (ProductMetadata) query.getSingleResult();
            }
            catch(NoResultException e){
            }
            catch(NonUniqueResultException e){
               LOG.warn("+++ There is no unique metadata associated with the urn {}. Ignoring results.",metadataURN);
               matchingMetadata = null;
            }

            if (matchingMetadata != null){
               associateMetadata(fileInfo, metadataURN, matchingMetadata);
               
               enforceFileExtension(fileInfo, wmofnc, matchingMetadata);
            }
         }
      }

      LOG.debug("PatternMap is null? " + (fncPatternMap == null));

      for(Pattern fncPattern : fncPatternMap.keySet()){
         Matcher matcher = fncPattern.matcher(wmofnc.getFileName());
         LOG.debug("+++ FNC-Pattern " + fncPattern.toString() + " matches " + wmofnc.getFileName() + " : " + matcher.matches());
         if (matcher.matches()){ // NOTE: nothing shall conflict here !!! make sure in the ProductMetadata table !!!

            ProductMetadata productMetadata = fncPatternMap.get(fncPattern);

            // only include metadata URNs once
            if (fileInfo.getMetadataIDList().contains(productMetadata.getId())) break;

            // add metadataURNs
            LOG.debug("+++ additional URN:" + productMetadata.getUrn());
            
            associateMetadata(fileInfo, productMetadata.getUrn(), productMetadata);
         }
      }
   }

   private void associateMetadata(final FileInfo fileInfo, String metadataURN,
         ProductMetadata matchingMetadata) {
      fileInfo.addMetadataId(matchingMetadata.getId());
      fileInfo.addMetadataURN(metadataURN);
      if (fileInfo.getGtsCategory() == GTScategory.LOCAL){
         GTScategory gtsCategory = GTScategory.getGTSCategoryFromString(matchingMetadata.getGtsCategory());

         // use overridden GTS category if it exists
         String overriddenGtsCategoryString = matchingMetadata.getOverridenGtsCategory();
         if (overriddenGtsCategoryString != null){
            gtsCategory = GTScategory.getGTSCategoryFromString(overriddenGtsCategoryString);
         }

         LOG.debug("+++ category:" + gtsCategory);
         if (gtsCategory == GTScategory.GLOBAL) fileInfo.setGtsCategory(gtsCategory);
      }
      int priority;
      if (matchingMetadata.getOverridenPriority() != null) {
         priority = matchingMetadata.getOverridenPriority();
      } else {
         priority = matchingMetadata.getPriority();
      }
      LOG.debug("++++ priority:" + priority);
      fileInfo.setPriority(priority);
   }
   
   private void enforceFileExtension(FileInfo fileInfo, WMOFNC wmofnc, ProductMetadata matchingMetadata) {
      if (fileInfo.getFileURL().endsWith(".bin")) {
         String ext = "bin";
         if (matchingMetadata.getOverridenFileExtension() != null) {
            ext = matchingMetadata.getOverridenFileExtension();
         } else if (matchingMetadata.getFileExtension() != null) {
            ext = matchingMetadata.getFileExtension();
         }
         fileInfo.setExtension(ext);
         wmofnc.setType(ext);
      }
   }

   /**
    * Tests if there is any associated metadata for the wmofnc file.
    * If so, checks if the file is considered global and if there is already a duplicate in the cache.
    */
   private boolean isValidForIngestion(final FileInfo fileInfo,WMOFNC wmofnc,HashMap<Pattern,ProductMetadata> fncPatternMap){
      collectAssociatedMetadata(fncPatternMap,wmofnc,fileInfo);

      if (fileInfo.getMetadataURNList() == null || fileInfo.getMetadataURNList().isEmpty()){
         handleFileWithoutMetadata(wmofnc, fileInfo);
      }

      GTScategory gtsCategory = fileInfo.getGtsCategory();
      if (gtsCategory == GTScategory.GLOBAL){
         if (isCheckForOriginator()){
            if (!isRegional(fileInfo)){
               return false;
            }
         }
         return true;
      }
      return false;
   }

   private boolean isDuplicate(FileInfo fileInfo, WMOFNC wmofnc) {
      if (cacheManager == null) {
         LOG.error("Could not tell whether file is duplicate or not. Assuming not.");
         return false;
      }
      
      if (!cacheManager.isDuplicate(wmofnc.getFileName(), fileInfo.getChecksum(),
            getNumberOfChecksumBytes())) {
         return false;
      } else {
         LOG.info("+++ File already exists in Cache : " + wmofnc.getFileName() + ", checksum: "
               + fileInfo.getChecksum());
         return true;
      }
   }

   /**
    * Checks if the fileInfo file is considered regional according to the ingestion filters based on the metadata URN.
    */
   private boolean isRegional(final FileInfo fileInfo) {
      boolean isRegional = false;
      List<String> metadataURNList = fileInfo.getMetadataURNList();
      List<Pattern> compiledOriginatorFilters = getCompiledOriginatorFilters();
      if (compiledOriginatorFilters == null){
         LOG.error("Could not find the compiled originator filters.");
         return false;
      }
      for (Pattern originatorFilter : compiledOriginatorFilters){
         for (String metadataURN : metadataURNList){
            LOG.debug("+++ Trying to match Pattern " + originatorFilter.toString() + " with URN " + metadataURN);
            if (originatorFilter.matcher(metadataURN).matches()){
               isRegional = true;
               break;
            }
         }
         if (isRegional) break;
      }
      LOG.debug("+++ " + metadataURNList + " is regional: " + isRegional);
      return isRegional;
   }

   private void raiseNoMetadataAlert(final String filename, long filesize) {
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null){
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-CollectionTimerServiceImpl";
      String location = "Ingestion";
      String severity = "WARN";
      String eventId = DataServiceAlerts.NO_METADATA_RECORD_FOUND_FOR_PRODUCT.getKey();

      Object productId = filename;
      Object productSize = "" + filesize;
      List<Object> arguments = Arrays.asList(source,productId,productSize);

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   private void createStopGapMetadata(final WMOFNC wmofnc, final FileInfo fileInfo, final int priority) {
      String ttaaiicccc = wmofnc.getMetadata();
      String originator = wmofnc.getOriginator();

      LOG.info("Creating stop-gap metadata for file: {}", wmofnc.getFileName());
      Long stopGapMetadataId = pmds.createStopGapMetadata(ttaaiicccc, originator, priority);
      ProductMetadata stopGapMetadata = pmds.getProductMetadataById(stopGapMetadataId);

      String stopGapMetadataUrn = stopGapMetadata.getUrn();
      GTScategory gtsCategory = GTScategory.getGTSCategoryFromString(stopGapMetadata.getGtsCategory());

      fileInfo.addMetadataId(stopGapMetadataId);
      fileInfo.addMetadataURN(stopGapMetadataUrn);
      fileInfo.setGtsCategory(gtsCategory);
      fileInfo.setPriority(Integer.valueOf(priority));

      raiseStopgapMetadataCreatedAlert(wmofnc.getFileName(), stopGapMetadataUrn);
   }

   private void raiseStopgapMetadataCreatedAlert(String filename, String metadataUrn){
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null){
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-CollectionTimerServiceImpl";
      String location = "Ingestion";
      String severity = "WARN";
      String eventId = DataServiceAlerts.STOPGAP_METADATA_CREATED.getKey();

      List<Object> arguments = new ArrayList<Object>();
      arguments.add(filename);
      arguments.add(metadataUrn);

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   public List<File> scanForNewFilesFiltered(final String searchDirectory, final String[] include,final String[] exclude, int maxNumberOfIncludedFiles){
      Pattern[] includePatterns = GlobalDataCollectionUtils.getPatternsFromStrings(include);
      Pattern[] excludePatterns = GlobalDataCollectionUtils.getPatternsFromStrings(exclude);

      IngestionFilenameFilter iff = new IngestionFilenameFilter(includePatterns, excludePatterns);

      LOG.debug("Start listing files.");
      GlobalDataCollectionUtils.listAllFilesIncludingSubdirectoriesFilteredSorted(searchDirectory, iff);
      LOG.debug("Listing of files done.");

      LOG.debug("Get sorted files.");
      List<File> returnList = iff.getSortedFiles(maxNumberOfIncludedFiles);
      LOG.debug("Get sorted files done.");

      return returnList;
   }

   /**
    * Copies the file in the cache directory and makes an entry in the cache index database.
    */
   private void collectNewFile(final FileInfo fileInfo, final WMOFNC wmofnc){
      if (fileInfo.getFileURL().contains(getReplicationWorkingDirectory())) {
         fileInfo.setReceivedFromGTS(false);
      } else {
         fileInfo.setReceivedFromGTS(true);
      }

      Date productDate = null;
      Date insertionDate = new Date(System.currentTimeMillis());
      productDate = wmofnc.getProductDate();

      fileInfo.setOriginator(wmofnc.getOriginator());
      fileInfo.setProductDate(productDate);
      fileInfo.setInsertionDate(insertionDate);

      LOG.info("Collecting new file: " + wmofnc.getFileName() +
            ", checksum: " + fileInfo.getChecksum() +
            ", numberOfChecksumBytes: " + fileInfo.getNumberOfChecksumBytes() +
            ", fileSize: " + fileInfo.getFilesize() +
            ", GTSCategory: " + fileInfo.getGtsCategory() +
            ", Priority: " + fileInfo.getPriority() +
            ", Originator: " + fileInfo.getOriginator() +
            ", Product Date: " + fileInfo.getProductDate() +
            ", associated metadata: " + fileInfo.getMetadataURNList());

      int prio = fileInfo.getPriority().intValue();
      if (prio == 1) raiseHighPriorityProductAlert(fileInfo);

      if (cacheManager != null){
        CachedFile cachedFile = cacheManager.moveFileIntoCache(fileInfo);
        if (cachedFile != null) {
           cacheManager.createNewIncomingDataMessage(wmofnc, fileInfo, cachedFile.getId());
        } else {
           LOG.error("File " + fileInfo.getProductFilename() + " could not be inserted into the Cache.");
           mdc.setRollbackOnly();
        }
      } else {
        LOG.error("Could not move file " + fileInfo.getFileURL() + " into the Cache.");
        LOG.error("Could not create a new incoming data message for file " + fileInfo.getFileURL());
      }
   }

   private void raiseHighPriorityProductAlert(FileInfo fileInfo){
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null){
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-CollectionTimerServiceImpl";
      String location = "Ingestion";
      String eventId = DataServiceAlerts.HIGH_PRIORITY_DATA_RECEIVED.getKey();

      List<Object> arguments = new ArrayList<Object>();
      arguments.add(source);
      arguments.add(fileInfo.getMetadataURNList().get(0)); // FIXME
      arguments.add(fileInfo.getPriority());

      alertService.raiseEvent(source, location, null, eventId, arguments);
   }

   public String getSourceDirectory(){
      if (sourceDirectory == null){
         sourceDirectory = JndiUtils.getString(HARNESS_WORKING_DIRECTORY_KEY);
      }
      return sourceDirectory;
   }

   public String getTempDirectory(){
      if (tempDirectoryPath == null){
         tempDirectoryPath = JndiUtils.getString(TEMP_DIRECTORY_KEY);
      }
      return tempDirectoryPath;
   }

   public String getReplicationWorkingDirectory(){
      if (replicationWorkingDirectory == null){
         replicationWorkingDirectory = new File(getWorkingDirectory(),
               JndiUtils.getString(REPLICATION_CONFIG_FROM_REPLICATION_FOLDER_KEY)).getPath();
      }
      return replicationWorkingDirectory;
   }

   public String[] getExcludePatterns(){
      if (excludePatterns == null){
         String excludePatternString = JndiUtils.getString(EXCLUDE_PATTERNS_KEY);
         excludePatternString = excludePatternString.replace(" ", "");
         excludePatterns = excludePatternString.split(";");
      }
      return excludePatterns;
   }

   public String[] getIncludePatterns(){
      if (includePatterns == null){
         String includePatternString = JndiUtils.getString(INCLUDE_PATTERNS_KEY);
         includePatternString = includePatternString.replace(" ", "");
         includePatterns = includePatternString.split(";");
      }
      return includePatterns;
   }

   public int getMaxNumberOfIncludedFiles(){
      if (maxNumberOfIncludedFiles == 0){
         maxNumberOfIncludedFiles = JndiUtils.getInt(MAX_NUMBER_INCLUDED_UNPACKED_FILES);
      }
      return maxNumberOfIncludedFiles;
   }

   public int getNumberOfChecksumBytes(){
      if (numberOfChecksumBytes == 0){
         numberOfChecksumBytes = JndiUtils.getInt(NUMBER_OF_CHECKSUM_BYTES_KEY);
      }
      return numberOfChecksumBytes;
   }

   public List<Pattern> getCompiledOriginatorFilters(){
      if (originatorFilterPatternList == null){
         originatorFilterPatternList = new ArrayList<Pattern>();
         List<String> originatorFilters = getOriginatorFilters();
         if (originatorFilters == null) return null;

         for (String urnExpression : originatorFilters){
            originatorFilterPatternList.add(Pattern.compile(urnExpression));
         }
      }
      return originatorFilterPatternList;
   }

   public List<String> getOriginatorFilters(){
      if (originatorFilterStringList == null){
         //ControlService controlService = ManagementServiceProvider.getControlService();
         List<org.openwis.management.entity.IngestionFilter> ingestionFilters = getControlService().getIngestionFilters(); //ManagementServiceProvider.getControlService().getIngestionFilters();
         originatorFilterStringList = new ArrayList<String>();
         for (org.openwis.management.entity.IngestionFilter filter : ingestionFilters){
            originatorFilterStringList.add(filter.getRegex());
         }
      }
      return originatorFilterStringList;
   }

   public boolean isCheckForOriginator(){
      if (getOriginatorFilters().size() > 0)
         checkForOriginator = true;
      else
         checkForOriginator = false;
      return checkForOriginator;
   }

   public long getCollectionInitialDelay(){
      if (collectionInitialDelay == 0){
         collectionInitialDelay = JndiUtils.getLong(COLLECTION_TIMER_INITIAL_DELAY_KEY);
      }
      return collectionInitialDelay;
   }

   public long getCollectionPeriod(){
      if (collectionPeriod == 0){
         collectionPeriod = JndiUtils.getLong(COLLECTION_TIMER_PERIOD_KEY);
      }
      return collectionPeriod;
   }

   public int getNumberOfDeletedFiles(){
      return numberOfDeletedFiles;
   }

   public void increaseNumberOfDeletedFiles(){
      numberOfDeletedFiles++;
   }

   public void setNumberOfDeletedFiles(int value){
      numberOfDeletedFiles = value;
   }

   public String getProductDateFormatString(){
      return productDateFormatString;
   }

   //   private void setServiceDegradedAndRaiseError(String cause){
   //      //ControlService controlService = ManagementServiceProvider.getControlService();
   //      getControlService().setServiceStatus(ManagedServiceIdentifier.INGESTION_SERVICE,
   //            ManagedServiceStatus.DEGRADED);
   //      LOG.error("Set Ingestion status to degraded. Cause: " + cause);
   //
   //      AlertService alertService = ManagementServiceProvider.getAlertService();
   //      if (alertService == null) {
   //         LOG.error("Could not find the AlertService.");
   //      } else {
   //         String source = "openwis-dataservice-cache-ejb-CollectionTimerServiceImpl";
   //         String location = "Ingestion";
   //         String severity = "ERROR";
   //         String eventId = DataServiceAlerts.SERVICE_DEGRADED.getKey();
   //
   //         List<Object> arguments = new ArrayList<Object>();
   //         arguments.add(source);
   //         arguments.add(cause);
   //
   //         alertService.raiseEvent(source, location, severity, eventId, arguments);
   //      }
   //
   //   }
}
