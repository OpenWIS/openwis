/**
 *
 */
package org.openwis.metadataportal.kernel.category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.SerialFactory;

import org.jdom.Element;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.metadata.DeletedMetadataManager;
import org.openwis.metadataportal.model.category.Category;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class CategoryManager extends AbstractManager {

   /**
    * Comment for <code>ID</code>
    * @member: ID
    */
   private static final String ID = "id";

   /**
    * Comment for <code>NAME</code>
    * @member: NAME
    */
   private static final String NAME = "name";

   /** The Constant DRAFT_CATEGORY_NAME. */
   public static final String DRAFT_CATEGORY_NAME = "draft";

   //----------------------------------------------------------------------- Constructors.

   /**
    * Default constructor.
    * Builds a CategoryManager.
    * @param dbms Database connection
    */
   public CategoryManager(Dbms dbms) {
      super(dbms);
   }

   //----------------------------------------------------------------------- Public methods.

   /**
    * Get all categories.
    * @return all categories.
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public List<Category> getAllCategories() throws SQLException {
      List<Category> allCategories = new ArrayList<Category>();
      String query = "SELECT * FROM categories";
      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         allCategories.add(buildCategoryFromElement(e));
      }
      return allCategories;
   }

   /**
    * Gets a category by its name.
    * @param name the name of the category.
    * @return the category.
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public Category getCategoryByName(String name) throws SQLException {
      Category category = null;
      String query = "SELECT * FROM categories WHERE name=?";
      List<Element> records = getDbms().select(query, name).getChildren();
      if (!records.isEmpty()) {
         category = buildCategoryFromElement(records.get(0));
      }
      return category;
   }

   /**
    * Gets the draft category.
    *
    * @return the draft category
    * @throws SQLException the SQL exception
    */
   public Category getDraftCategory() throws SQLException {
      return getCategoryByName(DRAFT_CATEGORY_NAME);
   }

   /**
    * Gets a category by its id.
    * @param id the id of the category.
    * @return the category or null
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public Category getCategoryById(int id) throws SQLException {
      Category category = null;
      String query = "SELECT * FROM categories WHERE id=?";
      List<Element> records = getDbms().select(query, id).getChildren();
      if (!records.isEmpty()) {
         category = buildCategoryFromElement(records.get(0));
      }
      return category;
   }

   /**
    * Get a category for a given metadata
    * Since we only had a single category for OpenWIS metadata.
    * It differs from GeoNetwork where multiple categories are possible.
    *
    * @param metadataUrn the metadata URN
    * @return the metadata categories
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   public Category getCategoryByMetadataUrn(String metadataUrn) throws SQLException {
      Category category = null;
      String query = "SELECT Categories.id, Categories.name FROM Categories, Metadata "
            + "WHERE Metadata.category=Categories.id AND Metadata.uuid=?";

      List<Element> records = getDbms().select(query, metadataUrn).getChildren();
      if (!records.isEmpty()) {
         category = buildCategoryFromElement(records.get(0));
      }
      return category;
   }

   /**
    * Creates the category.
    * @param category the category to create.
    * @throws Exception  if an error occurs.
    */
   public void createCategory(Category category) throws Exception {
      // check category rules
      checkCategory(category);

      // Generate a new category id
      category.setId(SerialFactory.getSerial(getDbms(), "categories"));
      String insertQuery = "INSERT INTO categories(id, name) VALUES (?, ?)";
      getDbms().execute(insertQuery, category.getId(), category.getName());
   }

   /**
    * Updates the category.
    * @param category the category to update.
    * @throws Exception  if an error occurs.
    */
   public void updateCategory(Category category) throws Exception {
      // check category rules
      checkCategory(category);

      // Update the category.
      String query = "UPDATE categories SET name=? WHERE id=?";

      getDbms().execute(query, category.getName(), category.getId());
   }

   /**
    * Deletes a category.
    * @param category the category to delete.
    * @throws Exception  if an error occurs.
    */
   public void removeCategory(Category category) throws Exception {
      // Remove the category descriptions.
      String queryCatDesc = "DELETE FROM categoriesdes WHERE iddes=?";
      getDbms().execute(queryCatDesc, category.getId());

      // Clean deleted metadata
      DeletedMetadataManager deletedMetadataManager = new DeletedMetadataManager(getDbms());
      deletedMetadataManager.clean(category.getId());
      
      // FIXME we should check any metadata linket to this category...

      // Remove the category.
      String queryCat = "DELETE FROM categories WHERE id=?";
      getDbms().execute(queryCat, category.getId());
   }
   
   /**
    * Count number of metadata in the given category.
    * 
    * @param category
    * @return the number of metadata
    * @throws SQLException
    */
   public int countMdInCategory(Category category) throws SQLException {
      // Check if category contains metadata
      String queryCatMd = "SELECT count(Metadata.id) as countMd from Categories " +
      "LEFT JOIN Metadata ON Metadata.category=Categories.id " +
      "WHERE Categories.id=?";

      @SuppressWarnings("unchecked")
      List<Element> records = getDbms().select(queryCatMd, category.getId()).getChildren();
      for (Element e : records) {
         return new Integer(e.getChildText("countmd"));
      }
      
      return 0;
   }

   /**
    * Get all categories and metadata count for OAI-PMH ListSets.
    *
    * @return
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   public Map<Category, Integer> getAllCategoriesAndMetadataCount() throws SQLException {
      Map<Category, Integer> categs = new HashMap<Category, Integer>();
      String query = "SELECT count(Metadata.id) as countMd, Categories.* from Categories " +
      		"LEFT JOIN Metadata ON Metadata.category=Categories.id GROUP BY Categories.id, Categories.name";

      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         categs.put(buildCategoryFromElement(e), new Integer(e.getChildText("countmd")));
      }
      return categs;
   }

   //----------------------------------------------------------------------- Private methods.

   /**
    * Builds a category from a JDOM element.
    * @param record the element.
    * @return the category.
    */
   private static Category buildCategoryFromElement(Element record) {
      Category category = new Category();
      category.setId(Integer.parseInt(record.getChildText(ID)));
      category.setName(record.getChildText(NAME));
      return category;
   }

   /**
    * Description goes here.
    * @param category The category to check.
    * @throws Exception if an error occurs.
    */
   private void checkCategory(Category category) throws Exception {
      Category categoryDb = getCategoryByName(category.getName());
      if (categoryDb != null) {
         if (category.getId() == null || category.getId() != categoryDb.getId()) {
            //Throw exception
            throw new CategoryAlreadyExistsException(category.getName());
         }
      }
   }

}
