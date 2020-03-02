package org.openwis.metadataportal.services.user.dto;

import java.sql.Timestamp;

public class UserActionLogDTO {


    private Timestamp date;

    private ActionLog action;

    private String username;

    private String attribute;

    private String actionerUsername;

    /**
     * Type of action
     */
    public ActionLog getAction() {
        return action;
    }

    public void setAction(ActionLog action) {
        this.action = action;
    }

    /**
     * name of the user who received the action
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * LDAP Attribute name
     */
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * Name of user who performed the action
     */
    public String getActionerUsername() {
        return actionerUsername;
    }

    public void setActionerUsername(String actionerUsername) {
        this.actionerUsername = actionerUsername;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
