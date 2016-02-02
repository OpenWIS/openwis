package org.openwis.metadataportal.services.user;

import jeeves.server.context.ServiceContext;

import org.openwis.metadataportal.services.user.dto.UserDTO;


/**
 * Retrieve information about the currently logged in user.  This is a specialization
 * of .user.Get which will only return the currently logged in user.
 * 
 * @author lmika
 *
 */
public class GetSelf extends Get {

   /**
    * {@inheritDoc}
    * 
    * This implementation only returns the currently logged in user.
    */
   @Override
   protected String getUsernameFromRequest(ServiceContext context,
         UserDTO userDTO) {
      return context.getUserSession().getUsername();
   }
}
