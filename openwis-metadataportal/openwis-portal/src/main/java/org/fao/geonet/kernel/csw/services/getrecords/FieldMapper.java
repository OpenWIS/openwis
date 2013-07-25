//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.kernel.csw.services.getrecords;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fao.geonet.kernel.csw.CatalogConfiguration;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Element;

/**
 * The Class FieldMapper. <P>
 * Explanation goes here. <P>
 */
public class FieldMapper {

   /**
    * Map.
    *
    * @param field the field
    * @return the index field
    */
   public static IndexField map(String field) {
      Map<String, IndexField> map = CatalogConfiguration.getFieldMapping();
      String key = getAbsolute(field);
      for (Entry<String, IndexField> entry : map.entrySet()) {
         if (key.equalsIgnoreCase(entry.getKey())) {
            return entry.getValue();
         }
      }
      
      // not found, try to find a field value, without mapping
      return IndexField.getField(field);
   }

   /**
    * Gets the mapped fields.
    *
    * @return the mapped fields
    */
   public static Iterable<IndexField> getMappedFields() {
      return CatalogConfiguration.getFieldMapping().values();
   }

   /**
    * Match.
    *
    * @param elem the elem
    * @param elemNames the elem names
    * @return true, if successful
    */
   public static boolean match(Element elem, Set<String> elemNames) {
      String name = elem.getQualifiedName();

      for (String field : elemNames)
         // Here we supposed that namespaces prefix are equals when removing elements
         // when an ElementName parameter is set.
         if (field.equals(name))
            return true;

      return false;
   }

   /**
    * Gets the properties by type.
    *
    * @param type the type
    * @return the properties by type
    */
   public static Set<String> getPropertiesByType(String type) {
      return CatalogConfiguration.getTypeMapping(type);
   }

   /**
    * Gets the absolute.
    *
    * @param field the field
    * @return the absolute
    */
   private static String getAbsolute(String field) {
      if (field.startsWith("./"))
         field = field.substring(2);

      // Remove any namespaces ... to be validated
      if (field.contains(":"))
         field = field.substring(field.indexOf(':') + 1);

      return field.toLowerCase();
   }

}
