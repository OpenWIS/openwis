package org.openwis.dataservice.replication.ftp.config;

/**
 * FTP Destination with which we replicate.
 */
public class Destination {

   private String server;

   private String port;

   private String username;

   private String password;

   private String localPath;

   private String remotePath;

   private boolean secured;
   
   private int maxConnections;
   
   private int connectionRetryDelay;

   public int getConnectionRetryDelay() {
      return connectionRetryDelay;
   }

   public void setConnectionRetryDelay(int connectionRetryDelay) {
      this.connectionRetryDelay = connectionRetryDelay;
   }

   public int getMaxConnections() {
      return maxConnections;
   }

   public void setMaxConnections(int maxConnections) {
      this.maxConnections = maxConnections;
   }

   public String getServer() {
      return server;
   }

   public void setServer(String server) {
      this.server = server;
   }

   public String getPort() {
      return port;
   }

   public void setPort(String port) {
      this.port = port;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getLocalPath() {
      return localPath;
   }

   public void setLocalPath(String localPath) {
      this.localPath = localPath;
   }

   public String getRemotePath() {
      return remotePath;
   }

   public void setRemotePath(String remotePath) {
      this.remotePath = remotePath;
   }

   public boolean isSecured() {
      return secured;
   }

   public void setSecured(boolean secured) {
      this.secured = secured;
   }
}
