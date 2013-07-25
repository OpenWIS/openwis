/**
 * 
 */
package org.openwis.metadataportal.services.availability;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.model.availability.AvailabilityStatistics;
import org.openwis.metadataportal.model.availability.AvailabilityStatisticsItem;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Jeeves service: Get availability statistics.
 */
public class GetStatistics implements Service {

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
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      AvailabilityManager availabilityManager = new AvailabilityManager(dbms);

      String serviceNameFilter = Util.getParam(params, "serviceNameFilter", null);
      String sortColumnName = Util.getParam(params, "sort", null);
      String sortDir = Util.getParam(params, "dir", null);
      String xml = Util.getParam(params, "xml", null);
      String indexStr = Util.getParam(params, "start", null);
      int index;
      if (indexStr != null) {
         index = Integer.parseInt(indexStr);
      } else {
         index = 0;
      }
      String limitStr = Util.getParam(params, "limit", null);
      int limit;
      if (limitStr != null) {
         limit = Integer.parseInt(limitStr);
      } else {
         limit = 0;
      }
      String sessionCount = Util.getParam(params, "sessionCount", null);

      AvailabilityStatistics availabilityStatistics;
      if (sessionCount == null) {
         availabilityStatistics = availabilityManager.getAvailabilityStatistics(serviceNameFilter,
               sortColumnName, sortDir, index, limit);
      } else {
         availabilityStatistics = availabilityManager.getSessionCountStatistics(sortColumnName,
               sortDir, index, limit);
      }

      // If XML export, just return the element with all the data
      if (xml != null) {
         Element root = new Element("AvailabilityStatistics");
         for (AvailabilityStatisticsItem item : availabilityStatistics.getItems()) {
            Element data = new Element("item");
            data.setAttribute("date", item.getDate());
            data.setAttribute("serviceName", item.getTask());
            data.setAttribute("available", String.valueOf(item.getAvailable()));
            data.setAttribute("notAvailable", String.valueOf(item.getNotAvailable()));
            root.addContent(data);
         }
         return root;
      }

      return JeevesJsonWrapper.send(availabilityStatistics);
   }

}
