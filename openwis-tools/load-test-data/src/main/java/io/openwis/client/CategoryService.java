package io.openwis.client;

import io.openwis.client.dto.Category;

import java.util.List;

/**
 * Provides access to the category service.
 */
public interface CategoryService {

   /**
    * Returns the ID of the category with the specific name.  Returns 0 if no such category exists.
    * 
    * @param name
    * @return
    */
   public long findWithName(String name);
   
   /**
    * List all the categories.
    * 
    * @return
    *    The list of categories.
    */
   public List<Category> list();
}
