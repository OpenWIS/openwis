/**
 *
 */
package org.openwis.metadataportal.services.category;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.services.category.dto.EditCategoryDTO;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class Edit implements Service {

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
      EditCategoryDTO dto = JeevesJsonWrapper.read(params, EditCategoryDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ISearchManager searchMan = gc.getSearchmanager();

      // Get the metadata manager
      MetadataManager mm = new MetadataManager(dbms);
      // Loop over product metadata and Update
      for (String urn : dto.getProductsMetadataUrn()) {
         Metadata metaData = mm.getMetadataInfoByUrn(urn);
         mm.updateCategory(urn, dto.getCategory().getId());
         searchMan.index(new DbmsIndexableElement(dbms, metaData.getUrn(), null));
      }

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
