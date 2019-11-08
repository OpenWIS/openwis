/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.request.MSSFSSManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GetAllMSSFSSProcessedRequests implements Service {

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
      String id = Util.getParam(params, "id");
      int start = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumn = Util.getParam(params, "sort", null);
      String sortDirection = Util.getParam(params, "dir", null);

      SearchCriteriaWrapper<String, String> wrapper = new SearchCriteriaWrapper<String, String>();
      wrapper.setCriteria(id);
      wrapper.setStart(start);
      wrapper.setLimit(limit);
      if (sortColumn != null && sortDirection != null) {
         wrapper.setSort(sortColumn);
         wrapper.setDir(SortDir.valueOf(sortDirection));
      }
      
      MSSFSSManager mssFssManager = new MSSFSSManager();
      return JeevesJsonWrapper.send(mssFssManager.getMSSFSSProcessedRequests(wrapper));
   }
}
