package org.openwis;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CopyWebContentToFile {

   private URL url;

   private File file;

   /**
    * Default constructor.
    * Builds a CopyWebContentToFile.
    * @param url
    * @param file
    */
   public CopyWebContentToFile(URL url, File file) {
      super();
      this.url = url;
      this.file = file;
   }

   public void copyWebContentToFile() {
      try {
         System.out.println("Opening connection to " + url.toString() + "...");

         url.openConnection();

         // Copy resource to local file, use remote file
         InputStream is = url.openStream();

         FileOutputStream fos = null;
         fos = new FileOutputStream(file);

         int oneChar, count = 0;
         while ((oneChar = is.read()) != -1) {
            fos.write(oneChar);
            count++;
         }
         is.close();
         fos.close();
         System.out.println(count + " byte(s) copied");
      } catch (MalformedURLException e) {
         System.err.println(e.toString());
      } catch (IOException e) {
         System.err.println(e.toString());
      }

   }

   public static void main(String[] args) {
      URL url;
      try {
         url = new URL(args[0]);
         File file = new File(args[1]);
         CopyWebContentToFile copyWebContentToFile = new CopyWebContentToFile(url, file);
         copyWebContentToFile.copyWebContentToFile();
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

   }
}
