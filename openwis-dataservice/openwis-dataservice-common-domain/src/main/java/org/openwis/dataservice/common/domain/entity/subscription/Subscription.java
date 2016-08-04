package org.openwis.dataservice.common.domain.entity.subscription;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.request.Request;

/**
 * The subscription entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscription")
@Entity
@DiscriminatorValue(value = "SUBSCRIPTION")
@NamedQueries({
      @NamedQuery(name = "Subscription.FindBySusbcriptionId", //
      query = "SELECT DISTINCT s FROM Subscription s LEFT JOIN FETCH s.productMetadata LEFT JOIN FETCH s.parameters WHERE s.id= :id"),
      @NamedQuery(name = "Subscription.FindByMetadataURN", //
      query = "SELECT DISTINCT s FROM Subscription s JOIN s.frequency f JOIN s.productMetadata meta WHERE meta.urn = :metadataurn AND s.startingDate <= :productDate AND s.state = :state AND s.valid = true AND f.reccurentScale IS NULL "), //
      @NamedQuery(name = "Subscription.FindRecurrentToProcess", //
      query = "SELECT DISTINCT s FROM Subscription s JOIN s.frequency f WHERE s.startingDate <=:date AND (f.nextDate IS NULL OR f.nextDate <= :date) AND f.reccurentScale IS NOT NULL "),
      @NamedQuery(name = "Subscription.FindByUser", query = "SELECT s FROM Subscription s WHERE s.user = :user")})
public class Subscription extends Request implements Serializable, Cloneable {

   /** The active. */
   @Column(name = "IS_VALID")
   private boolean valid;

   /** The state. */
   @Column(name = "STATE", nullable = true)
   @Enumerated(EnumType.STRING)
   private SubscriptionState state;

   /** The frequency. */
   @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.ALL)
   @JoinColumn(name = "FREQUENCY_ID")
   private Frequency frequency;

   /** The starting date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "STARTING_DATE", nullable = true)
   private Date startingDate;

   /** The last event date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "LAST_EVENT_DATE", nullable = true)
   private Date lastEventDate;

   /** The subscription backup. */
   @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
   private SubscriptionBackup subscriptionBackup;

   /** The backup. */
   @Column(name = "BACKUP")
   private boolean backup;

   /**
    * Default constructor.
    */
   public Subscription() {
      super();
      valid = true;
      backup = false;
      state = SubscriptionState.ACTIVE;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[Subscription {0}] {1} - {2} {3}", id, getProductMetadata()
            .getUrn(), frequency, state);
   }

   /**
    * Checks if is valid.
    *
    * @return true, if is valid
    */
   public boolean isValid() {
      return valid;
   }

   /**
    * Sets the valid.
    *
    * @param valid the new valid
    */
   public void setValid(boolean valid) {
      this.valid = valid;
   }

   /**
    * Gets the frequency.
    *
    * @return the frequency
    */
   public Frequency getFrequency() {
      return frequency;
   }

   /**
    * Sets the frequency.
    *
    * @param frequency the frequency to set
    */
   public void setFrequency(Frequency frequency) {
      this.frequency = frequency;
   }

   /**
    * Gets the startingDate.
    * @return the startingDate.
    */
   public Date getStartingDate() {
      return startingDate;
   }

   /**
    * Sets the startingDate.
    * @param startingDate the startingDate to set.
    */
   public void setStartingDate(Date startingDate) {
      this.startingDate = startingDate;
   }

   /**
    * Gets the lastEventDate.
    * @return the lastEventDate.
    */
   public Date getLastEventDate() {
      return lastEventDate;
   }

   /**
    * Sets the lastEventDate.
    * @param lastEventDate the lastEventDate to set.
    */
   public void setLastEventDate(Date lastEventDate) {
      this.lastEventDate = lastEventDate;
   }

   /**
    * Gets the state.
    *
    * @return the state
    */
   public SubscriptionState getState() {
      return state;
   }

   /**
    * Sets the state.
    *
    * @param state the new state
    */
   public void setState(SubscriptionState state) {
      this.state = state;
   }

   /**
    * Gets the subscription backup.
    *
    * @return the subscription backup
    */
   public SubscriptionBackup getSubscriptionBackup() {
      return subscriptionBackup;
   }

   /**
    * Sets the subscription backup.
    *
    * @param subscriptionBackup the new subscription backup
    */
   public void setSubscriptionBackup(SubscriptionBackup subscriptionBackup) {
      this.subscriptionBackup = subscriptionBackup;
   }

   /**
    * Checks if is backup.
    *
    * @return true, if is backup
    */
   public boolean isBackup() {
      return backup;
   }

   /**
    * Sets the backup.
    *
    * @param backup the new backup
    */
   public void setBackup(boolean backup) {
      this.backup = backup;
   }

}
