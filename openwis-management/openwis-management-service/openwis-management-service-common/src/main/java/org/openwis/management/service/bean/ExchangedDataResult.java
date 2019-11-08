package org.openwis.management.service.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.management.entity.ExchangedData;

/**
 * The Class ExchangedDataResult.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exchangedDataResult")
public class ExchangedDataResult implements Serializable {

   /** The list. */
   private List<ExchangedData> list;

   /** The count. */
   private int count;

   /**
    * Instantiates a new exchanged data result.
    */
   public ExchangedDataResult() {
      super();
   }

   /**
    * Gets the list.
    *
    * @return the list
    */
   public List<ExchangedData> getList() {
      return list;
   }

   /**
    * Sets the list.
    *
    * @param list the new list
    */
   public void setList(List<ExchangedData> list) {
      this.list = list;
   }

   /**
    * Gets the count.
    *
    * @return the count
    */
   public int getCount() {
      return count;
   }

   /**
    * Sets the count.
    *
    * @param count the new count
    */
   public void setCount(int count) {
      this.count = count;
   }

}
