/**
 * 
 */
package org.openwis.metadataportal.services.metadata;

import java.util.Arrays;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.IProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.metadata.ProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.catalog.CatalogStatUpdateHelper;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.CreateMetadataDTO;

import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Create implements Service {

   private String appPath;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      CreateMetadataDTO dto = JeevesJsonWrapper.read(params, CreateMetadataDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      DataManager dm = gc.getDataManager();
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);
      
      MetadataManager mm = new MetadataManager(dbms, gc.getDataManager(), gc.getSettingManager(),
            appPath);

      if (mm.getMetadataInfoByUrn(dto.getUuid()) != null) {
         // TODO I8n
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, "URN already exists. Choose an other one"));
      }
      
      IMetadataAligner metadataAligner = new MetadataAligner(dbms, gc.getDataManager(),
            gc.getSearchmanager(), dataDir);

      String parentUuid = null;
      String child = Util.getParam(params, Params.CHILD, "n");
      if (!child.equals("n")) {
         parentUuid = dm.getMetadataUuid(dbms, dto.getTemplate().getId().toString());
      }

      //Create metadata object.
      Metadata metadata = new Metadata(dto.getUuid());
      metadata.setCategory(dto.getCategory());
      metadata.setDataPolicy(dto.getDataPolicy());
      metadata.setChangeDate(new ISODate().toString());

      //Source.
      SiteSource source = new SiteSource();
      source.setUserName(context.getUserSession().getUsername());
      source.setSourceId(gc.getSiteId());
      source.setSourceName(gc.getSiteName());
      metadata.setSource(source);

      metadata = mm.createMetadataFromTemplate(metadata, dto.getTemplate());

      // Import the new metadata
      metadataAligner.importMetadatas(Arrays.asList(metadata), MetadataValidation.NONE,
            Lists.<PredicatedStylesheet> newArrayList(), new ChangeDateCollector());
      
      metadataAligner.indexImportedMetadatas();
      
      MetadataAlignerResult result = metadataAligner.getResult();

      if (result.getAdded() > 0) {
         IProductMetadataManager pmMan = new ProductMetadataManager();
         ProductMetadata pm = pmMan.getProductMetadataByUrn(dto.getUuid());
         
         if (result != null) {
            CatalogStatUpdateHelper.updateStatOnCreate(result.getTotal(), result.getVolume());
         }
         
         return JeevesJsonWrapper.send(new AcknowledgementDTO(true, pm));
      } else {
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, result.getErrors()));
      }
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig serviceConfig) throws Exception {
      this.appPath = appPath;
   }

}
