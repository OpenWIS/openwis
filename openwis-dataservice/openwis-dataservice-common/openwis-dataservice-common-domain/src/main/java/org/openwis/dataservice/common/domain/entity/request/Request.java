/**
 *
 */
package org.openwis.dataservice.common.domain.entity.request;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.enumeration.ClassOfService;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "request")
@XmlSeeAlso({AdHoc.class, Subscription.class})
@Entity
@Table(name = "OPENWIS_REQUEST")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "REQUEST_OBJECT_TYPE", length = 30)
@SequenceGenerator(name = "REQUEST_GEN", sequenceName = "REQUEST_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "ProcessedRequest.getByUser", query = "FROM Request r WHERE r.user= :user"),
      @NamedQuery(name = "Request.byProductMetadata", query = "FROM Request r WHERE r.productMetadata = :pm")})
public abstract class Request implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_GEN")
   @Column(name = "REQUEST_ID")
   protected Long id;

   /** The primary dissemination. */
   @OneToOne(optional = false, cascade = CascadeType.ALL)
   @JoinColumn(name = "PRIMARY_DISSEMINATION_ID", referencedColumnName = "DISSEMINATION_ID")
   private Dissemination primaryDissemination;

   /** The secondary dissemination. */
   @OneToOne(optional = true, cascade = CascadeType.ALL)
   @JoinColumn(name = "SECONDARY_DISSEMINATION_ID", referencedColumnName = "DISSEMINATION_ID")
   private Dissemination secondaryDissemination;

   /** The product metadata. */
   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "PRODUCT_METADATA_ID", referencedColumnName = "PRODUCT_METADATA_ID")
   protected ProductMetadata productMetadata;

   /** The parameters. */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JoinTable(name = "OPENWIS_REQUESTS_PARAMETERS", //
   joinColumns = @JoinColumn(name = "REQUEST_ID", referencedColumnName = "REQUEST_ID"), //
   inverseJoinColumns = @JoinColumn(name = "PARAMETER_ID", referencedColumnName = "PARAMETER_ID"))
   private Set<Parameter> parameters;

   /** The user. */
   @Column(name = "USER_ID")
   private String user;

   /** The user email. */
   @Column(name = "USER_EMAIL")
   private String email;
   
   /** The user SLA. */
   @Enumerated(EnumType.STRING)
   @Column(name = "USER_CLASS_OF_SERVICE")
   private ClassOfService classOfService;
   
   /** The extract mode. */
   @Enumerated(EnumType.STRING)
   @Column(name = "EXTRACT_MODE", nullable = false)
   private ExtractMode extractMode;

   /** The request type. */
   @Column(name = "REQUEST_OBJECT_TYPE", insertable = false, updatable = false)
   private String requestType;

   /**
    * Default constructor.
    * Builds a Request.
    */
   public Request() {
      super();
   }

   /**
    * Gets the primary dissemination.
    *
    * @return the primaryDissemination
    */
   public Dissemination getPrimaryDissemination() {
      return primaryDissemination;
   }

   /**
    * Sets the primary dissemination.
    *
    * @param primaryDissemination the primaryDissemination to set
    */
   public void setPrimaryDissemination(Dissemination primaryDissemination) {
      this.primaryDissemination = primaryDissemination;
   }

   /**
    * Gets the secondary dissemination.
    *
    * @return the secondaryDissemination
    */
   public Dissemination getSecondaryDissemination() {
      return secondaryDissemination;
   }

   /**
    * Sets the secondary dissemination.
    *
    * @param secondaryDissemination the secondaryDissemination to set
    */
   public void setSecondaryDissemination(Dissemination secondaryDissemination) {
      this.secondaryDissemination = secondaryDissemination;
   }

   /**
    * Gets the product metadata.
    *
    * @return the productMetadata
    */
   public ProductMetadata getProductMetadata() {
      return productMetadata;
   }

   /**
    * Sets the product metadata.
    *
    * @param productMetadata the productMetadata to set
    */
   public void setProductMetadata(ProductMetadata productMetadata) {
      this.productMetadata = productMetadata;
   }

   /**
    * Gets the parameters.
    *
    * @return the parameters
    */
   public Set<Parameter> getParameters() {
      return parameters;
   }

   /**
    * Sets the parameters.
    *
    * @param parameters the parameters to set
    */
   public void setParameters(Set<Parameter> parameters) {
      this.parameters = parameters;
   }

   /**
    * Gets the user.
    * @return the user.
    */
   public String getUser() {
      return user;
   }

   /**
    * Sets the user.
    * @param user the user to set.
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * Gets the extractMode.
    * @return the extractMode.
    */
   public ExtractMode getExtractMode() {
      return extractMode;
   }

   /**
    * Sets the extractMode.
    * @param extractMode the extractMode to set.
    */
   public void setExtractMode(ExtractMode extractMode) {
      this.extractMode = extractMode;
   }

   /**
    * Gets the requestType.
    * @return the requestType.
    */
   public String getRequestType() {
      return requestType;
   }

   /**
    * Sets the requestType.
    * @param requestType the requestType to set.
    */
   public void setRequestType(String requestType) {
      this.requestType = requestType;
   }

   /**
    * Gets the email.
    *
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * Sets the email.
    *
    * @param email the new email
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * Get the user's class of service.
    *
    * @return the class of service
    */
   public ClassOfService getClassOfService() {
      return classOfService;
   }
   
   /**
    * Set the user's Class of service.
    * 
    * @param sla the new user class of service
    */
   public void setClassOfService(ClassOfService classOfService) {
      this.classOfService = classOfService;
   }
   
}
