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

package org.fao.geonet.kernel.mef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jeeves.exceptions.BadFormatEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.Log;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.ISODate;
import org.fao.oaipmh.exceptions.BadArgumentException;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.services.metadata.dto.ImportMetadataDTO;

public class Importer {
	public static List<String> doImport(final Element params, final ImportMetadataDTO dto,
			final ServiceContext context, File mefFile, final String stylePath)
			throws Exception {
			return doImport(params, dto, context, mefFile, stylePath, false);
	}

	public static List<String> doImport(final Element params, final ImportMetadataDTO dto,
			final ServiceContext context, File mefFile, final String stylePath,
			final boolean indexGroup) throws Exception {
		final GeonetContext gc = (GeonetContext) context
				.getHandlerContext(Geonet.CONTEXT_NAME);
		final DataManager dm = gc.getDataManager();

		// Load preferred schema and set to iso19139 by default
		final String preferredSchema = (gc.getHandlerConfig()
				.getMandatoryValue("preferredSchema") != null ? gc
				.getHandlerConfig().getMandatoryValue("preferredSchema")
				: "iso19139");

		final Dbms dbms = (Dbms) context.getResourceManager().open(
				Geonet.Res.MAIN_DB);

		final List<String> id = new ArrayList<String>();
		final List<Element> md = new ArrayList<Element>();
		final List<Element> fc = new ArrayList<Element>();

		// Try to define MEF version from mef file not from parameter
		String fileType = dto.getFileType();
		if (fileType.equals("mef")) {
			MEFLib.Version version = MEFLib.getMEFVersion(mefFile);
			if (version.equals(MEFLib.Version.V2))
				fileType = "mef2";
		}

		IVisitor visitor;

		if (fileType.equals("single"))
			visitor = new XmlVisitor();
		else if (fileType.equals("mef"))
			visitor = new MEFVisitor();
		else if (fileType.equals("mef2"))
			visitor = new MEF2Visitor();
		else
			throw new BadArgumentException("Bad file type parameter.");

		// --- import metadata from MEF, Xml, ZIP files
		MEFLib.visit(mefFile, visitor, new IMEFVisitor() {

			@Override
         public void handleMetadata(Element metadata, int index)
					throws Exception {
				Log.debug(Geonet.MEF, "Collecting metadata:\n"
						+ Xml.getString(metadata));
				md.add(index, metadata);
			}

			@Override
         public void handleMetadataFiles(File[] Files, int index)
					throws Exception {
				Log.debug(Geonet.MEF, "Multiple metadata files");

				Element metadataValidForImport = null;

                for (File file : Files) {
                    if (file != null && !file.isDirectory()) {
                        Element metadata = Xml.loadFile(file);
                        String metadataSchema = dm.autodetectSchema(metadata);

                        // If local node doesn't know metadata
                        // schema try to load next xml file.
                        if (metadataSchema == null) {
                            continue;
                        }

                        // If schema is preferred local node schema
                        // load that file.
                        if (metadataSchema.equals(preferredSchema)) {
                            Log.debug(Geonet.MEF, "Found metadata file "
                                    + file.getName()
                                    + " with preferred schema ("
                                    + preferredSchema + ").");
                            handleMetadata(metadata, index);
                            return;
                        }
                        else {
                            Log.debug(Geonet.MEF, "Found metadata file "
                                    + file.getName() + " with known schema ("
                                    + metadataSchema + ").");
                            metadataValidForImport = metadata;
                        }
                    }
                }

				// Import a valid metadata if not one found
				// with preferred schema.
				if (metadataValidForImport != null) {
					Log
							.debug(Geonet.MEF,
									"Importing metadata with valide schema but not preferred one.");
					handleMetadata(metadataValidForImport, index);
                } else
					throw new BadFormatEx("No valid metadata file found.");
			}

			// --------------------------------------------------------------------

			@Override
         public void handleFeatureCat(Element featureCat, int index)
					throws Exception {
				if (featureCat != null) {
					Log.debug(Geonet.MEF, "Collecting feature catalog:\n"
							+ Xml.getString(featureCat));
				}
				fc.add(index, featureCat);
			}

			// --------------------------------------------------------------------

			/**
			 * Record is not a template by default. No category attached to
			 * record by default. No stylesheet used by default. If no site
			 * identifier provided, use current node id by default. No
			 * validation by default.
			 *
			 * If record is a template and not a MEF file always generate a new
			 * UUID.
			 */
			@Override
         @SuppressWarnings("unchecked")
         public void handleInfo(Element info, int index) throws Exception {

				String FS = File.separator;

				String uuid = null;
				String createDate = null;
				String changeDate = null;
				String source;
				String sourceName = null;
				// Schema in info.xml is not used anymore.
				// as we use autodetect schema to define
				// metadata schema.
				// String schema = null;
				String isTemplate = "n";
				String localId = null;
				String rating = null;
				String popularity = null;
				// Category
				Category category = null;

				boolean validate = false;

				Element metadata = md.get(index);
				String schema = dm.autodetectSchema(metadata);

				if (schema == null)
					throw new Exception("Unknown schema format : " + schema);

				// Handle non MEF files insertion
				if (info.getChildren().size() == 0) {
					source = gc.getSiteId();
					category = dto.getCategory();

					String style = "None";
					if (dto.getStylesheet() != null)
					{
					   style = dto.getStylesheet().getName();
					}

					// Apply a stylesheet transformation if requested
					if (!style.equals("None"))
						md.add(index, Xml.transform(md.get(index), stylePath
								+ FS + style));

					// Get the Metadata uuid if it's not a template.
					if (isTemplate.equals("n"))
						uuid = dm.extractUUID(schema, md.get(index));

					validate = Util.getParam(params, Params.VALIDATE, "off")
							.equals("on");

				} else {
					Element categsElt = info.getChild("categories");

					Collection<String> categNames = CollectionUtils.collect(categsElt.getChildren("category"), new Transformer() {
                  @Override
                  public Object transform(Object input) {
                     return ((Element) input).getAttributeValue("name") ;
                  }
               });

					CategoryManager cm = new CategoryManager(dbms);
					// FIXME Category may be null ... NPE!!
					category = mapLocalCategory(cm.getAllCategories(), categNames);

					Element general = info.getChild("general");

					uuid = general.getChildText("uuid");
					createDate = general.getChildText("createDate");
					changeDate = general.getChildText("changeDate");
					source = general.getChildText("siteId");
					sourceName = general.getChildText("siteName");
					localId = general.getChildText("localId");
					isTemplate = "n";
					rating = general.getChildText("rating");
					popularity = general.getChildText("popularity");
				}

				if (validate)
					dm.validate(schema, metadata);

				String uuidAction = Params.NOTHING;

				importRecord(uuid, localId, uuidAction, md, schema, index,
						source, sourceName, context, id, createDate,
						changeDate, isTemplate, category.getId());

				if (fc.size() != 0 && fc.get(index) != null) {
					// UUID is set as @uuid in root element
					uuid = UUID.randomUUID().toString();

					fc.add(index, dm.setUUID("iso19110", uuid, fc.get(index)));

					String fcId = dm.insertMetadataExt(dbms, "iso19110", fc.get(index),
					        source,createDate, changeDate, uuid, context.getUserSession().getUsername());

					Log.debug(Geonet.MEF, "Adding Feature catalog with uuid: "
							+ uuid);

					// Create database relation between metadata and feature
					// catalog
					String mdId = id.get(index);
					String query = "INSERT INTO Relations (id, relatedId) VALUES (?, ?)";
					dbms.execute(query, Integer.parseInt(mdId), Integer.parseInt(fcId));

					id.add(fcId);
					// TODO : privileges not handled for feature
					// catalog ...
				}

				int iId = Integer.parseInt(id.get(index));

				if (rating != null)
					dbms.execute("UPDATE Metadata SET rating=? WHERE id=?",
							new Integer(rating), iId);

				if (popularity != null)
					dbms.execute("UPDATE Metadata SET popularity=? WHERE id=?",
							new Integer(popularity), iId);

				dm.setTemplateExt(dbms, uuid, isTemplate, null);
				dm.setHarvestedExt(dbms, uuid, null);

				String pubDir = Lib.resource.getDir(context, "public", id
						.get(index));
				String priDir = Lib.resource.getDir(context, "private", id
						.get(index));

				new File(pubDir).mkdirs();
				new File(priDir).mkdirs();

				if (indexGroup) {
               dm.indexMetadataGroup(dbms, uuid, null);
				} else {
               dm.indexMetadata(dbms, uuid, null);
				}
			}

			// --------------------------------------------------------------------

			@Override
         public void handlePublicFile(String file, String changeDate,
					InputStream is, int index) throws IOException {
				Log.debug(Geonet.MEF, "Adding public file with name=" + file);
				saveFile(context, id.get(index), "public", file, changeDate, is);
			}

			// --------------------------------------------------------------------

			@Override
         public void handlePrivateFile(String file, String changeDate,
					InputStream is, int index) throws IOException {
				Log.debug(Geonet.MEF, "Adding private file with name=" + file);
				saveFile(context, id.get(index), "private", file, changeDate,
						is);
			}

		});

		return id;
	}

   public static void importRecord(String uuid, String localId, String uuidAction,
         List<Element> md, String schema, int index, String source, String sourceName,
         ServiceContext context, List<String> id, String createDate, String changeDate,
         String isTemplate, Integer category) throws Exception {

		GeonetContext gc = (GeonetContext) context
				.getHandlerContext(Geonet.CONTEXT_NAME);
		DataManager dm = gc.getDataManager();
		Dbms dbms = (Dbms) context.getResourceManager()
				.open(Geonet.Res.MAIN_DB);

		if (uuid == null || uuid.equals("")
				|| uuidAction.equals(Params.GENERATE_UUID)) {
			String newuuid = UUID.randomUUID().toString();
			source = null;

			Log
					.debug(Geonet.MEF, "Replacing UUID " + uuid + " with "
							+ newuuid);
			uuid = newuuid;

			// --- set uuid inside metadata
			md.add(index, dm.setUUID(schema, uuid, md.get(index)));
		} else {
			if (sourceName == null)
				sourceName = "???";

			if (source == null || source.trim().length() == 0)
				throw new Exception(
						"Missing siteId parameter from info.xml file");

			Lib.sources.update(dbms, source, sourceName, true);
		}

		try {
			if (dm.existsMetadataUuid(dbms, uuid)
					&& !uuidAction.equals(Params.NOTHING)) {
				dm.deleteMetadataById(dbms, dm.getMetadataId(dbms, uuid));
				Log.debug(Geonet.MEF, "Deleting existing metadata with UUID : "
						+ uuid);
			}
		} catch (Exception e) {
			throw new Exception(
					" Existing metadata with same UUID could not be deleted.");
		}

		Log.debug(Geonet.MEF, "Adding metadata with uuid:" + uuid);

		// Try to insert record with localId provided, if not use a new id.
		boolean insertedWithLocalId = false;
		if (localId != null && !localId.equals("")) {
			try {
				int iLocalId = Integer.parseInt(localId);

				// Use the same id to insert the metadata record.
				// This is an optional element. If present, indicates the
				// id used locally by the sourceId actor to store the metadata. Its
				// purpose is just to allow the reuse of the same local id when
				// reimporting a metadata.
				if (!dm.existsMetadata(dbms, iLocalId)) {
					Log.debug(Geonet.MEF, "Using given localId: " + localId);

               id.add(index, dm.insertMetadataExt(dbms, schema, md.get(index), iLocalId, source,
                     createDate, changeDate, uuid, context.getUserSession().getUsername(),
                     isTemplate, "n", null, null, category));
					insertedWithLocalId = true;
				}
			} catch (NumberFormatException e) {
				Log.debug(Geonet.MEF, "Invalid localId provided: " + localId + ". Adding record with a new id.");
			}
		}

		if (!insertedWithLocalId) {
			id.add(index, dm.insertMetadataExt(dbms, schema, md.get(index), source,
			        createDate, changeDate, uuid, context.getUserSession().getUsername()));
		}

	}

	// --------------------------------------------------------------------------

	protected static void saveFile(ServiceContext context, String id,
			String access, String file, String changeDate, InputStream is)
			throws IOException {
		String dir = Lib.resource.getDir(context, access, id);

		File outFile = new File(dir, file);
		FileOutputStream os = new FileOutputStream(outFile);
		BinaryFile.copy(is, os, false, true);

		outFile.setLastModified(new ISODate(changeDate).getSeconds() * 1000);
	}

	/**
	 * Map local categories.
	 * @param localCategs
	 * @param categs
	 * @return a local category or null if any.
	 */
   protected static Category mapLocalCategory(List<Category> localCategs, final Collection<String> categoryNames) {
      return (Category) CollectionUtils.find(localCategs, new Predicate() {

         @Override
         public boolean evaluate(Object object) {
            return categoryNames.contains(((Category) object).getName());
         }
      });
	}
}

// =============================================================================

