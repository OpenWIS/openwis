package org.openwis.metadataportal.services.system.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MaintenanceConfigurationDTO {

    private final String datePattern = "yyyy-MM-dd HH:mm";

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean enabled;

    @JsonIgnore
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public String getStartDateAsString() {
        if (startDate == null) {
            return "";
        }
        return startDate.format(DateTimeFormatter.ofPattern(datePattern));
    }

    @JsonIgnore
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(String startDate) {
        if (startDate.isEmpty()) {
            return;
        }
        this.startDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @JsonIgnore
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public String getEndDateAsString() {
        if (endDate == null) {
            return "";
        }
        return endDate.format(DateTimeFormatter.ofPattern(datePattern));
    }

    @JsonIgnore
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate) {
        if (endDate.isEmpty()) {
            return;
        }
        this.endDate = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
