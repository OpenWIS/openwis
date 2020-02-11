/**
 *
 */
package org.openwis.metadataportal.services.request;

import java.util.Set;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.harness.subselectionparameters.AbstractParameter;
import org.openwis.harness.subselectionparameters.DynamicParameters;
import org.openwis.harness.subselectionparameters.ModeType;
import org.openwis.harness.subselectionparameters.Parameters;
import org.openwis.harness.subselectionparameters.SubSelectionParameters;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.mock.MockSubSelectionParameters;
import org.openwis.metadataportal.services.request.dto.submit.SubmitParameterDTO;
import org.openwis.metadataportal.services.request.dto.subselectionparameters.GetSubSelectionParametersDTO;
import org.openwis.metadataportal.services.request.dto.subselectionparameters.ParametersDTO;

/**
 * The Jeeves Service to return the sub selections parameters to the client. <P>
 * When doing a request, the system must return a list of sub selection parameters depending on :<P>
 * <ul>
 *    <li>The Product Metadata URN.</li>
 *    <li>The request type (Adhoc or Subscription).</li>
 * </ul>
 */
public class GetAllSubSelectionParameters implements Service {

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

        GetSubSelectionParametersDTO dto = JeevesJsonWrapper.read(params,
                GetSubSelectionParametersDTO.class);

        String lang = context.getLanguage();
        Parameters subSelParam = null;

        // Interactive mode: current selection
        Set<SubmitParameterDTO> parameters = dto.getParameters();
        boolean dynamicMode = false;


        // Check if dynamic subselection harness is defined
        DynamicParameters dynamicParameters = HarnessProvider.getDynamicParametersService();
        if (dynamicParameters != null) {
            // Dynamic capabilities, check if using dynamic mode
            if (dynamicParameters.getMode(dto.getUrn()) == ModeType.DYNAMIC) {
                // dynamic mode
                dynamicMode = true;
                Log.info(Geonet.OPENWIS, "Using dynamic mode for subselection parameters");
            }
        }

        if (dynamicMode) {
            // Dynamic mode processing
            AbstractParameter nextParameter = null;
            if (dto.isSubscription()) {
                nextParameter = dynamicParameters.getNextSubSelectionParameterForSubscription(
                        dto.getUrn(), lang, dto.getParameterList());
            } else {
                nextParameter = dynamicParameters.getNextSubSelectionParameterForRequest(dto.getUrn(),
                        lang, dto.getParameterList());
            }
            // In dynamic mode, create a list of one parameter
            subSelParam = new Parameters();
            subSelParam.getParameters().add(nextParameter);
        } else {
            // Static mode processing
            if (dynamicParameters != null) {
                Log.info(Geonet.OPENWIS, "Using static mode for subselection parameters with dynamic harness");
                // via DynamicParameters interface
                if (dto.isSubscription()) {
                    subSelParam = dynamicParameters.getSubSelectionParametersForSubscription(
                            dto.getUrn(), lang);
                } else {
                    subSelParam = dynamicParameters.getSubSelectionParametersForRequest(dto.getUrn(),
                            lang);
                }
            } else {
                Log.info(Geonet.OPENWIS, "Using static mode for subselection parameters with static harness");
                // via SubSelectionParameters interface
                SubSelectionParameters subSelectionParameters = HarnessProvider
                        .getSubSelectionParametersService();
                if (dto.isSubscription()) {
                    subSelParam = subSelectionParameters.getSubSelectionParametersForSubscription(
                            dto.getUrn(), lang);
                } else {
                    subSelParam = subSelectionParameters.getSubSelectionParametersForRequest(dto.getUrn(),
                            lang);
                }
            }
        }


        ParametersDTO paramDTO = ParametersDTO.parametersToDTO(subSelParam);

        // Set dynamic mode
        paramDTO.setInteractive(dynamicMode);

        return JeevesJsonWrapper.send(paramDTO);
    }

}
