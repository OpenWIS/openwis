package io.openwis.client;

import java.io.Closeable;

/**
 * A client to OpenWIS.
 */
public interface OpenWISClient extends Closeable {

   /**
    * Provides access to the category service.
    */
   public CategoryService categoryService();
   
   /**
    * Provides access to the metadata service.
    */
   public MetadataService metadataService();
}
