/*
 *
 */
package org.openwis.datasource.server.service.impl;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.openwis.dataservice.common.service.ProductArrivalListener;

/**
 * The Class ProductArrivalListenerImpl.
 */
@WebService(targetNamespace = "http://dataservice.openwis.org/", name = "ProductArrivalListener", portName = "ProductArrivalListenerPort", serviceName = "ProductArrivalListener")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Local(ProductArrivalListener.class)
@Stateless(name = "ProductArrivalListener")
public class ProductArrivalListenerImpl extends ProductArrivalHandler implements
      ProductArrivalListener {

   /**
    * Instantiates a new blacklist service implementation.
    */
   public ProductArrivalListenerImpl() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProductArrivalListener#onProductArrival(java.lang.String, java.util.Set)
    */
   @Override
   public void onProductArrival(@WebParam(name = "productDate") String productDate,
         @WebParam(name = "urns") List<String> urns) {
      sendProductArrival(productDate, urns);
   }

}
