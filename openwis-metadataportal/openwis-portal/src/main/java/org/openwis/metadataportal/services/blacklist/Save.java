/**
 *
 */
package org.openwis.metadataportal.services.blacklist;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.BlacklistInfo;
import org.openwis.dataservice.BlacklistService;
import org.openwis.dataservice.BlacklistStatus;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.blacklist.dto.BlacklistDTO;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

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
      BlacklistDTO blacklistDTO = JeevesJsonWrapper.read(params, BlacklistDTO.class);

      BlacklistService blacklistService = DataServiceProvider.getBlacklistService();

      AcknowledgementDTO acknowledgementDTO = null;

      BlacklistInfo blacklistInfo = blacklistService.getUserBlackListInfo(blacklistDTO.getUser(),
            true);
      blacklistInfo.setNbDisseminationWarnThreshold(blacklistDTO.getNbDisseminationWarnThreshold());
      blacklistInfo.setVolDisseminationWarnThreshold(blacklistDTO
            .getVolDisseminationWarnThreshold());
      blacklistInfo.setNbDisseminationBlacklistThreshold(blacklistDTO
            .getNbDisseminationBlacklistThreshold());
      blacklistInfo.setVolDisseminationBlacklistThreshold(blacklistDTO
            .getVolDisseminationBlacklistThreshold());
      if (blacklistDTO.isBlacklisted())
      {
         blacklistInfo.setStatus(BlacklistStatus.BLACKLISTED_BY_ADMIN);
      }
      else
      {
         blacklistInfo.setStatus(BlacklistStatus.NOT_BLACKLISTED_BY_ADMIN);
      }

      blacklistService.updateUserBlackListInfo(blacklistInfo);

      acknowledgementDTO = new AcknowledgementDTO(true);

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }
}
