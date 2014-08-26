package org.openwis.metadataportal.services.search;

import java.util.ArrayList;

import jeeves.constants.Jeeves;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.fao.geonet.kernel.search.ISearchManager.Searcher;
import org.jdom.Element;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.search.dto.RelatedMetadataDTO;
import org.openwis.metadataportal.services.search.dto.RelatedMetadataListDTO;

/**
 * The Class RelatedServicesSearch. <P>
 * 
 */
public class RelatedServicesSearch implements Service {
   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      // Nothing to do
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //String uuid = Util.getParam(params, Params.UUID);
      RelatedMetadataDTO metadataDTO = JeevesJsonWrapper.read(params, RelatedMetadataDTO.class);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ISearchManager searchMan = gc.getSearchmanager();

      // perform the search
      Log.info(Geonet.SEARCH_ENGINE, "Creating metadata for children searcher");
      MetaSearcher searcher = searchMan.newSearcher(Searcher.INDEX);

      // Creating parameters for search, fast only to retrieve uuid
      Element requestParameters = new Element(Jeeves.Elem.REQUEST);
      requestParameters.addContent(new Element("operatesOn").setText(metadataDTO.getUuid()));
      requestParameters.addContent(new Element("fast").addContent("true"));

      searcher.search(context, requestParameters, null);      
      Element relatedElement = searcher.present(context, requestParameters, null);
      
      ArrayList<RelatedMetadataDTO> metadataList = new ArrayList<RelatedMetadataDTO>();
      for (Object relatedEltObj : relatedElement.getChildren("MD_Metadata")) {
         Element relatedElt = (Element) relatedEltObj;
         System.out.println(Xml.getString(relatedElt));
         RelatedMetadataDTO md = new RelatedMetadataDTO();
         md.setTitle(relatedElt.getChildText("title"));
         md.setUuid(relatedElt.getChildText("uuid"));
         metadataList.add(md);
      }
      
      RelatedMetadataListDTO listDto = new RelatedMetadataListDTO();
      listDto.setMetadataList(metadataList);
      
      return JeevesJsonWrapper.send(listDto);

   }

}
