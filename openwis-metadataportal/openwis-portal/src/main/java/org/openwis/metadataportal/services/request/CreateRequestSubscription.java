package org.openwis.metadataportal.services.request;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jeeves.exceptions.OperationNotAllowedEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import jeeves.utils.Log;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.dataservice.AdHoc;
import org.openwis.dataservice.ClassOfService;
import org.openwis.dataservice.Request;
import org.openwis.dataservice.RequestService;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionBackup;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.harness.mssfss.CreateRouting;
import org.openwis.harness.mssfss.CreateRoutingResponse;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.metadataportal.common.utils.Utils;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.request.dto.common.SimpleRequestDTO;
import org.openwis.metadataportal.services.request.dto.submit.SubmitRequestSubscriptionDTO;
import org.openwis.metadataportal.services.request.util.OperationEnumUtils;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;
import org.openwis.metadataportal.services.util.mail.OpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;

/**
 * The Jeeves Service to submit the request or subscription to the server. <P>
 * After selecting sub selection parameters and dissemination parameters,
 * the request must be submitted to the server in order to be processed.
 */
public class CreateRequestSubscription implements Service {

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
        if (operationsAllowed == null || !operationsAllowed.contains(OperationEnum.DOWNLOAD.getId())) {
            throw new OperationNotAllowedEx();
        }

        String userName = context.getUserSession().getUsername();

        String requestID = null;
        if (dto.getPrimaryDissemination().getMssFssDissemination() != null) {
            //Subscription on MSS/FSS : Create a Routing on MSS/FSS System.
            CreateRouting createRouting = dto.asCreateRouting();
            createRouting.setUser(userName);

            MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
            CreateRoutingResponse createRoutingResponse = mssFssService.createRouting(createRouting);
            requestID = createRoutingResponse.getIdRequest();
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

            Long id = null;
            if (dto.isSubscription()) {
                Subscription subscription = (Subscription) request;
                if (StringUtils.isNotBlank(dto.getBackupRequestId())) {
                    SubscriptionBackup subscriptionBackUp = new SubscriptionBackup();
                    subscriptionBackUp.setSubscriptionId(Long.valueOf(dto.getBackupRequestId()));
                    subscriptionBackUp.setDeployment(dto.getBackupDeployment());
                    subscription.setSubscriptionBackup(subscriptionBackUp);
                    subscription.setBackup(true);
                }

                SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
                id = subscriptionService.createSubscription(subscription, dto.getProductMetadataURN());
            } else {
                AdHoc adhoc = (AdHoc) request;
                RequestService requestService = DataServiceProvider.getRequestService();
                id = requestService.createRequest(adhoc, dto.getProductMetadataURN());
            }
            requestID = Utils.formatRequestID(id);
        }

        // send email to user
        sendEmailToUser(context,
                context.getUserSession().getMail(),
                context.getUserSession().getName(),
                context.getUserSession().getSurname(),
                requestID,
                dto);

        // Acknowledgment
        AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true, new SimpleRequestDTO(
                requestID));
        return JeevesJsonWrapper.send(acknowledgementDTO);
    }

    /**
     * Sending email notification to the administrator after the end user has requested an account
     * @param context context
     * @param email user email address
     * @param firstname firstname of the user
     * @param lastname last name of the user
     */
    private void sendEmailToUser(ServiceContext context, String email, String firstname, String lastname, String requestID, SubmitRequestSubscriptionDTO submitRequestSubscriptionDTO) {

        MailUtilities mail = new MailUtilities();

        Map<String, Object> content = new HashMap<>();
        content.put("firstname", firstname);
        content.put("lastname", lastname);
        content.put("username", email);
        content.put("subscription", submitRequestSubscriptionDTO);
        content.put("requestID", requestID);

        String subject = OpenWISMessages.format("SubscriptionMail.subject", context.getLanguage(), new String[]{submitRequestSubscriptionDTO.getProductMetadataURN()});
        OpenWISMail openWISMail = OpenWISMailFactory.buildSubscriptionNotificationMail(context, subject,new String[]{email}, content);
        boolean result = mail.send(openWISMail);
        if (!result) {
            // To be confirmed: Set ack dto if error message is requested
            //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
            Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the end user("+email+")");
        } else {
            Log.info(Geonet.SELF_REGISTER, "Account recovery email sent successfully to the end user("+email+") from "+openWISMail.getAdministratorAddress());
        }
    }

}
