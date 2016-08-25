/**
 * 
 */
package org.openwis.dataservice.common.domain.entity.timer;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
@Entity
@Table(name = "OPENWIS_TIMER_CONFIGURATION")
public class TimerConfiguration implements Serializable {

   /** The id. */
   @Id
   @Column(name = "TIMER_ID")
   private String id;

   /** The free lock. */
   private boolean freeLock;

   /**
    * Default constructor.
    * Builds a TimerConfiguration.
    */
   public TimerConfiguration() {
      // TODO Auto-generated constructor stub
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public String getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * Gets the freeLock.
    * @return the freeLock.
    */
   public boolean isFreeLock() {
      return freeLock;
   }

   /**
    * Sets the freeLock.
    * @param freeLock the freeLock to set.
    */
   public void setFreeLock(boolean freeLock) {
      this.freeLock = freeLock;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("<{0} - {1}>", getId(), isFreeLock());
   }

}
