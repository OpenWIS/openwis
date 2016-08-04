package org.openwis.dataservice.replication.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.openwis.dataservice.replication.ftp.config.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle an FTP Connection.
 */
public class FTPConnectionHandler {

   private static final Logger LOG = LoggerFactory.getLogger(FTPConnectionHandler.class);

   private FTPClient ftp = new FTPClient();

   private Destination destination;

   public FTPConnectionHandler(Destination destination) {
      this.destination = destination;
   }

   public Destination getDestination() {
      return destination;
   }

   public void setDestination(Destination destination) {
      this.destination = destination;
   }

   private void error(String message) {
      String msg = "[" + Thread.currentThread().getName() + "] - " + message;
      LOG.error(msg);
   }

   private void error(String message, Throwable t) {
      String msg = "[" + Thread.currentThread().getName() + "] - " + message;
      LOG.error(msg, t);
   }

   private void warn(String message) {
      String msg = "[" + Thread.currentThread().getName() + "] - " + message;
      LOG.warn(msg);
   }

   private void info(String message) {
      String msg = "[" + Thread.currentThread().getName() + "] - " + message;
      LOG.info(msg);
   }
   
   private void debug(String message) {
      String msg = "[" + Thread.currentThread().getName() + "] - " + message;
      LOG.debug(msg);
   }

   /**
    * Connect to server or re-use the existing connection.
    * @return <code>true</code> if connection success, else <code>false</code>
    * @throws IOException if an error occurs
    */
   private boolean connect() throws IOException {
      info("Connecting to server " + destination.getServer() + ":" + destination.getPort());

      if (destination.isSecured() && !(ftp instanceof FTPSClient)) {
         debug("Switching to FTPS client");
         ftp = new FTPSClient();
      }

      ftp.connect(destination.getServer(), Integer.parseInt(destination.getPort()));
      int reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
         error("Error - " + destination.getServer() + " reply code" + reply);
         ftp.disconnect();
         return false;
      }

      debug("Login to server as " + destination.getUsername());
      if (!ftp.login(destination.getUsername(), destination.getPassword())) {
         error("Error - login failed on " + destination.getServer() + " for "
               + destination.getUsername());
         ftp.disconnect();
         return false;
      }

      // set files type : BIN
      debug("Set File Type as Binary");
      ftp.setFileType(FTP.BINARY_FILE_TYPE);

      ftp.setKeepAlive(true);
      ftp.setControlKeepAliveTimeout(300);

      // Set passive mode
      debug("Entering local passive mode");
      ftp.enterLocalPassiveMode();

      info("Connection established with " + destination.getServer() + ":" + destination.getPort());

      info("Change dir to " + destination.getRemotePath());
      if (!ftp.changeWorkingDirectory(destination.getRemotePath())) {
         error("Unable to change dir to " + destination.getRemotePath());
         ftp.logout();
         ftp.disconnect();
         return false;
      }

      return true;
   }

   /**
    * Check if connection is valid, (re-)connect if necessary.
    * @return <code>true</code> if the connection is ok
    */
   public boolean checkConnection() {
      try {
         if (ftp.isConnected()) {
            try {
               if (ftp.sendNoOp()) {
                  return true;
               }
            } catch (IOException e) {
               closeConnection();
               debug("Invalid connection, will reconnect: " + e.getMessage());
            }
         }

         // Connect to server
         return connect();
      } catch (Exception e) {
         warn("Error while checking connection " + destination.getServer() + ": " + e.getMessage());
         try {
            info("Wait " + destination.getConnectionRetryDelay() + " ms before reconnecting");
            Thread.sleep(destination.getConnectionRetryDelay());
         } catch (InterruptedException e1) {
         }
         closeConnection();
         return false;
      }
   }

   public void closeConnection() {
      if (ftp.isConnected()) {
         try {
            info("Disconnecting from server " + destination.getServer());
            ftp.disconnect();
         } catch (Exception e) {
            warn("Unable to disconnect from " + destination.getServer());
         }
      }
   }

   private boolean fileExists(String outputFile) throws IOException {
      FTPFile[] files = ftp.listFiles(outputFile);
      if (files != null && files.length > 0 && files[0] != null) {
         return true;
      }
      return false;
   }

   /**
    * Upload the given file as <file>.tmp first and then rename it.
    * @param file the file to upload
    * @return <code>true</code> if the file was uploaded, else <code>false</code>
    */
   public boolean upload(File file) {
      long startUpload = System.currentTimeMillis();
      if (!checkConnection()) {
         error("Unable to upload file " + file + " - not connected");
         return false;
      }

      String outputFile = file.getName();

      // Upload as a tmp file to avoid ingestion before upload is complete
      String tmpOutputFile = file.getName() + ".tmp";
      InputStream input = null;
      try {
         // Check if local file (link) exists anymore - may occur using manual listing 
         // and concurrent access to queue/inprogress list
         // Skip upload in this case
         if (!file.exists()) {
            warn("File " + file + " does not exists in " + destination.getLocalPath()
                  + ". Check this file has already been sent.");
            return true;
         }

         // Check if output file already exists on destination
         // Skip upload in this case
         if (fileExists(outputFile)) {
            warn("File " + outputFile + " already exists on server " + destination.getServer()
                  + " in " + destination.getRemotePath());
            return true;
         }

         input = new FileInputStream(file);

         // try to remove old tmp file if it exists: renaming may have failed
         ftp.deleteFile(tmpOutputFile);

         debug("Localfile transfert start : " + file + " => " + destination.getServer() + ":"
               + tmpOutputFile);
         if (!ftp.storeFile(tmpOutputFile, input)) {
            error("Upload failed: " + file + " to " + tmpOutputFile);
            return false;
         }

      } catch (Exception e) {
         error("Error while uploading: " + file + " to " + tmpOutputFile, e);
         return false;
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (Exception e) {
               error("Unable to close FileInputStream of " + file);
            }
         }
      }

      // Renaming tmp file
      try {
         debug("Renaming " + tmpOutputFile + " to " + outputFile);
         if (!ftp.rename(tmpOutputFile, outputFile)) {
            error("Rename failed: " + tmpOutputFile + " to " + outputFile);
            return false;
         }
      } catch (Exception e) {
         error("Error while renaming: " + tmpOutputFile + " to " + outputFile, e);
         return false;
      }

      long endUpload = System.currentTimeMillis();
      info("Upload completed for " + file + " to " + destination.getServer() + ":"
            + destination.getPort() + " in " + destination.getRemotePath() + " - "
            + (endUpload - startUpload) + " ms");
      return true;
   }

}
