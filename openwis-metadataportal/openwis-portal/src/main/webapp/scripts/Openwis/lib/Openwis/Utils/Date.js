Ext.ns('Openwis.Utils.Date');

Openwis.Utils.Date.format = function(realDate) {
	var dt = new Date(realDate);
	return dt.format('d-M-Y')
};

Openwis.Utils.Date.formatDateTime = function(realDate) {
	if(!realDate) {
		return '';
	}
	var dt = new Date(realDate);
	return dt.format('d-m-Y H:i')
};

Openwis.Utils.Date.formatDateTimeUTC = function(realDate) {
	if(!realDate) {
		return '';
	}
	var d ='';
	var m = '';
	var y = '';
	var heure = '';
	if (realDate != null && realDate.length > 0)
	{
		d = realDate.substring(8, 10);
		m = realDate.substring(5, 7);
		y = realDate.substring(0, 4);
		heure = realDate.substring(11, 16);
	} 

	return d + '-' + m + '-' + y + ' ' + heure
};

Openwis.Utils.Date.formatDateTimeUTCfromLong = function(realDate) {
	
	return Openwis.Utils.Date.formatDateTimeUTCfromLongWithPattern(realDate, 'Y-m-d H:i:s');	
};

Openwis.Utils.Date.formatDateUTCfromLong = function(realDate) {
	
	return Openwis.Utils.Date.formatDateTimeUTCfromLongWithPattern(realDate, 'Y-m-d');	
};

Openwis.Utils.Date.formatDateTimeUTCfromLongWithPattern = function(realDate, pattern) {
	if(!realDate) {
		return '';
	}
		
	var date = new Date(realDate);
	var localOffset = date.getTimezoneOffset() * 60000;
	var localTime = date.getTime();
	var utc = localTime + localOffset;
	date = new Date(utc);
	return date.format(pattern);	
};

Openwis.Utils.Date.formatForComponents = function(realDate) {
	var dt = new Date(realDate);
	return dt.format('d/m/Y');
};

Openwis.Utils.Date.formatTimeForComponents = function(realTime) {
	var dt = new Date('1970-01-01T' + realTime);
	return dt.format('H:i');
};

Openwis.Utils.Date.formatDateInterval = function(dateFrom, dateTo) {
	var formattedFrom = Openwis.Utils.Date.formatDateForServer(dateFrom);
	var formattedTo = Openwis.Utils.Date.formatDateForServer(dateTo);
	return formattedFrom + "/" + formattedTo;
};

Openwis.Utils.Date.formatTimeInterval = function(dateFrom, dateTo) {
	return dateFrom + "Z/" + dateTo + "Z";
};


Openwis.Utils.Date.formatDateForServer = function(date) {
	return date.format('Y-m-d');
};


Openwis.Utils.Date.dateToISOUTC = function(date) {
    return Openwis.Utils.Date.toISOUTC(date, true, false);
};

Openwis.Utils.Date.timeToUTC = function(time) {
    return String.leftPad(time.getUTCHours(), 2, "0") + ":" + String.leftPad(time.getUTCMinutes(), 2, "0");
};

Openwis.Utils.Date.dateTimeToISOUTC = function(date) {
    return Openwis.Utils.Date.toISOUTC(date, true, true);
};


Openwis.Utils.Date.toISOUTC = function(date, withDate, withTime) {
    var dateStr = "";
    if(withDate) {
        dateStr += date.getUTCFullYear();
        dateStr += "-";
        dateStr += String.leftPad(date.getUTCMonth() + 1, 2, "0");
        dateStr += "-";
        dateStr += String.leftPad(date.getUTCDate(), 2, "0");
    }
    
    if(withTime) {
        if(withDate) {
            dateStr += "T";
        }
        dateStr += String.leftPad(date.getUTCHours(), 2, "0");
        dateStr += ":";
        dateStr += String.leftPad(date.getUTCMinutes(), 2, "0");
        dateStr += ":";
        dateStr += String.leftPad(date.getUTCSeconds(), 2, "0");
        dateStr += "Z";
    }
    return dateStr;
};

Openwis.Utils.Date.formatToISODate = function(date) {
	return date.format("Y-m-d\\TH:i:s\\Z");
};

Openwis.Utils.Date.parseISODate = function(dateStr) {
	return date.format("Y-m-d\\TH:i:s\\Z");
};

// We expect to have the following format "2011-06-04T00:00:00Z"
// Return the date "2011-06-04"
Openwis.Utils.Date.ISODateToCalendar = function(dateStr) {
	if (dateStr != null && dateStr.length > 0)
	{
		return dateStr.substring(0, 10);
	}
	return "";
};

Openwis.Utils.Date.ISODateToTime = function(dateStr) {
	if (dateStr != null && dateStr.length > 0)
	{
		return dateStr.substring(11, 16);
	}
	return "";
};