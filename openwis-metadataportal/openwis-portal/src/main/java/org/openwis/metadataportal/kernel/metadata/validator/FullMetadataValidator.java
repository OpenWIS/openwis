/**
 * FullMetadataValidator
 */
package org.openwis.metadataportal.kernel.metadata.validator;

import java.util.ArrayList;
import java.util.List;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A XSD + Schematron validator to validate metadata element. <P>
 * Explanation goes here. <P>
 * 
 */
public class FullMetadataValidator extends XSDMetadataValidator implements IMetadataValidator {

   /**
    * Comment for <code>NAMESPACE_GEONET</code>
    */
   private static final Namespace NAMESPACE_GEONET = Namespace.getNamespace("geonet",
         "http://www.fao.org/geonetwork");

   /**
    * Comment for <code>NAMESPACE_SVRL</code>
    */
   private static final Namespace NAMESPACE_SVRL = Namespace.getNamespace("svrl",
         "http://purl.oclc.org/dsdl/svrl");

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.validator.IMetadataValidator#validate(org.jdom.Element, java.lang.String)
    */
   @Override
   public MetadataValidatorResult validate(DataManager dataManager, Element md, String schema,
         ServiceContext context) {
      // VALIDATE WITH XSD
      MetadataValidatorResult metadataValidatorResult = super.validate(dataManager, md, schema, context);
      if (metadataValidatorResult.isValidate())
      {
         // VALIDATE SCHEMATRON
         Element report;
         try {
            report = dataManager.getSchemaTronXmlReport(schema, md, context.getLanguage());
            if (report != null) {
               List<Namespace> nsList = new ArrayList<Namespace>();
               nsList.add(NAMESPACE_GEONET);
               nsList.add(NAMESPACE_SVRL);
               final String xpath = "geonet:report/svrl:schematron-output/svrl:failed-assert";
               List<?> list = Xml.selectNodes(report, xpath, nsList);
               // Just get the first X errors
               if (list.size() > ERROR_SIZE_LIMIT) {
                  list = list.subList(0, ERROR_SIZE_LIMIT);
               }
               String mess = "";
               for (Object object : list) {
                  if (object instanceof Element) {
                     Element elt = (Element) object;

                     Element geonetReport = elt.getParentElement().getParentElement();
                     String schematronRule = geonetReport.getAttributeValue("rule", NAMESPACE_GEONET);
                     
                     mess = schematronRule + ":" + mess + elt.getValue() + "<br><br>";
                  }
               }
               
               if (list.size() > 0) {
                  metadataValidatorResult.setValidate(false);
                  metadataValidatorResult.setMessage(mess);
                  Log.warning(Geonet.METADATA_ALIGNER, "Schematron Validation failed: " + Xml.getString(report));
               } else {
                  Log.debug(Geonet.METADATA_ALIGNER, "Schematron validation report: \n" + report);
               }
            } else {
               metadataValidatorResult.setValidate(true);
            }
            return metadataValidatorResult;
         } catch (Exception e) {
            metadataValidatorResult.setValidate(false);
            return metadataValidatorResult;
         }
      }
      else
      {
         return metadataValidatorResult;
      }
      
   }

}
