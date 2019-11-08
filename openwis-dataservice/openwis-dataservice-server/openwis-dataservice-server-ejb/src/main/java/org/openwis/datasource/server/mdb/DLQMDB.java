package org.openwis.datasource.server.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.util.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DLQ MDB: 
 * - consume DLQ messages
 * - log message as error
 */
@MessageDriven(messageListenerInterface = MessageListener.class, activationConfig = {
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/DLQ"),
      @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "3")})
public class DLQMDB implements MessageListener, ConfigurationInfo {

   private final static Logger LOG = LoggerFactory.getLogger(DLQMDB.class);

   // -------------------------------------------------------------------------
   // Message listener implementation
   // -------------------------------------------------------------------------
   @Override
   public void onMessage(Message message) {
      try {
         StringBuilder builder = new StringBuilder(512);
         builder.append("The following message was sent to the DLQ: ");
         if (message instanceof TextMessage) {
            TextMessage messageReceived = (TextMessage) message;
            builder.append(messageReceived.getText());
         } else if (message instanceof ObjectMessage
               && ((ObjectMessage) message).getObject() instanceof FileInfo) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            FileInfo fileInfo = (FileInfo) objectMessage.getObject();
            builder.append("Replication message for file: ").append(fileInfo.getProductFilename());
            builder.append(", checksum: ").append(fileInfo.getChecksum());
         }

         LOG.error(builder.toString());
      } catch (Throwable t) {
         LOG.error("Unexpected error while processing DLQ message", t);
      }
   }
}
