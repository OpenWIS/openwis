/**
 * 
 */
package org.openwis.metadataportal.services.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.kernel.metadata.extractor.IMetadataAlignerExtractor;
import org.openwis.metadataportal.kernel.metadata.extractor.MetadataAlignerExtractorFactory;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.catalog.CatalogStatUpdateHelper;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.InsertMetadataDTO;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class InsertUpload implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //DTO.
      InsertMetadataDTO dto = new InsertMetadataDTO(params);

      //Creating the managers.
      final String uploadDir = context.getUploadDir();
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      DataManager dataManager = gc.getDataManager();
      ISearchManager searchManager = gc.getSearchmanager();
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);
      String preferredSchema = (gc.getHandlerConfig().getMandatoryValue("preferredSchema") != null ? gc
            .getHandlerConfig().getMandatoryValue("preferredSchema") : "iso19139");
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
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
      Collection<File> files = Collections2.transform(dto.getFiles(), new Function<String, File>() {
         @Override
         public File apply(String input) {
            return new File(uploadDir, input);
         }
      });

      List<Metadata> mds = new ArrayList<Metadata>();
      IMetadataAlignerExtractor extractor = null;
      for (File f : files) {
         String fileType = dto.getFileType();
         if (fileType.equals("mef")) {
            MEFLib.Version version = MEFLib.getMEFVersion(f);
            if (version.equals(MEFLib.Version.V2))
               fileType = "mef2";
         }

         extractor = MetadataAlignerExtractorFactory.getMetadataAlignerExtractor(fileType, dbms,
               context.getUserSession().getUsername(), preferredSchema, dataManager);
         List<Metadata> recs;
         try {
            recs = extractor.extract(f);
         } catch (Exception e) {
            MetadataAlignerResult result = new MetadataAlignerResult();
            result.setDate(new Date());
            result.incUnexpected();
            result.getErrors().add(
                  new MetadataAlignerError("", e.getClass() + " : " + e.getMessage()));
            return JeevesJsonWrapper.sendBasicFormResult(result);
         }

         for (Metadata rec : recs) {
            rec.setSource(Objects.firstNonNull(rec.getSource(), source));
            rec.setCategory(Objects.firstNonNull(rec.getCategory(), dto.getCategory()));
         }
         mds.addAll(recs);

         //Delete temporary file.
         f.delete();
      }

      // Process to import.
      aligner.importMetadatas(mds, dto.getValidationMode(), predicatedStylesheets,
            new ChangeDateCollector(), context);

      aligner.indexImportedMetadatas();

      MetadataAlignerResult result = aligner.getResult();
      if (result != null) {
         CatalogStatUpdateHelper.updateStatOnInsert(result.getTotal(), result.getVolume());
      }

      return JeevesJsonWrapper.sendBasicFormResult(result);
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig serviceConfig) throws Exception {

   }

}
