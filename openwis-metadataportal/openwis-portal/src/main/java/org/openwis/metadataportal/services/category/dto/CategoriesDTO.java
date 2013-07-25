/**
 * 
 */
package org.openwis.metadataportal.services.category.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.category.Category;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CategoriesDTO {

    private List<Category> categories;

    /**
     * Gets the categories.
     * @return the categories.
     */
    public List<Category> getCategories() {
        if(categories == null) {
           categories = new ArrayList<Category>();
        }
        return categories;
    }

    /**
     * Sets the categories.
     * @param categories the categories to set.
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
