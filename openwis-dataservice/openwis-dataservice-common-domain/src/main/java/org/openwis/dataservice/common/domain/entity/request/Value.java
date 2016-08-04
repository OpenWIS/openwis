package org.openwis.dataservice.common.domain.entity.request;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The value entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "value")
@Entity
@Table(name = "OPENWIS_VALUE")
@SequenceGenerator(name = "VALUE_GEN", sequenceName = "VALUE_SEQ", initialValue = 1, allocationSize = 1)
public class Value implements Serializable {

   /**
    * The Parameter.
    */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VALUE_GEN")
   @Column(name = "VALUE_ID")
   private Long id;

   /** */
   @Column(name = "VALUE")
   private String value;

   /**
    * Default constructor.
    */
   public Value() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("VAL {0}:{1}", id, value);
   }

   /**
    * @return the value
    */
   public String getValue() {
      return value;
   }

   /**
    * @param value
    *            the value to set
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

}
