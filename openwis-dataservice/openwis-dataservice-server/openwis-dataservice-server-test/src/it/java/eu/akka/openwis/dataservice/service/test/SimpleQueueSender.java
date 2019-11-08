package eu.akka.openwis.dataservice.service.test;

/**
 * The SimpleQueueSender class consists only of a main method, 
 * which sends several messages to a queue.
 * 
 * Run this program in conjunction with SimpleQueueReceiver.
 * Specify a queue name on the command line when you run the
 * program.  By default, the program sends one message.  Specify
 * a number after the queue name to send that number of messages.
 */

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleQueueSender {

   /**
    * 
    * Description goes here.
    * @param queueName
    * @param message
    */
   public static void sendMessage(String queueName, String message) {
      Context jndiContext = null;
      QueueConnectionFactory queueConnectionFactory = null;
      QueueConnection queueConnection = null;
      QueueSession queueSession = null;
      Queue queue = null;
      QueueSender queueSender = null;

      /* 
       * Create a JNDI API InitialContext object if none exists
       * yet.
       */
      try {
         jndiContext = new InitialContext();
         queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("ConnectionFactory");
         queue = (Queue) jndiContext.lookup(queueName);

         /*
          * Create connection.
          * Create session from connection; false means session is not transacted.
          * Create sender and text message.
          * Send messages, varying text slightly.
          * Send end-of-messages message.
          * Finally, close connection.
          */

         queueConnection = queueConnectionFactory.createQueueConnection();
         queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
         queueSender = queueSession.createSender(queue);
         TextMessage textMessage = queueSession.createTextMessage(message);
         System.out.println("Sending message: " + textMessage.getText());
         queueSender.send(textMessage);
         /* 
          * Send a non-text control message indicating end of
          * messages.
          */
         queueSender.send(queueSession.createMessage());
      } catch (NamingException e) {
         System.out.println("Could not create JNDI API " + "context: " + e.toString());
         System.exit(1);
      } catch (JMSException e) {
         System.out.println("Exception occurred: " + e.toString());
      } finally {
         if (queueConnection != null) {
            try {
               queueConnection.close();
            } catch (JMSException e) {
               //TODO
            }
         }
      }
   }

}