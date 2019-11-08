/**
 * 
 */
package org.openwis.metadataportal.kernel.category;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CategoryAlreadyExistsException extends Exception {
    
    /**
     * Comment for <code>name</code>
     */
    private String name;
    
    /**
     * Default constructor.
     * Builds a CategoryAlreadyExistsException.
     */
    public CategoryAlreadyExistsException(String name) {
        super();
        this.name = name;
    }

    /**
     * Gets the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

}
