package org.openwis.dataservice.replication.ftp.pool;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.openwis.dataservice.replication.ftp.FTPConnectionHandler;
import org.openwis.dataservice.replication.ftp.config.Destination;

/**
 * Pool of FTPConnectionHandler
 */
public class FTPConnectionPool extends GenericObjectPool<FTPConnectionHandler> implements FTPConnectionPoolMBean {

   public static final int MAX_WAIT = 15000;

   public FTPConnectionPool(Destination destination) {
      super(new FTPConnectionFactory(destination));

      // if no connection available -> throw NoSuchElementException
      //setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);

      // wait for connection
      setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
      // timeout to wait for connection (before throwing a NoSuchElementException)
      setMaxWait(MAX_WAIT);

      setMaxActive(destination.getMaxConnections());
   }

}