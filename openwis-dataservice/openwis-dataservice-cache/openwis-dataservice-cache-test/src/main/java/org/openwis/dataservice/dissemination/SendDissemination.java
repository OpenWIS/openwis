/**
 *
 */
package org.openwis.dataservice.dissemination;

import javax.ejb.Remote;

/**
 * Test ejb to insert messages in the dissemination queue.
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */
@Remote
public interface SendDissemination {

   /**
    * Description goes here.
    *
    * @param text
    */
	public boolean add(final Long id);

	public boolean testDisseminate(String ipAddress);
}
