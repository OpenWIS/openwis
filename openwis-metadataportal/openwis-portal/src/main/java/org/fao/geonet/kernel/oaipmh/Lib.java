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

package org.fao.geonet.kernel.oaipmh;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jeeves.constants.Jeeves;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.ISearchManager.Searcher;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.fao.oaipmh.exceptions.IdDoesNotExistException;
import org.fao.oaipmh.exceptions.OaiPmhException;
import org.jdom.Element;

//=============================================================================

public class Lib {

   public static final String SESSION_OBJECT = "oai-list-records-result";

   //---------------------------------------------------------------------------
   //---
   //--- API methods
   //---
   //---------------------------------------------------------------------------

   public static String getMetadataSchema(ServiceContext context, String uuid) throws Exception {
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      String query = "SELECT schemaId FROM Metadata WHERE uuid=?";

      List list = dbms.select(query, uuid).getChildren();

      if (list.size() == 0)
         throw new IdDoesNotExistException(uuid);

      Element elem = (Element) list.get(0);

      return elem.getChildText("schemaid");
   }

   //---------------------------------------------------------------------------

   public static String getSchemaUrl(ServiceContext context, String relativePath) {
      SettingInfo si = new SettingInfo(context);

      return si.getSiteUrl() + context.getBaseUrl() + "/" + relativePath;
   }

   //--------------------------------------------------------------------------

   public static boolean existsConverter(String schema, String appPath, String prefix) {
      File f = new File(appPath + Geonet.Path.SCHEMAS + schema + "/convert/" + prefix + ".xsl");
      return f.exists();
   }

   //--------------------------------------------------------------------------

   public static Element transform(String schema, Element md, String uuid, String changeDate,
         String appPath, String targetFormat) throws Exception {

      //--- setup environment

      Element env = new Element("env");

      env.addContent(new Element("uuid").setText(uuid));
      env.addContent(new Element("changeDate").setText(changeDate));

      //--- setup root element

      Element root = new Element("root");
      root.addContent(md);
      root.addContent(env);

      //--- do an XSL transformation

      String styleSheet = appPath + Geonet.Path.SCHEMAS + schema + "/convert/" + targetFormat
            + ".xsl";

      return Xml.transform(root, styleSheet);
   }

   //---------------------------------------------------------------------------

   /**
    * Perform fast searches and return a list of UUIDs.
    * @param context
    * @param params
    * @return
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   public static List<String> search(ServiceContext context, Element params) throws Exception {
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ISearchManager sm = gc.getSearchmanager();

      MetaSearcher searcher = sm.newSearcher(Searcher.INDEX);

      context.debug("Searching with params:\n" + Xml.getString(params));

      searcher.search(context, params, dummyConfig);

      params.addContent(new Element("fast").setText("true"));
      params.addContent(new Element("from").setText("1"));
      params.addContent(new Element("to").setText(searcher.getSize() + ""));

      context.info("Records found : " + searcher.getSize());

      Element records = searcher.present(context, params, dummyConfig);
      records.getChild("summary").detach();

      List<String> result = new ArrayList<String>();
      for (Element rec : (List<Element>) records.getChildren()) {
         Element info = rec.getChild("info", Edit.NAMESPACE);
         result.add(info.getChildText("uuid"));
      }

      return result;
   }

   //---------------------------------------------------------------------------

   public static Element toJeevesException(OaiPmhException e) {
      String msg = e.getMessage();
      String cls = e.getClass().getSimpleName();
      String id = e.getCode();
      Element res = e.getResponse();

      Element error = new Element(Jeeves.Elem.ERROR)
            .addContent(new Element("message").setText(msg)).addContent(
                  new Element("class").setText(cls));

      error.setAttribute("id", id);

      if (res != null) {
         Element elObj = new Element("object");
         elObj.addContent(res.detach());

         error.addContent(elObj);
      }

      return error;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Variables
   //---
   //---------------------------------------------------------------------------

   private static ServiceConfig dummyConfig = new ServiceConfig();
}

//=============================================================================

