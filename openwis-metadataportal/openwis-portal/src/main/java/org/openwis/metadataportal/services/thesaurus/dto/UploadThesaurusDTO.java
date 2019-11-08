/**
 * 
 */
package org.openwis.metadataportal.services.thesaurus.dto;

import java.io.File;
import java.util.StringTokenizer;

import jeeves.utils.Util;

import org.jdom.Element;

/**
 * The Upload Thesaurus DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class UploadThesaurusDTO {

   /**
    * The file.
    */
   private File file;

   /**
    * The fname.
    */
   private String fname;

   /**
    * The dname.
    */
   private String dname;

   /**
    * The type.
    */
   private String type;

   public UploadThesaurusDTO(Element params) throws Exception {
      String paramFiles       = Util.getParam(params, "openwisFiles");
      StringTokenizer tokenizer = new StringTokenizer(paramFiles, ",");
      while ( tokenizer.hasMoreTokens() ) {
         String file = tokenizer.nextToken();
         File f = new File(file);
         setFile(f);
      }
      setDname(Util.getParam(params, "dname"));
      setType(Util.getParam(params, "type"));
   }

   /**
    * Gets the file.
    * @return the file.
    */
   public File getFile() {
      return file;
   }

   /**
    * Sets the file.
    * @param file the file to set.
    */
   public void setFile(File file) {
      setFname(file.getName());
      this.file = file;
   }

   /**
    * Gets the fname.
    * @return the fname.
    */
   public String getFname() {
      return fname;
   }

   /**
    * Sets the fname.
    * @param fname the fname to set.
    */
   public void setFname(String fname) {
      this.fname = fname;
   }

   /**
    * Gets the dname.
    * @return the dname.
    */
   public String getDname() {
      return dname;
   }

   /**
    * Sets the dname.
    * @param dname the dname to set.
    */
   public void setDname(String dname) {
      this.dname = dname;
   }

   /**
    * Gets the type.
    * @return the type.
    */
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * @param type the type to set.
    */
   public void setType(String type) {
      this.type = type;
   }
}
