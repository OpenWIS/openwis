/**
 * 
 */
package org.openwis.metadataportal.services.datapolicy;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.InvalidDataPolicyAliasException;
import org.openwis.metadataportal.kernel.datapolicy.InvalidDataPolicyNameException;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
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
        DataPolicy datapolicy = JeevesJsonWrapper.read(params, DataPolicy.class);

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        DataPolicyManager dpm = new DataPolicyManager(dbms);

        AcknowledgementDTO acknowledgementDTO = null;

        try {
           if(datapolicy.getId() == 0) {
              dpm.createDataPolicy(datapolicy);
           } else {
              dpm.updateDataPolicy(datapolicy);
              // Call method checkSubscription on RequestManager service.
              RequestManager requestManager = new RequestManager();
              requestManager.checkUsersSubscription(context);
           }
            acknowledgementDTO = new AcknowledgementDTO(true);
        } catch (InvalidDataPolicyAliasException e) {
            acknowledgementDTO = new AcknowledgementDTO(false, "Alias invalid ...  " + e.getInvalidAliases());
        } catch (InvalidDataPolicyNameException e) {
            acknowledgementDTO = new AcknowledgementDTO(false, "Invalid name ...  " + e.getName());
        }
        
        return JeevesJsonWrapper.send(acknowledgementDTO);
    }

}
