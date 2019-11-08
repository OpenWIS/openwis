package org.openwis.dataservice.common.domain.entity.request.dissemination;

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

/**
 * The diffusion entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "diffusion")
@XmlSeeAlso({
    MailDiffusion.class,
    FTPDiffusion.class
})

@Entity
@Table(name = "OPENWIS_DIFFUSION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "REQUEST_OBJECT_TYPE", length = 30)
@SequenceGenerator(name = "DIFFUSION_GEN", sequenceName = "DIFFUSION_SEQ", initialValue = 1, allocationSize = 1)
public abstract class Diffusion implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIFFUSION_GEN")
   @Column(name = "DIFFUSION_ID")
   private Long id;

   /**
    * Default constructor.
    */
   public Diffusion() {
      super();
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

}
