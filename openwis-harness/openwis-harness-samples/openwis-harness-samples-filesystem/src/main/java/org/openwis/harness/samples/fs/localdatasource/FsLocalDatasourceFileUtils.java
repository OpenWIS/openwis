/**
 * 
 */
package org.openwis.harness.samples.fs.localdatasource;

import java.io.File;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.openwis.harness.samples.common.extraction.LocalDatasourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileNameUtils. <P>
 * Explanation goes here. <P>
 */
public class FsLocalDatasourceFileUtils extends LocalDatasourceUtils {
   /** The Constant PROPERTY_FOLDER_ROOT. */
   private static final String PROPERTY_FOLDER_ROOT = "FileSystemLocalDatasource.folder.root";

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(FsLocalDatasourceFileUtils.class);

   /** The root folder. */
   private final File rootFolder;

   /** The product folder pattern. */
   private final String productFolderPattern;

   /** The encoder. */
   private final Hex encoder;

   /**
    * Default constructor.
    * Builds a LocalDatasourceFileUtils.
    *
    * @param props the properties
    */
   public FsLocalDatasourceFileUtils(Properties props) {
      super(props);

      // Root folder
      rootFolder = new File(props.getProperty(PROPERTY_FOLDER_ROOT));
      logger.debug("Root folder: {}", rootFolder);

      // Product folder pattern
      productFolderPattern = "%1$tY/%1$tm/%1$td/%1$tH_%1$tM";
      logger.debug("Product folder pattern: {}", productFolderPattern);

      // urn encoder
      encoder = new Hex();
   }

   /**
    * Gets the metadata file.
    *
    * @param metadataURN the metadata urn
    * @return the metadata file
    */
   public File getMetadataFile(String metadataURN) {
      String filename = escape(metadataURN);
      return new File(rootFolder, filename);
   }

   /**
    * Escape.
    *
    * @param name the name
    * @return the string
    */
   private String escape(String name) {
      String result = Hex.encodeHexString(name.getBytes());
      return result;
   }

   /**
    * Gets the metadata urn.
    *
    * @param file the file
    * @return the metadata urn
    */
   public String getMetadataURN(File file) {
      String filename = file.getName();
      String urn = unescape(filename);
      return urn;
   }

   /**
    * Unescape.
    *
    * @param filename the filename
    * @return the string
    */
   private String unescape(String filename) {
      String result;
      try {
         result = new String((byte[]) encoder.decode(filename));
      } catch (DecoderException e) {
         logger.error("Could not decode file", e);
         throw new IllegalArgumentException(e);
      }
      return result;
   }

   /**
    * Gets the product file.
    *
    * @param metadataURN the metadata urn
    * @param productURN the product urn
    * @param timestamp the timestamp
    * @return the product file
    */
   public File getProductFile(String metadataURN, String productURN, Calendar timestamp) {
      File folder = getProductFolder(metadataURN, timestamp);
      return new File(folder, escape(productURN));
   }

   /**
    * Gets the product folder.
    *
    * @param metadataURN the metadata urn
    * @param timestamp the timestamp
    * @return the product folder
    */
   public File getProductFolder(String metadataURN, Calendar timestamp) {
      String path = String.format(productFolderPattern, timestamp);
      path = FilenameUtils.separatorsToSystem(path);
      return new File(getMetadataFile(metadataURN), path);
   }

   /**
    * Gets the root folder.
    *
    * @return the root folder
    */
   public File getRootFolder() {
      return rootFolder;
   }

}
