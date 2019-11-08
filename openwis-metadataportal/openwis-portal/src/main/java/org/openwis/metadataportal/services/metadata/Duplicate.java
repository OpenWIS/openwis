/**
 * 
 */
package org.openwis.metadataportal.services.metadata;

import java.util.Arrays;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.DuplicateMetadataDTO;

import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Duplicate implements Service {

   private String appPath;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig serviceConfig) throws Exception {
      this.appPath = appPath;
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      DuplicateMetadataDTO dto = JeevesJsonWrapper.read(params, DuplicateMetadataDTO.class);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      String dataDir = gc.getHandlerConfig().getMandatoryValue(Geonet.Config.DATA_DIR);

      MetadataManager mm = new MetadataManager(dbms, gc.getDataManager(), gc.getSettingManager(),
            appPath);
      IMetadataAligner metadataAligner = new MetadataAligner(dbms, gc.getDataManager(),
            gc.getSearchmanager(), dataDir);
      
      final String toUrn = StringUtils.trimToEmpty(dto.getToURN());

      if (mm.getMetadataInfoByUrn(toUrn) != null) {
         // TODO I8n
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, "URN already exists. Choose an other one"));
      }
      //Create metadata object.
      Metadata metadata = new Metadata(toUrn);

      //--- query the data policy manager
      IDataPolicyManager dpm = new DataPolicyManager(dbms);
      DataPolicy dp = dpm.getDataPolicyByMetadataUrn(dto.getFromURN(), false, false);
      metadata.setDataPolicy(dp);

      // query the category manager
      CategoryManager cm = new CategoryManager(dbms);
      Category category = cm.getCategoryByMetadataUrn(dto.getFromURN());
      metadata.setCategory(category);

      // Set the change date
      metadata.setChangeDate(new ISODate().toString());

      //Source.
      SiteSource source = new SiteSource();
      source.setUserName(context.getUserSession().getUsername());
      source.setSourceId(gc.getSiteId());
      source.setSourceName(gc.getSiteName());
      metadata.setSource(source);

      // Create the template
      Template template = new Template();
      template.setUrn(dto.getFromURN());

      // Duplicate the metadata
      metadata = mm.createMetadataFromTemplate(metadata, template);

      // Import the new metadata
      metadataAligner.importMetadatas(Arrays.asList(metadata), MetadataValidation.NONE,
            Lists.<PredicatedStylesheet> newArrayList(), new ChangeDateCollector());

      metadataAligner.indexImportedMetadatas();

      MetadataAlignerResult result = metadataAligner.getResult();

      // TODO Handle result on client side.
      return JeevesJsonWrapper.send(new AcknowledgementDTO(true, result));
   }

}
