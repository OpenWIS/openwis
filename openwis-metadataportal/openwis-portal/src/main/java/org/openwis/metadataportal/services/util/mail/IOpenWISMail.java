package org.openwis.metadataportal.services.util.mail;

import java.util.Map;

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

    /*
    * Set the subject
     */
    public void setSubject(String subject);
    /**
     * Return the body of the email in HTML format
     * @return email body
     */
    public String getBody();

    /**
     * Set content data
     * @param content
     */
    public void setContentData(Map<String, Object> content);

    /**
     * Adds a new variable to content data
     * @param name name of template variable
     * @param value value of the variable
     */
    public void addContentVariable(String name, Object value);

    /**
     * Return a list of destinations email addresses
     * @return list of addresses
     */
    public String[] getDestinations();

    /**
     * Set a list of destinations address
     * @param destinations
     */
    public void setDestinations(String[] destinations);

}
