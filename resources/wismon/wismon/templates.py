from copy import deepcopy
import json


header = {
    "wmo_wis_monitoring": "1.0",
    "centre": None,
    "timestamp": None
}

gisc_properties = {
    "catalogue_url": None,
    "monitor_url": None,
    "oai_url": None,
    "events_url": None,
    "centres_inAoR_url": None,
    "backup_giscs": None,  # A list of GISC city names, e.g. ['exeter', 'melbourne', 'toulouse']
    "contact_info": {
        "voice": None,
        "email": None
    }
}

metrics = {
    "rmdcn": None,
    "metadata_catalogue": {
        "number_of_records_at00UTC": None,
        "number_of_changes_insert_modify": None,
        "number_of_changes_delete": None
    },
    "cache_24h": {
        "number_of_products_all": None,
        "number_of_products_without_metadata": None,
        "bytes_of_cache_all": None,
        "bytes_of_cache_without_metadata": None,
        "number_of_unique_products_without_metadata_AMDCN": None,
        "number_of_unique_products_without_metadata_all": None
    },
    "services": {
        "oai_pmh": {
            "status": None
        },
        "catalogue": {
            "status": None
        },
        "distribution_system": {
            "status": None
        }
    }
}


class Message(object):
    def __init__(self, gisc_name, timestamp):
        self.data = deepcopy(header)
        self.data['centre'] = gisc_name
        self.data['timestamp'] = timestamp

    def serialize(self, indent=0):
        return json.dumps(self.data, indent=indent)

    @staticmethod
    def update_dict(d, **kwargs):
        keys = d.keys()
        for k, v in kwargs.items():
            options = [key for key in keys if key.startswith(k)]
            if len(options) == 1:
                d[options[0]] = v if v != '' else None
            else:
                raise KeyError(k)


class MonitorJSON(Message):
    def __init__(self, gisc_name, timestamp):
        super(MonitorJSON, self).__init__(gisc_name, timestamp)
        self.data['gisc_properties'] = deepcopy(gisc_properties)
        self.data['metrics'] = deepcopy(metrics)
        self.data['remarks'] = None

    def gisc_properties(self, **kwargs):
        properties = self.data['gisc_properties']
        Message.update_dict(properties, **kwargs)

    def metric_services(self, **kwargs):
        services = self.data['metrics']['services']
        Message.update_dict(services, **kwargs)

    def metrics_cache(self, **kwargs):
        cache_24h = self.data['metrics']['cache_24h']
        Message.update_dict(cache_24h, **kwargs)

    def metrics_catalogue(self, **kwargs):
        catalogue = self.data['metrics']['metadata_catalogue']
        Message.update_dict(catalogue, **kwargs)

    def metrics_rmdcn(self, value):
        self.data['metrics']['rmdcn'] = value

    def remarks(self, value):
        self.data['remarks'] = value


class CentresJSON(Message):

    def __init__(self, gisc_name, timestamp):
        super(CentresJSON, self).__init__(gisc_name, timestamp)
        self.data['centres'] = []

    def add_centre(self, name, n_products, size_cache):
        self.data['centres'].append({
            'centre': name,
            'count': n_products,
            'volumesize': size_cache
        })


class EventsJSON(Message):

    def __init__(self, gisc_name, timestamp):
        super(EventsJSON, self).__init__(gisc_name, timestamp)
        self.data['events'] = []

    def add_event(self, startdatetime, enddatetime, title, text=''):
        self.data['events'].append({
            'id': len(self.data['events']) + 1,
            'title': title,
            'text': text,
            'start': startdatetime,
            'end': enddatetime
        })


CONFIG_TEMPLATE = """[system]
# ALL options in this section are Compulsory.
#
# OpenWIS database hostname, port, username, password and database name
openwis_db_host=
openwis_db_port=5432
openwis_db_user=
openwis_db_pass=
openwis_db_name=
# Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
logging_level=INFO

[monitor]
# Options are Compulsory unless indicated otherwise.
#
# Name of the GISC, e.g. GISC Melbourne
centre=
# Name of the WIMMS set (WIS Interim Metadata Maintenance Service).
# The authoritative copy of this set is hosted by JMA as "WIS-UNASSOCIATED".
# So the default "WIS-UNASSOCIATED" value is often appropriate.
# However, in case when the set has a different name (e.g. UKMO has this set named
# as "WIS-Interim-Metadata"). The actual name should be used to replace the default value.
WIMMS_name=WIS-UNASSOCIATED
# Portal URL, e.g. http://wis.bom.gov.au
catalogue_url=
# URL of oai-pmh server, e.g. http://wis.bom.gov.au/openwis-user-portal/srv/en/oaipmh
oai_url=
# The spec requires the status (up/down) of the distribution system.
# We can use the portal status as an approximation, e.g. http://wis.bom.gov.au
dissemination_url=
# URL where centres.json file is hosted. This value depends on where the JSON files
# are served. This message is required in monitor.json so that other JSON files can be
# located by simply checking with this central JSON file (monitor.json).
# This entry is Optional but recommended since all three JSON files are created anyway.
centres_inAoR_url=
# URL where events.json file is hosted. Similar to centres_inAoR_url but for events.json
# This entry is Optional but recommended since all three JSON files are created anyway.
events_url=
# Url to GISC's own monitoring website (Optional).
monitor_url=
# Comma separated list for backup giscs, e.g. Exeter, Toulouse (Optional).
backup_giscs=
# Phone contact (Optional)
contact_info_voice=
# Email contact (Optional)
contact_info_email=
# URL of the GISC's RMDCN statistics published to the ECMWF web (Optional).
rmdcn=

[centre]
# This section is Optional.
#
# It is used to generate centres.json. An placeholder centres.json is generated
# if no entry is provided in this section.
# List centre names and regex patterns corresponding to their metadata urns
# i.e. Centre_Name=URN_REGEX_PATTERNS
#
# Since urn cannot contain spaces, it is safe to separate them with whitespace
# if there are more than one such as the follows:
#   centre_name=^urn.*XYZ$    ^urn.*ABC$
#
# The following are a few more examples to show centre definitions:
#
#   Australia=^urn:x-wmo:md:int.wmo.wis::.*(AMMC|ABRF|ADRM|APRF|AMRF)$
#   Fiji=^urn:x-wmo:md:int.wmo.wis::.*(NFFN|NFNA)$
#   Auckland - New Zealand=^urn:x-wmo:md:int.wmo.wis::.*NZAK$

[analysis]
# This section is Optional.
#
# It is to perform some statistical analysis to the snapshot of metadata catalogue.
#
metadata_source_breakdown=False

"""
