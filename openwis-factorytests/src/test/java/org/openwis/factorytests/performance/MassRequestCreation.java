package org.openwis.factorytests.performance;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.openwis.dataservice.AdHoc;
import org.openwis.dataservice.ClassOfService;
import org.openwis.dataservice.DisseminationZipMode;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.Parameter;
import org.openwis.dataservice.RequestService;
import org.openwis.dataservice.RequestService_Service;
import org.openwis.dataservice.ShoppingCartDissemination;
import org.openwis.dataservice.Value;
import org.openwis.management.utils.ServiceProviderUtil;

public class MassRequestCreation {

   public void createRequests() throws Exception {
      //String wsdl = "http://srv-openwis-vm2-1:8180/openwis-dataservice-openwis-dataservice-server-ejb-1.0-SNAPSHOT/RequestService?wsdl";
      String wsdl = "http://localhost:8180/openwis-dataservice-openwis-dataservice-server-ejb-1.0-SNAPSHOT/RequestService?wsdl";
      RequestService_Service service = new RequestService_Service(new URL(wsdl));
      RequestService requestService = service.getRequestServicePort();
      ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) requestService, wsdl);

      AdHoc adHoc = new AdHoc();
      adHoc.setEmail("myadmin@akka.eu");
      adHoc.setExtractMode(ExtractMode.GLOBAL);
      Parameter p = new Parameter();
      p.setCode("parameter.product.id");
      Value v = new Value();
      p.getValues().add(v);
      adHoc.getParameters().add(p);
      ShoppingCartDissemination diss = new ShoppingCartDissemination();
      diss.setZipMode(DisseminationZipMode.ZIPPED);
      adHoc.setPrimaryDissemination(diss);
      adHoc.setUser("myadmin");
      v.setValue("119883");
      
      for (int i=0; i<1000; i++) {
         if (i % 2 == 0) {
            adHoc.setClassOfService(ClassOfService.GOLD);
         } else {
            adHoc.setClassOfService(ClassOfService.BRONZE);
         }
         Long id = requestService.createRequest(adHoc, "urn:x-wmo:md:int.wmo.wis::SMVF11BIRK");
         System.out.println("Created request " + id);
         Thread.sleep(200);
      }
      
   }

   public static void main(String[] args) {
      try {
         new MassRequestCreation().createRequests();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
