/**
 * 
 */
package org.openwis.metadataportal.services.metainfo;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.metadata.IProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.ProductMetadataManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metainfo.dto.MetaInfoDTO;

/**
 * A service that returns all informations on a data policy.
 * <P>
 * Explanation goes here.
 * <P>
 * 
 */
public class Get implements Service {

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
    @SuppressWarnings("unchecked")
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        List<String> urns = JeevesJsonWrapper.read(params, List.class);

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        //Get managers.
        IProductMetadataManager pmm = new ProductMetadataManager();
        IDataPolicyManager dpm = new DataPolicyManager(dbms);

        // -- The DTO Containing the products metadata and all data policies.
        MetaInfoDTO dto = new MetaInfoDTO();
        
        //Get each selected product metadata and all datapolicies
        for (String urn : urns) {
            dto.getProductsMetadata().add(pmm.getProductMetadataByUrn(urn));
        }
        dto.setDataPolicies(dpm.getAllDataPolicies(false, false));
        
        return JeevesJsonWrapper.send(dto);
    }

}
