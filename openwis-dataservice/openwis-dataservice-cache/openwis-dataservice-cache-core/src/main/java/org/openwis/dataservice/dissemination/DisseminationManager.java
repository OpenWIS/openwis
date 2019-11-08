package org.openwis.dataservice.dissemination;

/**
 * Defines the DisseminationManager that listens on the DisseminationQueue for dissemination requests.
 * It distributes the incoming requests to 4 additional JMS queues according to the request priority.
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */

public interface DisseminationManager {

}
