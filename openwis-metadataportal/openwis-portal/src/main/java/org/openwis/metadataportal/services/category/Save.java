/**
 *
 */
package org.openwis.metadataportal.services.category;

import java.text.MessageFormat;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryAlreadyExistsException;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
      Category category = JeevesJsonWrapper.read(params, Category.class);
      final Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      CategoryManager cm = new CategoryManager(dbms);

      AcknowledgementDTO acknowledgementDTO = null;

      try {
         if (category.getId() == null) {
            cm.createCategory(category);
         } else {
            cm.updateCategory(category);

            // Update all associated metadata
            GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
            ISearchManager sm = gc.getSearchmanager();
            MetadataManager mm = new MetadataManager(dbms);

            List<Metadata> metadataList = mm.getAllMetadataByCategory(category.getId());
            Function<Metadata, IndexableElement> function = new Function<Metadata, IndexableElement>() {
               @Override
               public IndexableElement apply(Metadata md) {
                  return new DbmsIndexableElement(dbms, md.getUrn());
               }
            };
            List<IndexableElement> idxElts = Lists.transform(metadataList, function);
            sm.index(idxElts);
         }

         acknowledgementDTO = new AcknowledgementDTO(true);
      } catch (CategoryAlreadyExistsException e) {
         //TODO implement several exceptions to customize the message.
         acknowledgementDTO = new AcknowledgementDTO(false, MessageFormat.format(
               "The category {0} already exists", e.getName()));
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }
}
