package org.openwis.dataservice.common.domain.entity.request;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The temporal entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "temporal")
@Entity
@DiscriminatorValue(value = "TEMPORAL")
public class Temporal extends UpdateFrequency {

   /** */
   @javax.persistence.Temporal(TemporalType.TIMESTAMP)
   @Column(name = "FROM_DATE", nullable = true)
   private Date from;

   /** */
   @javax.persistence.Temporal(TemporalType.TIMESTAMP)
   @Column(name = "TO_DATE", nullable = true)
   private Date to;

   /**
    * Default constructor.
    */
   public Temporal() {
      super();
   }

   /**
    * @return the from
    */
   public Date getFrom() {
      return from;
   }

   /**
    * @param from
    *            the from to set
    */
   public void setFrom(Date from) {
      this.from = from;
   }

   /**
    * @return the to
    */
   public Date getTo() {
      return to;
   }

   /**
    * @param to
    *            the to to set
    */
   public void setTo(Date to) {
      this.to = to;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((from == null) ? 0 : from.hashCode());
      result = prime * result + ((to == null) ? 0 : to.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof Temporal)) {
         return false;
      }
      Temporal other = (Temporal) obj;
      if (from == null) {
         if (other.from != null) {
            return false;
         }
      } else if (!from.equals(other.from)) {
         return false;
      }
      if (to == null) {
         if (other.to != null) {
            return false;
         }
      } else if (!to.equals(other.to)) {
         return false;
      }
      return true;
   }

}
