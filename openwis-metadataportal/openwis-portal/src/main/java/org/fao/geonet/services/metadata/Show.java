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

package org.fao.geonet.services.metadata;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.exceptions.MetadataNotFoundEx;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.services.Utils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;

//=============================================================================

/**
 * Retrieves a particular metadata. Access is restricted
 */
public class Show implements Service {

   private boolean skipPopularity;

   private boolean skipInfo;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      String skip;

      skip = params.getValue("skipPopularity", "n");
      skipPopularity = skip.equals("y");

      skip = params.getValue("skipInfo", "n");
      skipInfo = skip.equals("y");
   }

   //--------------------------------------------------------------------------
   //---
   //--- Service
   //---
   //--------------------------------------------------------------------------

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      // Handle current tab
      EditUtils.setCurrTab(params, context);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      DataManager dm = gc.getDataManager();

      String id = Utils.getIdentifierFromParameters(params, context);

      if (id == null) {
         throw new MetadataNotFoundEx("Metadata not found.");
      }

      // Check access
      Lib.resource.checkPrivilege(context, id, OperationEnum.VIEW.getId());

      // Get the metadata
      boolean addEditing = false;
      Element elMd;
      if (!skipInfo) {
         elMd = dm.getMetadata(context, id, addEditing);
      } else {
         elMd = dm.getMetadataNoInfo(context, id);
      }

      if (elMd == null)
         throw new MetadataNotFoundEx(id);

      // FIXME setting schemaLocation - OpenWIS check metadata integrity
      // TODO currently it's only set for ISO metadata - this should all move to
      // the updatefixedinfo.xsl for each schema
      // document has ISO root element and ISO namespace
      if (elMd.getAttribute("schemaLocation", Csw.NAMESPACE_XSI) == null) {
         Namespace gmdNs = elMd.getNamespace("gmd");
         if (gmdNs != null && gmdNs.getURI().equals("http://www.isotc211.org/2005/gmd")) {
            // document gets default gmd namespace schemalocation
            String locations = "http://www.isotc211.org/2005/gmd http://www.ngdc.noaa.gov/metadata/published/xsd/schema/gmd/gmd.xsd";
            // if document has srv namespace then add srv namespace location
            if (elMd.getNamespace("srv") != null) {
               locations += " http://www.isotc211.org/2005/srv http://schemas.opengis.net/iso/19139/20060504/srv/srv.xsd";
            }

            if (elMd.getNamespace("gmx") != null) {
               locations += " http://www.isotc211.org/2005/gmx http://www.ngdc.noaa.gov/metadata/published/xsd/schema/gmx/gmx.xsd";
            }

            Attribute schemaLocation = new Attribute("schemaLocation", locations, Csw.NAMESPACE_XSI);
            elMd.setAttribute(schemaLocation);
         }
      }

      //--- increase metadata popularity

      if (!skipPopularity) {
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         String uuid = dm.getMetadataUuid(dbms, id);
         dm.increasePopularity(dbms, uuid);
      }

      return elMd;
   }

}
//=============================================================================

