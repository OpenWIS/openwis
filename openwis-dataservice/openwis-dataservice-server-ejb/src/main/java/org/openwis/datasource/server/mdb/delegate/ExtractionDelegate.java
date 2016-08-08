/**
 *
 */
package org.openwis.datasource.server.mdb.delegate;

import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public interface ExtractionDelegate {

   /**
    * Process message.
    *
    * @param requestMessage the request message
    * @return the processed request
    */
   Status processMessage(ProcessedRequestMessage requestMessage);

   /**
    * Sets the fail processed request.
    *
    * @param prId the new fail processed request
    */
   void setFailProcessedRequest(Long prId);

}
