package org.openwis.dataservice.common.domain.bean;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class UserDataPolicyOperations.
 */
@XmlRootElement(name = "UserDataPolicyOperations")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDataPolicyOperations implements Serializable {

   /** The user. */
   @XmlElement
   private String user;

   /** The data policy operations. */
   @XmlElement
   private Set<DataPolicyOperations> dataPolicyOperations;

   /**
    * Instantiates a new user data policy operations.
    */
   public UserDataPolicyOperations() {
      super();
   }

   /**
    * Gets the user.
    *
    * @return the user
    */
   public String getUser() {
      return user;
   }

   /**
    * Sets the user.
    *
    * @param user the new user
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * Gets the data policy operations.
    *
    * @return the data policy operations
    */
   public Set<DataPolicyOperations> getDataPolicyOperations() {
      return dataPolicyOperations;
   }

   /**
    * Sets the data policy operations.
    *
    * @param dataPolicyOperations the new data policy operations
    */
   public void setDataPolicyOperations(Set<DataPolicyOperations> dataPolicyOperations) {
      this.dataPolicyOperations = dataPolicyOperations;
   }

}
