/**
 * 
 */
package org.openwis.datasource.server.mdb.delegate;

import java.util.Collection;

import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public interface SubscriptionDelegate {

   /**
    * Process message.
    *
    * @param message the message
    * @return all processed request
    */
   Collection<ProcessedRequest> processMessage(IncomingDSMessage message);
   
   /**
    * Process subscriptions (used by tests).
    *
    * @param message the message
    * @return all processed request
    */
   Collection<ProcessedRequest> processSubscriptions(IncomingDSMessage message);
   
}
