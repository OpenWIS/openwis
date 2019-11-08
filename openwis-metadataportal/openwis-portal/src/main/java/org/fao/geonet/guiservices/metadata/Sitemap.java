//==============================================================================
//===
//=== Sitemap
//===
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

package org.fao.geonet.guiservices.metadata;

import java.util.Collection;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.AccessManager;
import org.jdom.Element;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;

/**
 * Create a sitemap document with metadata records
 *
 * Formats: xml (default if not specified), html
 *
 * See http://www.sitemaps.org/protocol.php
 */
public class Sitemap implements Service
{
    private final String FORMAT_XML = "xml";
    private final String FORMAT_HTML = "html";

    public void init(String appPath, ServiceConfig config) throws Exception { }

    public Element exec(Element params, ServiceContext context) throws Exception
    {
        String format = Util.getParam(params, "format", FORMAT_XML);
        if (!((format.equalsIgnoreCase(FORMAT_HTML)) ||
           (format.equalsIgnoreCase(FORMAT_XML)))) {

            format = FORMAT_XML;
        }

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        AccessManager am = new AccessManager(dbms);

        Collection<Group> groups = am.getUserGroups(context.getUserSession());

        String query = "SELECT DISTINCT id, uuid, schemaId, changeDate FROM Metadata, OperationAllowed "+
                            "WHERE Metadata.datapolicy=OperationAllowed.datapolicyId " +
                            "AND Metadata.isTemplate='n' AND OperationAllowed.operationId="
                            +OperationEnum.VIEW.getId()+" AND (";

        String aux = "";

        for (Group grp : groups)
            aux += " OR groupId="+grp.getId().toString();

        query += aux.substring(4);
        query += ") ORDER BY changeDate DESC";

        Element result = dbms.select(query);

        Element formatEl = new Element("format");
        formatEl.setText(format.toLowerCase());

        result.addContent(formatEl);
        
        return result;
    }
}
