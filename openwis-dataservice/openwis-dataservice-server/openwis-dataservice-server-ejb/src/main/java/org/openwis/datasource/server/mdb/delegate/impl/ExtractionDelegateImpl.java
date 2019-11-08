/**
 *
 */
package org.openwis.datasource.server.mdb.delegate.impl;

import java.io.File;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.exception.OpenWisException;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.mdb.delegate.ExtractionDelegate;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegate for the extraction manager mdb. <P>
 * Explanation goes here. <P>
 *
 */
@Stateless(name = "ExtractionDelegate")
@Local(ExtractionDelegate.class)
public class ExtractionDelegateImpl implements ExtractionDelegate {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ExtractionDelegateImpl.class);

   /** The Constant STAGING_POST_URI. */
//   private static final String STAGING_POST_URI = JndiUtils
//         .getString(DataServiceConfiguration.STAGING_POST_URI_KEY);
   private String stagingPostUri;

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The processed request service. */
   @EJB
   private ProcessedRequestService processedRequestService;
   
   @PostConstruct
   public void initialize() {
      stagingPostUri = ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.STAGING_POST_URI_KEY);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.datasource.server.mdb.delegate.ExtractionDelegate#
    * processMessage(
    *    org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage)
    */
   @Override
   public Status processMessage(ProcessedRequestMessage requestMessage) {
	   
	   Status result;

      Long id = requestMessage.getId();

      logger.debug("Processing request {}", id);
      // retrieve the processedRequest
      ProcessedRequest processedRequest = entityManager.getReference(ProcessedRequest.class, id);

      if (processedRequest == null) {
         logger.error("No process request found for processed request {}", id);
         result = Status.ERROR;
      } else {
         //creating staging post directory
         try {
            createStagingPostDirectory(processedRequest.getUri());
            result = processedRequestService
                  .extract(processedRequest, requestMessage.getProductDate(), requestMessage.getProductId());
         } catch (OpenWisException e) {
            logger.error("Error while processing request " + id, e);
            setFailProcessedRequest(id);
            result = Status.ERROR;
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.datasource.server.mdb.delegate.ExtractionDelegate#setFailProcessedRequest(java.lang.Long)
    */
   @Override
   public void setFailProcessedRequest(Long prId) {
      ProcessedRequest processedRequest = entityManager.getReference(ProcessedRequest.class, prId);
      processedRequest.setRequestResultStatus(RequestResultStatus.FAILED);
      processedRequest.setUri(null);
      entityManager.merge(processedRequest);
   }

   /**
    * Creates the staging post directory.
    *
    * @param uri the URI
    */
   private void createStagingPostDirectory(String uri) {
      File stagingPost = new File(stagingPostUri, uri);
      if (!stagingPost.exists() && !stagingPost.mkdirs()) {
         throw new OpenWisException(MessageFormat.format(
               "Could not create staging post directory : <{0}>", stagingPost.getAbsolutePath()));
      }
   }

}
