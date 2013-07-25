/**
 *
 */
package org.openwis.metadataportal.services.system;

import java.util.HashMap;
import java.util.Map;

import jeeves.exceptions.OperationAbortedEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.system.dto.SystemConfigurationDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class SystemConfiguration implements Service {

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
      SystemConfigurationDTO dto = JeevesJsonWrapper.read(params, SystemConfigurationDTO.class);
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      SettingManager sm = gc.getSettingManager();
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      Map<String, Object> values = new HashMap<String, Object>();
      // SITE
      if (dto.getSiteName() != null && !dto.getSiteName().trim().isEmpty()) {
         values.put("system/site/name", dto.getSiteName());
      }
      // SERVER
      if (dto.getServerHost() != null && !dto.getServerHost().trim().isEmpty()) {
         values.put("system/server/host", dto.getServerHost());
      }
      //if (StringUtils.isNumeric(dto.getServerPort()))
      if (dto.getServerPort() != null) {
         values.put("system/server/port", dto.getServerPort());
      }
      //  INDEX OPTIMIZER
      values.put("system/indexoptimizer/enable", dto.isIndexEnable());
      if (dto.getIndexRunAtHour() != null && !dto.getIndexRunAtHour().trim().isEmpty()) {
         values.put("system/indexoptimizer/at/hour", dto.getIndexRunAtHour());
      }
      if (dto.getIndexRunAtMinute() != null && !dto.getIndexRunAtMinute().trim().isEmpty()) {
         values.put("system/indexoptimizer/at/min", dto.getIndexRunAtMinute());
      }
      if (dto.getIndexRunAgain() != null && !dto.getIndexRunAgain().trim().isEmpty()) {
         values.put("system/indexoptimizer/interval/hour", dto.getIndexRunAgain());
      }
      // Z39.50 SERVER
      values.put("system/z3950/enable", dto.isZ3950ServerEnable());
      if (dto.getZ3950ServerPort() != null) {
         values.put("system/z3950/port", dto.getZ3950ServerPort());
      }
      // XLINK RESOLVER
      values.put("system/xlinkResolver/enable", dto.isXlinkResolverEnable());
      // CSW ISO PROFILE
      values.put("system/csw/enable", dto.isCswEnable());
      if (dto.getCswContactId() != null && !dto.getCswContactId().trim().isEmpty()) {
         values.put("system/csw/contactId", dto.getCswContactId());
      }
      if (dto.getCswTitle() != null) {
         values.put("system/csw/title", dto.getCswTitle());
      }
      if (dto.getCswAbstract() != null) {
         values.put("system/csw/abstract", dto.getCswAbstract());
      }
      if (dto.getCswFees() != null) {
         values.put("system/csw/fees", dto.getCswFees());
      }
      if (dto.getCswAccess() != null) {
         values.put("system/csw/accessConstraints", dto.getCswAccess());
      }
      // INSPIRE
      values.put("system/inspire/enable", dto.isInspireEnable());
      // PROXY
      values.put("system/proxy/use", dto.isProxyUse());
      if (dto.getProxyHost() != null) {
         values.put("system/proxy/host", dto.getProxyHost());
      }
      if (dto.getProxyPort() != null) {
         values.put("system/proxy/port", dto.getProxyPort());
      }
      if (dto.getProxyUserName() != null) {
         values.put("system/proxy/username", dto.getProxyUserName());
      }
      if (dto.getProxyPassword() != null) {
         values.put("system/proxy/password", dto.getProxyPassword());
      }
      // FEEDBACK
      if (dto.getFeedBackEmail() != null) {
         values.put("system/feedback/email", dto.getFeedBackEmail());
      }
      if (dto.getFeedBackSmtpHost() != null) {
         values.put("system/feedback/mailServer/host", dto.getFeedBackSmtpHost());
      }
      if (dto.getFeedBackSmtpPort() != null) {
         values.put("system/feedback/mailServer/port", dto.getFeedBackSmtpPort());
      }
      // AUTHENTICATION
      values.put("system/userSelfRegistration/enable", dto.isUserSelfRegistrationEnable());

      // SAVE VALUES
      if (!sm.setValues(dbms, values))
         throw new OperationAbortedEx("Cannot set all values");

      return JeevesJsonWrapper.send(new AcknowledgementDTO(true, "System Configuration updated"));
   }

}