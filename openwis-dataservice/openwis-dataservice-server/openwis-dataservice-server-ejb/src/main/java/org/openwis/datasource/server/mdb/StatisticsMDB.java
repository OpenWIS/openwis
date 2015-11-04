package org.openwis.datasource.server.mdb;

import java.io.StringReader;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.StatisticsMessage;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.DisseminatedDataStatistics;
import org.openwis.management.service.IngestedDataStatistics;
import org.openwis.management.service.ReplicatedDataStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AKKA Technologies / racaru
 */
@MessageDriven(activationConfig = {
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/StatisticsQueue"),
      @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")})
public class StatisticsMDB implements MessageListener {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(StatisticsMDB.class);

   /** The data statistics. */
//   private DisseminatedDataStatistics dataStatistics;

   /** The ingested statistics. */
//   private IngestedDataStatistics ingestedStatistics;

   /** The replicated statistics. */
   private ReplicatedDataStatistics replicatedStatistics;

   /**
   * injection Message Driven Context
   */
   @Resource
   private MessageDrivenContext mdc;

   /**
    * {@inheritDoc}
    * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
    */
   @Override
   public void onMessage(Message message) {
      if (message instanceof TextMessage) {
         TextMessage messageReceived = (TextMessage) message;
         try {
            logger.info("Received message:  {}", messageReceived.getText());

            // parse received message
            StringReader sr = new StringReader(messageReceived.getText());
            StatisticsMessage statisticsMessage = Serializer.deserialize(StatisticsMessage.class,
                  sr);

            if (StatisticsMessage.CMD_UPDATE_USER_EXTRACTED_DATA.equals(statisticsMessage
                  .getCommand())) {
               getDataStatistics().updateUserExtractedData(statisticsMessage.getUserId(),
                     statisticsMessage.getDate(), statisticsMessage.getNbFiles(),
                     statisticsMessage.getTotalSize());
            } else if (StatisticsMessage.CMD_UPDATE_USER_DISSEMINATED_BY_TOOL_DATA
                  .equals(statisticsMessage.getCommand())) {
               getDataStatistics().updateUserDisseminatedByToolData(statisticsMessage.getUserId(),
                     statisticsMessage.getDate(), statisticsMessage.getNbFiles(),
                     statisticsMessage.getTotalSize());
            } else if (StatisticsMessage.CMD_UPDATE_INGESTED_DATA.equals(statisticsMessage
                  .getCommand())) {
               getIngestedStatistics().updateIngestedData(statisticsMessage.getDate(),
                     statisticsMessage.getTotalSize());
            } else if (StatisticsMessage.CMD_UPDATE_REPLICATED_DATA.equals(statisticsMessage
                  .getCommand())) {
               getReplicatedStatistics().updateReplicatedData(statisticsMessage.getSource(),
                     statisticsMessage.getDate(), statisticsMessage.getTotalSize());
            } else {
               logger.warn("Statistics Message with command " + statisticsMessage.getCommand()
                     + " is ignored !!");
            }

         } catch (JMSException e) {
            logger.error("Unable to read the message from the queue", e);
            mdc.setRollbackOnly();
         } catch (JAXBException e) {
            logger.error("Unable to deseialize the message from the queue", e);
            mdc.setRollbackOnly();
         } catch (Throwable t) {
            logger.error("Unexpected error !!", t);
            mdc.setRollbackOnly();
         }
      }
   }

   /**
    * Gets the data statistics.
    *
    * @return the data statistics
    */
   private DisseminatedDataStatistics getDataStatistics() {
      try {
         return ManagementServiceBeans.getInstance().getDisseminatedDataStatistics();
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get DisseminatedDataStatistics bean", e);
      }
   }
   
   /**
    * Gets the data statistics.
    *
    * @return the data statistics
    */
   private IngestedDataStatistics getIngestedStatistics() {
//      if (ingestedStatistics == null) {
         try {
        	 
        	 return ManagementServiceBeans.getInstance().getIngestedDataStatistics();
         } catch (NamingException e) {
//            ingestedStatistics = null;
            throw new RuntimeException("Cannot get IngestedDataStatistics bean", e);
         }
//      }
      
//      return ingestedStatistics;
   }
   
   /**
    * Gets the data statistics.
    *
    * @return the data statistics
    */
   private ReplicatedDataStatistics getReplicatedStatistics() {
      if (replicatedStatistics == null) {
         try {
            replicatedStatistics = ManagementServiceBeans.getInstance().getReplicatedDataStatistics();
         } catch (NamingException e) {
            replicatedStatistics = null;
         }
      }
      
      return replicatedStatistics;
   }
}
