package org.openwis.metadataportal.services.util.mail;

/**
 * Represents a mail sent to user.
 * It wraps basic mail operation like se
 */
public interface IOpenWISMail {

    /**
     * Return the administrator email fetched from property variable.
     * @return administrator email
     */
    public String getAdministratorAddress();

    /**
     * Return the email subject
     * @return subject
     */
    public String getSubject();

    /**
     * Return the body of the email in HTML format
     * @return email body
     */
    public String getBody();

    /**
     * Return a list of destinations email addresses
     * @return list of addresses
     */
    public String[] getDestinations();
}
