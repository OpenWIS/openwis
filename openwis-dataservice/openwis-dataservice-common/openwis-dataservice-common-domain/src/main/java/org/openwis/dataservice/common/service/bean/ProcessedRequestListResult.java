package org.openwis.dataservice.common.service.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;

/**
 * The Class ProcessedRequestListResult.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processedRequestListResult")
public class ProcessedRequestListResult {

   /** The list. */
   private List<ProcessedRequest> list;

   /** The count. */
   private int count;

   /**
    * Instantiates a new data result.
    */
   public ProcessedRequestListResult() {
      super();
   }

   /**
    * Gets the list.
    *
    * @return the list
    */
   public List<ProcessedRequest> getList() {
      return list;
   }

   /**
    * Sets the list.
    *
    * @param list the new list
    */
   public void setList(List<ProcessedRequest> list) {
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
