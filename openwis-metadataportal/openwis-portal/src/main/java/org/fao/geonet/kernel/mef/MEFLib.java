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

import static org.fao.geonet.kernel.mef.MEFConstants.DIR_PRIVATE;
import static org.fao.geonet.kernel.mef.MEFConstants.DIR_PUBLIC;
import static org.fao.geonet.kernel.mef.MEFConstants.FS;
import static org.fao.geonet.kernel.mef.MEFConstants.VERSION;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jeeves.exceptions.BadInputEx;
import jeeves.exceptions.BadParameterEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.exceptions.MetadataNotFoundEx;
import org.fao.geonet.util.ISODate;
import org.jdom.Document;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.DataPolicyGroupPrivileges;
import org.openwis.metadataportal.model.datapolicy.DataPolicyOperationsPerGroup;
import org.openwis.metadataportal.services.metadata.dto.ImportMetadataDTO;


/**
 * Utility class for MEF import and export.
 */
public class MEFLib {

	public enum Format {
		/**
		 * Only metadata record and infomation
		 */
		SIMPLE,
		/**
		 * Include public folder
		 */
		PARTIAL,
		/**
		 * Include private folder. Full is default format if none defined.
		 */
		FULL;

		// ------------------------------------------------------------------------

		public static Format parse(String format) throws BadInputEx {
			if (format == null)
				return FULL;
			// throw new MissingParameterEx("format");

			if (format.equals("simple"))
				return SIMPLE;
			if (format.equals("partial"))
				return PARTIAL;
			if (format.equals("full"))
				return FULL;

			throw new BadParameterEx("format", format);
		}

		// ------------------------------------------------------------------------

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/**
	 * MEF file version.
	 * 
	 * MEF file is composed of one or more metadata record with extra
	 * information managed by GeoNetwork. Metadata is in XML format. An
	 * information file (info.xml) is used to transfert general informations,
	 * categories, privileges and file references information. A public and
	 * private directories allows data transfert (eg. thumbnails, data upload).
	 * 
	 */
	public enum Version {
		/**
		 * Version 1 is composed of one metadata file. <pre>
		 * Root 
		 * | 
		 * +--- metadata.xml
		 * +--- info.xml 
		 * +--- public 
		 * |    +---- all public documents and thumbnails
		 * +--- private 
		 *      +---- all private documents and thumbnails
		 * </pre>
		 */
		V1,
		/**
		 * Version 2 is composed of one or more metadata records. Each records
		 * are stored in a directory named using record's uuid.
		 * 
		 * <pre>
		 * Root 
		 * |
		 * + 0..n metadata 
		 *   +--- metadata 
		 *   |      +--- metadata.xml (ISO19139)
		 *   |      +--- (optional) metadata.profil.xml (ISO19139profil) Require a
		 * schema/convert/toiso19139.xsl to map to ISO. 
		 *   +--- info.xml 
		 *   +--- applschema ISO 19110 record 
		 *   +--- public 
		 *   |      +---- all public documents and thumbnails 
		 *   +--- private 
		 *          +---- all private documents and thumbnails
		 * </pre>
		 */
		V2
	}
	
	public static List<String> doImportIndexGroup(Element params, ImportMetadataDTO dto , ServiceContext context, File mefFile, String stylePath) throws Exception {
		return Importer.doImport(params, dto, context, mefFile, stylePath, true);
	}

	// --------------------------------------------------------------------------
	
	public static List<String> doImport(Element params, ImportMetadataDTO dto,ServiceContext context,
			File mefFile, String stylePath) throws Exception {
		return Importer.doImport(params, dto, context, mefFile, stylePath);
	}

	// --------------------------------------------------------------------------

	public static String doExport(ServiceContext context, String uuid,
			String format, boolean skipUUID) throws Exception {
		return MEFExporter.doExport(context, uuid, Format.parse(format),
				skipUUID);
	}

	// --------------------------------------------------------------------------

	public static String doMEF2Export(ServiceContext context,
			Set<String> uuids, String format, boolean skipUUID, String stylePath)
			throws Exception {
		return MEF2Exporter.doExport(context, uuids, Format.parse(format),
				skipUUID, stylePath);
	}

	// --------------------------------------------------------------------------

	public static void visit(File mefFile, IVisitor visitor, IMEFVisitor v)
			throws Exception {
		visitor.visit(mefFile, v);
	}

	/**
	 * Return MEF file version according to ZIP file content.
	 * 
	 * @param mefFile
	 *            mefFile to check version
	 * @return v1
	 */
	public static Version getMEFVersion(File mefFile) {

		try {
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(mefFile));
			ZipEntry entry;

			try {
				while ((entry = zis.getNextEntry()) != null) {
					String fullName = entry.getName();
					if (fullName.equals("metadata.xml") || fullName.equals("info.xml"))
						return Version.V1;
					zis.closeEntry();
				}
			} finally {
				zis.close();
			}
			return Version.V2;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	};

	/**
	 * Get metadata record.
	 * 
	 * @param dbms
	 * @param uuid
	 * @return
	 * @throws SQLException
	 * @throws MetadataNotFoundEx
	 */
	static Element retrieveMetadata(Dbms dbms, String uuid)
			throws SQLException, MetadataNotFoundEx {
		List list = dbms.select("SELECT * FROM Metadata WHERE uuid=?", uuid)
				.getChildren();

		if (list.size() == 0)
			throw new MetadataNotFoundEx("uuid=" + uuid);

		return (Element) list.get(0);
	}

	/**
	 * Add an entry to ZIP file
	 * 
	 * @param zos
	 * @param name
	 * @throws IOException
	 */
	static void createDir(ZipOutputStream zos, String name) throws IOException {
		ZipEntry entry = new ZipEntry(name);
		zos.putNextEntry(entry);
		zos.closeEntry();
	}

	/**
	 * Add file to ZIP file
	 * 
	 * @param zos
	 * @param name
	 * @param is
	 * @throws IOException
	 */
	static void addFile(ZipOutputStream zos, String name, InputStream is)
			throws IOException {
		ZipEntry entry = new ZipEntry(name);
		zos.putNextEntry(entry);
		BinaryFile.copy(is, zos, true, false);
		zos.closeEntry();
	}

	/**
	 * Save public directory (thumbnails or other uploaded documents).
	 * 
	 * @param zos
	 * @param dir
	 * @param uuid
	 *            Metadata uuid
	 * @throws IOException
	 */
	static void savePublic(ZipOutputStream zos, String dir, String uuid)
			throws IOException {
		File[] files = new File(dir).listFiles(filter);

		if (files != null)
			for (File file : files)
				addFile(zos, (uuid != null ? uuid : "") + FS + DIR_PUBLIC
						+ file.getName(), new FileInputStream(file));
	}

	/**
	 * Save private directory (thumbnails or other uploaded documents).
	 * 
	 * @param zos
	 * @param dir
	 * @param uuid
	 *            Metadata uuid
	 * @throws IOException
	 */
	static void savePrivate(ZipOutputStream zos, String dir, String uuid)
			throws IOException {
		File[] files = new File(dir).listFiles(filter);

		if (files != null)
			for (File file : files)
				addFile(zos, (uuid != null ? uuid : "") + FS + DIR_PRIVATE
						+ file.getName(), new FileInputStream(file));
	}

	/**
	 * Build an info file.
	 * 
	 * @param context
	 * @param md
	 * @param format
	 * @param pubDir
	 * @param priDir
	 * @param skipUUID
	 * @return
	 * @throws Exception
	 */
	static String buildInfoFile(ServiceContext context, Element md,
			Format format, String pubDir, String priDir, boolean skipUUID)
			throws Exception {
		Dbms dbms = (Dbms) context.getResourceManager()
				.open(Geonet.Res.MAIN_DB);

		Element info = new Element("info");
		info.setAttribute("version", VERSION);

		info.addContent(buildInfoGeneral(md, format, skipUUID, context));
		info.addContent(buildInfoCategories(dbms, md));
		info.addContent(buildInfoPrivileges(context, md));

		info.addContent(buildInfoFiles("public", pubDir));
		info.addContent(buildInfoFiles("private", priDir));

		return Xml.getString(new Document(info));
	}

	/**
	 * Build general section of info file.
	 * 
	 * 
	 * @param md
	 * @param format
	 * @param skipUUID
	 *            If true, do not add uuid, site identifier and site name.
	 * @param context
	 * @return
	 */
	static Element buildInfoGeneral(Element md, Format format,
			boolean skipUUID, ServiceContext context) {
		String id = md.getChildText("id");
		String uuid = md.getChildText("uuid");
		String schema = md.getChildText("schemaid");
		String isTemplate = md.getChildText("istemplate").equals("y") ? "true"
				: "false";
		String createDate = md.getChildText("createdate");
		String changeDate = md.getChildText("changedate");
		String siteId = md.getChildText("source");
		String rating = md.getChildText("rating");
		String popularity = md.getChildText("popularity");

		Element general = new Element("general").addContent(
				new Element("createDate").setText(createDate)).addContent(
				new Element("changeDate").setText(changeDate)).addContent(
				new Element("schema").setText(schema)).addContent(
				new Element("isTemplate").setText(isTemplate)).addContent(
				new Element("localId").setText(id)).addContent(
				new Element("format").setText(format.toString())).addContent(
				new Element("rating").setText(rating)).addContent(
				new Element("popularity").setText(popularity));

		if (!skipUUID) {
			GeonetContext gc = (GeonetContext) context
					.getHandlerContext(Geonet.CONTEXT_NAME);

			general.addContent(new Element("uuid").setText(uuid));
			general.addContent(new Element("siteId").setText(siteId));
			general.addContent(new Element("siteName")
					.setText(gc.getSiteName()));
		}

		return general;
	}

	/**
	 * Build category section of info file.
	 * 
	 * @param dbms
	 * @param md
	 * @return
	 * @throws SQLException
	 */
	static Element buildInfoCategories(Dbms dbms, Element md)
			throws SQLException {
		Element categ = new Element("categories");
		
		CategoryManager cm = new CategoryManager(dbms);
		Category category = cm.getCategoryByMetadataUrn(md.getChildText("uuid"));
		
		Element cat = new Element("category");
      cat.setAttribute("name", category.getName());
      
      categ.addContent(cat);

		return categ;
	}

	/**
	 * Build privileges section of info file.
	 * 
	 * @param context
	 * @param md
	 * @return
	 * @throws Exception
	 */
    static Element buildInfoPrivileges(ServiceContext context, Element md) throws Exception {

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        String urn = md.getChildText("uuid");
        
        IDataPolicyManager dpm = new DataPolicyManager(dbms);
        
        DataPolicy dp = dpm.getDataPolicyByMetadataUrn(urn, false, true);
        
		// --- generate elements

		Element privil = new Element("privileges");

		for (DataPolicyOperationsPerGroup dpOp : dp.getDpOpPerGroup()) {
			Element group = new Element("group");
			group.setAttribute("name", dpOp.getGroup().getName());
			privil.addContent(group);

			for (DataPolicyGroupPrivileges dpPriv : dpOp.getPrivilegesPerOp()) {
				Element oper = new Element("operation");
				oper.setAttribute("name", dpPriv.getOperation().getName());
				group.addContent(oper);
			}
		}

		return privil;
	}

	/**
	 * Build file section of info file.
	 * 
	 * @param name
	 * @param dir
	 * @return
	 */
	static Element buildInfoFiles(String name, String dir) {
		Element root = new Element(name);

		File[] files = new File(dir).listFiles(filter);

		if (files != null)
			for (File file : files) {
				String date = new ISODate(file.lastModified()).toString();

				Element el = new Element("file");
				el.setAttribute("name", file.getName());
				el.setAttribute("changeDate", date);

				root.addContent(el);
			}

		return root;
	}

	/**
	 * File filter to exclude .svn files.
	 */
	private static FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {
			if (pathname.getName().equals(".svn"))
				return false;

			return true;
		}
	};

	static String getChangeDate(List<Element> files, String fileName)
			throws Exception {
		for (Element f : files) {
			Element file = (Element) f;
			String name = file.getAttributeValue("name");
			String date = file.getAttributeValue("changeDate");

			if (name.equals(fileName))
				return date;
		}

		throw new Exception("File not found in info.xml : " + fileName);
	}

}

// =============================================================================

