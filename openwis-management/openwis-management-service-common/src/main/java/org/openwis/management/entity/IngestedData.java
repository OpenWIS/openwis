/**
 * 
 */
package org.openwis.management.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Volume of data ingested per day <P>
 */
@Entity
@Table(name = "OPENWIS_INGESTED_DATA")
@SequenceGenerator(name = "INGESTED_GEN", sequenceName = "INGESTED_SEQ", initialValue = 1, allocationSize = 1)
public class IngestedData implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INGESTED_GEN")
   @Column(name = "INGESTED_DATA_ID")
   private Long id;

   /** */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "DATE", unique = true)
   private Date date;

   /** */
   @Column(name = "SIZE")
   private Long size;

   /**
    * Default constructor.
    */
   public IngestedData() {
      super();
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * Gets the date.
    * @return the date.
    */
   public Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    * @param date the date to set.
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
    * Gets the size.
    * @return the size.
    */
   public Long getSize() {
      return size;
   }

   /**
    * Sets the size.
    * @param size the size to set.
    */
   public void setSize(Long size) {
      this.size = size;
   }

}
