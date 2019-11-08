/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.util.HashMap;
import java.util.Map;

import jeeves.interfaces.Service;
import jeeves.interfaces.ServiceWithJsp;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;

/**
 * OpenWis Choose Domain. <P>
 * 
 */
public class OpenWisChooseDomain implements Service, ServiceWithJsp {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {      
      throw new IllegalAccessException("Should not be called, use execWithJsp");
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.ServiceWithJsp#execWithJsp(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception {
      
      Map<String, Object> map = new HashMap<String, Object>();
      map.put(LoginConstants.RELAY_STATE, params.getChildText(LoginConstants.RELAY_STATE));

      return map;
   }
}
