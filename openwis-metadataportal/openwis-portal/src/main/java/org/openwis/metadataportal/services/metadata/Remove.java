/**
 *
 */
package org.openwis.metadataportal.services.metadata;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.AccessManager;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.FileCopyMgr;
import org.jdom.Element;
import org.openwis.dataservice.CannotDeleteProductMetadataException_Exception;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class Remove implements Service {

   /** The bundle. */
   private ResourceBundle bundle;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      bundle = ResourceBundle.getBundle("openwisMessage");
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //Read from Ajax Request.

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      List<String> urns = JeevesJsonWrapper.read(params, List.class);

      // Get managers
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      DataManager dm = gc.getDataManager();
      AccessManager am = new AccessManager(dbms);

      Map<String, Exception> result = new HashMap<String, Exception>();
      String id = null;
      for (String urn : urns) {
         Log.info(Geonet.DATA_MANAGER, "Deleting metadata with urn: " + urn);

         // Get metadata ID
         id = dm.getMetadataId(dbms, urn);

         // Check privileges
         if (am.canEdit(context, id)) {
            // Delete the metadata
            try {
               dm.deleteMetadata(dbms, urn, true);

               // then remove the metadata directory including the public and private directories.
               File pb = new File(Lib.resource.getMetadataDir(context, id));
               FileCopyMgr.removeDirectoryOrFile(pb);

               result.put(urn, null);
            } catch (CannotDeleteProductMetadataException_Exception e) {
               result.put(urn, e);
            }
         }
      }

      // Create Acknowledgment
      boolean succes = true;
      StringBuffer sb = new StringBuffer();
      boolean isFirst = true;
      Exception e;
      for (Entry<String, Exception> entry : result.entrySet()) {
         e = entry.getValue();
         if (e != null) {
            if (isFirst) {
               succes = false;
               isFirst = false;
            } else {
               sb.append(", ");
            }
            sb.append(entry.getKey());
         }
      }

      // Build message
      String msg;
      if (succes) {
         msg = MessageFormat.format(bundle.getString("RemoveMetadata.success"), result.entrySet());
      } else {
         msg = MessageFormat.format(bundle.getString("RemoveMetadata.fail"), sb);
      }
      AcknowledgementDTO ack = new AcknowledgementDTO(succes, msg);
      return JeevesJsonWrapper.send(ack);
   }
}
