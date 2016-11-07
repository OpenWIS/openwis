package io.openwis.client.http;

import io.openwis.client.CategoryService;
import io.openwis.client.MetadataService;
import io.openwis.client.MetadataSource;
import io.openwis.client.auth.InvocationFactory;
import io.openwis.client.dto.Acknowledgement;
import io.openwis.client.dto.JsonWrappedXMLResponse;
import io.openwis.client.dto.Metadata;
import io.openwis.client.dto.MonitorCatalogueContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class HttpMetadataService implements MetadataService {
   
   private static final Gson GSON = new Gson();
   
   private final InvocationFactory invocationFactory;
   private final CategoryService categoryService;
   
   public HttpMetadataService(InvocationFactory invocationFactory,
         CategoryService categoryService) {
      super();
      this.invocationFactory = invocationFactory;
      this.categoryService = categoryService;
   }

   @Override
   public void upload(String categoryName, boolean validate,
         List<MetadataSource> sources) throws IOException {
//      throw new UnsupportedOperationException("TODO");
      long catId = categoryService.findWithName(categoryName);
      Validate.isTrue(catId != 0, "No such category: " + categoryName);
      
      MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

      // Add fields
      multipartEntityBuilder.addTextBody("fileType", "single");
      multipartEntityBuilder.addTextBody("stylesheetName", "None");
      multipartEntityBuilder.addTextBody("stylesheet", "");
      multipartEntityBuilder.addTextBody("validationMode", (validate ? "XSD_ONLY" : "NONE"));
      multipartEntityBuilder.addTextBody("categoryId", String.valueOf(catId));
      multipartEntityBuilder.addTextBody("categoryName", categoryName);
      multipartEntityBuilder.addTextBody("files", StringUtils.join(sources, ","));

      // Add the actual content
      for (int i = 0; i < sources.size(); i++) {
         MetadataSource source = sources.get(i);
         InputStream inStream = source.getStream();
         
         System.err.println("Uploading: " + source.getName());
         
         try {
            byte[] content = IOUtils.toByteArray(inStream);
            multipartEntityBuilder.addBinaryBody("metadata-" + i, content, ContentType.APPLICATION_OCTET_STREAM, source.getName());
         } finally {
            IOUtils.closeQuietly(inStream);
         }
      }

      // Send the request
      System.err.println("Uploading metadata");
      invocationFactory.postMultipartForm("srv/en/xml.metadata.insert.upload", multipartEntityBuilder);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<Metadata> list(String searchString) {
      FormPoster poster = invocationFactory.newFormPoster("srv/en/xml.metadata.all")
         .setValue("dir", "ASC")
         .setValue("myMetadataOnly", "false")
         .setValue("sort", "urn")
         .setValue("any", searchString)
         .setValue("searchField", "")
         .setValue("start", "0")
         .setValue("limit", "500");
      JsonWrappedXMLResponse wrappedResponse = invocationFactory.postXmlWrappedJson(poster);
      return wrappedResponse.parseJson(MonitorCatalogueContent.class).getMetadatas();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(List<Metadata> metadatas) {
      List<String> urns = Lists.transform(metadatas, new Function<Metadata, String>() {
         @Override
         public String apply(Metadata arg0) {
            return arg0.getUrn();
         }
      });
      
      JsonWrappedXMLResponse wrappedResponse = invocationFactory.postJsonReturningXmlWrappedJson("srv/en/xml.metadata.remove", urns);
      Acknowledgement ack = wrappedResponse.parseJson(Acknowledgement.class);
      if (! ack.isOk()) {
         throw new RuntimeException("Error removing URNs");
      }
   }
}
