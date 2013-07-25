/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.validator;

import jeeves.server.context.ServiceContext;

import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class NoneMetadataValidator implements IMetadataValidator {

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.validator.IMetadataValidator#validate(org.fao.geonet.kernel.DataManager, org.jdom.Element, java.lang.String)
    */
   @Override
   public MetadataValidatorResult validate(DataManager dm, Element md, String schema, ServiceContext context) {
      MetadataValidatorResult metadataValidatorResult = new MetadataValidatorResult();
      metadataValidatorResult.setValidate(true);
      return metadataValidatorResult;
   }

}
