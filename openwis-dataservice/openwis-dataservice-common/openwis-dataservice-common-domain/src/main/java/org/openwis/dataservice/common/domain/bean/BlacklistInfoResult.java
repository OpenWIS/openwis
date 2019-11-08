package org.openwis.dataservice.common.domain.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.openwis.dataservice.common.domain.entity.blacklist.BlacklistInfo;

/**
 * The Class BlacklistInfoResult.
 */
@XmlRootElement(name = "blacklistInfoResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class BlacklistInfoResult implements Serializable {

   /** The count. */
   private int count;

   /** The list. */
   private List<BlacklistInfo> list;

   /**
    * Instantiates a new blacklist info result.
    */
   public BlacklistInfoResult() {
      super();
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

   /**
    * Gets the list.
    *
    * @return the list
    */
   public List<BlacklistInfo> getList() {
      return list;
   }

   /**
    * Sets the list.
    *
    * @param list the new list
    */
   public void setList(List<BlacklistInfo> list) {
      this.list = list;
   }

}
