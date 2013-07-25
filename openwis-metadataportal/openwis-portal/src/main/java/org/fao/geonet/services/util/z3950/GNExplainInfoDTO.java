//=============================================================================
//===  Copyright (C) 2009 World Meteorological Organization
//===  This program is free software; you can redistribute it and/or modify
//===  it under the terms of the GNU General Public License as published by
//===  the Free Software Foundation; either version 2 of the License, or (at
//===  your option) any later version.
//===
//===  This program is distributed in the hope that it will be useful, but
//===  WITHOUT ANY WARRANTY; without even the implied warranty of
//===  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===  General Public License for more details.
//===
//===  You should have received a copy of the GNU General Public License
//===  along with this program; if not, write to the Free Software
//===  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===  Contact: Timo Proescholdt
//===  email: tproescholdt_at_wmo.int
//==============================================================================

package org.fao.geonet.services.util.z3950;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Data transport object for explain operation.
 *
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 */
public class GNExplainInfoDTO {

   /** The id. */
   String id;

   /** The title. */
   String title;

   /** The map. */
   Map<String, String> map = new HashMap<String, String>();

   /**
    * Instantiates a new gN explain info dto.
    */
   public GNExplainInfoDTO() {

   }

   /**
    * Instantiates a new gN explain info dto.
    *
    * @param id the id
    */
   public GNExplainInfoDTO(String id) {
      this.id = id;
   }

   /**
    * Instantiates a new gN explain info dto.
    *
    * @param id the id
    * @param title the title
    */
   public GNExplainInfoDTO(String id, String title) {
      this.id = id;
      this.title = title;
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * Gets the title.
    *
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * Gets the mappings.
    *
    * @return the mappings
    * map containing indices (and their namespaces) that map to
    * this index
    */
   public Map<String, String> getMappings() {
      return map;
   }

   /**
    * Adds the mapping.
    *
    * @param index the index
    * @param namespace the namespace
    */
   public void addMapping(String index, String namespace) {
      map.put(index, namespace);
   }

}
