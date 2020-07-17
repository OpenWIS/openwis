/**
 *
 */
package org.openwis.metadataportal.services.access;

import jeeves.constants.Jeeves;
import jeeves.interfaces.Service;
import jeeves.interfaces.ServiceWithJsp;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ProfileManager;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import org.apache.commons.collections.CollectionUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.availability.IAvailabilityManager;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.services.mock.MockGetDisseminationParameters;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.system.MaintenanceConfiguration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get service. <P>
 * Get all services list + add service if MSS FSS service is allowed.
 */
public class Get implements Service, ServiceWithJsp {

    /**
     * True if the portal is the portal user.
     * @member: isUserPortal
     */
    private boolean isUserPortal;

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    public void init(String appPath, ServiceConfig params) throws Exception {
        isUserPortal = "user".equals(params.getValue("portal"));
    }

    //--------------------------------------------------------------------------
    //---
    //--- Service
    //---
    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    public Element exec(Element params, ServiceContext context) throws Exception {
        String profile = ProfileManager.GUEST;

        if (context.getUserSession().isAuthenticated()) {
            profile = context.getUserSession().getProfile();
        }

        Element servicesElement = context.getProfileManager().getAccessibleServices(profile);

        if (isUserPortal) {
            //Compute operations allowed.
            boolean isMssFssSupported = OpenwisMetadataPortalConfig
                    .getBoolean(ConfigurationConstants.MSSFSS_SUPPORT);
            List<String> channels = null;
            if (isMssFssSupported) {
                if (MockMode.isMockModeHarnessMSSFSS()) {
                    channels = MockGetDisseminationParameters.getMSSFSSChannelsMock();
                } else {
                    MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
                    channels = mssFssService.getChannelsForUser(context.getUserSession().getUsername());
                }
            }

            boolean isAllowedMSSFSS = isMssFssSupported && CollectionUtils.isNotEmpty(channels);
            if (isAllowedMSSFSS) {
                // Add allowedMSSFSS service to the service list.
                Element allowedMSSFSS = new Element(Jeeves.Elem.SERVICE);
                allowedMSSFSS.setAttribute(Jeeves.Attr.NAME, "allowedMSSFSS");
                servicesElement.addContent(allowedMSSFSS);
            }
        }

        return servicesElement;
    }

    public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception {
        Element servicesElement = exec(params, context);
        ArrayList<String> services = new ArrayList<String>();
        for (Object element : servicesElement.getChildren()) {
            Element el = (Element) element;
            String value = el.getAttributeValue(Jeeves.Attr.NAME);
            services.add(value);
        }

        Map<String, Object> attrMap = new HashMap<String, Object>();
        attrMap.put("availableServices", services);

        if (isUserPortal) {
            Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
            IAvailabilityManager availabilityManager = new AvailabilityManager(dbms);
            boolean isUserPortalEnabled = availabilityManager.isUserPortalEnable();
            attrMap.put("isUserPortalEnabled", isUserPortalEnabled);

            // set maintenance mode if enabled
            GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
            SettingManager sm = new SettingManager(dbms, context.getProviderManager());
            MaintenanceConfiguration maintenanceConfiguration = new MaintenanceConfiguration(sm);
            if (maintenanceConfiguration.isEnabled()) {
                attrMap.put("maintenanceBanner", maintenanceConfiguration.getMaintenanceBanner());
            }

            // get last login time
            if (context.getUserSession().isAuthenticated()) {
                try {
                    // attempt to load user from db.
                    String username = context.getUserSession().getUsername();
                    LocalDateTime lastLogin = getLastLogin(dbms, username);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    attrMap.put("userLastLogin", ZonedDateTime.of(lastLogin, ZoneOffset.ofHours(8)).format(formatter));
                } catch (SQLException ex) {
                    Log.error(Log.JEEVES, "Last login query error: " + ex.getMessage());
                }
            }
        }

        return attrMap;
    }

    /**
     * Return the last login of the authenticated user.
     * The last login is considered the login date before the current login.
     * @param username username
     * @return last login date or the current date if there are no login records from this user
     */
    private LocalDateTime getLastLogin(Dbms dbms, String username) throws SQLException {
        String query = "SELECT * FROM user_log WHERE username = ? AND action = ? ORDER BY ? DESC LIMIT ?";
        List<Element> list = dbms.select(query, username,"LOGIN",Geonet.Elem.DATE, 2).getChildren();

        // we should have at least one entry. The second entry is the one which we want
        if (list.size() == 2) {
            Element entry = (Element) list.get(1);
            String sLastLogin = entry.getChildText(Geonet.Elem.DATE);
            return LocalDateTime.parse(sLastLogin,DateTimeFormatter.ISO_DATE_TIME);
        } else {
            return LocalDateTime.now();
        }
    }
}
