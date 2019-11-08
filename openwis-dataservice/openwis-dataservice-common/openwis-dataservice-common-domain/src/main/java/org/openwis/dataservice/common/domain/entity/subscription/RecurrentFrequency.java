package org.openwis.dataservice.common.domain.entity.subscription;

import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.enumeration.RecurrentScale;

/**
 * The recurrent frequency entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recurrentFrequency")
@Entity
@DiscriminatorValue(value = "RECURRENT")
public class RecurrentFrequency extends Frequency {

   /** */
   @Column(name = "RECURRENCE_PERIOD")
   private Integer reccurencePeriod;

   /** */
   @Enumerated(EnumType.STRING)
   @Column(name = "RECURRENT_SCALE")
   private RecurrentScale reccurentScale;

   /** The next date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "NEXT_DATE", nullable = true)
   private Date nextDate;

   /**
    * Default constructor.
    */
   public RecurrentFrequency() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("Each.{0}.{1}", reccurencePeriod, reccurentScale);
   }

   /**
    * @return the reccurencePeriod
    */
   public Integer getReccurencePeriod() {
      return reccurencePeriod;
   }

   /**
    * @param reccurencePeriod
    *            the reccurencePeriod to set
    */
   public void setReccurencePeriod(Integer reccurencePeriod) {
      this.reccurencePeriod = reccurencePeriod;
   }

   /**
    * @return the reccurentScale
    */
   public RecurrentScale getReccurentScale() {
      return reccurentScale;
   }

   /**
    * @param reccurentScale
    *            the reccurentScale to set
    */
   public void setReccurentScale(RecurrentScale reccurentScale) {
      this.reccurentScale = reccurentScale;
   }

   /**
    * Gets the next date.
    *
    * @return the next date
    */
   public Date getNextDate() {
      return nextDate;
   }

   /**
    * Sets the next date.
    *
    * @param nextDate the new next date
    */
   public void setNextDate(Date nextDate) {
      this.nextDate = nextDate;
   }

}
