package io.openwis.client.http;

import io.openwis.client.CategoryService;
import io.openwis.client.MetadataService;
import io.openwis.client.OpenWISClient;
import io.openwis.client.auth.Authentication;
import io.openwis.client.auth.InvocationFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.Validate;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * An OpenWIS client which communicates to OpenWIS via the HTTP service layer.
 */
public class HttpOpenWISClient implements OpenWISClient {
   private final CloseableHttpClient client;
   private final URI baseTarget;
   private final InvocationFactory invocationFactory;
   
   private final CategoryService categoryService;
   private final MetadataService metadataService;
   
   public HttpOpenWISClient(String adminUrl, Authentication loginCredentials) {
      super();
      
      Validate.notNull(adminUrl);
      
      BasicCookieStore cookieStore = new BasicCookieStore();
      client = HttpClients.custom()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .setDefaultCookieStore(cookieStore)
            .build();
      
      // Adds a trailing slash if one is not present
      if (!adminUrl.endsWith("/")) {
         adminUrl = adminUrl + "/";
      }
      
      try {
         this.baseTarget = new URI(adminUrl);
      } catch (URISyntaxException e) {
         throw new RuntimeException("Bad URL: " + adminUrl);
      }
      loginCredentials.authenticate(client, baseTarget);

      this.invocationFactory = new InvocationFactory(client, baseTarget);
      this.categoryService = new HttpCategoryService(invocationFactory);
      this.metadataService = new HttpMetadataService(invocationFactory, categoryService);
   }


   /**
    * Closes the session with OpenWIS.
    */
   @Override
   public void close() throws IOException {
//      Response resp = invocationFactory.requestToPath("openWisLogout").post(Entity.form(new Form()));
//      resp.getStatus();
      
      client.close();
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public CategoryService categoryService() {
      return categoryService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MetadataService metadataService() {
      return metadataService;
   }
}
