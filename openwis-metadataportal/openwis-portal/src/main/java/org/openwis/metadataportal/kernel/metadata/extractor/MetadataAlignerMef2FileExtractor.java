/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipFile;

import jeeves.exceptions.BadFormatEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.util.ISODate;
import org.fao.geonet.util.ZipUtil;
import org.jdom.Element;
import org.openwis.metadataportal.common.io.DirectoryFileFilter;
import org.openwis.metadataportal.common.io.FileFileFilter;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataResource;
import org.openwis.metadataportal.model.metadata.source.SiteSource;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * A MEF v2 metadata extractor. <P>
 * The mefV2 file is with this form:
 * - Folder with URN as name [0..n]
 *    |- metadata
 *       |- metadata.xml
 *    |- info.xml
 * 
 */
public class MetadataAlignerMef2FileExtractor implements IMetadataAlignerExtractor {

   private Dbms dbms;

   private String userName;

   private String preferredSchema;

   private DataManager dataManager;

   /**
    * Default constructor.
    * Builds a MetadataAlignerMef2FileExtractor.
    * @param dbms
    * @param userName
    * @param preferredSchema
    * @param dataManager
    */
   public MetadataAlignerMef2FileExtractor(Dbms dbms, String userName, String preferredSchema,
         DataManager dataManager) {
      super();
      this.dbms = dbms;
      this.userName = userName;
      this.preferredSchema = preferredSchema;
      this.dataManager = dataManager;
   }

   /**
    * {@inheritDoc}
    * @param preferredSchema 
    * @see org.openwis.metadataportal.kernel.metadata.extractor.IMetadataAlignerExtractor#extract(java.io.File)
    */
   @Override
   public List<Metadata> extract(File f) throws Exception {

      List<Metadata> metadatas = new ArrayList<Metadata>();

      //Unzip the mef file.
      File unzipDir = new File(f.getParentFile(), "unzipping");
      if (unzipDir.exists()) {
         ZipUtil.deleteAllFiles(unzipDir);
      }
      ZipUtil.extract(new ZipFile(f), unzipDir);

      //Browse all metadata folders.
      File[] metadataMefDirectories = unzipDir.listFiles(new DirectoryFileFilter());
      for (File metadataMefDir : metadataMefDirectories) {
         //Initialization of elements.
         Element featureCatalog = null;
         Element info = null;

         // Handle metadata file
         File metadataDir = new File(metadataMefDir, "metadata");
         if (!metadataDir.exists()) {
            throw new BadFormatEx("Missing metadata folder for dir " + metadataMefDir.getName()
                  + " in MEF file " + f.getName() + ".");
         }
         //Check if there are XML files.
         File[] xmlFiles = metadataDir.listFiles(new FileFileFilter());
         if (ArrayUtils.isEmpty(xmlFiles)) {
            throw new BadFormatEx("Missing XML document in metadata folder in dir "
                  + metadataMefDir.getName() + " in MEF file " + f.getName() + ".");
         }

         // Handle feature catalog
         File appliSchemaDir = new File(metadataMefDir, MefConstants.APPLI_SCHEMA);
         if (appliSchemaDir.exists() && appliSchemaDir.isDirectory()) {
            File featureCatalogFile = null;
            File fcMetadataFile = new File(appliSchemaDir, MefConstants.FILE_METADATA);
            if (fcMetadataFile.exists()) {
               featureCatalogFile = fcMetadataFile;
            } else {
               File[] files = appliSchemaDir.listFiles(new FileFileFilter());
               // Retrieve first files into applschema directory, without any
               // tests.
               if (!ArrayUtils.isEmpty(files)) {
                  featureCatalogFile = files[0];
               }
            }

            if (featureCatalogFile != null) {
               featureCatalog = Xml.loadFile(featureCatalogFile);
            }
         }

         //Handle info.
         File fileInfo = new File(metadataMefDir, MefConstants.FILE_INFO);
         if (fileInfo.exists()) {
            info = Xml.loadFile(fileInfo);
         }

         Metadata metadata = new Metadata();

         //Get appropriate metadata file
         Element data = loadMetadataFile(xmlFiles);
         if (data == null) {
            throw new BadFormatEx("No valid metadata file found.");
         }
         metadata.setData(data);

         //If info is not null, manage information and binaries. 
         if (info != null) {
            //Extract info.
            extractInfo(info, metadata);

            //Handle binaries.
            extractBinaryFiles(metadataMefDir, info, metadata);
         }
         
         //Specify feature catalog.
         if(featureCatalog != null) {
            Metadata featureCatalogMetadata = new Metadata();
            featureCatalogMetadata.setUrn(UUID.randomUUID().toString());
            featureCatalogMetadata.setSchema("iso19110");
            featureCatalogMetadata.setSource(metadata.getSource());
            featureCatalogMetadata.setCreateDate(metadata.getCreateDate());
            featureCatalogMetadata.setChangeDate(metadata.getChangeDate());
            
            dataManager.setUUID("iso19110", featureCatalogMetadata.getUrn(), featureCatalog);
            
            metadata.getRelatedMetadatas().put(featureCatalogMetadata, Boolean.TRUE);
         }
         
         metadatas.add(metadata);
      }

      //Remove unzip directory.
      ZipUtil.deleteAllFiles(unzipDir);

      return metadatas;
   }

   /**
    * Loads the appropriate xml file according to the preferred schema.
    * @param xmlFiles the available XML files (not directories).
    * @return the appropriate xml file according to the preferred schema, <code>null</code> if no valid file can be loaded.
    */
   private Element loadMetadataFile(File[] xmlFiles) throws Exception {
      Element metadataValidForImport = null;

      for (File file : xmlFiles) {
         Element metadata = Xml.loadFile(file);
         String metadataSchema = this.dataManager.autodetectSchema(metadata);

         // If local node doesn't know metadata
         // schema try to load next xml file.
         if (metadataSchema == null) {
            continue;
         }

         // If schema is preferred local node schema
         // load that file.
         if (metadataSchema.equals(preferredSchema)) {
            Log.debug(Geonet.MEF, "Found metadata file " + file.getName()
                  + " with preferred schema (" + preferredSchema + ").");
            return metadata;
         } else {
            Log.debug(Geonet.MEF, "Found metadata file " + file.getName() + " with known schema ("
                  + metadataSchema + ").");
            metadataValidForImport = metadata;
         }
      }

      // Returns the valid metadata if any.
      return metadataValidForImport;
   }

   /**
    * Extract the binary files.
    * @param mefFile the mef file.
    * @param info the info element.
    * @param metadata the metadata to populate.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   private void extractInfo(Element info, Metadata metadata) throws Exception {
      if (CollectionUtils.isNotEmpty(info.getChildren())) {
         //Extract categories from the info.xml
         Element categsElt = info.getChild("categories");

         final Collection<String> categoryNames = Collections2.transform(
               categsElt.getChildren("category"), new Function<Element, String>() {

                  @Override
                  public String apply(Element input) {
                     return input.getAttributeValue("name");
                  }

               });

         CategoryManager categoryManager = new CategoryManager(dbms);
         Collection<Category> allCategories = categoryManager.getAllCategories();
         Collection<Category> matchingCategories = Collections2.filter(allCategories,
               new Predicate<Category>() {

                  @Override
                  public boolean apply(Category input) {
                     return categoryNames.contains(input.getName());
                  }
               });

         if (!matchingCategories.isEmpty()) {
            //If at least one category matches, return the first one.
            metadata.setCategory(matchingCategories.iterator().next());
         }

         Element general = info.getChild("general");

         metadata.setUrn(general.getChildText("uuid"));
         metadata.setCreateDate(general.getChildText("createDate"));
         metadata.setChangeDate(general.getChildText("changeDate"));
         String sourceId = general.getChildText("siteId");
         String sourceName = general.getChildText("siteName");
         metadata.setSource(new SiteSource(userName, sourceId, sourceName));
      }
   }

   /**
    * Extract the binary files.
    * @param mefFile the mef file.
    * @param info the info element.
    * @param metadata the metadata to populate.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   private void extractBinaryFiles(File metadataMefDir, Element info, Metadata metadata)
         throws Exception {
      //Use to manage the change date.
      Map<String, String> publicFilesChangeDate = new HashMap<String, String>();
      if (info.getChild("public") != null) {
         List<Element> publicElements = info.getChild("public").getChildren();
         for (Element e : publicElements) {
            publicFilesChangeDate.put(e.getAttributeValue("name"),
                  e.getAttributeValue("changeDate"));
         }
      }

      Map<String, String> privateFilesChangeDate = new HashMap<String, String>();
      if (info.getChild("private") != null) {
         List<Element> privateElements = info.getChild("private").getChildren();
         for (Element e : privateElements) {
            privateFilesChangeDate.put(e.getAttributeValue("name"),
                  e.getAttributeValue("changeDate"));
         }
      }

      //Extract public files.
      File publicResourceDir = new File(metadataMefDir, MefConstants.DIR_PUBLIC);
      if (publicResourceDir.exists()) {
         File[] publicResources = publicResourceDir.listFiles(new FileFileFilter());
         for (File resource : publicResources) {
            MetadataResource publicResource = new MetadataResource();
            publicResource.setData(IOUtils.toByteArray(new FileInputStream(resource)));
            publicResource.setName(resource.getName());

            String changeDate = publicFilesChangeDate.get(resource.getName());
            publicResource.setChangeDate(new ISODate(changeDate).getSeconds() * 1000);

            metadata.getPublicDocs().add(publicResource);
         }
      }

      //Extract private files.
      File privateResourceDir = new File(metadataMefDir, MefConstants.DIR_PRIVATE);
      if (privateResourceDir.exists()) {
         File[] privateResources = privateResourceDir.listFiles(new FileFileFilter());
         for (File resource : privateResources) {
            MetadataResource privateResource = new MetadataResource();
            privateResource.setData(IOUtils.toByteArray(new FileInputStream(resource)));
            privateResource.setName(resource.getName());

            String changeDate = privateFilesChangeDate.get(resource.getName());
            privateResource.setChangeDate(new ISODate(changeDate).getSeconds() * 1000);

            metadata.getPrivateDocs().add(privateResource);
         }
      }
   }
}
