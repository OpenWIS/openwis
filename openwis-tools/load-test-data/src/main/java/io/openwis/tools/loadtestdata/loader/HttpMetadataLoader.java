package io.openwis.tools.loadtestdata.loader;

import io.openwis.client.MetadataSource;
import io.openwis.client.OpenWISClient;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class HttpMetadataLoader implements MetadataLoader {
   
   private final OpenWISClient owClient;

   public HttpMetadataLoader(OpenWISClient owClient) {
      super();
      this.owClient = owClient;
   }

   @Override
   public void uploadMetadata(List<URL> urls) {
      
      try {
         // Try to upload a metadata record
         owClient.metadataService().upload("datasets", true, Lists.transform(urls, new Function<URL, MetadataSource>() {
            @Override
            public MetadataSource apply(URL arg0) {
               return MetadataSource.fromUrl(arg0);
            }
         }));
         
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         //IOUtils.closeQuietly(owClient);
      }
   }
}
