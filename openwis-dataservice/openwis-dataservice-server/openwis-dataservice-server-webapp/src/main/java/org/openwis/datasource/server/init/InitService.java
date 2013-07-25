package org.openwis.datasource.server.init;

/**
 * 
 * Init Services. <P>
 * Explanation goes here. <P>
 *
 */
public interface InitService {

   /**
    * Init service.
    *
    * @throws Exception the exception
    */
   public void init() throws Exception;

   /**
    * Destroy service.
    *
    * @throws Exception the exception
    */
   public void destroy() throws Exception;
}
