/**
 *
 */
package org.openwis.metadataportal.services.datapolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.interfaces.Service;
import jeeves.interfaces.ServiceWithJsp;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;

/**
 * A Jeeves service to return all data policies into an XML File (produced by the JSP). <P>
 * The administrator may be able to consult all data policies. <P>
 *
 */
public class Export implements Service, ServiceWithJsp {

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
      throw new IllegalAccessException("Should not be called, use execWithJsp");
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.ServiceWithJsp#execWithJsp(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception {
      GeonetContext geoContext = (GeonetContext) context.getHandlerContext("contextName");
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      IDataPolicyManager dpm = new DataPolicyManager(dbms);

      List<DataPolicy> dataPolicies = dpm.getAllDataPolicies(true, true);
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("dataPolicies", dataPolicies);
      result.put("siteName", geoContext.getSiteName());
      result.put("siteId", geoContext.getSiteId());
      return result;
   }

}
