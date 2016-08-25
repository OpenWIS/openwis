package org.openwis.dataservice.common.domain.bean;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class DataPolicyOperations.
 */
@XmlRootElement(name = "DataPolicyOperations")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPolicyOperations implements Serializable {

   /** The data policy. */
   @XmlElement
   private String dataPolicy;

   /** The operations. */
   @XmlElement
   private Set<Operation> operations;

   /**
    * Instantiates a new data policy operations.
    */
   public DataPolicyOperations() {
      super();
   }

   /**
    * Gets the data policy.
    *
    * @return the data policy
    */
   public String getDataPolicy() {
      return dataPolicy;
   }

   /**
    * Sets the data policy.
    *
    * @param dataPolicy the new data policy
    */
   public void setDataPolicy(String dataPolicy) {
      this.dataPolicy = dataPolicy;
   }

   /**
    * Gets the operations.
    *
    * @return the operations
    */
   public Set<Operation> getOperations() {
      return operations;
   }

   /**
    * Sets the operations.
    *
    * @param operations the new operations
    */
   public void setOperations(Set<Operation> operations) {
      this.operations = operations;
   }

}
