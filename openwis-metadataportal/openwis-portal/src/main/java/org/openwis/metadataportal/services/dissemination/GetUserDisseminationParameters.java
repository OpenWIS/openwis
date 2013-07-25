package org.openwis.metadataportal.services.dissemination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.collections.CollectionUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.dissemination.dto.AllDiffusionDisseminationParameterDTO;
import org.openwis.metadataportal.services.dissemination.dto.AllDisseminationParametersDTO;
import org.openwis.metadataportal.services.dissemination.dto.AllMSSFSSDisseminationParameterDTO;
import org.openwis.metadataportal.services.mock.MockGetDisseminationParameters;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.request.dto.subselectionparameters.GetSubSelectionParametersDTO;
import org.openwis.securityservice.DisseminationParametersService;
import org.openwis.securityservice.DisseminationTool;
import org.openwis.securityservice.OpenWISEmail;
import org.openwis.securityservice.OpenWISFTP;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * The Jeeves Service to return all dissemination parameters to the client. <P>
 * When doing a request, the user must have the choice between several dissemination types :<P>
 * <ul>
 *    <li>The MSS/FSS Channels.</li>
 *    <li>The user favorites disseminations parameters.</li>
 * </ul>
 */
public class GetUserDisseminationParameters implements Service {

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
      //Get URN
      GetSubSelectionParametersDTO simpleMetadataDTO = JeevesJsonWrapper.read(params,
            GetSubSelectionParametersDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      IDataPolicyManager dpm = new DataPolicyManager(dbms);
      GroupManager groupManager = new GroupManager(dbms);

      //Get user groups.
      List<Group> userGroups = null;
      if (context.getUserSession().isAuthenticated()) {
         if (context.getUserSession().getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
            userGroups = groupManager.getAllGroups();
         } else {
            userGroups = groupManager.getAllUserGroups(context.getUserSession().getUserId());
         }
      } else {
         userGroups = new ArrayList<Group>();
         userGroups.add(new Group(-1, Geonet.Profile.GUEST));
      }

      //Get all operations allowed.
      Collection<Operation> operationsAllowed = dpm.getAllOperationAllowedByMetadataUrn(
            simpleMetadataDTO.getUrn(), userGroups);

      //Compute operations allowed.
      boolean isMssFssSupported = OpenwisMetadataPortalConfig
            .getBoolean(ConfigurationConstants.MSSFSS_SUPPORT);
      List<String> channels = null;
      if (isMssFssSupported && simpleMetadataDTO.isSubscription()) {
         if (MockMode.isMockModeHarnessMSSFSS()) {
            channels = MockGetDisseminationParameters.getMSSFSSChannelsMock();
         } else {
            MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
            channels = mssFssService.getChannelsForUser(context.getUserSession().getUsername());
         }
      }

      boolean isAllowedMSSFSS = isMssFssSupported && simpleMetadataDTO.isSubscription()
            && CollectionUtils.isNotEmpty(channels);
      boolean isAllowedRMDCNFtp = isAllowed(OperationEnum.RMDCN_FTP, operationsAllowed);
      boolean isAllowedRMDCNMail = isAllowed(OperationEnum.RMDCN_EMAIL, operationsAllowed);
      boolean isAllowedPublicFtp = isAllowed(OperationEnum.PUBLIC_FTP, operationsAllowed);
      boolean isAllowedPublicMail = isAllowed(OperationEnum.PUBLIC_EMAIL, operationsAllowed);

      AllDisseminationParametersDTO dto = new AllDisseminationParametersDTO();

      //Get MSS/FSS channels.
      dto.setMssFss(new AllMSSFSSDisseminationParameterDTO());
      dto.getMssFss().setMssFssChannels(channels);
      dto.getMssFss().setAuthorized(isAllowedMSSFSS);

      dto.setRmdcnDiffusion(new AllDiffusionDisseminationParameterDTO());
      dto.getRmdcnDiffusion().setAuthorizedFtp(isAllowedRMDCNFtp);
      dto.getRmdcnDiffusion().setAuthorizedMail(isAllowedRMDCNMail);

      dto.setPublicDiffusion(new AllDiffusionDisseminationParameterDTO());
      dto.getPublicDiffusion().setAuthorizedFtp(isAllowedPublicFtp);
      dto.getPublicDiffusion().setAuthorizedMail(isAllowedPublicMail);

      //If at least one operation is allowed, retrieve information from LDAP.
      if (isAllowedRMDCNFtp || isAllowedRMDCNMail || isAllowedPublicFtp || isAllowedPublicMail) {
         //Get user name from session
         String userName = context.getUserSession().getUsername();

         //Get user information from LDAP.
         DisseminationParametersService disseminationParametersService = SecurityServiceProvider
               .getDisseminationParametersManagementService();

         //If RMDCN or public FTP is allowed.
         if (isAllowedRMDCNFtp || isAllowedPublicFtp) {
            List<OpenWISFTP> ftps = disseminationParametersService
                  .getFTPForDisseminationParameters(userName);

            for (OpenWISFTP ftp : ftps) {
               if (ftp.getDisseminationTool().equals(DisseminationTool.RMDCN) && isAllowedRMDCNFtp) {
                  dto.getRmdcnDiffusion().getFtp().add(ftp);
               } else if (ftp.getDisseminationTool().equals(DisseminationTool.PUBLIC)
                     && isAllowedPublicFtp) {
                  dto.getPublicDiffusion().getFtp().add(ftp);
               }
            }
         }

         //If RMDCN or public Mail is allowed.
         if (isAllowedRMDCNMail || isAllowedPublicMail) {
            List<OpenWISEmail> mails = disseminationParametersService
                  .getEmailForDisseminationParameters(userName);

            for (OpenWISEmail mail : mails) {
               if (mail.getDisseminationTool().equals(DisseminationTool.RMDCN)
                     && isAllowedRMDCNMail) {
                  dto.getRmdcnDiffusion().getMail().add(mail);
               } else if (mail.getDisseminationTool().equals(DisseminationTool.PUBLIC)
                     && isAllowedPublicMail) {
                  dto.getPublicDiffusion().getMail().add(mail);
               }
            }
         }
      }

      return JeevesJsonWrapper.send(dto);
   }

   //---------------------------------------------------------------- Helper method.
   private boolean isAllowed(final OperationEnum operationEnum,
         Collection<Operation> operationsAllowed) {
      return Iterables.any(operationsAllowed, new Predicate<Operation>() {

         @Override
         public boolean apply(Operation operation) {
            return operationEnum.getId() == operation.getId().intValue();
         }
         
      });
   }

}
