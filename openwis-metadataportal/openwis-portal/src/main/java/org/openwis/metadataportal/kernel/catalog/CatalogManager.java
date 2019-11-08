package org.openwis.metadataportal.kernel.catalog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jeeves.resources.dbms.Dbms;

import org.jdom.Element;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.model.catalog.Catalog;
import org.openwis.metadataportal.model.catalog.CatalogItem;

/**
 * Catalog Manager. <P>
 * 
 */
public class CatalogManager extends AbstractManager {

	/**
	 * Default constructor.
	 * Builds a CatalogManager.
	 * @param dbms The dbms.
	 */
	public CatalogManager(Dbms dbms) {
		super(dbms);
	}

	/**
	 * Get all catalog.
	 * 
	 * @return all catalog.
	 * @throws SQLException
	 *             if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public List<CatalogItem> getAllCatalogItems() throws SQLException {

		List<CatalogItem> all = new ArrayList<CatalogItem>();
      // TODO To Implement
		return all;
	}

	/**
	 * Get all catalog items.
	 * @param searchCriteriaWrapper The wrapper.
	 * @return search results.
	 * @throws SQLException if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public SearchResultWrapper<CatalogItem> getAllCatalogItems(
			SearchCriteriaWrapper<?, String> searchCriteriaWrapper)
			throws SQLException {

		SearchResultWrapper<CatalogItem> wrapper = new SearchResultWrapper<CatalogItem>();
		List<CatalogItem> allCategories = new ArrayList<CatalogItem>();
		 
		// TODO To Implement
		wrapper.setTotal(150);
		wrapper.setRows(allCategories);

		return wrapper;
	}

	/**
	 * récuperation des infos 'catalog size' et 'nbMetadata'
	 * @return Catalog catalog
	 * @throws SQLException
	 */
	public Catalog getCatalog() throws SQLException {
		String catalogSize = "";
		String query = "SELECT pg_size_pretty(pg_total_relation_size('metadata')) as size";
		List<Element> records = getDbms().select(query).getChildren();
		for (Element e : records) {
			catalogSize = e.getChildText("size");
		}
		List<Element> recs = getDbms().select("select count(*) as count from metadata").getChildren();
		int nbMetadata = 0;
		for (Element e : recs) {
			nbMetadata = new Integer(e.getChildText("count"));
		}

		return new Catalog(catalogSize, nbMetadata);
	}

	/**
	 * Builds a catalogItem from a JDOM element.
	 * 
	 * @param record
	 *            the element.
	 * @return the catalog item.
	 */
	private static CatalogItem buildCatalogItemFromElement(Element record) {
		CatalogItem item = new CatalogItem();
	    // TODO To Implement
//		item.setDate(date);
//		item.setNbRecords(nbRecords);
//		item.setSize(size);
//		item.setSource(source);
		return item;
	}

}
