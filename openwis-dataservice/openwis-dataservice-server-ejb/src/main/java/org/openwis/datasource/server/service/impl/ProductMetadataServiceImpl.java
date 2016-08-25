/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openwis.dataservice.common.domain.entity.cache.PatternMetadataMapping;
import org.openwis.dataservice.common.domain.entity.enumeration.ProductMetadataColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Request;
import org.openwis.dataservice.common.exception.CannotDeleteAllProductMetadataException;
import org.openwis.dataservice.common.exception.CannotDeleteProductMetadataException;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation of class Product metadata service
 */
@Remote(ProductMetadataService.class)
@Stateless(name = "ProductMetadataService")
@WebService(targetNamespace = "http://dataservice.openwis.org/", name = "ProductMetadataService", portName = "ProductMetadataServicePort", serviceName = "ProductMetadataService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class ProductMetadataServiceImpl implements ProductMetadataService {

   /** The Constant STOP_GAP_GTS_CATEGORY. */
   private static final String STOP_GAP_GTS_CATEGORY = "WMO Additional";

   /** The Constant STOP_GAP_DATA_POLICY. */
   private static final String STOP_GAP_DATA_POLICY = "additional-default";

   /** The Constant STOP_GAP_PROCESS. */
   private static final String STOP_GAP_PROCESS = "LOCAL";

   /** The Constant STOP_GAP_TITLE_TEMPLATE. */
   private static final String STOP_GAP_TITLE_TEMPLATE = "[Draft] for {0}";

   /** The Constant STOP_GAP_URN_TEMPLATE. */
   private static final String STOP_GAP_URN_TEMPLATE = "urn:x-wmo:md:int.wmo.wis::{0}";

   /** The logger */
   private static Logger logger = LoggerFactory.getLogger(ProductMetadataServiceImpl.class);

   /**
    * The entity manager.
    */
   @PersistenceContext
   private EntityManager entityManager;

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#getProductMetadataByUrn(java.lang.String)
    */
   @Override
   public ProductMetadata getProductMetadataByUrn(@WebParam(name = "productMetadataUrn") String urn) {
      ProductMetadata result;
      logger.debug("Get a ProductMetaData by URN: {}", urn);
      if (urn == null) {
         throw new IllegalArgumentException("Product Metadata urn must not be null!");
      }
      Query query = entityManager.createNamedQuery("ProductMetadata.FindByUrn").setParameter("urn",
            urn);
      try {
         result = (ProductMetadata) query.getSingleResult();
      } catch (NoResultException e) {
         result = null;
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#getProductMetadataById(java.lang.Long)
    */
   @Override
   public ProductMetadata getProductMetadataById(@WebParam(name = "id") Long id) {
      ProductMetadata result = null;
      logger.debug("Get a ProductMetaData by id: {}", id);
      if (id != null) {
         result = entityManager.find(ProductMetadata.class, id);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#
    * getAllProductsMetadata(int, int, org.openwis.dataservice.common.domain.entity.enumeration.ProductMetadataColumn,
    * org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<ProductMetadata> getAllProductsMetadata(
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") ProductMetadataColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {
      List<ProductMetadata> result;
      logger.debug("Get All ProductMetaData");
      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0!");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }

      // Default column is URN
      ProductMetadataColumn col;
      if (column != null) {
         col = column;
      } else {
         col = ProductMetadataColumn.URN;
      }

      // Default direction is Descending
      SortDirection dir;
      if (sortDirection != null) {
         dir = sortDirection;
      } else {
         dir = SortDirection.ASC;
      }

      // Create query
      String q = MessageFormat.format("SELECT pm FROM ProductMetadata pm ORDER BY pm.{0} {1}",
            col.getAttribute(), dir);
      Query query = entityManager.createQuery(q);
      query.setFirstResult(firstResult);
      query.setMaxResults(maxResults);

      // Process query
      try {
         result = query.getResultList();
      } catch (NoResultException e) {
         result = Collections.emptyList();
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#getLastStopGapMetadata(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<ProductMetadata> getLastStopGapMetadata(@WebParam(name = "since") String since) {
      List<ProductMetadata> result = null;
      Query query = null;
      try {
         Date date = DateTimeUtils.parseDateTime(since);
         query = entityManager.createNamedQuery("ProductMetadata.getLastStopGap");
         query.setParameter("since", date);
      } catch (Exception e) { // ParseException or NullPointerException
         query = entityManager.createNamedQuery("ProductMetadata.getAllStopGap");
      }

      result = query.getResultList();
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#createStopGapMetadata(java.lang.String, java.lang.String, java.lang.String, int)
    */
   @Override
   public Long createStopGapMetadata(@WebParam(name = "ttaaii") String ttaaii,
         @WebParam(name = "originator") String originator, @WebParam(name = "priority") int priority) {
      ProductMetadata pm = new ProductMetadata();
      pm.setUrn(MessageFormat.format(STOP_GAP_URN_TEMPLATE, ttaaii));
      pm.setOriginator(originator);
      pm.setPriority(priority);
      pm.setTitle(MessageFormat.format(STOP_GAP_TITLE_TEMPLATE, ttaaii));
      pm.setProcess(STOP_GAP_PROCESS);
      pm.setDataPolicy(STOP_GAP_DATA_POLICY);
      pm.setGtsCategory(STOP_GAP_GTS_CATEGORY);
      pm.setStopGap(true);

      pm.setFed(Boolean.FALSE);
      pm.setIngested(Boolean.FALSE);
      pm.setLocalDataSource("");

      return createProductMetadata(pm);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#getProductsMetadataCount()
    */
   @Override
   public int getProductsMetadataCount() {
      Query query = entityManager.createNamedQuery("ProductMetadata.count");
      Number result = (Number) query.getSingleResult();
      return result.intValue();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#
    * getProductsMetadataByUrns(java.util.List, int, int,
    * org.openwis.dataservice.common.domain.entity.enumeration.ProductMetadataColumn,
    * org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<ProductMetadata> getProductsMetadataByUrns(
         @WebParam(name = "urns") List<String> urns,
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") ProductMetadataColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {
      List<ProductMetadata> result;
      logger.debug("Get ProductsMetaData by URNs: {}", urns);
      // Check arguments
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0 !");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0 !");
      }
      // Default column is URN
      ProductMetadataColumn col;
      if (column != null) {
         col = column;
      } else {
         col = ProductMetadataColumn.URN;
      }

      // Default direction is Descending
      SortDirection dir;
      if (sortDirection != null) {
         dir = sortDirection;
      } else {
         dir = SortDirection.ASC;
      }

      // build URNs
      if (urns == null || urns.isEmpty()) {
         result = Collections.emptyList();
      } else {
         // create URN list
         String allUrns = buildUrns(urns);

         // Create query
         String q = MessageFormat
               .format(
                     "SELECT pm FROM ProductMetadata pm WHERE lower(pm.urn) IN ({0}) ORDER BY pm.{1} {2}",
                     allUrns, col.getAttribute(), dir);
         Query query = entityManager.createQuery(q);
         query.setFirstResult(firstResult);
         query.setMaxResults(maxResults);

         // Process query
         try {
            result = query.getResultList();
         } catch (NoResultException e) {
            result = Collections.emptyList();
         }
      }
      return result;
   }

   /**
    * Description goes here.
    *
    * @param urns the urns
    * @return the string
    */
   private String buildUrns(List<String> urns) {
      StringBuffer allUrns = new StringBuffer();
      boolean isFirst = true;
      for (String urn : urns) {
         if (StringUtils.isNotEmpty(urn)) {
            if (isFirst) {
               isFirst = false;
            } else {
               allUrns.append(", ");
            }
            allUrns.append('\'');
            allUrns.append(StringEscapeUtils.escapeSql(urn.toLowerCase()));
            allUrns.append('\'');
         }
      }
      return allUrns.toString();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#
    * createProductMetadata(org.openwis.dataservice.common.domain.entity.request.ProductMetadata)
    */
   @Override
   public Long createProductMetadata(
         @WebParam(name = "productMetadata") ProductMetadata productMetadata) {
      if (productMetadata == null) {
         throw new IllegalArgumentException("ProductMetadata must not being null !");
      }
      // Check URN
      String urn = productMetadata.getUrn();
      if (StringUtils.isEmpty(urn)) {
         throw new IllegalArgumentException("URN must not being empty !");
      }

      // Check Unicity
      if (getProductMetadataByUrn(urn) != null) {
         throw new IllegalArgumentException("A product with the same URN already exists");
      }

      if (productMetadata.getCreationDate() == null) {
         productMetadata.setCreationDate(DateTimeUtils.getUTCTime());
      }

      logger.info("Creating a ProductMetaData: " + urn);

      // Creation
      entityManager.persist(productMetadata);
      // Get pattern
      String pmPattern;
      if (productMetadata.getOverridenFncPattern() != null) {
         pmPattern = productMetadata.getOverridenFncPattern();
      } else {
         pmPattern = productMetadata.getFncPattern();
      }

      // Handle pattern mapping
      PatternMetadataMapping pmm;
      if (StringUtils.isNotBlank(pmPattern)) {
         // Create
         pmm = new PatternMetadataMapping();
         pmm.setPattern(pmPattern);
         pmm.setProductMetadata(productMetadata);

         entityManager.persist(pmm);
      }

      entityManager.flush();
      return productMetadata.getId();
   }

   /**
    * Gets the all pattern metadata mapping.
    *
    * @return the all pattern metadata mapping
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<PatternMetadataMapping> getAllPatternMetadataMapping() {
      Query query = entityManager.createNamedQuery("PatternMetadataMapping.all");
      List<PatternMetadataMapping> result = query.getResultList();
      if (result == null) {
         result = new ArrayList<PatternMetadataMapping>();
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @throws CannotDeleteProductMetadataException
    * @see org.openwis.dataservice.common.service.ProductMetadataService#deleteProductMetadata(java.lang.Long)
    */
   @Override
   public void deleteProductMetadata(@WebParam(name = "productMetadataId") Long id) {
      ProductMetadata productMetadata = entityManager.find(ProductMetadata.class, id);

      if (productMetadata != null) {
         logger.info("Removing a ProductMetaData: " + productMetadata.getUrn());
         PatternMetadataMapping pmm = retrievePatternMetadataMapping(productMetadata);
         if (pmm != null) {
            entityManager.remove(pmm);
         }
         entityManager.remove(productMetadata);
      }
   }

   /**
    * Retrieve pattern metadata mapping.
    *
    * @param productMetadata the product metadata
    * @return the pattern metadata mapping
    */
   private PatternMetadataMapping retrievePatternMetadataMapping(ProductMetadata productMetadata) {
      PatternMetadataMapping result = null;
      if (productMetadata != null) {
         Query query = entityManager.createNamedQuery("PatternMetadataMapping.byProductMetadata");
         query.setParameter("pm", productMetadata);
         try {
            result = (PatternMetadataMapping) query.getSingleResult();
         } catch (NoResultException e) {
            result = null;
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductMetadataService#
    * updateProductMetadata(org.openwis.dataservice.common.domain.entity.request.ProductMetadata)
    */
   @Override
   public void updateProductMetadata(
         @WebParam(name = "productMetadata") ProductMetadata productMetadata) {
      logger.info("Updating a ProductMetaData: " + productMetadata.getUrn());
      entityManager.merge(productMetadata);
      // Get pattern
      String pmPattern;
      if (productMetadata.getOverridenFncPattern() != null) {
         pmPattern = productMetadata.getOverridenFncPattern();
      } else {
         pmPattern = productMetadata.getFncPattern();
      }

      // Handle pattern mapping
      PatternMetadataMapping pmm = retrievePatternMetadataMapping(productMetadata);
      if (pmm == null && StringUtils.isNotBlank(pmPattern)) {
         // Create
         pmm = new PatternMetadataMapping();
         pmm.setPattern(pmPattern);
         pmm.setProductMetadata(productMetadata);

         entityManager.persist(pmm);
      } else if (pmm != null && !pmm.getPattern().equals(pmPattern)) {
         if (pmPattern == null) {
            entityManager.remove(pmm);
         } else {
            // need update
            pmm.setPattern(pmPattern);
            entityManager.merge(pmm);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @throws CannotDeleteProductMetadataException
    * @see org.openwis.dataservice.common.service.ProductMetadataService#deleteProductMetadataByURN(java.lang.String)
    */
   @Override
   public void deleteProductMetadataByURN(@WebParam(name = "productMetadataUrn") String urn)
         throws CannotDeleteProductMetadataException {
      ProductMetadata productMetadata = getProductMetadataByUrn(urn);
      if (productMetadata != null) {

         // Check delete ability
         Query query = entityManager.createNamedQuery("Request.byProductMetadata");
         query.setParameter("pm", productMetadata);

         @SuppressWarnings("unchecked")
         List<Request> lst = query.getResultList();
         if (lst.isEmpty()) {
            deleteProductMetadata(productMetadata.getId());
         } else {
            throw new CannotDeleteProductMetadataException(urn);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteProductMetadatasWithURN(List<String> urns)
         throws CannotDeleteAllProductMetadataException {
      List<String> metadataThatCouldNotBeDeleted = null;

      for (String urn : urns) {
         try {
            deleteProductMetadataByURN(urn);
         } catch (CannotDeleteProductMetadataException e) {
            // Record metadata records that cannot be deleted
            if (metadataThatCouldNotBeDeleted == null) {
               metadataThatCouldNotBeDeleted = new ArrayList<String>();
            }
            metadataThatCouldNotBeDeleted.add(urn);
         }
      }

      // The operation did not fully complete, so raise an exception providing the metadata URNs
      // that could not be deleted.
      if (metadataThatCouldNotBeDeleted != null)
      {
         throw new CannotDeleteAllProductMetadataException(metadataThatCouldNotBeDeleted.toArray(new String[metadataThatCouldNotBeDeleted.size()]));
      }
   }
}
