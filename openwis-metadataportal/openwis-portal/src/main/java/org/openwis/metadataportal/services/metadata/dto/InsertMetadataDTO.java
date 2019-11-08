/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.styleSheet.Stylesheet;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class InsertMetadataDTO extends ImportMetadataDTO {

   private List<String> files;

   public InsertMetadataDTO() {
      super();
   }

   public InsertMetadataDTO(Element params) throws Exception {
      String paramFiles       = Util.getParam(params, "openwisFiles");
      List<String> files = new ArrayList<String>();
      StringTokenizer tokenizer = new StringTokenizer(paramFiles, ",");
      while ( tokenizer.hasMoreTokens() ) {
         String file = tokenizer.nextToken();
         File f = new File(file);
         files.add(f.getName());
      }
      setFiles(files);
      setFileType(Util.getParam(params, "fileType"));
      String styleSheet = Util.getParam(params, "stylesheet", null);
      if(styleSheet != null) {
         setStylesheet(new Stylesheet(null, styleSheet));
      }
      setValidationMode(MetadataValidation.valueOf(Util.getParam(params, "validationMode")));
      setCategory(new Category(Util.getParamAsInt(params, "categoryId"), Util.getParam(params, "categoryName")));
   }

   /**
    * Gets the files.
    * @return the files.
    */
   public List<String> getFiles() {
      return files;
   }

   /**
    * Sets the files.
    * @param file the files to set.
    */
   public void setFiles(List<String> files) {
      this.files = files;
   }
}
