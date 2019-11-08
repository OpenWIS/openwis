/**
 *
 */
package org.openwis.metadataportal.services.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openwis.dataservice.cache.CachedFile;
import org.openwis.dataservice.cache.CachedFileInfo;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class MockCachedFiles {

   public static List<CachedFile> get() {
      List<CachedFile> files = new ArrayList<CachedFile>();
      for(int i = 0; i < 10; i++) {
         CachedFile cf = new CachedFile();
         cf.setChecksum("CheckSum_" + i);
         cf.setFilename("Toto" + i + ".txt");
         cf.setReceivedFromGTS(true);
         cf.setPriority(i);
         cf.setPath("Path/To/" + i);
         files.add(cf);
      }
      return files;
   }

   public static List<CachedFileInfo> browse() {
      List<CachedFileInfo> files = new ArrayList<CachedFileInfo>();

      // current time
      GregorianCalendar calendar = new GregorianCalendar();
      
      for(int i = 0; i < 10; i++) {
         CachedFileInfo cfi = new CachedFileInfo();
         cfi.setChecksum("CheckSum_" + i);
         cfi.setName("CachedFile_" + i + ".txt");
         cfi.setMetadataUrn("Metadata_" + i);
         cfi.setOrigin("Origin_" + i);
         cfi.setInsertionDate(getXmlCalendar(calendar));
         files.add(cfi);
      }
      return files;
   }

   private static XMLGregorianCalendar getXmlCalendar(final GregorianCalendar calendar) {
      XMLGregorianCalendar xmlcal = null;
      
      try {
         calendar.add(Calendar.DAY_OF_YEAR, 1);
         xmlcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
      }
      catch (DatatypeConfigurationException e) {
         
      }
      return xmlcal;
   }
   
}
