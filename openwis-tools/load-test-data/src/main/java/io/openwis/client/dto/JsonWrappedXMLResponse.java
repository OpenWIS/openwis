package io.openwis.client.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A JSON wrapped XML response.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="jsonData")
public class JsonWrappedXMLResponse {
   
   private static final Gson GSON = new Gson();

   @XmlValue
   private String data;

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }
   
   public <T> T parseJson(Class<T> clazz) {
      return  (T) GSON.fromJson(data, clazz);
   }
   
   public <T> T parseJson(TypeToken<T> type) {
      return  (T) GSON.fromJson(data, type.getType());
   }
}
