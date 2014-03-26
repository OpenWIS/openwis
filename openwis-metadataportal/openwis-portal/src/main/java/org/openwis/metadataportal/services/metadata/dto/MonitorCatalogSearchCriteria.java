/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

/**
 * Short Description goes here.
 * <P>
 * Explanation goes here.
 * <P>
 * 
 */
public class MonitorCatalogSearchCriteria {

	/** The full text. */
	private String fullText;

	/** The owner. */
	private String owner;

	/** The search field. */
	private String searchField;

   /** The category value */
   private String category;

	/**
	 * Default constructor. Builds a MonitorCatalogSearchCriteria.
	 *
	 * @param fullText the full text
	 */
	public MonitorCatalogSearchCriteria(String fullText) {
		super();
		this.fullText = fullText;
	}

	/**
	 * Default constructor. Builds a MonitorCatalogSearchCriteria.
	 *
	 * @param fullText the full text
	 * @param owner the owner
	 */
	public MonitorCatalogSearchCriteria(String fullText, String owner) {
		super();
		this.fullText = fullText;
		this.owner = owner;
	}

	/**
	 * Gets the fullText.
	 * 
	 * @return the fullText.
	 */
	public String getFullText() {
		return fullText;
	}

	/**
	 * Sets the fullText.
	 * 
	 * @param fullText
	 *            the fullText to set.
	 */
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	/**
	 * Gets the owner.
	 * 
	 * @return the owner.
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 * 
	 * @param owner
	 *            the owner to set.
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * Sets the search field.
	 *
	 * @param searchField the new search field
	 */
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	
	/**
	 * Gets the search field.
	 *
	 * @return the search field
	 */
	public String getSearchField() {
		return searchField;
	}
	
    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
    	return category;
    }

    /**
     * Sets the category.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
    	this.category = category;
    }

}
