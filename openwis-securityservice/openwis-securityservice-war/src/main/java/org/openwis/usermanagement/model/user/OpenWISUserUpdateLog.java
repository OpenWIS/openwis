package org.openwis.usermanagement.model.user;

import java.io.Serializable;

public class OpenWISUserUpdateLog implements Serializable {

    /**
     * @member name of action
     */
    private String action;

    /**
     * @member name of the attribute changed
     */
    private String attribute;
    private String username;

    public String getAttribute() {
        return attribute;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getUsername() {
        return username;
    }
}
