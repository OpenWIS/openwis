/**
 * 
 */
package org.openwis.metadataportal.kernel.stylesheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jeeves.resources.dbms.Dbms;

import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.model.styleSheet.Stylesheet;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class StyleSheetManager extends AbstractManager {

   //----------------------------------------------------------------------- Constructors.

   /**
    * Default constructor.
    * Builds a StyleSheetManager.
    * @param dbms Database connection
    */
   public StyleSheetManager(Dbms dbms) {
      super(dbms);
   }

   //----------------------------------------------------------------------- Public methods.

   /**
    * Get all categories.
    * @return all categories.
    * @throws Exception 
    */
   public List<Stylesheet> getAllStyleSheet(String appPath) throws Exception {
      List<Stylesheet> allStylesheet = new ArrayList<Stylesheet>();

      String dir = appPath + Geonet.Path.IMPORT_STYLESHEETS;
      String sheets[] = new File(dir).list();
      if (sheets == null)
         throw new Exception("Cannot scan directory : " + dir);

      for (String sheet : sheets) {
         if (sheet.endsWith(".xsl")) {
            int pos = sheet.lastIndexOf(".xsl");
            allStylesheet.add(new Stylesheet(new File(dir, sheet).toString(), sheet.substring(0, pos)));
         }
      }

      return allStylesheet;
   }

}
