import json
from copy import deepcopy


class Message(object):
    stub = {}

    def __init__(self, gisc_name, timestamp):
        self.data = {
            'wmo_wis_monitoring': '1.1',
            'centre': gisc_name,
            'timestamp': timestamp
        }
        self.data.update(self.stub)

    def serialize(self, indent=0):
        return json.dumps(self.data, indent=indent)

    def to_file(self, filename):
        with open(filename, 'w') as outs:
            outs.write(self.serialize(indent=4))

    @staticmethod
    def parse_path_component(path_component):
        if path_component.endswith(']'):
            assert path_component.count('[') == 1, 'Invalid indexing: {0}'.format(path_component)

            name, index_string = path_component[:-1].split('[')
            return name, int(index_string)
        else:
            return path_component, None

    def set(self, path, value):
        """
        Set a new value to a child node pointed by the given value.

        :param path: A comma separated names each specifying a key name.
                     It can optionally has a trailing indexing component.
                     For an example, 'centres[0].metrics.number_of_metadata'
        :param value: A new value to be set.
        """
        path = path.replace(' ', '')  # remove all whites
        assert path, 'Invalid path for setting value: {0}'.format(path)
        path_components = path.split('.')
        data = self.data
        for i, path_component in enumerate(path_components):
            name, index = Message.parse_path_component(path_component)
            if i < len(path_components) - 1:
                data = data[name] if index is None else data[name][index]
            else:
                if index is None:
                    data[name] = value
                else:
                    data[name][index] = value

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
    stub = {
        'gisc_properties': {
            'portal_url': None,
            'oaipmh_url': None,
            'sru_url': None,
            'monitor_url': None,
            'cache_url': None,
            'centres_url': None,
            'events_url': None,
            'backup_giscs': None,
            'rmdcn': {
                'main': None,
                'sub': None,
                'DR_main': None
            },
            'contact_info': {
                'voice': None,
                'email': None
            }
        },

        'metrics': {
            'metadata_catalogue': {
                'number_of_metadata': None,
                'number_of_changes_insert_modify': None,
                'number_of_changes_delete': None
            },
            'cache_24h': {
                'number_of_product_instances': None,
                'number_of_product_instances_missing_metadata': None,
                'size_of_cache': None,
                'size_of_product_instances_missing_metadata': None,
                'number_of_unique_products_missing_metadata': None,
                'number_of_unique_products_missing_metadata_AoR': None
            },
            'services': {
                'portal': {
                    'status': None
                },
                'oaipmh': {
                    'status': None
                },
                'sru': {
                    'status': None
                },
                'distribution_system': {
                    'status': None
                }
            }
        },
        'remarks': ''
    }

    def __init__(self, gisc_name, timestamp):
        super(MonitorJSON, self).__init__(gisc_name, timestamp)

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


class CacheJSON(Message):
    stub = {
        'centres': []
    }

    member_stub = {
        'centre': None,
        'metrics': {
            'number_of_product_instances': None,
            'size_of_product_instances': None,
            'number_of_product_instances_missing_metadata': None,
            'size_of_product_instances_missing_metadata': None,
            'number_of_unique_products_missing_metadata': None,
            'number_of_metadata': None,
        }
    }

    def __init__(self, gisc_name, timestamp):
        super(CacheJSON, self).__init__(gisc_name, timestamp)

    def new_member(self):
        self.data['centres'].append(deepcopy(self.member_stub))
        return len(self.data['centres']) - 1


class CentresJSON(Message):
    stub = {
        'centres': []
    }

    member_stub = {
        'centre': None,
        'metrics': {
            'portal_response_time': None,
            'oaipmh_response_time': None,
            'sru_response_time': None,
        }
    }

    def __init__(self, gisc_name, timestamp):
        super(CentresJSON, self).__init__(gisc_name, timestamp)

    def new_member(self):
        self.data['centres'].append(deepcopy(self.member_stub))
        return len(self.data['centres']) - 1


class EventsJSON(Message):
    stub = {
        'events': []
    }
    member_stub = {
        'id': None,
        'title': None,
        'text': None,
        'start': None,
        'end': None,
    }

    def __init__(self, gisc_name, timestamp):
        super(EventsJSON, self).__init__(gisc_name, timestamp)

    def new_member(self):
        self.data['events'].append(deepcopy(self.member_stub))
        return len(self.data['events']) - 1
