package io.openwis.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A source of metadata.
 */
public abstract class MetadataSource {

   /**
    * Returns an input stream to the metadata.
    */
   public abstract InputStream getStream() throws IOException;
   
   /**
    * Returns the name of the metadata source.
    */
   public abstract String getName();
   
   /**
    * {@inheritDoc}
    * 
    * This returns the name of this metadata source.
    */
   public String toString() {
      return getName();
   }
   
   /**
    * Creates a new metadata source from a local file.
    * 
    * @param file
    * @return
    */
   public static MetadataSource fromFile(final File file) {
      return new MetadataSource() {
         @Override
         public InputStream getStream() throws IOException {
            return new FileInputStream(file);
         }

         @Override
         public String getName() {
            return file.getName();
         }
      };
   }
   
   /**
    * Creates a new metadata source from a URL.
    * 
    * @param url
    * @return
    */
   public static MetadataSource fromUrl(final URL url) {
      return new MetadataSource() {
         @Override
         public InputStream getStream() throws IOException {
            return url.openStream();
         }
         
         @Override
         public String getName() {
            return new File(url.getPath()).getName();
         }
      };
   }
}
