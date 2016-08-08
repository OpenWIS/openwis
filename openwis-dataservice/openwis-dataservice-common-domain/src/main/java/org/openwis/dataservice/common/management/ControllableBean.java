/**
 *
 */
package org.openwis.dataservice.common.management;

import java.util.List;
import java.util.Map;

/**
 * Defines a object which allows to modify its runtime information data. <p>
 *
 * @param <T> The type of the controllable resource
 */
public interface ControllableBean<T> {

   /**
    * Description goes here.
    *
    * @param configurationName
    * @param arguments
    */
   void create(String configurationName, Map<String, Object> arguments);

   /**
    * Description goes here.
    *
    * @param dynamicName
    * @param attributes
    */
   void modify(String dynamicName, List<Object> attributes);

   /**
    * Description goes here.
    *
    * @param dynamicName
    */
   void delete(String dynamicName);

}
