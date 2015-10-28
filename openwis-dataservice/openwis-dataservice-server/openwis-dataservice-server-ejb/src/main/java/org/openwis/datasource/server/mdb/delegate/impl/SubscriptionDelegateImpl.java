/**
 *
 */
package org.openwis.datasource.server.mdb.delegate.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;
import org.openwis.datasource.server.mdb.delegate.SubscriptionDelegate;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegate for the subscription data manager mdb. <P>
 * Explanation goes here. <P>
 *
 */
@Stateless(name = "SubscriptionDelegate")
@Local(SubscriptionDelegate.class)
public class SubscriptionDelegateImpl implements SubscriptionDelegate {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionDelegateImpl.class);

   /** The subscription service. */
   @EJB(name = "SubscriptionService")
   private SubscriptionService subscriptionService;

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The ctrl service. */
   private ControlService ctrlService;

   /**
    * {@inheritDoc}
    * @return
    * @see org.openwis.datasource.server.mdb.delegate.SubscriptionDelegate#
    * processMessage(org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage)
    */
   @Override
   public Collection<ProcessedRequest> processMessage(IncomingDSMessage message) {
      Collection<ProcessedRequest> result = null;
      logger.info("Processing message: {}", message.toString());

      ControlService controlService = getControlService();
      if (controlService != null
            && controlService.isServiceEnabled(ManagedServiceIdentifier.SUBSCRIPTION_SERVICE)) {
         result = processSubscriptions(message);
      } else {
         logger.info("Subscription service disable => does not process onProductArrival subscription");
      }

      return result;
   }

   /**
    * Process subscriptions.
    *
    * @param message the message
    * @param result the result
    * @return the collection
    */
   @SuppressWarnings("unchecked")
   public Collection<ProcessedRequest> processSubscriptions(IncomingDSMessage message) {
      Collection<ProcessedRequest> result = new ArrayList<ProcessedRequest>();
      try {
         // retrieve subscription
         Query query;
         for (String urn : message.getMetadataURNs()) {
            query = entityManager
                  .createNamedQuery("Subscription.FindByMetadataURN")
                  .setParameter("metadataurn", urn)
                  .setParameter("state", SubscriptionState.ACTIVE)
                  .setParameter("productDate",
                        DateTimeUtils.parseDateTime(message.getProductDate()));

            List<Subscription> subscriptions = query.getResultList();
            if (subscriptions != null && subscriptions.size() > 0) {
               logger.info("Found {} subscriptions for metadata {}", subscriptions.size(), urn);
               result.addAll(subscriptionService.processEventSubscriptions(subscriptions, message.getProductDate()));
            } else {
               logger.info("No subscription found for metadata {}", urn);
            }
         }
      } catch (ParseException pe) {
         result = Collections.emptyList();
         logger.error("Error parsing received date in the message " + message.getProductDate(), pe);
      } catch (Throwable t) {
         logger.error("Cannot process event subscription", t);
         result = Collections.emptyList();
      }
      return result;
   }

   private ControlService getControlService() {
      if (ctrlService == null) {
         try {
            ctrlService = ManagementServiceBeans.getInstance().getControlService();
         } catch (NamingException e) {
            ctrlService = null;
         }
      }
      return ctrlService;
   }

}
