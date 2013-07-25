/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

import org.jdom.Element;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.source.AbstractSource;

import com.google.common.base.Objects;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public abstract class AbstractMetadata {

   /**
    * The metadata <code>id</code>
    */
   private Integer id;

   /**
    * The metadata <code>urn</code>
    */
   private String urn;

   /**
    * The metadata <code>title</code>
    */
   private String title;

   /**
    * Comment for <code>createDate</code>
    */
   private String createDate;

   /**
    * The <code>changeDate</code>
    */
   private String changeDate;

   /**
    * Comment for <code>schema</code>
    */
   private String schema;

   /**
    * Comment for <code>data</code>
    */
   private Element data;

   /**
    * The source.
    */
   private AbstractSource source;

   /**
    * The data policy.
    */
   private DataPolicy dataPolicy;

   /**
    * Default constructor.
    * Builds a AbstractMetadata.
    */
   public AbstractMetadata() {
   }

   /**
    * Default constructor.
    * Builds a AbstractMetadata.
    * @param urn
    */
   public AbstractMetadata(String urn) {
      this.urn = urn;
   }

   /**
    * Default constructor.
    * Builds a AbstractMetadata.
    * @param id the metadata ID
    * @param urn the metadata URN
    */
   public AbstractMetadata(Integer id, String urn) {
      this.id = id;
      this.urn = urn;
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public Integer getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(Integer id) {
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
    * Gets the title.
    * @return the title.
    */
   public String getTitle() {
      return title;
   }

   /**
    * Sets the title.
    * @param title the title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * Gets the createDate.
    * @return the createDate.
    */
   public String getCreateDate() {
      return createDate;
   }

   /**
    * Sets the createDate.
    * @param createDate the createDate to set.
    */
   public void setCreateDate(String createDate) {
      this.createDate = createDate;
   }

   /**
    * Gets the changeDate.
    * @return the changeDate.
    */
   public String getChangeDate() {
      return changeDate;
   }

   /**
    * Sets the changeDate.
    * @param changeDate the changeDate to set.
    */
   public void setChangeDate(String changeDate) {
      this.changeDate = changeDate;
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
    * Gets the data.
    * @return the data.
    */
   public Element getData() {
      return data;
   }

   /**
    * Sets the data.
    * @param data the data to set.
    */
   public void setData(Element data) {
      this.data = data;
   }

   /**
    * Gets the source.
    * @return the source.
    */
   public AbstractSource getSource() {
      return source;
   }

   /**
    * Sets the source.
    * @param source the source to set.
    */
   public void setSource(AbstractSource source) {
      this.source = source;
   }

   /**
    * Gets the dataPolicy.
    * @return the dataPolicy.
    */
   public DataPolicy getDataPolicy() {
      return dataPolicy;
   }

   /**
    * Sets the dataPolicy.
    * @param dataPolicy the dataPolicy to set.
    */
   public void setDataPolicy(DataPolicy dataPolicy) {
      this.dataPolicy = dataPolicy;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(urn);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AbstractMetadata) {
         AbstractMetadata m = (AbstractMetadata) obj;
         return Objects.equal(urn, m.getUrn());
      }
      return false;
   }

}
