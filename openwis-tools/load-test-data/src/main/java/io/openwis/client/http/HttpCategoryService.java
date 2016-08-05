package io.openwis.client.http;

import io.openwis.client.CategoryService;
import io.openwis.client.auth.InvocationFactory;
import io.openwis.client.dto.Category;
import io.openwis.client.dto.JsonWrappedXMLResponse;

import java.util.List;

import com.google.gson.reflect.TypeToken;

/**
 * Implementation of the category service. 
 */
public class HttpCategoryService implements CategoryService {
   
   private final InvocationFactory invocationFactory;
   
   public HttpCategoryService(InvocationFactory invocationFactory) {
      super();
      this.invocationFactory = invocationFactory;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public long findWithName(String name) {
      List<Category> categories = list();
      for (Category c : categories) {
         if (c.getName().equals(name)) {
            return c.getId();
         }
      }
      
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Category> list() {
      JsonWrappedXMLResponse res = invocationFactory.getXmlWrappedJson("srv/en/xml.category.all");
      TypeToken<List<Category>> categoryListType = new TypeToken<List<Category>>() { };

      return res.parseJson(categoryListType);
   }
}
