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

package org.fao.geonet.services.main;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.AccessManager;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;

//=============================================================================

/** main.result service. shows search results
  */

public class Result implements Service {
   private ServiceConfig _config;

   //--------------------------------------------------------------------------
   //---
   //--- Init
   //---
   //--------------------------------------------------------------------------

   @Override
   public void init(String appPath, ServiceConfig config) throws Exception {
      _config = config;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Service
   //---
   //--------------------------------------------------------------------------

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      // build result data
      UserSession session = context.getUserSession();

      MetaSearcher searcher = (MetaSearcher) session.getProperty(Geonet.Session.SEARCH_RESULT);

      String range = _config.getValue("range");

      if (range != null)
         if (range.equals("all")) {
            params.addContent(new Element("from").setText("1"));
            params.addContent(new Element("to").setText(searcher.getSize() + ""));
         } else {
            params.addContent(new Element("from").setText("1"));
            params.addContent(new Element("to").setText(range));
         }

      Element result = searcher.present(context, params, _config);

      // add Operations information
      Set<String> ids = getMetadataIds(result);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      Map<String, Set<OperationEnum>> operationsAllowed = getOperations(dbms, session, ids);
      updateInfo(context, result, operationsAllowed, new AccessManager(dbms));

      // Restore last search if set
      String restoreLastSearch = params.getChildText(Geonet.SearchResult.RESTORELASTSEARCH);
      if (restoreLastSearch != null && restoreLastSearch.equals("yes")) {
         Object oldSearcher = session.getProperty(Geonet.Session.LAST_SEARCH_RESULT);
         if (oldSearcher != null) {
            context.info("Restoring last search");
            session.setProperty(Geonet.Session.SEARCH_RESULT, oldSearcher);
         }
      }

      return result;
   }

   /**
    * Update info.
    *
    * @param context the context
    * @param searchResult the search result
    * @param operationsAllowed the operations allowed
    * @param accessManager the access manager
    * @throws Exception the exception
    */
   private void updateInfo(ServiceContext context, Element searchResult,
         Map<String, Set<OperationEnum>> operationsAllowed, AccessManager accessManager)
         throws Exception {
      @SuppressWarnings("unchecked")
      List<Element> elements = searchResult.getChildren();
      Element info;
      String uuid;
      String id;
      Set<OperationEnum> operations;
      for (Element elt : elements) {
         if (elt.getName().equals("request")) {
            continue;
         }
         // get data policy
         info = elt.getChild(Edit.RootChild.INFO, Edit.NAMESPACE);
         if (info != null) {
            uuid = info.getChildText(Edit.Info.Elem.UUID);
            if (uuid != null) {
               operations = operationsAllowed.get(uuid);
               if (operations == null) {
                  operations = Collections.emptySet();
               }
               // Operation
               info.addContent(new Element(Edit.Info.Elem.VIEW).setText(String.valueOf(operations
                     .contains(OperationEnum.VIEW))));
               info.addContent(new Element(Edit.Info.Elem.DOWNLOAD).setText(String
                     .valueOf(operations.contains(OperationEnum.DOWNLOAD))));
               // info.addContent(new Element(Edit.Info.Elem.FEATURED).setText(String
               //   .valueOf(operations.contains(OperationEnum.FEATURED))));
            }
            // Access
            id = info.getChildText(Edit.Info.Elem.ID);
            if (id != null) {
               if (accessManager.canEdit(context, id)) {
                  info.addContent(new Element(Edit.Info.Elem.EDIT).setText(Boolean.TRUE.toString()));
               }
               if (accessManager.isOwner(context, id)) {
                  info.addContent(new Element(Edit.Info.Elem.OWNER).setText(Boolean.TRUE.toString()));
               }
            }
         }
      }
   }

   /**
    * Gets the operations.
    *
    * @param dbms the dbms
    * @param session the session
    * @param metadataId the metadata id
    * @return the operations
    * @throws SQLException the SQL exception
    */
   public Map<String, Set<OperationEnum>> getOperations(Dbms dbms, UserSession session,
         Set<String> metadataId) throws Exception {
      Map<String, Set<OperationEnum>> result;

      // add operations
      GroupManager gm = new GroupManager(dbms);
      List<Group> groups = null;

      if (session.isAuthenticated()) {
         if (session.getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
            groups = gm.getAllGroups();
         } else {
            groups = gm.getAllUserGroups(session.getUserId());
         }
         IDataPolicyManager dpm = new DataPolicyManager(dbms);
         result = dpm.getAllOperationAllowedByMetadataId(metadataId, groups);
      } else {
         result = new LinkedHashMap<String, Set<OperationEnum>>();
         // Always grant VIEW privileges for non authenticated users.
         for (String id : metadataId) {
            result.put(id, Collections.singleton(OperationEnum.VIEW));
         }
      }
      return result;
   }

   /**
    * Gets the metadata ids.
    *
    * @param searchResult the search result
    * @return the data policies
    */
   private Set<String> getMetadataIds(Element searchResult) {
      Set<String> result = new LinkedHashSet<String>();
      @SuppressWarnings("unchecked")
      List<Element> elements = searchResult.getChildren();
      String id;
      for (Element elt : elements) {
         if (elt.getName().equals("request")) {
            continue;
         }
         // get data policy
         Element info = elt.getChild(Edit.RootChild.INFO, Edit.NAMESPACE);
         if (info != null) {
            id = info.getChildText(Edit.Info.Elem.UUID);
            if (id != null) {
               result.add(id);
            }
         }
      }
      return result;
   }
}

//=============================================================================

