/**
 *
 */
package org.openwis.metadataportal.services.availability;

import java.util.Calendar;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.availability.IAvailabilityManager;
import org.openwis.metadataportal.services.availability.dto.SwitchToBackupDTO;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class SwitchToBackup implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      SwitchToBackupDTO dto = JeevesJsonWrapper.read(params, SwitchToBackupDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      IAvailabilityManager availabilityManager = new AvailabilityManager(dbms);

      // Get the retro-processing date
      Calendar retrProcessCal = DateTimeUtils.getUTCCalendar();
      retrProcessCal.add(Calendar.HOUR, -dto.getHour());

      availabilityManager.switchBackupMode(dto.isSwitchedOn(), dto.getDeploymentName(),
            retrProcessCal.getTime(), context);

      return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
   }
}
