/**
 * 
 */
package org.openwis.metadataportal.model.datapolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicyAlias {

    private int id;
    
    private String alias;
    
    /**
     * Default constructor.
     * Builds a MockDataPolicyAlias.
     */
    public DataPolicyAlias() {
        super();
    }
    
    /**
     * Default constructor.
     * Builds a MockDataPolicyAlias.
     * @param alias
     */
    public DataPolicyAlias(String alias) {
       super();
       this.alias = alias;
    }

    /**
     * Gets the id.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the alias.
     * @return the alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     * @param alias the alias to set.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    

}
