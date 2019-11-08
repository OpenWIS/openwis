package org.openwis.metadataportal.services.dissemination.dto;


/**
 * This DTO is used to wrap all dissemination parameters. <P>
 * There are several types of disseminations parameters :<P>
 * <ul>
 *    <li>The MSS/FSS Channels.</li>
 *    <li>The RMDCN Diffusion.</li>
 *    <li>The Public Diffusion.</li>
 * </ul> 
 * 
 */
public class AllDisseminationParametersDTO {

    /**
    * A DTO for MSS/FSS Dissemination.
    */
    private AllMSSFSSDisseminationParameterDTO mssFss;

    /**
     * A DTO for RMDCN Diffusion.
     */
    private AllDiffusionDisseminationParameterDTO rmdcnDiffusion;

    /**
     * A DTO for Public Diffusion.
     */
    private AllDiffusionDisseminationParameterDTO publicDiffusion;

    /**
     * Gets the mssFss.
     * @return the mssFss.
     */
    public AllMSSFSSDisseminationParameterDTO getMssFss() {
        return mssFss;
    }

    /**
     * Sets the mssFss.
     * @param mssFss the mssFss to set.
     */
    public void setMssFss(AllMSSFSSDisseminationParameterDTO mssFss) {
        this.mssFss = mssFss;
    }

    /**
     * Gets the rmdcnDiffusion.
     * @return the rmdcnDiffusion.
     */
    public AllDiffusionDisseminationParameterDTO getRmdcnDiffusion() {
        return rmdcnDiffusion;
    }

    /**
     * Sets the rmdcnDiffusion.
     * @param rmdcnDiffusion the rmdcnDiffusion to set.
     */
    public void setRmdcnDiffusion(AllDiffusionDisseminationParameterDTO rmdcnDiffusion) {
        this.rmdcnDiffusion = rmdcnDiffusion;
    }

    /**
     * Gets the publicDiffusion.
     * @return the publicDiffusion.
     */
    public AllDiffusionDisseminationParameterDTO getPublicDiffusion() {
        return publicDiffusion;
    }

    /**
     * Sets the publicDiffusion.
     * @param publicDiffusion the publicDiffusion to set.
     */
    public void setPublicDiffusion(AllDiffusionDisseminationParameterDTO publicDiffusion) {
        this.publicDiffusion = publicDiffusion;
    }
}
