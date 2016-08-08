/**
 *
 */
package org.openwis.dataservice.common.management;

import java.util.List;

/**
 * Defines a object which allows to modify its execution status. <p>
 *
 * @param <T> The type of the manageable resource
 */
public interface ManageableBean<T> {

   /**
    * Description goes here.
    */
   void enable();

   /**
    * Description goes here.
    * @param attributes
    */
   void modify(List<Object> attributes);

   /**
    * Description goes here.
    */
   void disable();

   /**
    * Description goes here.
    */
   void shutdown();
}
