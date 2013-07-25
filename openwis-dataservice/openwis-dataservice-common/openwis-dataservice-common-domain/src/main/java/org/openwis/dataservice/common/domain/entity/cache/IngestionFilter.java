/**
 *
 */
package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Defines a filter allowing to filter on the ID of the metadata to ingest.
 */
@Entity
@Table(name = "OPENWIS_MSSFSS_INGESTION_FILTER")
@SequenceGenerator(
      name = "MSSFSS_INGESTION_FILTER_GEN",
      sequenceName = "MSSFSS_INGESTION_FILTER_SEQ",
      initialValue = 1, allocationSize = 1
)
public class IngestionFilter implements Serializable {

   /** Generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MSSFSS_INGESTION_FILTER_GEN")
   @Column(name = "FILTER_ID")
   private Long id;

   /** Regular expression to filter on the ID of the metadata to ingest. */
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
}
