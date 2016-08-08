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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class ExchangedData.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exchangedData")
@Entity
@Table(name = "OPENWIS_EXCHANGED_DATA")
@SequenceGenerator(name = "EXCHANGED_GEN", sequenceName = "EXCHANGED_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "ExchangedData.getBySourceAndDate", query = "FROM ExchangedData WHERE source = :src AND date = :date"),
      @NamedQuery(name = "ExchangedData.getByDate", query = "FROM ExchangedData WHERE date BETWEEN :from AND :to"),
      @NamedQuery(name = "ExchangedData.countInIntervalForAllSources", query = "SELECT COUNT(ed) FROM ExchangedData ed WHERE ed.date BETWEEN :from AND :to"),
      @NamedQuery(name = "ExchangedData.countBySources", query = "SELECT COUNT(ed) FROM ExchangedData ed WHERE ed.source LIKE :source"),
      @NamedQuery(name = "ExchangedData.getBetweenDate", query = "SELECT SUM(ed.nbMetadata), SUM(ed.totalSize) FROM ExchangedData ed WHERE ed.date BETWEEN :from AND :to")})
public class ExchangedData implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXCHANGED_GEN")
   @Column(name = "EXCHANGED_DATA_ID")
   private Long id;

   /** The date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "DATE", nullable = true)
   private Date date;

   /** The total size. */
   @Column(name = "TOTAL_SIZE")
   private Long totalSize;

   /** The number metadata. */
   @Column(name = "NB_MD")
   private Long nbMetadata;

   /** The source. */
   @Column(name = "SOURCE")
   private String source;

   /**
    * Default constructor.
    */
   public ExchangedData() {
      super();
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public Long getId() {
      return id;
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
    * Gets the date.
    *
    * @return the date
    */
   public Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    *
    * @param date the new date
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
    * Gets the total size.
    *
    * @return the total size
    */
   public Long getTotalSize() {
      return totalSize;
   }

   /**
    * Sets the total size.
    *
    * @param totalSize the new total size
    */
   public void setTotalSize(Long totalSize) {
      this.totalSize = totalSize;
   }

   /**
    * Gets the nb metadata.
    *
    * @return the nb metadata
    */
   public Long getNbMetadata() {
      return nbMetadata;
   }

   /**
    * Sets the nb metadata.
    *
    * @param nbMetadata the new nb metadata
    */
   public void setNbMetadata(Long nbMetadata) {
      this.nbMetadata = nbMetadata;
   }

   /**
    * Gets the source.
    *
    * @return the source
    */
   public String getSource() {
      return source;
   }

   /**
    * Sets the source.
    *
    * @param source the new source
    */
   public void setSource(String source) {
      this.source = source;
   }

}
