/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.validator;

import java.util.ArrayList;
import java.util.List;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;
import jeeves.utils.Xml.ErrorHandler;

import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A XSD validator to validate metadata element. <P>
 * <P>
 * <P>
 * !!! Patch: skip validation when old GML namespace is detected. !!!
 * Old GML namespace: "http://www.opengis.net/gml".
 * The new version is http://www.opengis.net/gml/3.2.
 * <P>
 */
public class XSDMetadataValidator implements IMetadataValidator {

   /**
    * Comment for <code>NAMESPACE_GEONET</code>
    */
   private static final Namespace NAMESPACE_GEONET = Namespace.getNamespace("geonet",
         "http://www.fao.org/geonetwork");

   /**
    * !!! Patch: skip validation when old GML namespace is detected. !!!
    * Old GML namespace: "http://www.opengis.net/gml".
    * The new version is http://www.opengis.net/gml/3.2.
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.validator.IMetadataValidator#validate(org.jdom.Element, java.lang.String)
    */
   @Override
   public MetadataValidatorResult validate(DataManager dataManager, Element md, String schema,
         ServiceContext context) {
      MetadataValidatorResult metadataValidatorResult = new MetadataValidatorResult();
      String schemaDir = dataManager.getSchemaDir(schema);
      ErrorHandler errorHandler = new ErrorHandler();
      errorHandler.setNs(Edit.NAMESPACE);
      Element xsdErrors;

      try {

         if (isOldGmlNamespace(md, schema)) {
            Log.info(Geonet.METADATA_ALIGNER, "Old GML Schema detected - XSD Validation skipped");
            metadataValidatorResult.setValidate(true);
            return metadataValidatorResult;
         }

         xsdErrors = Xml.validateInfo(schemaDir + Geonet.File.SCHEMA, md, errorHandler);
         if (xsdErrors != null) {
            List<Namespace> nsList = new ArrayList<Namespace>();
            nsList.add(NAMESPACE_GEONET);
            final String xpathMess = "geonet:error/geonet:message";
            List<?> list = Xml.selectNodes(xsdErrors, xpathMess, nsList);
            // Just get the first X errors
            if (list.size() > ERROR_SIZE_LIMIT) {
               list = list.subList(0, ERROR_SIZE_LIMIT);
            }
            String mess = "";
            for (Object object : list) {
               if (object instanceof Element) {
                  Element elt = (Element) object;
                  mess = mess + elt.getValue() + "<br><br>";
               }
            }
            metadataValidatorResult.setMessage(mess);
            Log.warning(Geonet.METADATA_ALIGNER,
                  "XSD Validation failed: " + Xml.getString(xsdErrors));
         }
         metadataValidatorResult.setValidate(xsdErrors == null);
         return metadataValidatorResult;
      } catch (Exception e) {
         metadataValidatorResult.setValidate(false);
         return metadataValidatorResult;
      }
   }

   /**
    * !!! Patch: skip validation when old GML namespace is detected. !!!
    * Detect if md contains an old GML namespace: "http://www.opengis.net/gml".
    * The new version is http://www.opengis.net/gml/3.2.
    */
   @SuppressWarnings("unchecked")
   private boolean isOldGmlNamespace(Element md, String schema) {
      if (!"iso19139".equals(schema)) {
         return false;
      }
      List<Namespace> metadataAdditionalNS = md.getAdditionalNamespaces();
      for (Namespace namespace : metadataAdditionalNS) {
         if ("gml".equalsIgnoreCase(namespace.getPrefix())) {
            if ("http://www.opengis.net/gml".equalsIgnoreCase(namespace.getURI())) {
               return true;
            }
            return false;
         }
      }
      return false;
   }

}
