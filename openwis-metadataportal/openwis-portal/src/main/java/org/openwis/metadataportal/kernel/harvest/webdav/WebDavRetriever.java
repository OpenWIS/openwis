/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.webdav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jeeves.utils.Log;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.lang.StringUtils;
import org.apache.webdav.lib.WebdavResource;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.lib.Lib;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class WebDavRetriever {
   
   private List<WebDavRemoteFile> files = new ArrayList<WebDavRemoteFile>();
   
   public List<WebDavRemoteFile> retrieve(String webDavName, String webDavURL, 
         String username, String pswd, boolean recurse, SettingManager sm) throws Exception {
      WebdavResource davRes = open(webDavName, webDavURL, username, pswd, sm);
      files.clear();
      retrieveFiles(davRes, recurse);
      return files;
   }

   private WebdavResource open(String webDavName, String webDavURL, String username, String pswd, SettingManager sm) throws Exception {
      Log.debug(Geonet.WEB_DAV, "opening webdav resource with URL: " + webDavURL);
      if (!webDavURL.endsWith("/")) {
         Log.debug(Geonet.WEB_DAV, "URL " + webDavURL + "does not end in slash -- will be appended");
         webDavURL += "/";
      }
      try {
         Log.debug(Geonet.WEB_DAV, "Connecting to webdav url for node : "+ webDavName + " URL: " + webDavURL);
         WebdavResource wr = createResource(webDavURL, username, pswd, sm);
         Log.debug(Geonet.WEB_DAV, "Connected to webdav resource at : "+ webDavURL);

         //--- we are interested only in folders
         // heikki: somehow this works fine here, but see retrieveFiles()
         if (!wr.isCollection()) {
            Log.error(Geonet.WEB_DAV, "Resource url is not a collection : "+ webDavURL);
            wr.close();
            throw new Exception("Resource url is not a collection : "+ webDavURL);
         }
         else {
            Log.info(Geonet.WEB_DAV, "Resource path is : "+ wr.getPath());
            return wr;
         }
      }
      catch(HttpException e) {
         throw new Exception("HTTPException: " + e.getMessage());
      }
   }

   //---------------------------------------------------------------------------

   private WebdavResource createResource(String webDavURL, String username, String pswd, SettingManager sm) throws Exception {
      Log.debug(Geonet.WEB_DAV, "Creating WebdavResource");

      HttpURL http = webDavURL.startsWith("https") ? new HttpsURL(webDavURL) : new HttpURL(webDavURL);

      if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(pswd)) {
         Log.debug(Geonet.WEB_DAV, "using account, username: " + username + " password: " + pswd);
         http.setUserinfo(username, pswd);
      }
      else {
         Log.debug(Geonet.WEB_DAV, "not using account");
      }

      //--- setup proxy, if the case

//      GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
//      SettingManager sm = gc.getSettingManager();

      boolean enabled = sm.getValueAsBool("system/proxy/use", false);
      String  host    = sm.getValue("system/proxy/host");
      String  port    = sm.getValue("system/proxy/port");
      
      if (!enabled) {
         Log.debug(Geonet.WEB_DAV, "local proxy not enabled");
         Log.debug(Geonet.WEB_DAV, "returning a new WebdavResource");
         Log.debug(Geonet.WEB_DAV, "using http port: " + http.getPort() + " http uri: " + http.getURI());
            return new WebdavResource(http, 1);
      }
      else {
         Log.debug(Geonet.WEB_DAV, "local proxy enabled");         
         if (!Lib.type.isInteger(port)) {
            throw new Exception("Proxy port is not an integer : "+ port);
         }
         Log.debug(Geonet.WEB_DAV, "returning a new WebdavResource");
         Log.debug(Geonet.WEB_DAV, "using http proxy port: " + port + " proxy host: " + host + " http uri: " + http.getURI());
         return new WebdavResource(http, host, Integer.parseInt(port));
      }
   }

   //---------------------------------------------------------------------------

   private void retrieveFiles(WebdavResource wr, boolean recurse) throws IOException {
      String path = wr.getPath();
      Log.debug(Geonet.WEB_DAV, "Scanning resource : "+ path);
      WebdavResource[] wa = wr.listWebdavResources();
      Log.debug(Geonet.WEB_DAV, "# " + wa.length + " webdav resources found in: " + wr.getPath());
      if(wa.length > 0) {
         int startSize = files.size();
         for(WebdavResource w : wa) {
            // heikki: even though response indicates that a sub directory is a collection, according to Slide
            //         that is never the case. To determine if a resource is a sub directory, use the following
            //         trick :            
            // if(w.getIsCollection()) {
            if(w.getPath().equals(wr.getPath()) && w.getDisplayName().length() > 0) {
               if(recurse) {
                  Log.debug(Geonet.WEB_DAV, w.getPath() + " is a collection, processed recursively");
                  String url = w.getHttpURL().getURI();
                  url = url + w.getDisplayName()+ "/";
                  HttpURL http = url.startsWith("https") ? new HttpsURL(url) : new HttpURL(url);
                  WebdavResource huh = new WebdavResource(http, 1);
                  retrieveFiles(huh, recurse);                 
               }
               else {
                  Log.debug(Geonet.WEB_DAV, w.getPath() + " is a collection. Ignoring because recursion is disabled.");
               }
            }
            else {
               Log.debug(Geonet.WEB_DAV, w.getName() + " is not a collection");
               if (w.getName().toLowerCase().endsWith(".xml")) {
                  Log.debug(Geonet.WEB_DAV, "found xml file ! " + w.getName().toLowerCase());
                  files.add(new WebDavRemoteFile(w));
               }
               else {
                  Log.debug(Geonet.WEB_DAV, w.getName().toLowerCase() + " is not an xml file");
               }              
            }
         }  
         int endSize = files.size();
         int added = endSize - startSize;
         if (added == 0) {
            Log.debug(Geonet.WEB_DAV, "No xml files found in path : "+ path);
         }
         else {
            Log.debug(Geonet.WEB_DAV, "Found "+ added +" xml file(s) in path : "+ path);
         }        
      }     
   }
}
