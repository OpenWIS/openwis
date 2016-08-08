package org.openwis.dataservice.common.service;

import java.util.List;

/**
 * The ProductArrivalListener WebService.
 */
public interface ProductArrivalListener {

   /**
    * On product arrival.
    *
    * @param productDate the product date
    * @param urns the product urns
    */
   void onProductArrival(String productDate, List<String> urns);

}
