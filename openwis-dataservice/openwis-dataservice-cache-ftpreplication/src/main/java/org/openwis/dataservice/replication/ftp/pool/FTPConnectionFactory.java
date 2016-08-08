package org.openwis.dataservice.replication.ftp.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.openwis.dataservice.replication.ftp.FTPConnectionHandler;
import org.openwis.dataservice.replication.ftp.config.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory of {@link FTPConnectionHandler}.
 */
public class FTPConnectionFactory implements PoolableObjectFactory<FTPConnectionHandler> {
   private static final Logger LOG = LoggerFactory.getLogger(FTPConnectionFactory.class);

   private Destination destination;

   public FTPConnectionFactory(Destination destination) {
      this.destination = destination;
   }

   @Override
   public void activateObject(FTPConnectionHandler connection) throws Exception {
      LOG.debug("Activating connection " + connection.getDestination().getLocalPath());
   }

   @Override
   public void destroyObject(FTPConnectionHandler connection) throws Exception {
      LOG.debug("Destroying connection " + connection.getDestination().getLocalPath());
      connection.closeConnection();
   }

   @Override
   public FTPConnectionHandler makeObject() throws Exception {
      FTPConnectionHandler connection = new FTPConnectionHandler(destination);
      LOG.debug("Creating connection " + connection.getDestination().getLocalPath());
      return connection;
   }

   @Override
   public void passivateObject(FTPConnectionHandler connection) throws Exception {
      LOG.debug("Passivating connection " + connection.getDestination().getLocalPath());
   }

   @Override
   public boolean validateObject(FTPConnectionHandler arg0) {
      return false;
   }

}