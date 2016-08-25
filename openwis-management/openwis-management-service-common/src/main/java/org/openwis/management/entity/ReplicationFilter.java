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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Defines a filter allowing to configure the replication mechanism of a list of replicated sources.
 *
 * Explanation goes here.
 */
@Entity
@Table(name = "OPENWIS_CACHE_REPLICATION_FILTER")
@SequenceGenerator(
      name = "CACHE_REPLICATION_FILTER_GEN",
      sequenceName = "CACHE_REPLICATION_FILTER_SEQ",
      initialValue = 1, allocationSize = 1
)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class ReplicationFilter implements Serializable {

   /** Generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CACHE_REPLICATION_FILTER_GEN")
   @Column(name = "FILTER_ID")
   private Long id;

   /** Regular expression to filter on the ID of the metadata to ingest. */
   @Column(name = "REGEX")
   private String regex;

   /** A description for the regex filter. */
   @Column(name = "DESCRIPTION")
   private String description;

   /** Replicated source type */
   @Column(name = "TYPE")
   private String type;

   /** replicated source. */
   @Column(name = "SOURCE")
   private String source;

   /** . */
   @Column(name = "ACTIVE")
   private boolean active;

   /** Specifies the last time this filter was applied. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "UPTIME", nullable = true)
   private Date uptime;

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * Gets the type.
    * @return the type.
    */
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * @param type the type to set.
    */
   public void setType(final String type) {
      this.type = type;
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
   public void setSource(final String source) {
      this.source = source;
   }

   /**
    * Gets the regex.
    * @return the regex.
    */
   public String getRegex() {
      return regex;
   }

   /**
    * Sets the regex.
    * @param regex the regex to set.
    */
   public void setRegex(final String regex) {
      this.regex = regex;
   }

   /**
    * Gets the description.
    * @return the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Sets the description.
    * @param description the description to set.
    */
   public void setDescription(final String description) {
      this.description = description;
   }

   /**
    * Gets the uptime.
    * @return the uptime.
    */
   public Date getUptime() {
      return uptime;
   }

   /**
    * Sets the uptime.
    * @param uptime the uptime to set.
    */
   public void setUptime(final Date uptime) {
      this.uptime = uptime;
   }

   /**
    * Gets the active.
    * @return the active.
    */
   public boolean isActive() {
      return active;
   }

   /**
    * Sets the active.
    * @param active the active to set.
    */
   public void setActive(final boolean active) {
      this.active = active;
   }

   @Override
	public String toString() {
		return "Replication filter. Description: " + description + ", regex: " + regex + ", active: " + active + ", source: " + source + ", type: " + type + ", uptime: " + uptime;
	}
}
