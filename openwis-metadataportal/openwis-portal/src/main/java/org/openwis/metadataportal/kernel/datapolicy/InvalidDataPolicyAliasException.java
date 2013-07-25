/**
 * 
 */
package org.openwis.metadataportal.kernel.datapolicy;

import java.util.Collection;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class InvalidDataPolicyAliasException extends Exception {
    
    /**
     * Comment for <code>invalidAliases</code>
     */
    private Collection<String> invalidAliases;
    
    /**
     * Gets the invalidAliases.
     * @return the invalidAliases.
     */
    public Collection<String> getInvalidAliases() {
        return invalidAliases;
    }

    /**
     * Default constructor.
     * Builds a InvalidDataPolicyAliasException.
     */
    public InvalidDataPolicyAliasException(Collection<String> invalidAliases) {
        super();
        this.invalidAliases = invalidAliases;
    }


}
