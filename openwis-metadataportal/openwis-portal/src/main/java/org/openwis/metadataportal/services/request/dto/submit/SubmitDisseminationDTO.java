/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.submit;

import org.openwis.dataservice.Diffusion;
import org.openwis.dataservice.Dissemination;
import org.openwis.dataservice.MssfssDissemination;
import org.openwis.dataservice.PublicDissemination;
import org.openwis.dataservice.RmdcnDissemination;
import org.openwis.dataservice.ShoppingCartDissemination;

/**
 * A dissemination DTO. <P>
 * During the Submit request phase, the user must specify a dissemination way. <P>
 * There are four types of dissemination :<P>
 * <ul>
 *    <li>The MSS/FSS Dissemination.</li>
 *    <li>The RMDCN Diffusion Dissemination.</li>
 *    <li>The Public Diffusion Dissemination.</li>
 *    <li>The Shopping Cart Dissemination.</li>
 * </ul>
 */
public class SubmitDisseminationDTO {

   /**
    * The MSS/FSS Dissemination.
    */
   private MssfssDissemination mssFssDissemination;

   /**
    * The RMDCN Diffusion Dissemination.
    */
   private DiffusionDTO rmdcnDiffusion;

   /**
    * The Public Diffusion Dissemination.
    */
   private DiffusionDTO publicDiffusion;

   /**
    * The Shopping Cart Dissemination.
    */
   private ShoppingCartDissemination shoppingCartDissemination;

   /**
    * Gets the mssFssDissemination.
    * @return the mssFssDissemination.
    */
   public MssfssDissemination getMssFssDissemination() {
      return mssFssDissemination;
   }

   /**
    * Sets the mssFssDissemination.
    * @param mssFssDissemination the mssFssDissemination to set.
    */
   public void setMssFssDissemination(MssfssDissemination mssFssDissemination) {
      this.mssFssDissemination = mssFssDissemination;
   }

   /**
    * Gets the rmdcnDiffusion.
    * @return the rmdcnDiffusion.
    */
   public DiffusionDTO getRmdcnDiffusion() {
      return rmdcnDiffusion;
   }

   /**
    * Sets the rmdcnDiffusion.
    * @param rmdcnDiffusion the rmdcnDiffusion to set.
    */
   public void setRmdcnDiffusion(DiffusionDTO rmdcnDiffusion) {
      this.rmdcnDiffusion = rmdcnDiffusion;
   }

   /**
    * Gets the publicDiffusion.
    * @return the publicDiffusion.
    */
   public DiffusionDTO getPublicDiffusion() {
      return publicDiffusion;
   }

   /**
    * Sets the publicDiffusion.
    * @param publicDiffusion the publicDiffusion to set.
    */
   public void setPublicDiffusion(DiffusionDTO publicDiffusion) {
      this.publicDiffusion = publicDiffusion;
   }

   /**
    * Gets the shoppingCartDissemination.
    * @return the shoppingCartDissemination.
    */
   public ShoppingCartDissemination getShoppingCartDissemination() {
      return shoppingCartDissemination;
   }

   /**
    * Sets the shoppingCartDissemination.
    * @param shoppingCartDissemination the shoppingCartDissemination to set.
    */
   public void setShoppingCartDissemination(ShoppingCartDissemination shoppingCartDissemination) {
      this.shoppingCartDissemination = shoppingCartDissemination;
   }

   /**
    * Returns a Dissemination object from the specified values.
    * @return a Dissemination object from the specified values.
    */
   public Dissemination asDissemination() {
      Dissemination dissemination = null;
      if (this.getMssFssDissemination() != null) {
         dissemination = this.getMssFssDissemination();
      } else if (this.getRmdcnDiffusion() != null) {
         dissemination = new RmdcnDissemination();
         RmdcnDissemination rmdcnDissemination = (RmdcnDissemination) dissemination;
         rmdcnDissemination.setZipMode(this.getRmdcnDiffusion().getZipMode());
         Diffusion diffusion = null;
         if (this.getRmdcnDiffusion().getFtp() != null) {
            diffusion = this.getRmdcnDiffusion().getFtp();
         } else if (this.getRmdcnDiffusion().getMail() != null) {
            diffusion = this.getRmdcnDiffusion().getMail();
         }
         rmdcnDissemination.setDiffusion(diffusion);
      } else if (this.getPublicDiffusion() != null) {
         dissemination = new PublicDissemination();
         PublicDissemination publicDissemination = (PublicDissemination) dissemination;
         publicDissemination.setZipMode(this.getPublicDiffusion().getZipMode());
         Diffusion diffusion = null;
         if (this.getPublicDiffusion().getFtp() != null) {
            diffusion = this.getPublicDiffusion().getFtp();
         } else if (this.getPublicDiffusion().getMail() != null) {
            diffusion = this.getPublicDiffusion().getMail();
         }
         publicDissemination.setDiffusion(diffusion);
      } else if (this.getShoppingCartDissemination() != null) {
         dissemination = this.getShoppingCartDissemination();
      }
      return dissemination;
   }
}
