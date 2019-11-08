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

import java.util.Hashtable;
import java.util.List;

import jeeves.exceptions.BadParameterEx;
import jeeves.exceptions.OperationNotAllowedEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.exceptions.ConcurrentUpdateEx;
import org.fao.geonet.kernel.AccessManager;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.services.Utils;
import org.jdom.Element;

//=============================================================================

/** Utilities
  */

class EditUtils {
    //--------------------------------------------------------------------------
    //---
    //--- API methods
    //---
    //--------------------------------------------------------------------------

    /** Perform common editor preprocessing tasks
      */

    public static void preprocessUpdate(Element params, ServiceContext context) throws Exception {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        DataManager dataMan = gc.getDataManager();
        UserSession session = context.getUserSession();
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        AccessManager accessMan = new AccessManager(dbms);

        String id = Utils.getIdentifierFromParameters(params, context);

        //-----------------------------------------------------------------------
        //--- handle current tab and position

        Element elCurrTab = params.getChild(Params.CURRTAB);
        Element elCurrPos = params.getChild(Params.POSITION);

        if (elCurrTab != null) {
            session.setProperty(Geonet.Session.METADATA_SHOW, elCurrTab.getText());
        }
        if (elCurrPos != null)
            session.setProperty(Geonet.Session.METADATA_POSITION, elCurrPos.getText());

        //-----------------------------------------------------------------------
        //--- check access
        int iLocalId = Integer.parseInt(id);

        if (!dataMan.existsMetadata(dbms, iLocalId))
            throw new BadParameterEx("id", id);

        if (!accessMan.canEdit(context, id))
            throw new OperationNotAllowedEx();
    }

    //--------------------------------------------------------------------------
    /** Update metadata content
      */

    public static void updateContent(Element params, ServiceContext context, boolean validate)
            throws Exception {
        updateContent(params, context, validate, false);

    }

    @SuppressWarnings("unchecked")
   public static void updateContent(Element params, ServiceContext context, boolean validate,
            boolean embedded) throws Exception {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        DataManager dataMan = gc.getDataManager();

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        String id = Util.getParam(params, Params.ID);
        String version = Util.getParam(params, Params.VERSION);

        //--- build hashtable with changes
        //--- each change is a couple (pos, value)

        Hashtable<String, String> htChanges = new Hashtable<String, String>(100);

        List<Element> list = params.getChildren();

        for (Element el : list) {
            String sPos = el.getName();
            String sVal = el.getText();

            if (sPos.startsWith("_"))
                htChanges.put(sPos.substring(1), sVal);
        }

        //-----------------------------------------------------------------------
        //--- update element and return status

        boolean result;
        if (embedded) {
            result = dataMan.updateMetadataEmbedded(context.getUserSession(), dbms, id, version,
                    htChanges, context.getLanguage());
        } else {
            result = dataMan.updateMetadata(context.getUserSession(), dbms, id, version, htChanges,
                    validate, context.getLanguage());
        }

        if (!result)
            throw new ConcurrentUpdateEx(id);

    }

    //--------------------------------------------------------------------------

    public static void updateContent(Element params, ServiceContext context) throws Exception {
        updateContent(params, context, false);
    }

    public static void setCurrTab(Element params, ServiceContext context) {
        Element elCurrTab = params.getChild(Params.CURRTAB);

        if (elCurrTab != null)
            context.getUserSession().setProperty(Geonet.Session.METADATA_SHOW, elCurrTab.getText());
    }
}

//=============================================================================

