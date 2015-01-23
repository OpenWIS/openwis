package org.openwis.dataservice.common.service;

import java.util.List;

import org.openwis.dataservice.common.domain.entity.cache.PatternMetadataMapping;
import org.openwis.dataservice.common.domain.entity.enumeration.ProductMetadataColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.exception.CannotDeleteAllProductMetadataException;
import org.openwis.dataservice.common.exception.CannotDeleteProductMetadataException;

/**
 * The Product Metadata service. <P>
 */
public interface ProductMetadataService {
   /**
    * @member: DIRECTION
    */
   public static final String DIRECTION_PARAMETER = "direction";

   /**
    * @member: COLUMN2
    */
   public static final String COLUMN_PARAMETER = "column";

   /**
    * Gets the product metadata by its URN.
    * @param urn the URN of the product metadata.
    * @return the product metadata having given URN, <code>null</code> otherwise.
    */
   ProductMetadata getProductMetadataByUrn(String urn);

   /**
    * Gets the product metadata by id.
    *
    * @param id the id
    * @return the product metadata by id
    */
   ProductMetadata getProductMetadataById(Long id);

   /**
    * Gets the products metadata count.
    *
    * @return the products metadata count
    */
   int getProductsMetadataCount();

   /**
    * Gets the all products metadata.
    *
    * @param firstResult the first result
    * @param maxResults the max results
    * @param column the column to sort
    * @param sortDirection the sort direction
    * @return the all products metadata
    */
   List<ProductMetadata> getAllProductsMetadata(int firstResult, int maxResults,
         ProductMetadataColumn column, SortDirection sortDirection);

   /**
    * Gets the last stop gap metadata.
    *
    * @param since the since
    * @return the last stop gap metadata
    */
   List<ProductMetadata> getLastStopGapMetadata(String since);

   /**
    * Creates a stop gap metadata.
    *
    * @param TTAAII the TTAAII
    * @param originator the originator
    * @param priority the priority
    * @return the created metadata id
    */
   Long createStopGapMetadata(String ttaaii, String originator, int priority);

   /**
    * Gets the products metadata matching URNs.
    *
    * @param urns the URNs
    * if <code>null</code> or empty, return an empty list
    * @param firstResult the first result
    * @param maxResults the max results
    * @param column the column to sort
    * @param sortDirection the sort direction
    * @return the all products metadata
    */
   List<ProductMetadata> getProductsMetadataByUrns(List<String> urns, int firstResult,
         int maxResults, ProductMetadataColumn column, SortDirection sortDirection);

   /**
    * Creates a product metadata.
    *
    * @param productMetadata the product metadata to persist.
    * @return the identifier
    */
   Long createProductMetadata(ProductMetadata productMetadata);

   /**
    * Update a product metadata.
    *
    * @param productMetadata the product metadata
    */
   void updateProductMetadata(ProductMetadata productMetadata);

   /**
    * Delete product metadata.
    *
    * @param id the id
    */
   void deleteProductMetadata(Long id);

   /**
    * Delete the product metadata identified by the given urn.
    *
    * @param urn the URN
    * @throws CannotDeleteProductMetadataException if cannot delete product metadata
    */
   void deleteProductMetadataByURN(String urn) throws CannotDeleteProductMetadataException;

   /**
    * Delete all product metadata identified by the given urn.
    *
    * @param urn
    *       The list of URNs.
    * @throws CannotDeleteAllProductMetadataException
    *       Thrown if one or more metadata records could not be deleted.
    */
   void deleteProductMetadatasWithURN(List<String> urn) throws CannotDeleteAllProductMetadataException;

   /**
    * Gets the all pattern metadata mapping.
    *
    * @return the all pattern metadata mapping
    */
   List<PatternMetadataMapping> getAllPatternMetadataMapping();
}
