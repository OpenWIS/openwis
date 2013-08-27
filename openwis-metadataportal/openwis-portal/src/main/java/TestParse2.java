import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestParse2 {

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
         //System.out.println(retourOpenam);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      Map<String, Object> attributeMap = new HashMap<String, Object>();
      String [] attributes = retourOpenam.replace("\n", "").split("userdetails.attribute.name=");
      // on ignore le 1er (role et token)
      for (int i= 1; i<attributes.length;i++){
         String [] details = attributes[i].split("userdetails.attribute.value=");
         String attributeName = details[0];
         // on teste si l'attribut contient une valeur ou plusieurs 
         // dans le cas où c'est une, le hashmap contient une string
         // si plusieurs, ce sera une liste de strings
         if (details.length==2){
            attributeMap.put(attributeName, details[1]);
         }else if (details.length>2){
            // création d'une liste modifiable qu'on initialise 
            //à partir de la liste non modifiable(nbre d'elements) Arrays.asList(details)
            List<String>listeValue = new ArrayList<String>(Arrays.asList(details)); 
            // on enlève la clé : attributeName = details[0]
            listeValue.remove(0);
            attributeMap.put(attributeName, listeValue);
            
         }
         
      }
      System.out.println(attributeMap);

   }

}
