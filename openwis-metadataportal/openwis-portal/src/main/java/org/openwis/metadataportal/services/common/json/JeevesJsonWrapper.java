/**
 * 
 */
package org.openwis.metadataportal.services.common.json;

import jeeves.utils.Util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.type.TypeFactory;
import org.jdom.Element;

/**
 * A utility class to return JSON response. <P>
 * Explanation goes here. <P>
 * 
 */
public final class JeevesJsonWrapper {
   

   /**
    * The name of the parameter containing the JSON data.
    */
   public final static String JSON = "jsonData";

   /**
    * Default constructor.
    * Builds a JsonJeevesWrapper.
    */
   private JeevesJsonWrapper() {
      super();
   }

   /**
    * Reads JSON and cast it into object.
    * @param <T>
    * @param json
    * @param valueType
    * @return
    * @throws Exception
    */
   public static <T> T read(Element params, Class<T> valueType) throws Exception {
      String jsonData = Util.getParam(params, JSON);
      return read(jsonData, valueType);
   }
   
   /**
    * Reads JSON and cast it into object.
    * @param <T>
    * @param json
    * @param valueType
    * @return
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   public static <T> T read(String jsonData, Class<T> valueType) throws Exception {
      ObjectMapper mapper = new ObjectMapper();
      mapper.getSerializationConfig().enable(Feature.USE_ANNOTATIONS);
      return (T) mapper.readValue(jsonData, TypeFactory.type(valueType));
   }

   /**
    * Serializes a DTO in JSON and return it as a JDOM element compliant with Jeeves.
    * @param o the object to return.
    * @return the object serialized into JSON.
    * @throws Exception if an error occurs.
    */
   public static Element send(Object o) throws Exception {
      ObjectMapper mapper = new ObjectMapper();
      mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
      mapper.getSerializationConfig().enable(Feature.USE_ANNOTATIONS);
      Element jsonData = new Element(JSON);
      jsonData.setText(mapper.writeValueAsString(o));
      return jsonData;
   }

   /**
    * Serializes a DTO in JSON and return it as a JDOM element compliant with Jeeves.
    * @param o the object to return.
    * @return the object serialized into JSON.
    * @throws Exception if an error occurs.
    */
   public static Element sendBasicFormResult(Object o) throws Exception {
      ObjectMapper mapper = new ObjectMapper();
      mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
      mapper.getSerializationConfig().enable(Feature.USE_ANNOTATIONS);
      
      Element submitReply = new Element("message");
      submitReply.setAttribute("success", Boolean.TRUE.toString());
      submitReply.setText(mapper.writeValueAsString(o));
      return submitReply;
   }
}
