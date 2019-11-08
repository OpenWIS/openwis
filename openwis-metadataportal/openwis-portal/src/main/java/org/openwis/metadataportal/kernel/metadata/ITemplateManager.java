/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.util.List;

import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.model.metadata.source.SiteSource;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public interface ITemplateManager {

   /**
    * Updates the display order of the given templates.
    * @param templates the templates sorted with the new order.
    * @throws Exception if an error occurs.
    */
   void updateDisplayOrder(List<Template> templates) throws Exception;

   /**
    * Deletes a template with its URN.
    * @param urn the urn of the template.
    */
   void removeTemplate(String urn) throws Exception;

   /**
    * Adds a default template from a local directory.
    * @param schema the schema.
    * @param templateDirectory the template directory.
    * @param source the source.
    */
   void addDefaultTemplateFromLocalDirectory(String schema, String templateDirectory,
         SiteSource source) throws Exception;

   /**
    * Creates a template.
    * @param template a template.
    * @throws Exception if an error occurs.
    */
   void createTemplate(Template template) throws Exception;

   /**
    * Gets a template by its URN.
    * @param urn the urn.
    * @return the template.
    * @throws Exception if an error occurs.
    */
   Template getTemplateByUrn(String urn) throws Exception;

   /**
    * Gets the default template.
    *
    * @return the default template
    * @throws Exception the exception
    */
   Template getDefaultTemplate() throws Exception;
   
   /**
    * Gets the stop-gap template.
    *
    * @return the stop-gap template
    * @throws Exception the exception
    */
   Template getStopGapTemplate() throws Exception;

}
