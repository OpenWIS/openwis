Ext.ns('Openwis.Admin.Availability.DeploymentAvailabilityUtils');

Openwis.Admin.Availability.DeploymentAvailabilityUtils.simpleAvailabilityRenderer = function(availability) {
	return Openwis.i18n('Availability.Level.' + availability.level);
};

    
Openwis.Admin.Availability.DeploymentAvailabilityUtils.harvestingAvailabilityRenderer = function(availability) {
    var msg = Openwis.i18n('Availability.Level.' + availability.level);
    if(availability.additionalInfo) {
        msg += " " + Openwis.i18n('Availability.Harvesting', availability.additionalInfo);
    }
    return msg;
};

Openwis.Admin.Availability.DeploymentAvailabilityUtils.getAvailabilityRenderer = function(availability, serviceName) {
	if ((serviceName == "synchronization" || serviceName == "harvesting") && Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(availability)){
		return Openwis.Admin.Availability.DeploymentAvailabilityUtils.harvestingAvailabilityRenderer(availability);
	} else {
		return Openwis.Admin.Availability.DeploymentAvailabilityUtils.simpleAvailabilityRenderer(availability);
	}
}

Openwis.Admin.Availability.DeploymentAvailabilityUtils.clsAvailability = function(availability) {
    if(availability.level == 'UP') {
        return 'availabilityLevelUp';
    } else if(availability.level == 'WARN') {
        return 'availabilityLevelWarn';
    } else if(availability.level == 'DOWN') {
        return 'availabilityLevelDown';
    } else if(availability.level == 'UNKNOWN') {
        return 'availabilityLevelUnknown';
    } else if(availability.level == 'STOPPED') {
        return 'availabilityLevelStopped';
    } else if(availability.level == 'ALL_SUSPENDED') {
        return 'availabilityLevelAllSuspended';
    } else if(availability.level == 'NONE') {
        return 'availabilityLevelNone';
    }
};

Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted = function(service) {
	return service.level == 'UP' || service.level == 'WARN' || service.level == 'DOWN' || service.level == 'UNKNOWN';
};

Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceEnabled = function(service) {
	return service.level != 'NONE';
};

Openwis.Admin.Availability.DeploymentAvailabilityUtils.isStartStopButtonDisplayed = function(local, serviceType, serviceName, service) {
	var displayButton = false;
	if (local) {
		if (serviceType == "Data") {
			displayButton = true;
		}else if (serviceType == "Metadata") {
			if (serviceName != "indexing" && Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceEnabled(service)) {
				displayButton = true;
			}
		}
	}
	return displayButton;
};

