/**
 * 
 */
package org.openwis.metadataportal.model.metadata;


/**
 * Delete Metadata object.
 * Handle deletion support for OAI-PMH harvester
 *  
 */
public class DeletedMetadata {

   /**
    * Comment for <code>id</code>
    */
   private int id;
   
   /**
   * Comment for <code>urn</code>
   */
   private String urn;

   /**
   * Comment for <code>schema</code>
   */
   private String schema;

   /**
    * Comment for <code>deletionDate</code>
    */
   private String deletionDate;

   /**
    * Comment for <code>category</code>
    */
   private int category;

   /**
    * Gets the id.
    * @return the id.
    */
   public int getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(int id) {
      this.id = id;
   }

   /**
    * Gets the urn.
    * @return the urn.
    */
   public String getUrn() {
      return urn;
   }

   /**
    * Sets the urn.
    * @param urn the urn to set.
    */
   public void setUrn(String urn) {
      this.urn = urn;
   }

   /**
    * Gets the schema.
    * @return the schema.
    */
   public String getSchema() {
      return schema;
   }

   /**
    * Sets the schema.
    * @param schema the schema to set.
    */
   public void setSchema(String schema) {
      this.schema = schema;
   }

   /**
    * Gets the deletionDate.
    * @return the deletionDate.
    */
   public String getDeletionDate() {
      return deletionDate;
   }

   /**
    * Sets the deletionDate.
    * @param deletionDate the deletionDate to set.
    */
   public void setDeletionDate(String deletionDate) {
      this.deletionDate = deletionDate;
   }

   /**
    * Gets the category.
    * @return the category.
    */
   public int getCategory() {
      return category;
   }

   /**
    * Sets the category.
    * @param category the category to set.
    */
   public void setCategory(int category) {
      this.category = category;
   }

}
