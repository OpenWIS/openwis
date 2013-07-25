/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import jeeves.exceptions.BadFormatEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataResource;
import org.openwis.metadataportal.model.metadata.source.SiteSource;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

/**
 * A MEF v1 metadata extractor. <P>
 * The mefV1 file is with this form:
 * - metadata.xml. <P>
 * - info.xml. <P>
 * 
 */
public class MetadataAlignerMefFileExtractor implements IMetadataAlignerExtractor {

   private Dbms dbms;

   private String userName;

   /**
    * Default constructor.
    * Builds a MetadataAlignerMefFileExtractor.
    * @param dbms
    * @param userName
    */
   public MetadataAlignerMefFileExtractor(Dbms dbms, String userName) {
      super();
      this.dbms = dbms;
      this.userName = userName;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.extractor.IMetadataAlignerExtractor#extract(java.io.File)
    */
   @Override
   public List<Metadata> extract(File f) throws Exception {
      //File MEF is a ZIP. Browse the file to extract the appropriate informations.
      Element md = null;
      Element info = null;

      ZipFile zip = new ZipFile(f);
      try {
         Enumeration<? extends ZipEntry> e = zip.entries();
         while (e.hasMoreElements() && (md == null || info == null)) {
            ZipEntry entry = e.nextElement();
            String name = entry.getName();

            if (name.equals(MefConstants.FILE_METADATA)) {
               md = Xml.loadStream(zip.getInputStream(entry));
            } else if (name.equals(MefConstants.FILE_INFO)) {
               info = Xml.loadStream(zip.getInputStream(entry));
            }
         }
      } finally {
         zip.close();
      }

      if (md == null) {
         throw new BadFormatEx("Missing metadata file: " + MefConstants.FILE_METADATA);
      }

      if (info == null) {
         throw new BadFormatEx("Missing info file: " + MefConstants.FILE_INFO);
      }

      //We have the XML metadata and the information file.
      Metadata metadata = new Metadata();
      metadata.setData(md);

      //Handle Info.
      extractInfo(info, metadata);

      //Handle binary files.
      extractBinaryFiles(f, info, metadata);

      return Lists.newArrayList(metadata);
   }

   /**
    * Extract the binary files.
    * @param mefFile the mef file.
    * @param info the info element.
    * @param metadata the metadata to populate.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   private void extractInfo(Element info, Metadata metadata)
         throws Exception {
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
   private void extractBinaryFiles(File mefFile, Element info, Metadata metadata) throws Exception {
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

      //Extract the binary files.
      ZipInputStream zis = new ZipInputStream(new FileInputStream(mefFile));
      ZipEntry entry;

      try {
         while ((entry = zis.getNextEntry()) != null) {
            String fullName = entry.getName();
            String simpleName = new File(fullName).getName();

            if (fullName.equals(MefConstants.DIR_PUBLIC)
                  || fullName.equals(MefConstants.DIR_PRIVATE))
               continue;

            if (fullName.startsWith(MefConstants.DIR_PUBLIC)) {
               //Found public directory.
               MetadataResource publicResource = new MetadataResource();
               publicResource.setData(ByteStreams.toByteArray(zis));
               publicResource.setName(simpleName);

               String changeDate = publicFilesChangeDate.get(simpleName);
               publicResource.setChangeDate(new ISODate(changeDate).getSeconds() * 1000);

               metadata.getPublicDocs().add(publicResource);
            } else if (fullName.startsWith(MefConstants.DIR_PRIVATE)) {
               //Found private directory.
               MetadataResource privateResource = new MetadataResource();
               privateResource.setData(ByteStreams.toByteArray(zis));
               privateResource.setName(simpleName);

               String changeDate = privateFilesChangeDate.get(simpleName);
               privateResource.setChangeDate(new ISODate(changeDate).getSeconds() * 1000);

               metadata.getPrivateDocs().add(privateResource);
            }

            zis.closeEntry();
         }
      } finally {
         zis.close();
      }
   }
}
