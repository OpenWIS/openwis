/**
 * 
 */
package org.openwis.metadataportal.services.dissemination;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.dissemination.dto.SaveFavoriteDisseminationParameterDTO;
import org.openwis.securityservice.DisseminationParametersService;

/**
 * This service enables to save a favorite dissemination parameter. <P>
 * The user must be able to manage his favorite dissemination parameters. <P>
 * 
 */
public class SaveFavoriteDisseminationParameter implements Service {

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
      SaveFavoriteDisseminationParameterDTO dto = JeevesJsonWrapper.read(params,
            SaveFavoriteDisseminationParameterDTO.class);

      DisseminationParametersService disseminationParametersService = SecurityServiceProvider
            .getDisseminationParametersManagementService();
      String userName = context.getUserSession().getUsername();

      if (dto.getFtp() != null) {
         disseminationParametersService.addOrUpdateFTPForDisseminationParameters(userName,
               dto.getFtp());
      } else if (dto.getMail() != null) {
         disseminationParametersService.addOrUpdateEmailForDisseminationParameters(userName,
               dto.getMail());
      }
      return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
   }

}
