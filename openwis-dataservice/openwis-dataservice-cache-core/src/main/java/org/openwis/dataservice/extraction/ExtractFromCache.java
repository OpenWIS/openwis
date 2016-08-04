/**
 *
 */
package org.openwis.dataservice.extraction;

import javax.ejb.Local;

import org.openwis.dataservice.common.service.CacheExtraService;

/**
 * Encapsulate the request for an instance of a Global product in the Cache.
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
@Local
public interface ExtractFromCache extends CacheExtraService {

}
