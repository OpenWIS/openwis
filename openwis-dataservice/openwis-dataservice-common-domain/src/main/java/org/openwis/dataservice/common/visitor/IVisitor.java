package org.openwis.dataservice.common.visitor;

/**
 * The basic IVisitor interface.
 * To implements the Visitor pattern you can creates a concrete Visitor class who implements this interface.
 *
 * @param <T> the generic type
 */
public interface IVisitor<T extends IVisitable<T>> {

   /**
    * Visit the IVisitable object.
    *
    * @param visitable the visitable
    * @throws VisitException the visit exception
    */
   void visit(T visitable) throws VisitException;
}
