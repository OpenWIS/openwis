/**
 *
 */
package org.openwis.metadataportal.model.metadata;

import java.text.MessageFormat;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A template to create product metadata. <P>
 * Explanation goes here. <P>
 */
@JsonIgnoreProperties({"data"})
public class Template extends AbstractMetadata implements Comparable<Template> {

   /** The display order. */
   private Integer displayOrder;

   /**
    * <code>true</code> if it is a subtemplate, <code>false</code> otherwise.
    */
   private boolean subTemplate;

   /**
    * Default constructor.
    * Builds a Template.
    */
   public Template() {
      super();
   }

   /**
    * Default constructor.
    * Builds a Template.
    * @param id the id of the template.
    */
   public Template(String urn) {
      super(urn);
   }

   /**
    * Default constructor.
    * Builds a Template.
    * @param id
    * @param name
    * @param displayOrder
    */
   public Template(Integer id, String urn) {
      super(id, urn);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[{0}] {1}", getId(), getTitle());
   }

   /**
    * Gets the displayOrder.
    * @return the displayOrder.
    */
   public Integer getDisplayOrder() {
      return displayOrder;
   }

   /**
    * Sets the displayOrder.
    * @param displayOrder the displayOrder to set.
    */
   public void setDisplayOrder(Integer displayOrder) {
      this.displayOrder = displayOrder;
   }

   /**
    * Gets the subTemplate.
    * @return the subTemplate.
    */
   public boolean isSubTemplate() {
      return subTemplate;
   }

   /**
    * Sets the subTemplate.
    * @param subTemplate the subTemplate to set.
    */
   public void setSubTemplate(boolean subTemplate) {
      this.subTemplate = subTemplate;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(Template o) {
      return getDisplayOrder().compareTo(o.getDisplayOrder());
   }
}
