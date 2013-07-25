/**
 *
 */
package org.openwis.management.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Defines a filter allowing to filter on the ID of the metadata to feed.
 */
@Entity
@Table(name = "OPENWIS_MSSFSS_FEEDING_FILTER")
@SequenceGenerator(
      name = "MSSFSS_FEEDING_FILTER_GEN",
      sequenceName = "MSSFSS_FEEDING_FILTER_SEQ",
      initialValue = 1, allocationSize = 1
)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class FeedingFilter implements Serializable {

   /** Generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MSSFSS_FEEDING_FILTER_GEN")
   @Column(name = "FILTER_ID")
   private Long id;

   /** Regular expression to filter on the ID of the metadata to feed. */
   @Column(name = "REGEX")
   private String regex;

   /** A description for the regex filter. */
   @Column(name = "DESCRIPTION")
   private String description;

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
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
   
   @Override
	public String toString() {
		return "Feeding filter. Description: " + description + ", regex: " + regex;
	}
}
