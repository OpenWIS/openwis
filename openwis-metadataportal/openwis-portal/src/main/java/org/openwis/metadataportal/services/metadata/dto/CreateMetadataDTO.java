/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Template;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CreateMetadataDTO {

    private DataPolicy dataPolicy;

    private String uuid;

    private Template template;

    private Category category;

    /**
     * Gets the dataPolicy.
     * @return the dataPolicy.
     */
    public DataPolicy getDataPolicy() {
        return dataPolicy;
    }

    /**
     * Sets the dataPolicy.
     * @param dataPolicy the dataPolicy to set.
     */
    public void setDataPolicy(DataPolicy dataPolicy) {
        this.dataPolicy = dataPolicy;
    }

    /**
     * Gets the uuid.
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     * @param uuid the uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the template.
     * @return the template.
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Sets the template.
     * @param template the template to set.
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * Gets the category.
     * @return the category.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category.
     * @param category the v to set.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
}
