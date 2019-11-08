/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Template;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CreateMetadataFormDTO {

    private List<DataPolicy> dataPolicies;

    private List<Template> templates;

    private List<Category> categories;

    /**
     * Gets the dataPolicies.
     * @return the dataPolicies.
     */
    public List<DataPolicy> getDataPolicies() {
        if (dataPolicies == null) {
            dataPolicies = new ArrayList<DataPolicy>();
        }
        return dataPolicies;
    }

    /**
     * Sets the dataPolicies.
     * @param dataPolicies the dataPolicies to set.
     */
    public void setDataPolicies(List<DataPolicy> dataPolicies) {
        this.dataPolicies = dataPolicies;
    }

    /**
     * Gets the templates.
     * @return the templates.
     */
    public List<Template> getTemplates() {
        if (templates == null) {
            templates = new ArrayList<Template>();
        }
        return templates;
    }

    /**
     * Sets the templates.
     * @param templates the templates to set.
     */
    public void setTemplates(List<Template> templates) {
        this.templates = templates;
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
