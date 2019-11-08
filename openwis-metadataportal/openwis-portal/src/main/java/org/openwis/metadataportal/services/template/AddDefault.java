//=============================================================================
//===	Copyright (C) 2010 Food and Agriculture Organization of the
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

package org.openwis.metadataportal.services.template;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.metadata.ITemplateManager;
import org.openwis.metadataportal.kernel.metadata.TemplateManager;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleStringDTO;

/**
 * A simple service that add all metadata templates available in templates
 * directory.
 * 
 */
public class AddDefault implements Service {
   String templateDirectoryPath;

   public void init(String appPath, ServiceConfig params) throws Exception {
      templateDirectoryPath = appPath + "/WEB-INF/classes/setup/templates";
   }

   /**
    * 
    * schemaList is a list of comma separated schemas to load, if null will
    * load all schema.
    * 
    * @return A report on the template import with information about the status
    *         of the insertion operation (failed|loaded).
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
      // String schemaList = Util.getParam(params, Params.SCHEMA, "");
      SimpleStringDTO schemaName = JeevesJsonWrapper.read(params, SimpleStringDTO.class);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      String owner = context.getUserSession().getUsername();
      SiteSource source = new SiteSource(owner, gc.getSiteId(), gc.getSiteName());

      ITemplateManager templateManager = new TemplateManager(dbms, gc.getDataManager(), gc.getSearchmanager());
      templateManager.addDefaultTemplateFromLocalDirectory(schemaName.getContent(), templateDirectoryPath,
            source);

      return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
   }
}