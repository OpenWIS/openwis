/**
 * 
 */
package org.openwis.metadataportal.services.group.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.group.Group;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GroupsDTO {

    private List<Group> groups;

    /**
     * Gets the groups.
     * @return the groups.
     */
    public List<Group> getGroups() {
        if(groups == null) {
            groups = new ArrayList<Group>();
        }
        return groups;
    }

    /**
     * Sets the groups.
     * @param groups the groups to set.
     */
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
