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
    private String attributeName;
    private String username;

    public String getAttributeName() {
        return attributeName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getUsername() {
        return username;
    }
}
