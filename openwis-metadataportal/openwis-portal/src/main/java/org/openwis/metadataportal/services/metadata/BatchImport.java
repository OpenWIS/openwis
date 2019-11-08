/**
 *
 */
package org.openwis.metadataportal.services.metadata;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.mef.MEFLib;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.kernel.metadata.extractor.IMetadataAlignerExtractor;
import org.openwis.metadataportal.kernel.metadata.extractor.MetadataAlignerExtractorFactory;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.BatchImportMetadataDTO;

import com.google.common.base.Objects;
import com.google.common.base.Predicates;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class BatchImport implements Service {

   /** The filename filter. */
   private final FilenameFilter filenameFilter = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
         return !("CVS".equals(name) || name.startsWith("."));
      }
   };

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //Creating the managers.
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      DataManager dataManager = gc.getDataManager();
      ISearchManager searchManager = gc.getSearchmanager();
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);
      String preferredSchema = (gc.getHandlerConfig().getMandatoryValue("preferredSchema") != null ? gc
            .getHandlerConfig().getMandatoryValue("preferredSchema") : "iso19139");
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      BatchImportMetadataDTO dto;
      if (Boolean.valueOf(params.getChildText("rest"))) {
         dto = new BatchImportMetadataDTO();
         String fileType = params.getChildText("file_type");
         dto.setFileType(fileType);
         String dir = params.getChildText("dir");
         dto.setDirectory(dir);
         String categoryName = params.getChildText("category");
         Category category = new CategoryManager(dbms).getCategoryByName(categoryName); 
         dto.setCategory(category);
         // default values for REST mode
         dto.setStylesheet(null);
         dto.setValidationMode(MetadataValidation.NONE);
      } else {
         dto = JeevesJsonWrapper.read(params, BatchImportMetadataDTO.class);
      }

      IMetadataAligner aligner = new MetadataAligner(dbms, dataManager, searchManager, dataDir);

      //Predicated style sheets.
      List<PredicatedStylesheet> predicatedStylesheets = new ArrayList<PredicatedStylesheet>();
      if (dto.getStylesheet() != null) {
         PredicatedStylesheet ps = new PredicatedStylesheet(dto.getStylesheet().getName(),
               Predicates.<Element> alwaysTrue());
         predicatedStylesheets.add(ps);
      }

      //Source.
      SiteSource source = new SiteSource();
      source.setUserName(context.getUserSession().getUsername());
      source.setSourceId(gc.getSiteId());
      source.setSourceName(gc.getSiteName());

      //Gets the metadatas.
      File dir = new File(dto.getDirectory());
      List<Metadata> mds = importDirectory(dto, source, dir, dbms, preferredSchema, dataManager);

      // Process to import.
      aligner.importMetadatas(mds, dto.getValidationMode(), predicatedStylesheets,
            new ChangeDateCollector());

      aligner.indexImportedMetadatas();

      return JeevesJsonWrapper.send(new AcknowledgementDTO(true, aligner.getResult()));
   }

   /**
    * Import directory.
    *
    * @param dto the dto
    * @param source the source
    * @param dir the dir
    * @return the list
    * @throws Exception the exception
    */
   private List<Metadata> importDirectory(BatchImportMetadataDTO dto, SiteSource source, File dir, Dbms dbms, String preferredSchema, DataManager dataManager)
         throws Exception {
      List<Metadata> result = new ArrayList<Metadata>();
      if (dir != null) {
         if (dir.canRead() && dir.isFile()) {
            result.addAll(importFile(dto, source, dir, dbms, preferredSchema, dataManager));
         } else if (dir.canRead() && dir.isDirectory()) {
            for (File file : dir.listFiles(filenameFilter)) {
               result.addAll(importDirectory(dto, source, file, dbms, preferredSchema, dataManager));
            }
         }
      }
      return result;
   }

   /**
    * Import file.
    *
    * @param dto the dto
    * @param source the source
    * @param mds the mds
    * @param f the f
    * @return the metadata
    * @throws Exception the exception
    */
   private List<Metadata> importFile(BatchImportMetadataDTO dto, SiteSource source, File f, Dbms dbms, String preferredSchema, DataManager dataManager)
         throws Exception {
      String fileType = dto.getFileType();
      if (fileType.equals("mef")) {
         MEFLib.Version version = MEFLib.getMEFVersion(f);
         if (version.equals(MEFLib.Version.V2))
            fileType = "mef2";
      }
      IMetadataAlignerExtractor extractor = MetadataAlignerExtractorFactory
            .getMetadataAlignerExtractor(fileType, dbms, source.getUserName(), preferredSchema, dataManager);
      List<Metadata> recs = extractor.extract(f);
      for (Metadata rec : recs) {
         rec.setSource(Objects.firstNonNull(rec.getSource(), source));
         rec.setCategory(Objects.firstNonNull(rec.getCategory(), dto.getCategory()));
      }
      return recs;
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig serviceConfig) throws Exception {
      //NOOP
   }
}
