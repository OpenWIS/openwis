/**
 *
 */
package org.openwis.harness.samples.common.extraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LocalDatasourceFileUtils. <P>
 * Explanation goes here. <P>
 */
public class LocalDatasourceUtils {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(LocalDatasourceUtils.class);

   /** The Constant PROPERTY_STAGING_POST. */
   private static final String PROPERTY_STAGING_POST = "LocalDatasourceFileUtils.folder.staging.post";

   /** The Constant PROPERTY_FOLDER_TMP. */
   private static final String PROPERTY_FOLDER_TMP = "LocalDatasourceFileUtils.folder.tmp";

   /** The temp root folder. */
   private final File tmpRootFolder;

   /** The staging post root folder. */
   private final File stagingPostRootFolder;

   /** The file lock. */
   private final Map<File, Lock> fileLock;

   /** The results number. */
   private final Map<Long, Integer> results;

   /**
    * Instantiates a new local datasource file utils.
    *
    * @param props the props
    */
   public LocalDatasourceUtils(Properties props) {
      super();

      // Temp root folder
      tmpRootFolder = new File(props.getProperty(PROPERTY_FOLDER_TMP));
      logger.debug("Temp root folder: {}", tmpRootFolder);

      // Staging post URI
      stagingPostRootFolder = new File(props.getProperty(PROPERTY_STAGING_POST));

      // file lock
      fileLock = Collections.synchronizedMap(new HashMap<File, Lock>());

      // Number of results
      results = Collections.synchronizedMap(new HashMap<Long, Integer>());
   }

   /**
    * Gets the tmp root folder.
    *
    * @return the tmp root folder
    */
   public File getTmpRootFolder() {
      return tmpRootFolder;
   }

   /**
    * Gets the staging post file.
    *
    * @param stagingPostURI the staging post URI
    * @return the staging post file
    */
   public File getStagingPostFile(String stagingPostURI) {
      File file = new File(stagingPostRootFolder, stagingPostURI);
      file.getParentFile().mkdirs();
      return file;
   }

   /**
    * Creates the temp file.
    *
    * @param requestId the request id
    * @return the file
    */
   public File getTempFile(long requestId) {
      return new File(tmpRootFolder, MessageFormat.format("extraction{0}.tmp", requestId));
   }

   /**
    * Gets the err file.
    *
    * @param requestId the request id
    * @return the err file
    */
   public File getErrFile(long requestId) {
      return new File(tmpRootFolder, MessageFormat.format("extraction{0}.err", requestId));
   }

   /**
    * Gets the ok file.
    *
    * @param requestId the request id
    * @return the ok file
    */
   public File getOkFile(long requestId) {
      return new File(tmpRootFolder, MessageFormat.format("extraction{0}.ok", requestId));
   }

   /**
    * Creates the error file.
    *
    * @param requestId the request id
    * @param message the message
    * @param cause the cause
    */
   public void createErrorFile(long requestId, String message, Exception cause) {
      File errFile = getErrFile(requestId);
      Writer writer = null;
      PrintWriter printWriter = null;
      // handle tmpFile
      File tmpFile = getTempFile(requestId);
      Lock lock = getLock(tmpFile);
      lock.lock();
      try {
         if (tmpFile.exists()) {
            tmpFile.renameTo(errFile);
         }
         FileUtils.touch(errFile);

         // Append information
         writer = new FileWriter(errFile, true);
         if (message != null) {
            // append message
            writer.append('\n');
            writer.write(message);
         }
         // append error to file
         if (cause != null) {
            writer.append('\n');
            printWriter = new PrintWriter(writer);
            cause.printStackTrace(printWriter);
         }

      } catch (IOException ioe) {
         logger.error("Cannot write information to error file :" + errFile, ioe);
      } finally {
         IOUtils.closeQuietly(printWriter);
         IOUtils.closeQuietly(writer);
         lock.unlock();
      }
   }

   /**
    * Creates the OK file.
    *
    * @param requestId the request id
    */
   public void createOkFile(long requestId) {
      File okFile = getOkFile(requestId);
      Writer writer = null;

      File tmpFile = getTempFile(requestId);
      Lock lock = getLock(tmpFile);
      lock.lock();
      try {
         // handle tmpFile
         if (tmpFile.exists()) {
            tmpFile.renameTo(okFile);
         }
         FileUtils.touch(okFile);

         // Append information
         writer = new FileWriter(okFile, true);
         // append OK
         writer.write("[OK]");

      } catch (IOException ioe) {
         logger.error("Cannot write information to OK file" + okFile, ioe);
      } finally {
         IOUtils.closeQuietly(writer);
         lock.unlock();
      }
   }

   /**
    * Sets the number product found.
    *
    * @param requestId the request id
    * @param size the size
    */
   public void setNumberProductFound(long requestId, int size) {
      results.put(requestId, size);
   }

   /**
    * Read status.
    *
    * @param requestId the id
    * @return the monitor status
    */
   public MonitorStatus readStatus(long requestId) {
      MonitorStatus status = new MonitorStatus();
      File file;

      // Check OK
      File tmpFile = getOkFile(requestId);
      Lock lock = getLock(tmpFile);
      lock.lock();
      try {
         if (tmpFile.exists()) {
            Integer count = results.get(requestId);
            if (count != null && count > 0) {
               status.setStatus(Status.EXTRACTED);
            } else {
               status.setStatus(Status.NO_RESULT_FOUND);
            }
            status.setMessage(readFileContent(tmpFile));
            tmpFile.delete();
            fileLock.remove(tmpFile);
            results.remove(requestId);
         } else {
            // Check error
            file = getErrFile(requestId);
            if (file.exists()) {
               status.setStatus(Status.ERROR);
               status.setMessage(readFileContent(file));
               file.delete();
               fileLock.remove(tmpFile);
               results.remove(requestId);
            } else {
               file = getTempFile(requestId);
               if (file.exists()) {
                  status.setStatus(Status.ONGOING_EXTRACTION);
                  status.setMessage(readFileContent(file));
               } else {
                  status.setStatus(Status.ERROR);
                  status.setMessage("Cannot read extraction status");
               }
            }
         }
      } finally {
         lock.unlock();
      }

      return status;
   }

   /**
    * Append file content.
    *
    * @param file the file
    * @return the string
    */
   private String readFileContent(File file) {
      String result;
      // already Locked done by readStatus
      try {
         result = FileUtils.readFileToString(file);
      } catch (IOException ioe) {
         logger.warn("Error in reading status message", ioe);
         result = "Could not read file status";
      }
      return result;
   }

   /**
    * Append to file.
    *
    * @param file the file
    * @param content the content
    */
   public void appendToFile(File file, String content) {
      FileWriter fw = null;
      Lock lock = getLock(file);
      lock.lock();
      try {
         fw = new FileWriter(file, true);
         fw.write(content);
      } catch (IOException ioe) {
         logger.warn("Error in reading status", ioe);
      } finally {
         IOUtils.closeQuietly(fw);
         lock.unlock();
      }
   }

   /**
    * Gets the lock.
    *
    * @param file the file
    * @return the lock
    */
   private Lock getLock(File file) {
      Lock lock = fileLock.get(file);
      if (lock == null) {
         lock = new ReentrantLock();
         fileLock.put(file, lock);
      }
      return lock;
   }

}
