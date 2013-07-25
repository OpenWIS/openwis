/**
 * 
 */
package org.openwis.metadataportal.services.dissemination.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.securityservice.OpenWISEmail;
import org.openwis.securityservice.OpenWISFTP;

/**
 * This DTO is used to encapsulate all the diffusion dissemination parameters. <P>
 * The dissemination diffusion parameters are of two types : <P>
 * <ul>
 *    <li>Mail</li>
 *    <li>FTP</li>
 * </ul>
 */
public class AllDiffusionDisseminationParameterDTO {

    /**
     * True if dissemination is authorized for user, false otherwise.
     */
    private boolean authorizedFtp;

    /**
     * The FTP dissemination parameters.
     */
    private List<OpenWISFTP> ftp;

    /**
     * True if dissemination is authorized for user, false otherwise.
     */
    private boolean authorizedMail;

    /**
     * The mail dissemination parameters.
     */
    private List<OpenWISEmail> mail;

    /**
     * Gets the authorizedFtp.
     * @return the authorizedFtp.
     */
    public boolean isAuthorizedFtp() {
        return authorizedFtp;
    }

    /**
     * Sets the authorizedFtp.
     * @param authorizedFtp the authorizedFtp to set.
     */
    public void setAuthorizedFtp(boolean authorizedFtp) {
        this.authorizedFtp = authorizedFtp;
    }

    /**
     * Gets the authorizedMail.
     * @return the authorizedMail.
     */
    public boolean isAuthorizedMail() {
        return authorizedMail;
    }

    /**
     * Sets the authorizedMail.
     * @param authorizedMail the authorizedMail to set.
     */
    public void setAuthorizedMail(boolean authorizedMail) {
        this.authorizedMail = authorizedMail;
    }

    /**
     * Gets the ftp.
     * @return the ftp.
     */
    public List<OpenWISFTP> getFtp() {
        if (ftp == null) {
            ftp = new ArrayList<OpenWISFTP>();
        }
        return ftp;
    }

    /**
     * Sets the ftp.
     * @param ftp the ftp to set.
     */
    public void setFtp(List<OpenWISFTP> ftp) {
        this.ftp = ftp;
    }

    /**
     * Gets the mail.
     * @return the mail.
     */
    public List<OpenWISEmail> getMail() {
        if (mail == null) {
            mail = new ArrayList<OpenWISEmail>();
        }
        return mail;
    }

    /**
     * Sets the mail.
     * @param mail the mail to set.
     */
    public void setMail(List<OpenWISEmail> mail) {
        this.mail = mail;
    }
}
