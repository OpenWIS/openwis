package org.openwis.dataservice.common.domain.entity.request;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Parameter entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parameter")
@Entity
@Table(name = "OPENWIS_PARAMETER")
@SequenceGenerator(name = "PARAMETER_GEN", sequenceName = "PARAMETER_SEQ", initialValue = 1, allocationSize = 1)
public class Parameter implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETER_GEN")
   @Column(name = "PARAMETER_ID")
   private Long id;

   /** */
   @Column(name = "CODE")
   private String code;

   /** The sets of value */
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JoinTable(name = "OPENWIS_PARAMETER_VALUES", //
   joinColumns = @JoinColumn(name = "PARAMETER_ID", referencedColumnName = "PARAMETER_ID"), //
   inverseJoinColumns = @JoinColumn(name = "VALUE_ID", referencedColumnName = "VALUE_ID"))
   private Set<Value> values;

   /**
    * Default constructor.
    * Builds a Parameter.
    */
   public Parameter() {
      super();
      values = new HashSet<Value>();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[Paramerter] {0} : {1}", code, values);
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

   /**
   * Gets the values.
   * @return the values.
   */
   public Set<Value> getValues() {
      return values;
   }

   /**
    * Sets the values.
    * @param values the values to set.
    */
   public void setValues(Set<Value> values) {
      this.values = values;
   }

   /**
       * Gets the code.
       * @return the code.
       */
   public String getCode() {
      return code;
   }

   /**
    * Sets the code.
    * @param code the code to set.
    */
   public void setCode(String code) {
      this.code = code;
   }

}
