/**
 *
 */
package org.openwis.datasource.server.init;

import java.util.ArrayList;
import java.util.List;

import org.openwis.datasource.server.init.agent.ExtractionTimerAgent;
import org.openwis.datasource.server.init.agent.LocalDataSourcePollingTimerAgent;
import org.openwis.datasource.server.init.agent.SubscriptionTimerAgent;

/**
 * Factory of server agents.
 */
public final class ServerAgentFactory {

   /**
    * Default constructor.
    * Builds a ServerAgentFactory.
    */
   private ServerAgentFactory() {
      super();
   }

   /**
    * Creates a new ServerAgent object.
    *
    * @return the inits the service
    */
   public static final InitService createInitService() {
      InitServiceImpl initService = new InitServiceImpl();
      List<ServerAgent> agents = new ArrayList<ServerAgent>();
      agents.add(new ExtractionTimerAgent());
      agents.add(new SubscriptionTimerAgent());
      agents.add(new LocalDataSourcePollingTimerAgent());
      initService.setAgents(agents);
      return initService;
   }

   /**
    * The Class InitServiceImpl. <P>
    * Explanation goes here. <P>
    */
   private static class InitServiceImpl implements InitService {

      /**
       * Default constructor.
       * Builds a ServerAgentFactory.InitServiceImpl.
       */
      public InitServiceImpl() {
         //
      }

      /**
       * The list of agents to be started or stopped. Note that the with the list
       * we have control over the ordering of the start and stop procedure.
       * <code>_agents</code>
       */
      private List<ServerAgent> agents;

      /**
       * Called when the initServlet is deployed and started.
       *
       * @throws Exception the exception
       */
      @Override
      public void init() throws Exception {
         try {
            for (ServerAgent agent : agents) {
               agent.startup();
            }
         } catch (Exception e) {
            throw new Exception(e);
         }
      }

      /**
       * Is called to correctly shut down server processes.
       *
       * @throws Exception the exception
       */
      @Override
      public void destroy() throws Exception {
         for (ServerAgent agent : agents) {
            agent.shutdown();
         }
      }

      /**
       * Sets the agents.
       *
       * @param agents the new agents
       */
      public void setAgents(List<ServerAgent> agents) {
         this.agents = agents;
      }

   }

}
