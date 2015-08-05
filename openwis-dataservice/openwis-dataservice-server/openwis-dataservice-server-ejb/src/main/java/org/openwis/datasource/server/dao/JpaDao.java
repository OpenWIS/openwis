package org.openwis.datasource.server.dao;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.openwis.dataservice.common.domain.dao.Dao;

/**
 * The generic JPA implementation. <P>
 *
 * @param <K> the type of the primary key.
 * @param <E> the type of the entity.
 */
public abstract class JpaDao<K, E> implements Dao<K, E> {

   /**
    * The persistent class.
    */
   protected Class<E> entityClass;

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /**
    * Default constructor.
    * Builds a JpaDao.
    */
   @SuppressWarnings("unchecked")
   public JpaDao() {
      ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
      this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];

      /*
      Class<?> cls = getClass();
      while (!(cls.getSuperclass() == null
          || cls.getSuperclass().equals(AbstractExpression.class))) {
        cls = cls.getSuperclass();
      }

      if (cls.getSuperclass() == null)
        throw new RuntimeException("Unexpected exception occurred.");

      this.entityClass = ((Class) ((ParameterizedType)
        cls.getGenericSuperclass()).getActualTypeArguments()[0]);
      */
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.dao.Dao#persist(java.lang.Object)
    */
   @Override
   public void persist(E entity) {
      entityManager.persist(entity);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.dao.Dao#remove(java.lang.Object)
    */
   @Override
   public void remove(E entity) {
      entityManager.remove(entity);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.dao.Dao#merge(java.lang.Object)
    */
   @Override
   public void merge(E entity) {
      entityManager.merge(entity);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.dao.Dao#findById(java.lang.Object)
    */
   @Override
   public E findById(K id) {
      return entityManager.find(entityClass, id);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.dao.Dao#getById(java.lang.Object)
    */
   @Override
   public E getById(K id) {
      E entity = entityManager.find(entityClass, id);
      if (entity == null) {
         throw new EntityNotFoundException("Entity " + entityClass.getName() + " with id " + id
               + " not found");
      }
      return entity;
   }
}
