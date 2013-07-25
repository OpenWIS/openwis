/**
 *
 */
package org.openwis.metadataportal.services.system;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.system.dto.SystemConfigurationDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class SystemConfigurationForm implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig serviceConfig) throws Exception {
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      UserManager userManager = new UserManager(dbms);

      String xslPath = context.getAppPath() + Geonet.Path.STYLESHEETS + "/xml";
      Element system = gc.getSettingManager().get("system", -1);
      Element guiConfig = Xml.transform(system, xslPath + "/config.xsl");

      //
      // SET VALUE FROM CONFIG
      //

      SystemConfigurationDTO dto = new SystemConfigurationDTO();
      // Site
      dto.setSiteName(guiConfig.getChild("site").getChild("name").getValue());
      // Server
      dto.setServerHost(guiConfig.getChild("server").getChild("host").getValue());
      dto.setServerPort(guiConfig.getChild("server").getChild("port").getValue());
      // Index Optimizer
      dto.setIndexEnable(guiConfig.getChild("indexoptimizer").getChild("enable").getValue().equals("true"));
      dto.setIndexRunAtHour(guiConfig.getChild("indexoptimizer").getChild("at").getChild("hour").getValue());
      dto.setIndexRunAtMinute(guiConfig.getChild("indexoptimizer").getChild("at").getChild("min").getValue());
      dto.setIndexRunAgain(guiConfig.getChild("indexoptimizer").getChild("interval").getChild("hour").getValue());
      // Z39.50 Server
      dto.setZ3950ServerEnable(guiConfig.getChild("z3950").getChild("enable").getValue().equals("true"));
      dto.setZ3950ServerPort(guiConfig.getChild("z3950").getChild("port").getValue());
      // XLink Resolver
      dto.setXlinkResolverEnable(guiConfig.getChild("xlinkResolver").getChild("enable").getValue().equals("true"));
      // CSW ISO Profile
      dto.setCswEnable(guiConfig.getChild("csw").getChild("enable").getValue().equals("true"));
//      dto.setCswContactAllUsers(userManager.getAllUsers());
      dto.setCswContactId(guiConfig.getChild("csw").getChild("contactId").getValue());
      dto.setCswTitle(guiConfig.getChild("csw").getChild("title").getValue());
      dto.setCswAbstract(guiConfig.getChild("csw").getChild("abstract").getValue());
      dto.setCswFees(guiConfig.getChild("csw").getChild("fees").getValue());
      dto.setCswAccess(guiConfig.getChild("csw").getChild("accessConstraints").getValue());
      // Inspire
      dto.setInspireEnable(guiConfig.getChild("inspire").getChild("enable").getValue().equals("true"));
      // Proxy
      dto.setProxyUse(guiConfig.getChild("proxy").getChild("use").getValue().equals("true"));
      dto.setProxyHost(guiConfig.getChild("proxy").getChild("host").getValue());
      dto.setProxyPort(guiConfig.getChild("proxy").getChild("port").getValue());
      dto.setProxyUserName(guiConfig.getChild("proxy").getChild("username").getValue());
      dto.setProxyPassword(guiConfig.getChild("proxy").getChild("password").getValue());
      // Feedback
      dto.setFeedBackEmail(guiConfig.getChild("feedback").getChild("email").getValue());
      dto.setFeedBackSmtpHost(guiConfig.getChild("feedback").getChild("mailServer").getChild("host").getValue());
      dto.setFeedBackSmtpPort(guiConfig.getChild("feedback").getChild("mailServer").getChild("port").getValue());
      // Authentication
      dto.setUserSelfRegistrationEnable(guiConfig.getChild("userSelfRegistration").getChild("enable").getValue().equals("true"));
      return JeevesJsonWrapper.send(dto);
   }

}
