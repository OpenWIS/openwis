/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.harness.subselectionparameters.Parameters;
import org.openwis.harness.subselectionparameters.SubSelectionParameters;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.mock.MockSubSelectionParameters;
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

        if (MockMode.isMockModeHarnessSSP()) {
            subSelParam = MockSubSelectionParameters.getSubSelectionParamters();
        } else {
           Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
           Metadata metadata = new MetadataManager(dbms).getMetadataInfoByUrn(dto.getUrn());
           
           
            SubSelectionParameters subSelectionParameters = HarnessProvider
                    .getSubSelectionParametersService();
            if (dto.isSubscription()) {
                subSelParam = subSelectionParameters.getSubSelectionParametersForSubscription(
                        metadata.getUrn(), lang);
            } else {
                subSelParam = subSelectionParameters.getSubSelectionParametersForRequest(
                      metadata.getUrn(), lang);
            }
        }

        ParametersDTO paramDTO = ParametersDTO.parametersToDTO(subSelParam);

        return JeevesJsonWrapper.send(paramDTO);
    }
}
