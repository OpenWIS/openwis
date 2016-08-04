/**
 *
 */
package org.openwis.dataservice.common.service;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Remote;

import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.entity.request.Parameter;

/**
 * Specifies the extraction service for products stored in the cache.
 */
@Remote
public interface CacheExtraService {

   /**
    * Extract a product from cache.
    *
    * @param userId user id
    * @param metadataURN metadata URN of the product
    * @param parameters sub selection parameter list
    * @param processedRequestId request technical identifier
    * @param stagingPostURI the suffix of the URI
    * @param lowerBoundInsertionDate lower bound for insertion date of the extracted file (may be null)
    * @return the message status
    */
   MessageStatus extract(String userId, String metadataURN, List<Parameter> parameters,
         Long processedRequestId, String stagingPostURI, Calendar lowerBoundInsertionDate);

}
