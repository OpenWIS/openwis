/**
 * 
 */
package org.openwis.metadataportal.services.metainfo;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.collections.CollectionUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.metadata.IProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.metadata.ProductMetadataManager;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metainfo.dto.MetaInfoDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Save implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      MetaInfoDTO dto = JeevesJsonWrapper.read(params, MetaInfoDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      // Get the product metadata manager
      IProductMetadataManager pmm = new ProductMetadataManager();
      MetadataManager metadataManager = new MetadataManager(dbms);

      // Loop over product metadata and Update
      for (ProductMetadata pmDTO : dto.getProductsMetadata()) {
         ProductMetadata pm = pmm.getProductMetadataByUrn(pmDTO.getUrn());

         // Update overridden GTS category
         if (pmDTO.getOverridenGtsCategory() != null) {
            if (pmDTO.getOverridenGtsCategory().equals("-1")) {
               pm.setOverridenGtsCategory(null);
            } else {
               pm.setOverridenGtsCategory(pmDTO.getOverridenGtsCategory());
            }
         }

         // Update overriden data policy 
         if (!CollectionUtils.isEmpty(dto.getDataPolicies())) {
            IDataPolicyManager dpm = new DataPolicyManager(dbms);

            Metadata metadata = metadataManager.getMetadataInfoByUrn(pmDTO.getUrn());

            // Only one data policy posted.
            DataPolicy dp = dto.getDataPolicies().get(0);
            if (dp.getId() == -1) {
               pm.setOverridenDataPolicy(null);
               DataPolicy dataPolicyOriginal = dpm.getDataPolicyByNameOrAlias(pm.getDataPolicy(),
                     null);
               metadataManager.updateMetadata(dataPolicyOriginal.getId(), metadata.getId());
            } else {
               dp = dpm.getDataPolicyById(dp.getId(), false, false);
               pm.setOverridenDataPolicy(dp.getName());
               metadataManager.updateMetadata(dp.getId(), metadata.getId());
            }

         }

         // Update overriden FNC pattern
         if (pmDTO.getOverridenFncPattern() != null) {
            if (pmDTO.getOverridenFncPattern().equals("-1")) {
               pm.setOverridenFncPattern(null);
            } else {
               pm.setOverridenFncPattern(pmDTO.getOverridenFncPattern());
            }
         }

         // Update overriden GTS priority
         if (pmDTO.getOverridenPriority() != null) {
            if (pmDTO.getOverridenPriority() == -1) {
               pm.setOverridenPriority(null);
            } else {
               pm.setOverridenPriority(pmDTO.getOverridenPriority());
            }
         }

         // Update overriden File Extension
         if (pmDTO.getOverridenFileExtension() != null) {
            if (pmDTO.getOverridenFileExtension().equals("-1")) {
               pm.setOverridenFileExtension(null);
            } else {
               pm.setOverridenFileExtension(pmDTO.getOverridenFileExtension());
            }
         }
         pmm.saveOrUpdate(pm);
         // Then index the new product metadata.
         GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
         DataManager dm = gc.getDataManager();
         dm.indexMetadata(dbms, pm.getUrn(), pm);
      }

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
