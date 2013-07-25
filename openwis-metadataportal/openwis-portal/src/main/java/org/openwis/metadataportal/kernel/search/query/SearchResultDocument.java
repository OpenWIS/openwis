package org.openwis.metadataportal.kernel.search.query;

import java.util.List;

import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;

/**
 * The Interface SearchResultDocument. <P>
 * Explanation goes here. <P>
 */
public interface SearchResultDocument {

   /**
    * Gets the score.
    *
    * @return the score
    */
   float getScore();

   /**
    * Gets the id.
    *
    * @return the id
    */
   String getId();

   /**
    * Gets the field.
    *
    * @param field the field
    * @return the field
    */
   Object getField(IndexField field);

   /**
    * Gets the field as string.
    *
    * @param field the field
    * @return the field as string
    */
   String getFieldAsString(IndexField field);

   /**
    * Gets the field as list of string.
    *
    * @param field the field
    * @return the field as list of string
    */
   List<String> getFieldAsListOfString(IndexField field);

   /**
    * Gets the element.
    *
    * @return the element
    */
   Element getElement();

}
