package org.openwis.dataservice.common.visitor;

/**
 * The basic IVisitor interface.
 *
 * @param <T> the generic type
 * @author eKameleon
 */
public interface IVisitable<T extends IVisitable<T>> {

   /**
    * Accept a IVisitor object.
    *
    * @param visitor the visitor
    * @throws Exception the exception
    */
   void accept(IVisitor<T> visitor) throws Exception;

}
