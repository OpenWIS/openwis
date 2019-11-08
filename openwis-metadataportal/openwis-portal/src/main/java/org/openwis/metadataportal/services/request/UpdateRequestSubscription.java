package org.openwis.metadataportal.services.request;

import java.util.Collection;

import jeeves.exceptions.OperationNotAllowedEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.dataservice.ClassOfService;
import org.openwis.dataservice.Request;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.harness.mssfss.ChangeRouting;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.request.dto.common.SimpleRequestDTO;
import org.openwis.metadataportal.services.request.dto.submit.SubmitRequestSubscriptionDTO;
import org.openwis.metadataportal.services.request.util.OperationEnumUtils;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * The Jeeves Service to submit the request or subscription to the server. <P>
 * After selecting sub selection parameters and dissemination parameters,
 * the request must be submitted to the server in order to be processed.
 */
public class UpdateRequestSubscription implements Service {

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

      SubmitRequestSubscriptionDTO dto = JeevesJsonWrapper.read(params,
            SubmitRequestSubscriptionDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      UserSession session = context.getUserSession();
      Collection<Integer> operationsAllowed = OperationEnumUtils.getOperationEnum(session, dbms,
            dto.getProductMetadataURN());
      // Access admin, operator and so Administrator are allowed to modify metadata
      boolean operator = session.getProfile().equals(Geonet.Profile.ADMINISTRATOR)
            || session.getProfile().equals(Geonet.Profile.OPERATOR)
            || session.getProfile().equals(Geonet.Profile.ACCESS_ADMIN);
      if (!operator && (operationsAllowed == null
            || !operationsAllowed.contains(OperationEnum.DOWNLOAD.getId()))) {
         throw new OperationNotAllowedEx();
      }

      if (dto.getPrimaryDissemination().getMssFssDissemination() != null) {
         //Subscription on MSS/FSS : Create a Routing on MSS/FSS System.
         ChangeRouting changeRouting = dto.asChangeRouting();

         MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
         mssFssService.changeRouting(changeRouting);
      } else {
         //Request on Data Service.
         Request request = dto.asRequest();
         request.setUser(context.getUserSession().getUsername());
         request.setEmail(context.getUserSession().getMail());
         Object classOfService = context.getUserSession().getProperty(
               LoginConstants.CLASS_OF_SERVICE);
         if (classOfService != null) {
            try {
               request.setClassOfService(ClassOfService.valueOf(classOfService.toString()));
            } catch (Exception e) {
               // ignore, class of service will be null
            }
         }

         if (dto.isSubscription()) {
            Subscription subscription = (Subscription) request;
            SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
            subscriptionService.updateSubscriptionConfig(
                  subscription.getId(),
                  subscription.getParameters(),
                  subscription.getPrimaryDissemination(),
                  subscription.getSecondaryDissemination(),
                  subscription.getFrequency(),
                  DateTimeUtils.format(subscription.getStartingDate().toGregorianCalendar()
                        .getTime()));
         }
      }

      // Acknowledgment
      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true, new SimpleRequestDTO(
            dto.getRequestID()));
      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
