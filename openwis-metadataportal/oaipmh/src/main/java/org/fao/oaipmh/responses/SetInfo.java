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

package org.fao.oaipmh.responses;

import java.util.ArrayList;
import java.util.List;

import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.util.Lib;
import org.jdom.Element;

//=============================================================================

public class SetInfo {
   private String spec;

   private String name;

   private List<String> descriptions = new ArrayList<String>();

   /**
    * Default constructor.
    * Builds a SetInfo.
    */
   public SetInfo() {
   }

   //---------------------------------------------------------------------------

   /**
    * Default constructor.
    * Builds a SetInfo.
    * @param spec
    * @param name
    * @param description
    */
   public SetInfo(String spec, String name, String description) {
      this.spec = spec;
      this.name = name;
      this.descriptions.add(description);
   }

   //---------------------------------------------------------------------------

   public SetInfo(Element set) {
      build(set);
   }

   //---------------------------------------------------------------------------
   //---
   //--- API methods
   //---
   //---------------------------------------------------------------------------

   public String getSpec() {
      return spec;
   }

   public String getName() {
      return name;
   }

   //---------------------------------------------------------------------------

   public List<String> getDescriptions() {
      return descriptions;
   }

   //---------------------------------------------------------------------------

   public Element toXml() {
      Element set = new Element("set", OaiPmh.Namespaces.OAI_PMH);

      Lib.add(set, "setSpec", spec);
      Lib.add(set, "setName", name);

      if (!descriptions.isEmpty()) {
         Element desc = new Element("setDescription", OaiPmh.Namespaces.OAI_PMH);
         Element dc = new Element("dc", OaiPmh.Namespaces.OAI_DC);
         dc.setAttribute("schemaLocation", OaiPmh.OAI_DC_SCHEMA_LOCATION, OaiPmh.Namespaces.XSI);
         for (String descr : descriptions) {
            dc.addContent(new Element("description", OaiPmh.Namespaces.DC).setText(descr));
         }
         desc.addContent(dc);
         set.addContent(desc);
      }

      return set;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Private methods
   //---
   //---------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private void build(Element set) {
      spec = set.getChildText("setSpec", OaiPmh.Namespaces.OAI_PMH);
      name = set.getChildText("setName", OaiPmh.Namespaces.OAI_PMH);

      //--- add description information

      for (Element e : (List<Element>) set.getChildren("setDescription", OaiPmh.Namespaces.OAI_PMH)) {
         Element dc = e.getChild("dc", OaiPmh.Namespaces.OAI_DC);
         for (Element des : (List<Element>) dc.getChildren()) {
            descriptions.add(des.getText());
         }
      }
   }

}

//=============================================================================

