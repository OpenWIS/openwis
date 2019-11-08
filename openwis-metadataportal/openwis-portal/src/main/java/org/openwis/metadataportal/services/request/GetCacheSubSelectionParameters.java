/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.cache.CacheIndexWebService;
import org.openwis.dataservice.cache.CachedFile;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockCachedFiles;
import org.openwis.metadataportal.services.mock.MockMode;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GetCacheSubSelectionParameters implements Service {

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
      String urn = Util.getParam(params, "urn");
      String startDate = Util.getParam(params, "startDate");
      String endDate = Util.getParam(params, "endDate");

      List<CachedFile> cachedFiles = null;
      if (MockMode.isMockModeDataServiceCache()) {
         cachedFiles = MockCachedFiles.get();
      } else {
         CacheIndexWebService cacheIndexService = DataServiceProvider.getCacheIndexService();
         cachedFiles = cacheIndexService.listFilesByMetadataUrnAndDate(urn, startDate, endDate);
      }
      return JeevesJsonWrapper.send(cachedFiles);
   }

}
