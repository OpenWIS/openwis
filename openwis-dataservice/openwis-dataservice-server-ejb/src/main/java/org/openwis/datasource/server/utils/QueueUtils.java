/**
 *
 */
package org.openwis.datasource.server.utils;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.openwis.dataservice.common.domain.entity.enumeration.ClassOfService;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Request;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.DisseminationMessage;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public final class QueueUtils {

   /**
    * Default constructor.
    * Builds a QueueUtils.
    */
   private QueueUtils() {
      super();
   }

   /**
    *
    * Create a ProcessedRequestMessage JMS message to be sent
    * @param processedRequest processed request
    * @return ProcessedRequestMessage
    */
   public static ProcessedRequestMessage createRequestMessage(ProcessedRequest processedRequest) {
      ProcessedRequestMessage requestMessage = new ProcessedRequestMessage();
      requestMessage.setId(processedRequest.getId());
      return requestMessage;
   }

   /**
    *
    * Create a DisseminationMessage JMS message to be sent
    * @param prId processedRequest ID
    * @return DisseminationMessage
    */
   public static DisseminationMessage createDisseminationMessage(Long prId) {
      DisseminationMessage disseminationMessage = new DisseminationMessage();
      disseminationMessage.setId(prId);
      return disseminationMessage;
   }

   /**
    * Create the request message with the request id and serialize this object to xml.
    * @param requestMessage the message.
    * @return the xml string.
    * @throws JAXBException exception if an error occurs.
    */
   public static String createXMLRequestMessage(ProcessedRequestMessage requestMessage)
         throws JAXBException {
      StringWriter sw;
      sw = new StringWriter();
      Serializer.serialize(requestMessage, sw);
      return sw.toString();
   }

   /**
    * Create the request message with the request id and serialize this object to xml.
    * @param disseminationMessage the message.
    * @return the xml string.
    * @throws JAXBException exception if an error occurs.
    */
   public static String createXMLDisseminationMessage(DisseminationMessage disseminationMessage)
         throws JAXBException {
      StringWriter sw;
      sw = new StringWriter();
      Serializer.serialize(disseminationMessage, sw);
      return sw.toString();
   }
   
   /**
    * Compute JMS priority for dissemination event.
    */
   public static int getJMSPriorityForProcessedRequest(ProcessedRequest processedRequest) {
      Request request = processedRequest.getRequest();
      ProductMetadata pm = request.getProductMetadata();

      int jmsPriority = 0;
      Integer priority = pm.getPriority();
      if (pm.getOverridenPriority() != null) {
         priority = pm.getOverridenPriority();
      }
      if (priority == 1) {
         jmsPriority = 9;
      } else if (priority == 2) {
         jmsPriority = 8;
      } else if (request.getClassOfService() == ClassOfService.GOLD) {
         jmsPriority = 5;
      } else if (request.getClassOfService() == ClassOfService.SILVER) {
         jmsPriority = 3;
      } else if (request.getClassOfService() == ClassOfService.BRONZE) {
         jmsPriority = 1;
      }
      
      return jmsPriority;
   }

}
