package org.openwis.dataservice.common.domain.entity.cache;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;

/**
 * The Class PatternMetadataMapping. <P>
 * Explanation goes here. <P>
 */
@Entity
@Table(name = "OPENWIS_PATTERN_METADATA_MAPPING")
@SequenceGenerator(name = "PATTERN_METADATA_MAPPING_GEN", sequenceName = "PATTERN_METADATA_MAPPING_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "PatternMetadataMapping.all", query = "FROM PatternMetadataMapping pmm"),
      @NamedQuery(name = "PatternMetadataMapping.byProductMetadata", query = "FROM PatternMetadataMapping pmm  WHERE pmm.productMetadata= :pm")})
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PatternMetadataMapping implements Serializable {

   /** The id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PATTERN_METADATA_MAPPING_GEN")
   @Column(name = "PATTERN_METADATA_MAPPING_ID")
   private Long id;

   /** The pattern. */
   @Column(name = "PATTERN", nullable = false, length = 1024, unique = false)
   private String pattern;

   /** The compiled pattern. */
   @Transient
   private transient Pattern compiledPattern;

   /** The product metadata. */
   @OneToOne(fetch = FetchType.EAGER)
   private ProductMetadata productMetadata;

   /**
    * Instantiates a new pattern metadata mapping.
    */
   public PatternMetadataMapping() {
   }

   /**
    * Gets the compiled pattern.
    *
    * @return the compiled pattern
    */
   public synchronized Pattern getCompiledPattern() {
      if (compiledPattern == null) {
    	 String modifiedPattern = pattern.trim();
    	 
    	 if (!modifiedPattern.startsWith("^")) modifiedPattern = "^.*" + modifiedPattern;
    	 if (!modifiedPattern.endsWith("$")) modifiedPattern = modifiedPattern + ".*$";
    	 
         compiledPattern = Pattern.compile(modifiedPattern);
      }
      return compiledPattern;
   }

   /**
    * Gets the pattern.
    *
    * @return the pattern
    */
   public String getPattern() {
      return pattern;
   }

   /**
    * Sets the pattern.
    *
    * @param pattern the new pattern
    */
   public void setPattern(String pattern) {
      this.pattern = pattern;
      compiledPattern = null;
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
    * Gets the product metadata.
    *
    * @return the product metadata
    */
   public ProductMetadata getProductMetadata() {
      return productMetadata;
   }

   /**
    * Sets the product metadata.
    *
    * @param productMetadata the new product metadata
    */
   public void setProductMetadata(ProductMetadata productMetadata) {
      this.productMetadata = productMetadata;
   }

}