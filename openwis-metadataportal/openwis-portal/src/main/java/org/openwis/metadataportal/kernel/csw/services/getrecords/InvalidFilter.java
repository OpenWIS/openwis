package org.openwis.metadataportal.kernel.csw.services.getrecords;

import org.jdom.Element;

/**
 * The Class InvalidFilter. <P>
 * Explanation goes here. <P>
 */
public class InvalidFilter extends Exception {

   /** The filter element. */
   private final Element filterElt;

   /**
    * Instantiates a new invalid filter.
    *
    * @param filterElt the filter element
    */
   public InvalidFilter(Element filterElt) {
      super();
      this.filterElt = filterElt;
   }

   /**
    * Instantiates a new invalid filter.
    *
    * @param filterElt the filter element
    * @param cause the cause
    */
   public InvalidFilter(Element filterElt, Throwable cause) {
      super(cause);
      this.filterElt = filterElt;
   }

   /**
    * Gets the filter.
    *
    * @return the filter
    */
   public Element getFilter() {
      return filterElt;
   }

}
