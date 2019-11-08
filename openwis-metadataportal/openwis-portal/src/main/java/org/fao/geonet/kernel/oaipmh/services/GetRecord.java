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

package org.fao.geonet.kernel.oaipmh.services;

import java.text.MessageFormat;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.oaipmh.Lib;
import org.fao.geonet.kernel.oaipmh.OaiPmhService;
import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.exceptions.CannotDisseminateFormatException;
import org.fao.oaipmh.exceptions.IdDoesNotExistException;
import org.fao.oaipmh.requests.AbstractRequest;
import org.fao.oaipmh.requests.GetRecordRequest;
import org.fao.oaipmh.responses.AbstractResponse;
import org.fao.oaipmh.responses.GetRecordResponse;
import org.fao.oaipmh.responses.Header;
import org.fao.oaipmh.responses.Record;
import org.fao.oaipmh.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.DeletedMetadataManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;

//=============================================================================

public class GetRecord implements OaiPmhService
{
	public String getVerb() { return GetRecordRequest.VERB; }

	//---------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//---------------------------------------------------------------------------

	public AbstractResponse execute(AbstractRequest request, ServiceContext context) throws Exception
	{
		GetRecordRequest  req = (GetRecordRequest) request;
		GetRecordResponse res = new GetRecordResponse();

		String uuid   = req.getIdentifier();
		String prefix = req.getMetadataPrefix();
		
		res.setRecord(buildRecord(context, null, uuid, prefix, null));

		return res;
	}
	
	
	/**
    * Build a record for a GetRecord and a ListRecords response.
    * 
    * @param context
    * @param dbms null in case of GetRecord request.
    * @param urn the metadata URN
    * @param prefix the metadata prexif
    * @param category category; if null -> not a deleted md (ie GetRecord)
    * @return a record
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   protected static Record buildRecord(ServiceContext context, Dbms dbms, String urn, String prefix, Category category)
         throws Exception {
      // In case of GetRecord request.
      if (dbms == null) {
         dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      }

      // Check if it's a deleted metadata or metadata removed from a category
      if (category != null) {
      DeletedMetadata dm = new DeletedMetadataManager(dbms).getDeletedMetadataByUrn(urn, category);
         if (dm != null) {
            Header h =  AbstractTokenLister.buildHeaderFromDeletedMetadata(dbms, dm, prefix, context.getAppPath());
            Record record = new Record();
            record.setHeader(h);
            return record;
         }
      }

      String query = "SELECT schemaId, localimportdate, data FROM Metadata WHERE uuid=?";
      List<Element> list = dbms.select(query, urn).getChildren();

      // The metadata has been removed
      if (list.isEmpty()) {
         Log.warning(Geonet.OAI,
               MessageFormat.format("No record matching the specified metadata URN : {0}", urn));
         throw new IdDoesNotExistException(urn);
      }

      Element rec = list.get(0);

      String schema = rec.getChildText("schemaid");
      String localImportDate = rec.getChildText("localimportdate");
      String data = rec.getChildText("data");

      Element md = Xml.loadString(data, false);

      //--- try to disseminate format

      if (prefix.equals(schema)) {
         // Set a schema location only if not exists in metadata.
         String schemaLocation = "schemaLocation";
         if (md.getAttribute(schemaLocation, OaiPmh.Namespaces.XSI) == null) {
            String schemaUrl = Lib.getSchemaUrl(context, "xml/schemas/" + schema + "/schema.xsd");
            String schemaLoc = md.getNamespace().getURI() + " " + schemaUrl;
            md.setAttribute(schemaLocation, schemaLoc, OaiPmh.Namespaces.XSI);
         }
      } else {
         if (Lib.existsConverter(schema, context.getAppPath(), prefix)) {
            md = Lib.transform(schema, md, urn, localImportDate, context.getAppPath(), prefix);
         } else {
            Log.warning(Geonet.OAI,
                  MessageFormat.format("Unknown prefix : {0} for metadata URN : {1}", prefix, urn));
            throw new CannotDisseminateFormatException("Unknown prefix : "+ prefix);
         }
      }

      //--- build header and set some infos

      Header h = new Header();

      h.setIdentifier(urn);
      h.setDateStamp(new ISODate(localImportDate));

      String categoryName = null;
      if (category == null || category.getId() == null) {
         CategoryManager cm = new CategoryManager(dbms);
         Category cat = cm.getCategoryByMetadataUrn(urn);
         if (cat != null) {
            categoryName = cat.getName();
         }
      } else {
         // the category is given by the request itself
         categoryName = category.getName();
      }
      h.addSet(categoryName);

      // Build and return record
      Record r = new Record();

      r.setHeader(h);
      r.setMetadata(md);

      return r;
   }

}

//=============================================================================

