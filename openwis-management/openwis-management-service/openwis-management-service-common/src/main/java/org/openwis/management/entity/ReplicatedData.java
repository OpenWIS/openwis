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
import javax.persistence.UniqueConstraint;

/**
 * Volume of data replicated per source and per day <P>
 */
@Entity
@Table(name = "OPENWIS_REPLICATED_DATA", uniqueConstraints={@UniqueConstraint(columnNames = {"DATE", "SOURCE"})})
@SequenceGenerator(name = "REPLICATED_GEN", sequenceName = "REPLICATED_SEQ", initialValue = 1, allocationSize = 1)
public class ReplicatedData implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLICATED_GEN")
   @Column(name = "REPLICATED_DATA_ID")
   private Long id;

   /** */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "DATE")
   private Date date;

   /** */
   @Column(name = "SIZE")
   private Long size;

   /** */
   @Column(name = "SOURCE")
   private String source;

   /**
    * Default constructor.
    */
   public ReplicatedData() {
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

   /**
    * Gets the source.
    * @return the source.
    */
   public String getSource() {
      return source;
   }

   /**
    * Sets the source.
    * @param source the source to set.
    */
   public void setSource(String source) {
      this.source = source;
   }

}
