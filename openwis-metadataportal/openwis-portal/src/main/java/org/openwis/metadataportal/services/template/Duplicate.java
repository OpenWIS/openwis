/**
 * 
 */
package org.openwis.metadataportal.services.template;

import java.util.UUID;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.metadata.ITemplateManager;
import org.openwis.metadataportal.kernel.metadata.TemplateManager;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.model.metadata.source.SiteSource;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleMetadataDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Duplicate implements Service {

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
        SimpleMetadataDTO dto = JeevesJsonWrapper.read(params, SimpleMetadataDTO.class);

        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        ITemplateManager templateManager = new TemplateManager(dbms,gc.getDataManager(), gc.getSearchmanager());
        
        //--- Get the template from DB.
        Template templateDb = templateManager.getTemplateByUrn(dto.getUrn());
        
        Template duplicateTemplate = new Template();
        duplicateTemplate.setSchema(templateDb.getSchema());
        duplicateTemplate.setData(templateDb.getData());
        duplicateTemplate.setUrn(UUID.randomUUID().toString());
        duplicateTemplate.setSubTemplate(templateDb.isSubTemplate());
        duplicateTemplate.setTitle(templateDb.getTitle());
        duplicateTemplate.setDataPolicy(templateDb.getDataPolicy());
        
        String owner = context.getUserSession().getUsername();
        SiteSource source = new SiteSource(owner, gc.getSiteId(), gc.getSiteName());
        duplicateTemplate.setSource(source);

        templateManager.createTemplate(duplicateTemplate);
        
        return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
    }


}
