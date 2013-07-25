/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.XmlSerializer;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.model.metadata.source.SiteSource;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class TemplateManager extends AbstractManager implements ITemplateManager {

   private static final String STOP_GAP_TEMPLATE_FILTER = "Stop-Gap";
   private static final String DEFAULT_TEMPLATE_FILTER = "OpenWIS";
   
   /**
    * The data manager.
    */
   private final DataManager dataManager;

   private final ISearchManager searchManager;

   /**
    * Default constructor.
    * Builds a TemplateManager.
    * @param dbms
    */
   public TemplateManager(Dbms dbms, DataManager dataManager, ISearchManager searchManager) {
      super(dbms);
      this.dataManager = dataManager;
      this.searchManager = searchManager;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.ITemplateManager#updateDisplayOrder(java.util.List)
    */
   @Override
   public void updateDisplayOrder(List<Template> templates) throws Exception {
      if (CollectionUtils.isNotEmpty(templates)) {
         int displayOrder = 0;
         Collection<IndexableElement> indexableElements = new ArrayList<IndexableElement>();
         for (Template tpl : templates) {
            String query = "UPDATE Metadata SET displayOrder=? where uuid=?";
            getDbms().execute(query, displayOrder++, tpl.getUrn());
            indexableElements.add(new DbmsIndexableElement(getDbms(), tpl.getUrn(), null));
         }
         searchManager.index(indexableElements);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.ITemplateManager#removeTemplate(java.lang.String)
    */
   @Override
   public void removeTemplate(String urn) throws Exception {
      dataManager.deleteTemplate(getDbms(), urn);
      // update display reorder
      List<Template> templates = searchManager.getAllTemplates();
      updateDisplayOrder(templates);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.ITemplateManager#addDefaultTemplateFromLocalDirectory(java.lang.String, java.lang.String, org.openwis.metadataportal.model.metadata.source.SiteSource)
    */
   @Override
   public void addDefaultTemplateFromLocalDirectory(String schemaName,
         String templateDirectoryPath, SiteSource source) throws Exception {

      File schemaDirectory = new File(templateDirectoryPath, schemaName).getAbsoluteFile();

      File[] templateFiles = schemaDirectory.listFiles(new TemplateFilenameFilter());

      for (File temp : templateFiles) {
         try {
            Template template = new Template();
            template.setSchema(schemaName);
            template.setData(Xml.loadFile(temp));
            template.setUrn(UUID.randomUUID().toString());

            if (temp.getName().startsWith("sub-")) {
               String title = temp.getName().substring(4, temp.getName().length() - 4);
               template.setSubTemplate(true);
               template.setTitle(title);
            }
            template.setSource(source);

            createTemplate(template);
         } catch (Exception e) {
            getDbms().abort();
            Log.error(Geonet.DATA_MANAGER, "Error loading template: " + e.getMessage());
            throw e;
         }
      }
   }

   /**
    * Gets the default template.
    *
    * @return the default template
    * @throws Exception the exception
    */
   @Override
   public Template getDefaultTemplate() throws Exception {
      return getIso19139Template(false);
   }
   
   /**
    * Gets the stop-gap template.
    *
    * @return the stop-gap template
    * @throws Exception the exception
    */
   @Override
   public Template getStopGapTemplate() throws Exception {
      return getIso19139Template(true);
   }
   
   /**
    * Get ISO19139 template; default use in OpenWIS for creation template and stop-gap.
    *
    * @return the iso19139 template
    * @throws Exception the exception
    */
   private Template getIso19139Template(boolean stopGap) throws Exception {
      List<Template> templates = searchManager.getAllTemplates();
      for (Template template : templates) {
         if (stopGap) { 
            if (template.getTitle() != null && template.getTitle().contains(STOP_GAP_TEMPLATE_FILTER)) {
               return template;
            }
         } else {
            if (template.getTitle() != null && template.getTitle().contains(DEFAULT_TEMPLATE_FILTER)) {
               return template;
            }
         }
      }
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.ITemplateManager#createTemplate(org.openwis.metadataportal.model.metadata.Template)
    */
   @Override
   public void createTemplate(Template template) throws Exception {
      dataManager.setNamespacePrefixUsingSchemas(template.getData(), template.getSchema());

      //Get default data policy.
      if (template.getDataPolicy() == null) {
         DataPolicyManager dpm = new DataPolicyManager(getDbms());
         DataPolicy dp = dpm.getDataPolicyByName(dpm.getDefaultDataPolicyName(), false, false);
         template.setDataPolicy(dp);
      }

      //Set the display order.
      template.setDisplayOrder(getNextDisplayOrder());

      //Create template.
      XmlSerializer.insertTemplate(getDbms(), template);

      getDbms().commit();

      try {
         IndexableElement element = new DbmsIndexableElement(getDbms(), template.getUrn(), null);
         searchManager.index(element);
      } catch (IndexException e) {
         Log.warning(Geonet.INDEX_ENGINE, "Could not index the template " + template.getUrn(), e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.ITemplateManager#getTemplateByUrn(java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Template getTemplateByUrn(String urn) throws Exception {
      String query = "SELECT id, uuid, data, schemaId, isTemplate, title, datapolicy "
            + "FROM Metadata WHERE uuid=?";

      // Check record already into the DB and get information.
      List<Element> records = getDbms().select(query, urn).getChildren();

      Template template = null;
      if (!records.isEmpty()) {
         Element e = records.get(0);
         template = buildTemplate(e);
      }
      return template;
   }

   /**
    * Builds the template.
    *
    * @param e the e
    * @return the template
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JDOMException the jDOM exception
    */
   private Template buildTemplate(Element e) throws IOException, JDOMException {
      Template template = new Template();
      template.setId(Integer.parseInt(e.getChildText("id")));
      template.setUrn(e.getChildText("uuid"));
      template.setData(Xml.loadString(e.getChildText("data"), false));
      template.setSchema(e.getChildText("schemaid"));
      template.setSubTemplate(BooleanUtils.toBoolean(e.getChildText("istemplate"), "s", "y"));
      template.setTitle(e.getChildText("title"));

      DataPolicy dp = new DataPolicy();
      dp.setId(Integer.parseInt(e.getChildText("datapolicy")));
      template.setDataPolicy(dp);
      return template;
   }

   /**
    * Gets the next display order available.
    * @return the next display order available.
    * @throws Exception if an error occurs.
    */
   @SuppressWarnings("unchecked")
   private Integer getNextDisplayOrder() throws Exception {
      String query = "SELECT (MAX(displayorder) + 1) AS nextDisplayOrder FROM Metadata";
      List<Element> records = getDbms().select(query).getChildren();

      int nextDisplayOrder = 0;
      if (!records.isEmpty()) {
         Element rec = records.get(0);
         String nextDisplayOrderStr = rec.getChildText("nextdisplayorder");
         if (StringUtils.isNotBlank(nextDisplayOrderStr)
               && StringUtils.isNumeric(nextDisplayOrderStr)) {
            nextDisplayOrder = new Integer(nextDisplayOrderStr);
         }
      }
      return nextDisplayOrder;
   }
}
