/**
 * 
 */
package org.openwis.metadataportal.model.datapolicy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicy {

   private int id;

   private String name;

   private String description;

   private Set<DataPolicyAlias> aliases;

   private List<DataPolicyOperationsPerGroup> dpOpPerGroup;

   /**
    * Default constructor.
    * Builds a DataPolicy.
    */
   public DataPolicy() {
      super();
   }

   /**
   * Default constructor.
   * Builds a DataPolicy.
   * @param name
   */
   public DataPolicy(String name) {
      super();
      this.name = name;
   }

   /**
    * Default constructor.
    * Builds a DataPolicy.
    * @param name
    * @param description
    */
   public DataPolicy(String name, String description) {
      super();
      this.name = name;
      this.description = description;
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
    * Gets the description.
    * @return the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Sets the description.
    * @param description the description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * Gets the aliases.
    * @return the aliases.
    */
   public Set<DataPolicyAlias> getAliases() {
      if (aliases == null) {
         aliases = new HashSet<DataPolicyAlias>();
      }
      return aliases;
   }

   /**
    * Sets the aliases.
    * @param aliases the aliases to set.
    */
   public void setAliases(Set<DataPolicyAlias> aliases) {
      this.aliases = aliases;
   }

   /**
    * Gets the dpOpPerGroup.
    * @return the dpOpPerGroup.
    */
   public List<DataPolicyOperationsPerGroup> getDpOpPerGroup() {
      if (dpOpPerGroup == null) {
         dpOpPerGroup = new ArrayList<DataPolicyOperationsPerGroup>();
      }
      return dpOpPerGroup;
   }

   /**
    * Sets the dpOpPerGroup.
    * @param dpOpPerGroup the dpOpPerGroup to set.
    */
   public void setDpOpPerGroup(List<DataPolicyOperationsPerGroup> dpOpPerGroup) {
      this.dpOpPerGroup = dpOpPerGroup;
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
      if (!(obj instanceof DataPolicy)) {
         return false;
      }
      DataPolicy other = (DataPolicy) obj;
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      return true;
   }

}
