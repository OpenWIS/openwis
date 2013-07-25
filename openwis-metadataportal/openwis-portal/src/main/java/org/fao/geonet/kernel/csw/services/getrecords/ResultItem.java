package org.fao.geonet.kernel.csw.services.getrecords;

import java.util.HashMap;
import java.util.Map;

import org.fao.geonet.kernel.search.IndexField;

/**
 * Class containing result items with information retrieved from index.
 */
public class ResultItem {
   /**
    * Metadata identifier
    */
   private final String id;

   /**
    * Other index information declared in {@link FieldMapper}
    */
   private final Map<IndexField, String> hmFields = new HashMap<IndexField, String>();

   /**
    * Instantiates a new result item.
    *
    * @param id the id
    */
   public ResultItem(String id) {
      this.id = id;
   }

   /**
    * Gets the iD.
    *
    * @return the iD
    */
   public String getID() {
      return id;
   }

   /**
    * Adds the.
    *
    * @param field the field
    * @param value the value
    */
   public void add(IndexField field, String value) {
      hmFields.put(field, value);
   }

   /**
    * Gets the value.
    *
    * @param field the field
    * @return the value
    */
   public String getValue(IndexField field) {
      return hmFields.get(field);
   }
}
