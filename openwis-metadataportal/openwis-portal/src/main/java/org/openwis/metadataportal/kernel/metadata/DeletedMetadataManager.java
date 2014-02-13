/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.SerialFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;

/**
 * Deleted Metadata are used to track the metadata that have been removed from a
 * category, to allow removal propagation in OAI-PMH protocol.
 */
public class DeletedMetadataManager implements IDeletedMetadataManager {

   private static final String UUID = "uuid";

   private static final String SCHEMA = "schemaid";

   private static final String CATEGORY = "category";
   
   private static final String DELETION_DATE = "deletiondate";

   /**
    * Comment for <code>dbms</code>
    */
   private Dbms dbms;

   /**
    * Default constructor.
    * Builds a DeletedMetadataManager.
    */
   public DeletedMetadataManager(Dbms dbms) {
      super();
      this.dbms = dbms;
   }

   /**
    * Gets the dbms.
    * @return the dbms.
    */
   public Dbms getDbms() {
      return dbms;
   }

   /**
    * Sets the dbms.
    * @param dbms the dbms to set.
    */
   public void setDbms(Dbms dbms) {
      this.dbms = dbms;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IDeletedMetadataManager#createDeletedMetadataFromMetadataUrn(java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public DeletedMetadata createDeletedMetadataFromMetadataUrn(String urn)
         throws SQLException {
      String query = "SELECT uuid, schemaId, category FROM Metadata WHERE uuid=?";
      List<Element> records = getDbms().select(query, StringEscapeUtils.escapeSql(urn)).getChildren();
      if (!records.isEmpty()) {
         // For now only get the first child
         // as far as we have only one category per metadata in OpenWIS
         return buildDeletedMetadataFromElement(records.get(0));
      }
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IDeletedMetadataManager#insertDeletedMetadata(org.openwis.metadataportal.model.metadata.DeletedMetadata)
    */
   @Override
   public void insertDeletedMetadata(DeletedMetadata dm) throws SQLException {
      // Clean any old remaining deleted metadata before adding a new one
      clean(dm.getUrn(), dm.getCategory());
      String insertQuery = "INSERT INTO DeletedMetadata (id, uuid, schemaId, category) VALUES (?,?,?,?)";
      // Generate a new deleted metadata id
      dm.setId(SerialFactory.getSerial(getDbms(), "DeletedMetadata"));
      getDbms().execute(insertQuery, dm.getId(), dm.getUrn(), dm.getSchema(), dm.getCategory());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IDeletedMetadataManager#getDeletedMetadataUrns(java.lang.String, java.lang.String, org.openwis.metadataportal.model.category.Category)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Collection<String> getDeletedMetadataUrns(String from, String until, Category category) throws SQLException {
      StringBuffer query = new StringBuffer();
      query.append("SELECT DeletedMetadata.uuid FROM DeletedMetadata");
      
      if (StringUtils.isNotBlank(from) || StringUtils.isNotBlank(until) || category.getId()!= null) {
         query.append(" WHERE ");
         List<String> list = new ArrayList<String>();

         if (StringUtils.isNotBlank(from)) {
            list.add("deletionDate >= TIMESTAMP '"+from+"'" );
         }
         
         if (StringUtils.isNotBlank(until)) {
            list.add("deletionDate <= TIMESTAMP '"+until+"'");
         }
         
         if (category.getId() != null) {
            list.add("category = "+category.getId());
         }
         
         query.append(StringUtils.join(list.toArray(), " AND "));
      }
      
      List<Element> urnsEl = getDbms().select(query.toString()).getChildren();
      
      return CollectionUtils.collect(urnsEl, new Transformer() {
         
         @Override
         public Object transform(Object arg0) {
            return (String) ((Element) arg0).getChildText(UUID);
         }
      });
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IDeletedMetadataManager#getDeletedMetadataByUrn(java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public DeletedMetadata getDeletedMetadataByUrn(String urn, Category category) throws SQLException {
      String query = "SELECT uuid, schemaId, category, deletionDate FROM DeletedMetadata WHERE uuid=? AND category=?";
      List<Element> records = (List<Element>) getDbms().select(query, urn, category.getId()).getChildren();
      if (!records.isEmpty()) {
         return buildDeletedMetadataFromElement(records.get(0));
      }
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IDeletedMetadataManager#clean(java.lang.String)
    */
   @Override
   public void clean(String urn, Integer categoryId) throws SQLException {
      String query = "DELETE FROM DeletedMetadata WHERE uuid=? AND category=?";
      getDbms().execute(query, urn, categoryId);
   }
   
   @Override
   public void clean(Integer categoryId) throws SQLException {
      String query = "DELETE FROM DeletedMetadata WHERE category=?";
      getDbms().execute(query, categoryId);
   }
   
   /**
    * Build a deletedMetadata from a JDOM Element
    * @param record a JDOM element 
    * @return a DeletedMetadata
    */
   private static DeletedMetadata buildDeletedMetadataFromElement(Element record) {
      DeletedMetadata deletedMetadata = new DeletedMetadata();
      deletedMetadata.setUrn(record.getChildText(UUID));
      deletedMetadata.setSchema(record.getChildText(SCHEMA));
      deletedMetadata.setCategory(Integer.parseInt(record.getChildText(CATEGORY)));
      deletedMetadata.setDeletionDate(record.getChildText(DELETION_DATE));
      return deletedMetadata;
   }
}
