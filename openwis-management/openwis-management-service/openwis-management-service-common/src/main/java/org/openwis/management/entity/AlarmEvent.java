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
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents a recorded alarm event reported by a component of the OpenWIS
 * system.
 */
@Entity
@Table(name = "OPENWIS_ALARMS")
@SequenceGenerator(name = "ALARMS_GEN", sequenceName = "ALARMS_SEQ", initialValue = 1, allocationSize = 1)
public class AlarmEvent implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALARMS_GEN")
   @Column(name = "EVENT_ID")
   private Long id;

   /** The date at which the event was created. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "DATE", nullable = true)
   private Date date;

   /** Identifies the component who emitted the event.  */
   @Column(name = "SOURCE")
   private String source;

   /** Identifies the software module from where the event was created.  */
   @Column(name = "MODULE")
   private String module;

   /** Specifies the severity of the event; One of WARNING, ERROR or FATAL. */
   @Column(name = "SEVERITY")
   private String severity;

   /** A description of the alarm event. */
   // @Lob has been replaced with 'columnDefinition="text"'.  See: http://www.shredzone.de/cilla/page/299/string-lobs-on-postgresql-with-hibernate-36.html
   @Column(name = "MESSAGE", columnDefinition = "text")
   private String message;

   /**
    * Bean constructor.
    */
   public AlarmEvent() {
   }

   /**
    * Gets the id.
    *
    * @return the id.
    */
   public final Long getId() {
      return id;
   }

   /**
    * Sets the id.
    *
    * @param id
    *           the id to set.
    */
   public final void setId(final Long id) {
      this.id = id;
   }

   /**
    * Gets the date.
    *
    * @return the date.
    */
   public final Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    *
    * @param date
    *           the date to set.
    */
   public final void setDate(final Date date) {
      this.date = date;
   }

   /**
    * Gets the source.
    *
    * @return the source.
    */
   public final String getSource() {
      return source;
   }

   /**
    * Sets the source.
    *
    * @param source
    *           the source to set.
    */
   public final void setSource(final String source) {
      this.source = source;
   }

   /**
    * Gets the module.
    *
    * @return the module.
    */
   public final String getModule() {
      return module;
   }

   /**
    * Sets the module.
    *
    * @param module
    *           the module to set.
    */
   public final void setModule(final String module) {
      this.module = module;
   }

   /**
    * Gets the severity.
    *
    * @return the severity.
    */
   public final String getSeverity() {
      return severity;
   }

   /**
    * Sets the severity.
    *
    * @param severity the severity to set.
    */
   public final void setSeverity(final String severity) {
      this.severity = severity;
   }

   /**
    * Gets the message.
    *
    * @return the message.
    */
   public final String getMessage() {
      return message;
   }

   /**
    * Sets the message.
    *
    * @param message the message to set.
    */
   public final void setMessage(final String message) {
      this.message = message;
   }

}
