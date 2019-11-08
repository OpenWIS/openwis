package org.openwis.dataservice.common.domain.entity.subscription;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.visitor.IVisitable;
import org.openwis.dataservice.common.visitor.IVisitor;
import org.openwis.dataservice.common.visitor.VisitException;

/**
 * The frequency entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "frequency")
@XmlSeeAlso({EventBasedFrequency.class, RecurrentFrequency.class})
@Entity
@Table(name = "OPENWIS_FREQUENCY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "REQUEST_OBJECT_TYPE", length = 30)
@SequenceGenerator(name = "FREQUENCY_GEN", sequenceName = "FREQUENCY_SEQ", initialValue = 1, allocationSize = 1)
public abstract class Frequency implements Serializable, IVisitable<Frequency> {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FREQUENCY_GEN")
   @Column(name = "FREQUENCY_ID")
   private Long id;

   /** */
   @Column(name = "IS_ZIPPED")
   private Boolean zipped;

   /**
    * Default constructor.
    */
   public Frequency() {
      super();
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

   /**
    * @return the zipped
    */
   public Boolean getZipped() {
      return zipped;
   }

   /**
    * @param zipped
    *            the zipped to set
    */
   public void setZipped(Boolean zipped) {
      this.zipped = zipped;
   }

   @Override
   public void accept(IVisitor<Frequency> visitor) throws VisitException {
      visitor.visit(this);
   }

}
