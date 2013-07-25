/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.webdav;

import jeeves.utils.Xml;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.WebdavResource;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class WebDavRemoteFile {
      

      //---------------------------------------------------------------------------
      //---
      //--- Variables
      //---
      //---------------------------------------------------------------------------

      private String path;
      private String changeDate;

      private WebdavResource wr;

      //---------------------------------------------------------------------------
      //---
      //--- Constructor
      //---
      //---------------------------------------------------------------------------

      public WebDavRemoteFile(WebdavResource wr) {
         this.wr = wr;
         path       = wr.getPath();
         changeDate = new ISODate(wr.getGetLastModified()).toString();
      }

      //---------------------------------------------------------------------------
      //---
      //--- RemoteFile interface
      //---
      //---------------------------------------------------------------------------

      public String getPath()       { return path;       }
      public String getChangeDate() { return changeDate; }

      //---------------------------------------------------------------------------

      public Element getMetadata() throws Exception {
         try {
            wr.setPath(path);
               return Xml.loadStream(wr.getMethodData());
         }
         catch (HttpException x) {
            throw new Exception("HTTPException : " + x.getMessage());
         }
      }

      //---------------------------------------------------------------------------

      public boolean isMoreRecentThan(String localChangeDate) {
         ISODate remoteDate = new ISODate(changeDate);
         ISODate localDate  = new ISODate(localChangeDate);
         //--- accept if remote date is greater than local date
         return (remoteDate.sub(localDate) > 0);
      }
}
