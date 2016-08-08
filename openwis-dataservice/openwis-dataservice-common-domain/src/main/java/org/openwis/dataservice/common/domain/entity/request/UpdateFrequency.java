package org.openwis.dataservice.common.domain.entity.request;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The update frequency entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateFrequency")
@XmlSeeAlso({
    Temporal.class,
    RecurrentUpdateFrequency.class
})

@Entity
@Table(name = "OPENWIS_UPDATE_FREQUENCY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "REQUEST_OBJECT_TYPE", length = 30)
@SequenceGenerator(name = "UPDATE_FREQUENCY_GEN", sequenceName = "UPDATE_FREQUENCY_SEQ", initialValue = 1, allocationSize = 1)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public abstract class UpdateFrequency implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UPDATE_FREQUENCY_GEN")
   @Column(name = "UPDATE_FREQUENCY_ID")
   private Long id;

   /**
    * Default constructor.
    */
   public UpdateFrequency() {
      super();
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

}
