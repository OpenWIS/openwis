/**
 *
 */
package org.openwis.dataservice.common.management;

import java.util.Map;

/**
 * Defines a object which allows to modify its configuration data. <p>
 *
 * @param <T> The type of the mutable resource
 */
public interface MutableBean<T> {

   /**
    * Description goes here.
    * @param propertyName
    * @return
    */
   Object getProperty(String propertyName);

   /**
    * Description goes here.
    * @param propertyName
    * @param propertyValue
    * @return
    */
   Object setProperty(String propertyName, Object propertyValue);

   /**
    * Description goes here.
    * @param propertyName
    * @param attributeName
    * @param attributeValue
    * @return
    */
   Object modifyProperty(String propertyName, String attributeName, Object attributeValue);

   /**
    * Description goes here.
    * @param propertyName
    * @return
    */
   Object deleteProperty(String propertyName);

   /**
    * Description goes here.
    * @param propertyName
    * @param attributes
    * @return
    */
   Object newProperty(String propertyName, Map<String, Object> attributes);
}
