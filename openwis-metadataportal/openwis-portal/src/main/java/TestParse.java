import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;


public class TestParse {

   public static void main(String[] args) {
      // TODO Auto-generated method stub
      String retourOpenam = "";
      try {
         BufferedReader in= new BufferedReader(new FileReader("retourOpenam.txt"));
         String line = in.readLine();
         while (line != null){
         retourOpenam +=line+"\n";
         line = in.readLine();
         
         }
         System.out.println(retourOpenam);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      String isMemberOf = "ismemberof";
      int indexIsmemberof = retourOpenam.indexOf(isMemberOf);
      int indexNextAttribute = retourOpenam.indexOf("userdetails.attribute.name",indexIsmemberof);
      String isMemeberofValues = retourOpenam.substring(indexIsmemberof+isMemberOf.length(), indexNextAttribute);
      System.out.println("\n\nvaleur= "+isMemeberofValues);
      String [] values = isMemeberofValues.replace("\n","").split("userdetails.attribute.value=");
      System.out.println(Arrays.asList(values));
      
   }
 
}
