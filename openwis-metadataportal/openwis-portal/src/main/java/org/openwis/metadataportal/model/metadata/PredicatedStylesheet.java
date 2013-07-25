/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

import org.jdom.Element;

import com.google.common.base.Predicate;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class PredicatedStylesheet {

   private String stylesheet;

   private Predicate<Element> predicate;

   //------------------------------------------------ Constructors.

   /**
    * Default constructor.
    * Builds a PredicatedStylesheet.
    * @param stylesheet the style sheet to apply.
    * @param predicate the predicate used to apply the style sheet or not.
    */
   public PredicatedStylesheet(String stylesheet, Predicate<Element> predicate) {
      super();
      this.stylesheet = stylesheet;
      this.predicate = predicate;
   }

   //------------------------------------------------ Getters & Setters

   /**
    * Gets the stylesheet.
    * @return the stylesheet.
    */
   public String getStylesheet() {
      return stylesheet;
   }

   /**
    * Sets the stylesheet.
    * @param stylesheet the stylesheet to set.
    */
   public void setStylesheet(String stylesheet) {
      this.stylesheet = stylesheet;
   }

   /**
    * Gets the predicate.
    * @return the predicate.
    */
   public Predicate<Element> getPredicate() {
      return predicate;
   }

   /**
    * Sets the predicate.
    * @param predicate the predicate to set.
    */
   public void setPredicate(Predicate<Element> predicate) {
      this.predicate = predicate;
   }
}
