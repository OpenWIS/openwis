/**
 *
 */
package org.openwis.dataservice.common.management;

/**
 * Defines a object which emits notification on its execution state. <p>
 *
 * @param <T> The type of the observable resource
 */
public interface ObservableBean<T> {

   /**
    * Defines responsible service provider can issue events capturing the evolution of the service entity.
    *
    * @param source the object on which the event initially occurred.
    * @param type an identifies the event category (type).
    * @param attachment optional variable list of objects specified as attachment to the event.
    */
   void publishEvent(String type, Object... attachment);
}
