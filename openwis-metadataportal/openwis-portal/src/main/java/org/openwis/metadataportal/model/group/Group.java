/**
 * 
 */
package org.openwis.metadataportal.model.group;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 * A POJO that describes a group in GeoNetwork. <P>
 * Explanation goes here. <P>
 * 
 */
public class Group {

    /**
     * The id of the group.
     */
    private Integer id;

    /**
     * The name of the group.
     */
    private String name;

    /**
     * <code>true</code> if the group is global, <code>false</code> otherwise.
     */
    private boolean global;

    /**
     * Default constructor.
     * Builds a Group.
     */
    public Group() {
        super();
    }

    /**
     * Default constructor.
     * Builds a Group.
     * @param id
     * @param name
     */
    public Group(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the id.
     * @return the id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the global.
     * @return the global.
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Sets the global.
     * @param global the global to set.
     */
    public void setGlobal(boolean global) {
        this.global = global;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static Collection<String> collectIds(Collection<Group> groups) {
        return CollectionUtils.collect(groups, new Transformer() {
            @Override
            public Object transform(Object arg0) {
                return ((Group) arg0).getId().toString();
            }
        });
    }
}
