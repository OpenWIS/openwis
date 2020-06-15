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
        return startDate.format(DateTimeFormatter.ofPattern(datePattern));
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @JsonIgnore
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public String getEndDateAsString() {
        return endDate.format(DateTimeFormatter.ofPattern(datePattern));
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
