/**
 * 
 */
package org.openwis.metadataportal.services.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.common.utils.Utils;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.metadataportal.services.request.dto.follow.AdhocDTO;
import org.openwis.metadataportal.services.request.dto.follow.ProcessedRequestDTO;
import org.openwis.metadataportal.services.request.dto.follow.StatusDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class MockFollowAdhocDTO {

   /**
    * Default constructor.
    * Builds a MockFollowAdhocDTO.
    */
   private MockFollowAdhocDTO() {
   }

   /**
    * Description goes here.
    * @return a followAdhocDTO
    */
   public static List<AdhocDTO> getFollowAdhocDTO() {
      SimpleDateFormat sdf = new SimpleDateFormat(
            OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DATETIME_FORMAT));

      List<AdhocDTO> dtos = new ArrayList<AdhocDTO>();

      
      for(long i = 0; i < 5; i++) {
         AdhocDTO firstAdhoc = new AdhocDTO();
         firstAdhoc.setRequestID(Utils.formatRequestID(i));
         firstAdhoc.setProductMetadataURN("urn-wmo-meteo:" + firstAdhoc.getRequestID());
         firstAdhoc.setProductMetadataTitle("Warnings == " + i);
         firstAdhoc.setDeployment(new Deployment("GISC-A"));
         firstAdhoc.setProcessedRequestDTO(new ProcessedRequestDTO());
         firstAdhoc.getProcessedRequestDTO().setCreationDate(sdf.format(new Date()));
         firstAdhoc.getProcessedRequestDTO().setSubmittedDisseminationDate(sdf.format(new Date()));
         firstAdhoc.getProcessedRequestDTO().setCompletedDate(sdf.format(new Date()));
         firstAdhoc.getProcessedRequestDTO().setSize(i * 1000);
         if(i % 2 == 0) {
            firstAdhoc.getProcessedRequestDTO().setUrl(null);
         } else {
            firstAdhoc.getProcessedRequestDTO().setUrl("http://srv-openwis2.silogic.fr:18080/static/6629b508/images/24x24/user.gif");
         }
         firstAdhoc.getProcessedRequestDTO().setStatus(StatusDTO.COMPLETE);
         dtos.add(firstAdhoc);
      }

      return dtos;
   }

}
