package org.openwis.datasource.server.init;

/**
 * The Interface ServerAgent. <P>
 * Explanation goes here. <P>
 */
public interface ServerAgent {
   /**
    * Initialize and start the agent
    * 
    * @throws Exception
    */
   public void startup() throws Exception;

   /**
    * Stop the agent and free resources
    * 
    * @throws Exception
    */
   public void shutdown() throws Exception;
}
