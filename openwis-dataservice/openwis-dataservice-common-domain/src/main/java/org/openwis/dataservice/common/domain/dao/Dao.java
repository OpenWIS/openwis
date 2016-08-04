package org.openwis.dataservice.common.domain.dao;


/**
 * The generic dao interface. <P>
 *
 * @author  AKKA Technologies on behalf of PTC
 * @param <K> the type of the primary key.
 * @param <E> the type of the entity.
 */
public interface Dao<K, E> {

   /**
    * Persists an entity.
    * @param entity the entity to persist.
    */
   void persist(E entity);

   /**
    * Remove an entity.
    * @param entity the entity to remove.
    */
   void remove(E entity);

   /**
    * Merges an entity.
    * @param entity the entity.
    */
   void merge(E entity);

   /**
    * Finds an entity using its id.
    * @param id the id.
    * @return an entity using its id.
    */
   E findById(K id);

   /**
    * Gets an entity using its id.
    * @param id the id.
    * @return an entity using its id.
    */
   E getById(K id);

}
