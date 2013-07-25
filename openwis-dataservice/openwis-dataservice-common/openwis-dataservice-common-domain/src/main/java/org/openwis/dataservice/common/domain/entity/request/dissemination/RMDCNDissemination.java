package org.openwis.dataservice.common.domain.entity.request.dissemination;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The RMD/CND dissemination. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rmdcnDissemination")

@Entity
@DiscriminatorValue(value = "RMD_CND")
public class RMDCNDissemination extends Dissemination {

   /** */
   @OneToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.ALL)
   @JoinColumn(name = "DIFFUSION_ID", referencedColumnName = "DIFFUSION_ID")
   private Diffusion diffusion;

   /**
    * Default constructor.
    */
   public RMDCNDissemination() {
      super();
   }

   /**
    * @return the diffusion
    */
   public Diffusion getDiffusion() {
      return diffusion;
   }

   /**
    * @param diffusion
    *            the diffusion to set
    */
   public void setDiffusion(Diffusion diffusion) {
      this.diffusion = diffusion;
   }

}
