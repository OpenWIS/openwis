package io.openwis.client;

import io.openwis.client.dto.Metadata;

import java.io.IOException;
import java.util.List;

/**
 * Provides access to the meta-data services.
 */
public interface MetadataService {

   /**
    * Upload metadata records to the category with the specific name.
    * 
    * @param categoryName
    *       The category name.
    * @param validate
    *       <code>true</code> if the metadata is to be validated, <code>false</code> otherwise.
    * @param sources
    *       The metadata records to upload.
    */
   public void upload(String categoryName, boolean validate, List<MetadataSource> sources) throws IOException;

   /**
    * Return a list of metadata records that match the search string.
    * 
    * @param searchString
    *       The search string.
    * @return
    *       The list of metadata records that match search string.
    */
   public Iterable<Metadata> list(String searchString);
   
   /**
    * Remove metadatas with the given URN.
    * 
    * @param urns
    *       The URNs to remove
    */
   public void remove(List<Metadata> metadatas);
}
