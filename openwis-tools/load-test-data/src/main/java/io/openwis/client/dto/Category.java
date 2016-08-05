package io.openwis.client.dto;

/**
 * An OpenWIS category;
 */
public class Category {
   private long id;
   private String name;

   /**
    * The category ID.
    */
   public long getId() {
      return id;
   }
   
   public void setId(long id) {
      this.id = id;
   }

   /**
    * The category name.
    */
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
}
