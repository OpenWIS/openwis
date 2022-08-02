"""
WMO WIS monitoring tool for OpenWIS.

All datetime values must conform to ISO 8601.
    date - YYYY-MM-DD
    datetime - YYYY-MM-DDThh:mm:ssZ

"""
import os
import sys
import time
from datetime import datetime
from ConfigParser import ConfigParser, NoSectionError, NoOptionError
import urllib2
import json
import logging
import logging.handlers

from sqlite3 import OperationalError

from .db import *
from .db import WisMonDB
from .templates import MonitorJSON, CacheJSON, CentresJSON, EventsJSON

LOGGER = logging.getLogger('wismon')

BASE_DIR = os.path.dirname(__file__)

DATE_PATTERN = re.compile('^[0-9]{4}-[0-9]{2}-[0-9]{2}$')
DATETIME_PATTERN = re.compile('^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$')

MONITOR_JSON_NAME = 'monitor'
CACHE_JSON_NAME = 'cache'
CENTRES_JSON_NAME = 'centres'
EVENTS_JSON_NAME = 'events'

NON_DRAFT = 0
DRAFT = 1


class WmError(Exception):
    pass


def assert_valid_date_string(date_string):
    if DATE_PATTERN.match(date_string) is None:
        raise WmError('Invalid ISO-8601 date string: {0}'.format(date_string))
    return True


def assert_valid_datetime_string(datetime_string):
    if DATETIME_PATTERN.match(datetime_string) is None:
        raise WmError('Invalid ISO-8601 datetime string: {0}'.format(datetime_string))
    return True


def get_uniform_datetime_string(s):
    """
    This is mainly to ensure a date string is converted to datetime string.
    """
    if s is None:  # Do nothing for None value
        return s
    elif assert_valid_datetime_string(s):
        return s
    elif assert_valid_date_string(s):
        return '{0}T00:00:00Z'.format(s)
    else:
        raise WmError('Invalid string for specifying time: {0}'.format(s))


def ping_url(url, timeout=20, n_retries=1):
    """
    Return the response time of the given URL or -1 in case of failure
    """
    for _ in xrange(n_retries):
        try:
            start_time = time.time()
            req = urllib2.urlopen(url, timeout=timeout)
            if 200 <= req.getcode() < 300:
                return time.time() - start_time
        except Exception:
            pass
    else:
        return -1


def ping_oaipmh(url, timeout=20, n_retries=1):
    return ping_url('{0}?verb=Identify'.format(url), timeout, n_retries)


class WisMon(object):
    def __init__(self, working_dir):

        self.working_dir = working_dir
        self.config_file = os.path.join(self.working_dir, 'config', 'wismon.cfg')
        if not (os.path.exists(self.config_file) and os.path.isfile(self.config_file)):
            raise WmError('Config file not exists: %s' % self.config_file)
        self.config = ConfigParser()
        self.config.optionxform = str  # preserve case
        self.config.read(os.path.join(self.working_dir, 'config', 'wismon.cfg'))

        self.gisc_name = self.config.get('monitor', 'centre')
        self.time_now = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')

        self.data_dir = os.path.join(self.working_dir, 'data')
        self.log_dir = os.path.join(self.working_dir, 'logs')
        self.json_dir = os.path.join(self.data_dir, 'JSON')

        self.n_messages_retain = self.config.getint('system', 'n_messages_retain')

        # Set up the logging file
        log_handler = logging.handlers.RotatingFileHandler(
            os.path.join(self.log_dir, 'wismon.log'),
            maxBytes=1048576,
            backupCount=5)

        log_handler.setFormatter(logging.Formatter(
            '%(asctime)s %(levelname)s [%(funcName)s] - %(message)s'))
        LOGGER.addHandler(log_handler)

        level = self.config.get('system', 'logging_level')
        try:
            level = logging._levelNames[level.upper()]
            LOGGER.setLevel(level)
        except NameError:
            LOGGER.warning('invalid logging level: %s' % level)

        # Initialize the database after logging is configured so logging
        # messages are properly directed.
        self.wismon_db = WisMonDB(os.path.join(self.data_dir, 'wismon.sqlite3'))

    def config_get_with_default(self, section_name, option_name, default=None):
        if self.config.has_section(section_name) and self.config.has_option(section_name, option_name):
            return self.config.get(section_name, option_name)
        else:
            return default

    def monitor_cache_json_gen(self, force_regen=False):
        """
        Generate json messages for Monitor and Cache JSON
        """

        date_now = '{0}T00:00:00Z'.format(self.time_now[:10])

        if self.wismon_db.json_exists('monitor', date_now):
            if force_regen:
                LOGGER.info('Re-generate JSON files for date: {0}'.format(date_now))
                self.wismon_db.restore_metadata(date_now)
                self.json_del(MONITOR_JSON_NAME, date_now)
                self.json_del(CACHE_JSON_NAME, date_now)
            else:
                raise WmError('JSON messages already exist for date: {0}'.format(date_now))

        else:
            LOGGER.info('Creating JSON messages for date: {0}'.format(date_now))

        # Create JSON file objects
        monitor_json = MonitorJSON(self.gisc_name, date_now)
        cache_json = CacheJSON(self.gisc_name, date_now)

        # Read the category name for WIMMS set
        wimms_name = self.config_get_with_default('monitor', 'WIMMS_name', '')

        LOGGER.info('Sending query to OpenWIS DB ...')
        rows = query_openwis(
            host=self.config.get('system', 'openwis_db_host'),
            port=self.config.getint('system', 'openwis_db_port'),
            database=self.config.get('system', 'openwis_db_name'),
            user=self.config.get('system', 'openwis_db_user'),
            password=self.config.get('system', 'openwis_db_pass')
        )

        # Save data from the previous day
        # TODO: Somehow the alter and create table statements have to be executed as a single
        #       script. Otherwise, the table will not be created after the alter statement.
        LOGGER.info("Saving snapshot of the metadata catalogue from previous day ...")
        self.wismon_db.archive_metadata()
        LOGGER.info('Saving new snapshot of the metadata catalogue ...')
        self.wismon_db.save_metadata(rows)

        LOGGER.info('Querying for overall metadata stats ...')
        stats = self.wismon_db.group_by_metadata_status(
            "category LIKE 'WIS-GISC-%' OR category IN ('{0}', '{1}')".format('draft', wimms_name)
        )
        monitor_json.set('metrics.metadata_catalogue.number_of_metadata', stats[NON_DRAFT].n_metadata)
        monitor_json.set(
            'metrics.cache_24h.number_of_product_instances',
            stats[NON_DRAFT].n_mapped_files
        )
        monitor_json.set(
            'metrics.cache_24h.number_of_product_instances_missing_metadata',
            stats[DRAFT].n_mapped_files
        )
        monitor_json.set(
            'metrics.cache_24h.size_of_cache',
            stats[NON_DRAFT].size
        )
        monitor_json.set(
            'metrics.cache_24h.size_of_product_instances_missing_metadata',
            stats[DRAFT].size
        )
        monitor_json.set(
            'metrics.cache_24h.number_of_unique_products_missing_metadata',
            stats[DRAFT].n_metadata
        )

        # Get the urn patterns
        urn_patterns = {}
        try:
            for centre_name in self.config.options('cache'):
                urn_patterns[centre_name] = self.config.get('cache', centre_name, raw=True).split()
        except NoSectionError:
            pass

        LOGGER.info('Querying for AMDCN metadata stats ...')
        number_of_unique_products_missing_metadata_AoR = 0
        for centre_name, patterns in urn_patterns.items():
            centre_idx = cache_json.new_member()
            cache_json.set('centres[{0}].centre'.format(centre_idx), centre_name)

            where_expr = "(category LIKE 'WIS-GISC-%' OR category IN ('{0}', '{1}')) AND ({2})".format(
                'draft', wimms_name,
                ' OR '.join("uuid REGEXP '{0}'".format(p) for p in patterns)
            )
            stats = self.wismon_db.group_by_metadata_status(where_expr)
            cache_json.set(
                'centres[{0}].metrics.number_of_product_instances'.format(centre_idx),
                stats[NON_DRAFT].n_mapped_files
            )
            cache_json.set(
                'centres[{0}].metrics.size_of_product_instances'.format(centre_idx),
                stats[NON_DRAFT].size
            )
            cache_json.set(
                'centres[{0}].metrics.number_of_product_instances_missing_metadata'.format(centre_idx),
                stats[DRAFT].n_mapped_files
            )
            cache_json.set(
                'centres[{0}].metrics.size_of_product_instances_missing_metadata'.format(centre_idx),
                stats[DRAFT].size
            )
            cache_json.set(
                'centres[{0}].metrics.number_of_unique_products_missing_metadata'.format(centre_idx),
                stats[DRAFT].n_metadata
            )
            number_of_unique_products_missing_metadata_AoR += stats[DRAFT].n_metadata
            cache_json.set(
                'centres[{0}].metrics.number_of_metadata'.format(centre_idx),
                stats[NON_DRAFT].n_mapped_files
            )

        monitor_json.set(
            'metrics.cache_24h.number_of_unique_products_missing_metadata_AoR',
            number_of_unique_products_missing_metadata_AoR
        )

        LOGGER.info('Checking self service status ...')
        portal_url = self.config.get('monitor', 'portal_url')
        monitor_json.set('metrics.services.portal.status', ping_url(portal_url) >= 0)

        # Check whether OAI-PMH server is up
        oaipmh_url = self.config.get('monitor', 'oaipmh_url')
        monitor_json.set('metrics.services.oaipmh.status', ping_oaipmh(oaipmh_url) >= 0)

        sru_url = self.config.get('monitor', 'sru_url')
        monitor_json.set('metrics.services.sru.status', ping_url(sru_url) >= 0)

        distribution_url = self.config.get('monitor', 'distribution_url')
        monitor_json.set('metrics.services.distribution_system.status', ping_url(distribution_url) >= 0)

        monitor_json.set('gisc_properties.portal_url', portal_url)
        monitor_json.set('gisc_properties.oaipmh_url', oaipmh_url)
        monitor_json.set('gisc_properties.sru_url', sru_url)
        monitor_json.set('gisc_properties.monitor_url', self.config.get('monitor', 'monitor_url') or None)
        monitor_json.set('gisc_properties.cache_url', self.config.get('monitor', 'cache_url'))
        monitor_json.set('gisc_properties.centres_url', self.config.get('monitor', 'centres_url'))
        monitor_json.set('gisc_properties.events_url', self.config.get('monitor', 'events_url'))
        monitor_json.set('gisc_properties.backup_giscs',
                         [x.strip() for x in self.config.get('monitor', 'backup_giscs').split(',')])
        monitor_json.set('gisc_properties.rmdcn.main', self.config.get('monitor', 'rmdcn.main'))
        monitor_json.set('gisc_properties.rmdcn.sub', self.config.get('monitor', 'rmdcn.sub'))
        monitor_json.set('gisc_properties.rmdcn.DR_main', self.config.get('monitor', 'rmdcn.DR_main'))
        monitor_json.set('gisc_properties.contact_info.voice',
                         self.config.get('monitor', 'contact_info.voice'))
        monitor_json.set('gisc_properties.contact_info.email',
                         self.config.get('monitor', 'contact_info.email'))

        LOGGER.info('Querying stats for new and modified metadata ...')
        monitor_json.set('metrics.metadata_catalogue.number_of_changes_insert_modify',
                         self.wismon_db.stats_inserted_modified(wimms_name))
        LOGGER.info('Querying stats for deleted metadata ...')
        monitor_json.set('metrics.metadata_catalogue.number_of_changes_delete',
                         self.wismon_db.stats_deleted(wimms_name))

        monitor_json.set('remarks', self.wismon_db.remarks_get())

        # Metadata breakdown stats
        try:
            if self.config.getboolean('analysis', 'metadata_source_breakdown'):
                LOGGER.info('Calculating metadata source breakdown stats')
                self.wismon_db.calc_metadata_breakdown(date_now)
        except (NoSectionError, NoOptionError):
            pass

        LOGGER.info('Saving JSON messages to files')
        monitor_json.to_file(os.path.join(self.json_dir, '{0}.json'.format(MONITOR_JSON_NAME)))
        cache_json.to_file(os.path.join(self.json_dir, '{0}.json'.format(CACHE_JSON_NAME)))

        LOGGER.info('Saving JSON messages to local database')
        self.wismon_db.json_save(MONITOR_JSON_NAME, date_now, monitor_json)
        self.wismon_db.json_save(CACHE_JSON_NAME, date_now, cache_json)

        if self.n_messages_retain >= 0:
            self.wismon_db.json_throttle(MONITOR_JSON_NAME, self.n_messages_retain)
            self.wismon_db.json_throttle(CACHE_JSON_NAME, self.n_messages_retain)

        return monitor_json, cache_json

    def centres_json_gen(self, force_regen=False):
        import threading

        time0_now = '{0}00Z'.format(self.time_now[:17])

        if self.wismon_db.json_exists(CENTRES_JSON_NAME, time0_now):
            if force_regen:
                LOGGER.info('Re-generate Centres JSON for datetime: {0}'.format(time0_now))
                self.wismon_db.json_del(CENTRES_JSON_NAME, time0_now)
            else:
                raise WmError('Centres JSON already exists for datetime: {0}'.format(time0_now))
        else:
            LOGGER.info('Creating Centres JSON for datetime: {0}'.format(time0_now))

        centres_json = CentresJSON(self.gisc_name, time0_now)

        n_threads = self.config.getint('system', 'n_threads')
        LOGGER.info('About to run {0} threads to ping service URLs of WIS Centres ...'.format(n_threads))
        centres_sections = [name for name in self.config.sections() if name.startswith('centres-')]

        def f(s, url, path_to_json_element):
            if url is None or url.strip() == '':
                res = None
            else:
                LOGGER.info('Ping {0}'.format(url))
                with s:
                    res = ping_url(url, timeout=20, n_retries=3)
            centres_json.set(path_to_json_element, res)

        semaphore = threading.Semaphore(n_threads)
        all_threads = []
        for section_name in centres_sections:
            idx_centre = centres_json.new_member()
            centres_json.set('centres[{0}].centre'.format(idx_centre), self.config.get(section_name, 'name'))
            for option_name_stub in ('portal', 'oaipmh', 'sru'):
                t = threading.Thread(
                    target=f,
                    args=(semaphore,
                          self.config_get_with_default(section_name, '{0}_url'.format(option_name_stub)),
                          'centres[{0}].metrics.{1}_response_time'.format(idx_centre, option_name_stub))
                )
                t.start()
                all_threads.append(t)

        for idx, t in enumerate(all_threads):
            t.join()

        centres_json.to_file(os.path.join(self.json_dir, '{0}.json'.format(CENTRES_JSON_NAME)))

        try:
            self.wismon_db.json_save(CENTRES_JSON_NAME, time0_now, centres_json)
        except OperationalError as e:
            LOGGER.warn('Database error: {}. Retry in 60 seconds'.format(e))
            time.sleep(60)
            self.wismon_db.json_save(CENTRES_JSON_NAME, time0_now, centres_json)

        if self.n_messages_retain >= 0:
            self.wismon_db.json_throttle(CENTRES_JSON_NAME, self.n_messages_retain)

        return centres_json

    def events_json_gen(self, force_regen=False):
        if self.wismon_db.json_exists(EVENTS_JSON_NAME, self.time_now):
            if force_regen:
                LOGGER.info('Re-generate Events JSON for datetime: {0}'.format(self.time_now))
                self.wismon_db.json_del(EVENTS_JSON_NAME, self.time_now)
            else:
                raise WmError('Events JSON already exists for datetime: {0}'.format(self.time_now))
        else:
            LOGGER.info('Creating Events JSON for datetime: {0}'.format(self.time_now))

        events_json = EventsJSON(self.gisc_name, self.time_now)
        # Events JSON
        LOGGER.info('Gathering events ...')
        for _, title, text, start_datetime_string, end_datetime_string in self.wismon_db.events_get(
                self.time_now):
            idx_event = events_json.new_member()
            events_json.set('events[{0}].id'.format(idx_event), idx_event + 1)
            events_json.set('events[{0}].title'.format(idx_event), title)
            events_json.set('events[{0}].text'.format(idx_event), text)
            events_json.set('events[{0}].start'.format(idx_event), start_datetime_string)
            events_json.set('events[{0}].end'.format(idx_event), end_datetime_string)

        events_json.to_file(os.path.join(self.json_dir, '{0}.json'.format(EVENTS_JSON_NAME)))
        self.wismon_db.json_save(EVENTS_JSON_NAME, self.time_now, events_json)

        if self.n_messages_retain >= 0:
            self.wismon_db.json_throttle(EVENTS_JSON_NAME, self.n_messages_retain)

        return events_json

    def json_get(self, name, datetime_string):
        datetime_string = get_uniform_datetime_string(datetime_string)
        row = self.wismon_db.json_get(name, datetime_string)
        if row:
            return json.loads(row[3])
        else:
            raise WmError('No {0} JSON message for datetime: {1}'.format(
                name, datetime_string or 'Most Recent'))

    def json_del(self, name, datetime_string):
        datetime_string = get_uniform_datetime_string(datetime_string)
        count = self.wismon_db.json_del(name, datetime_string)
        if count == 0:
            raise WmError('No {0} JSON messages for datetime: {1}'.format(
                name, datetime_string or 'Most Recent'))

    def event_add(self, start_datetime_string, end_datetime_string, title, text=''):
        start_datetime_string = get_uniform_datetime_string(start_datetime_string)
        end_datetime_string = get_uniform_datetime_string(end_datetime_string)
        LOGGER.info('Adding event: {0}'.format(title))
        self.wismon_db.event_add(start_datetime_string, end_datetime_string, title, text)

    def event_get(self, datetime_string):
        datetime_string = get_uniform_datetime_string(datetime_string)
        rows = self.wismon_db.events_get(datetime_string)
        if rows:
            return [
                {
                    'id': eid, 'title': title, 'text': text or '',
                    'start_datetime_string': sd,
                    'end_datetime_string': ed
                }
                for eid, title, text, sd, ed in rows
                ]
        else:
            raise WmError('No event for datetime: {0}'.format(datetime_string))

    def event_del(self, eid):
        count = self.wismon_db.event_del(eid)
        if count == 0:
            raise WmError('No event of id: {0}'.format(eid))

    def remarks_set(self, text):
        self.wismon_db.remarks_set(text)

    def remarks_get(self):
        row = self.wismon_db.remarks_get()
        if row is not None:
            return row[0]
        else:
            raise WmError('No remarks is found')

    @staticmethod
    def init_working_directory(working_directory):
        config_dir = os.path.join(working_directory, 'config')
        if not os.path.exists(config_dir):
            os.makedirs(config_dir)

        with open(os.path.join(BASE_DIR, 'config_template.cfg')) as ins:
            config_template = ins.read()

        with open(os.path.join(config_dir, 'wismon.cfg'), 'w') as outs:
            outs.write(config_template)

        json_dir = os.path.join(working_directory, 'data', 'JSON')
        if not os.path.exists(json_dir):
            os.makedirs(json_dir)

        log_dir = os.path.join(working_directory, 'logs')
        if not os.path.exists(log_dir):
            os.mkdir(log_dir)
