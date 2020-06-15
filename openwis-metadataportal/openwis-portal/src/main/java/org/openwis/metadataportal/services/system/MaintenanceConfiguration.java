package org.openwis.metadataportal.services.system;

import jeeves.resources.dbms.Dbms;
import org.fao.geonet.kernel.setting.SettingManager;
import org.openwis.metadataportal.services.system.dto.MaintenanceConfigurationDTO;
import org.openwis.metadataportal.services.util.OpenWISMessages;

import javax.ejb.Local;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class to set the maintenance banner on home page.
 *  This class retrieves maintenance configuration from system configuration.
 *  It can return the maintenance banner if the maintenance mode is enabled and the current date is
 *  between the start_date and end_date.
 *  The maintenance banner will be display on the home page from "start_date" to "end_date"
 */
public class MaintenanceConfiguration {
    private final String START_DATE_KEY = "system/maintenance/start_date";

    private final String END_DATE_KEY = "system/maintenance/end_date";

    private final String ENABLED_KEY = "system/maintenance/enabled";

    private final String MAINTENANCE_BANNER_TEMPLATE = "MaintenanceBanner";

    private final String MAINTENANCE_BANNER_STANDARD_TEMPLATE = "The WIS Portal will be undergoing scheduled maintenance and will be unavailable on {0} to {1}";

    private final String datePattern = "yyyy-MM-dd HH:mm";
    private final SettingManager settingManager;

    // The date when the maintenance banner will be displayed
    private LocalDateTime startDate;

    // Last day when maintenance banner will be displayed
    private LocalDateTime endDate;

    // True if maintenance mode is enabled
    private boolean enabled;

    public MaintenanceConfiguration(SettingManager settingManager) {
        this.settingManager = settingManager;
        this.readConfiguration(settingManager);
    }

    /**
     * Maintenance mode is enabled if enabled is set to True and
     * the current time is between sDate and eDate
     * @return True if maintenance mode enabled
     */
    public boolean isEnabled() {
        return enabled && (LocalDateTime.now().isAfter(startDate) && LocalDateTime.now().isBefore(endDate));
    }

    /**
     * Return the maintenance banner.
     * It does not check if maintenance mode is enabled
     * @return maintenance banner
     */
    public String getMaintenanceBanner() {
        String sDate = this.startDate.format(DateTimeFormatter.ofPattern(datePattern));
        String eDate = this.endDate.format(DateTimeFormatter.ofPattern(datePattern));
        try {
            return this.formatMaintenanceBanner(sDate, eDate);
        } catch (NullPointerException e) {
            return MessageFormat.format(MAINTENANCE_BANNER_STANDARD_TEMPLATE, sDate, eDate);
        }
    }

    /**
     * Update maintenance configuration
     * @param dbms
     * @param startDate start date of maintenance
     * @param endDate end date of maintenance
     * @param enabled true if maintenance mode is enabled
     * @throws IllegalArgumentException if start data is after end date
     * @throws SQLException is data cannot be saved to db
     */
    public void update(Dbms dbms, LocalDateTime startDate, LocalDateTime endDate, Boolean enabled) throws IllegalArgumentException, SQLException {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("start date is after end date");
        }
        this.settingManager.setValue(dbms, START_DATE_KEY, startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        this.settingManager.setValue(dbms, END_DATE_KEY, endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        this.settingManager.setValue(dbms, END_DATE_KEY, enabled);
    }

    /**
     * Return the dto
     * @return MaintenanceConfigurationDTO
     */
    public MaintenanceConfigurationDTO getDTO() {
        MaintenanceConfigurationDTO dto = new MaintenanceConfigurationDTO();
        dto.setEnabled(this.enabled);
        dto.setStartDate(this.startDate.format(DateTimeFormatter.ofPattern(datePattern)));
        dto.setEndDate(this.endDate.format(DateTimeFormatter.ofPattern(datePattern)));
        return dto;
    }

    private void readConfiguration(SettingManager settingManager) {
        try {
            String sStartDate = settingManager.getValue(START_DATE_KEY);
            if ( !sStartDate.isEmpty()) {
                startDate = LocalDateTime.parse(sStartDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            String sEndDate = settingManager.getValue(END_DATE_KEY);
            if ( !sEndDate.isEmpty() ) {
                endDate = LocalDateTime.parse(sEndDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            String sEnabled = settingManager.getValue(ENABLED_KEY);
            enabled = !sEnabled.isEmpty() && Boolean.parseBoolean(sEnabled);
        } catch (NullPointerException | DateTimeParseException e) {
            enabled = false;
            startDate = LocalDateTime.now();
            endDate= LocalDateTime.now();
        }
    }

    /**
     * Return the banner
     * @param startDate string of formatted start date
     * @param endDate string of formatted end date
     * @return banner
     * @throws NullPointerException if banner key is not found in openwisMessage.properties file
     */
    private String formatMaintenanceBanner(String startDate, String endDate) throws NullPointerException {
        String banner = OpenWISMessages.format(MAINTENANCE_BANNER_TEMPLATE, "en", startDate, endDate);
        if (banner.startsWith("!") && banner.endsWith("!")) {
            throw new NullPointerException("banner not found");
        }

        return banner;
    }
}
