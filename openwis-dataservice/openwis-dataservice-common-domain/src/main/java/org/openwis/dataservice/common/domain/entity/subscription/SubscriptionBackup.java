package org.openwis.dataservice.common.domain.entity.subscription;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class SubscriptionBackup.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscriptionBackup")
@Entity
@Table(name = "OPENWIS_SUBSCRIPTION_BACKUP")
@SequenceGenerator(name = "SUBSCRIPTION_BACKUP_GEN", sequenceName = "SUBSCRIPTION_BACKUP_SEQ", initialValue = 1, allocationSize = 1)
public class SubscriptionBackup implements Serializable {
   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUBSCRIPTION_BACKUP_GEN")
   @Column(name = "SUBSCRIPTION_BACKUP_ID")
   private Long id;

   /** The subscription id. */
   @XmlElement
   @Column(name = "SUBCRIPTION_ID")
   private long subscriptionId;

   /** The deployment. */
   @XmlElement
   @Column(name = "DEPLOYMENT")
   private String deployment;

   /**
    * Instantiates a new subscription backup.
    */
   public SubscriptionBackup() {
      super();
   }

   /**
    * Gets the subscription id.
    *
    * @return the subscription id
    */
   public long getSubscriptionId() {
      return subscriptionId;
   }

   /**
    * Sets the subscription id.
    *
    * @param subscriptionId the new subscription id
    */
   public void setSubscriptionId(long subscriptionId) {
      this.subscriptionId = subscriptionId;
   }

   /**
    * Gets the deployment.
    *
    * @return the deployment
    */
   public String getDeployment() {
      return deployment;
   }

   /**
    * Sets the deployment.
    *
    * @param deployment the new deployment
    */
   public void setDeployment(String deployment) {
      this.deployment = deployment;
   }

   /**
    * Sets the id.
    *
    * @param id the new id
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public Long getId() {
      return id;
   }

}
