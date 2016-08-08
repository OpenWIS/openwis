package org.openwis.dataservice.dissemination;

import org.openwis.dataservice.util.DisseminationRequestInfo;

public interface DisseminationDelegate {

   
   void processMessage(DisseminationRequestInfo dissRequestInfo);
   
   void processJobs();
}
