package org.openwis.dataservice.replication.ftp.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Configuration settings of FTP replicator.
 */
public class Config {

   private static final Logger LOG = LoggerFactory.getLogger(Config.class);

   /** Configuration file to read this Config instance */
   private static final String CONFIG_FILE = "ftp-replication-config.xml";

   /** Configuration file name can be overridden by this system property */
   private static final String CONFIG_FILE_PROPERTY = "ftpReplicationConfigFile";

   // Config singleton
   private static Config instance = readConfig();

   public static Config getInstance() {
      return instance;
   }

   /** List of destinations of replication */
   private List<Destination> destinations;

   /** Folder of "sending/destinations", containing the files to send for each destination */
   private String destinationsFolder;

   /** Folder in which the replication status file will be dropped */
   private String replicationStatusFolder;
   
   /** Period of dir monitoring for the ManualSendingScanner */
   private int sendingScannerPeriod;

   /** Use JNotify scanner */
   private boolean useJNotifyScanner;

   /**
    * @return Period of dir monitoring for the ManualSendingScanner
    */
   public int getSendingScannerPeriod() {
      return sendingScannerPeriod;
   }

   public void setSendingScannerPeriod(int sendingScannerPeriod) {
      this.sendingScannerPeriod = sendingScannerPeriod;
   }

   /**
    * @return Folder in which the replication status file will be dropped
    */
   public String getReplicationStatusFolder() {
      return replicationStatusFolder;
   }

   public void setReplicationStatusFolder(String replicationStatusFolder) {
      this.replicationStatusFolder = replicationStatusFolder;
   }

   /**
    * @return Folder of "sending/destinations", containing the files to send for each destination
    */
   public String getDestinationsFolder() {
      return destinationsFolder;
   }

   public void setDestinationsFolder(String destinationsFolder) {
      this.destinationsFolder = destinationsFolder;
   }

   /**
    * @return List of destinations of replication
    */
   public List<Destination> getDestinations() {
      return destinations;
   }

   public void setDestinations(List<Destination> destinations) {
      this.destinations = destinations;
   }

   /**
    * Get the destination absolute local folder for a given destination.
    * @param d the {@link Destination}
    * @return the folder
    */
   public String getDestinationAbsoluteLocalFolder(Destination d) {
      return new File(destinationsFolder, d.getLocalPath()).getAbsolutePath();
   }

   /**
    * Create and configure XStream to read the Config from file.
    */
   private static XStream configureXStream() {
      XStream xStream = new XStream(new StaxDriver());
      xStream.alias("Replication-Config", Config.class);
      xStream.alias("Destination", Destination.class);
      xStream.useAttributeFor(Destination.class, "server");
      xStream.alias("Destination", Destination.class);
      return xStream;
   }

   /** Test only to generate the config file */
   @SuppressWarnings("unused")
   private static void writeConfig() {
      try {
         FileOutputStream out = new FileOutputStream(CONFIG_FILE);

         XStream xStream = configureXStream();

         // Serialize state as xml
         xStream.toXML(instance, out);
      } catch (Exception e) {
         LOG.error("Unable to write config", e);
      }
   }

   /**
    * Get the Config instance from config file.
    */
   private static Config readConfig() {
      // Check if config file name is overridden
      String configFile = System.getProperty(CONFIG_FILE_PROPERTY, CONFIG_FILE);
      
      try {
         InputStream in = Config.class.getClassLoader().getResourceAsStream(configFile);
         XStream xStream = configureXStream();
         return (Config) xStream.fromXML(in);
      } catch (Exception e) {
         LOG.error("Unable to read config", e);
         return new Config();
      }
   }

   public boolean isUseJNotifyScanner() {
      return useJNotifyScanner;
   }

   public void setUseJNotifyScanner(boolean useJNotifyScanner) {
      this.useJNotifyScanner = useJNotifyScanner;
   }

}
