/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.styleSheet.Stylesheet;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class InsertMetadataFormDTO {

    private List<Stylesheet> stylesheet;

    private List<Category> categories;

    /**
     * Gets the stylesheet.
     * @return the stylesheet.
     */
    public List<Stylesheet> getStyleSheet() {
        if (stylesheet == null) {
           stylesheet = new ArrayList<Stylesheet>();
        }
        return stylesheet;
    }

    /**
     * Sets the stylesheet.
     * @param stylesheet the stylesheet to set.
     */
    public void setStyleSheet(List<Stylesheet> stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * Gets the categories.
     * @return the categories.
     */
    public List<Category> getCategories() {
        if (categories == null) {
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
