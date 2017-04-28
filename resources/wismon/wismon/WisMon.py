"""
OpenWIS monitoring tool for WMO Common Dashboard pilot.

All datetime values must conform to ISO 8601.
    date - YYYY-MM-DD
    datetime - YYYY-MM-DDThh:mm:ssZ


"""
import sys
import os
from datetime import datetime
from ConfigParser import ConfigParser, NoSectionError, NoOptionError
import urllib2
import json
import logging
import logging.handlers

from .db import *
from .templates import MonitorJSON, CentresJSON, EventsJSON, CONFIG_TEMPLATE

logger = logging.getLogger('wismon')


class WmError(Exception):
    pass

class WisMon(object):

    def __init__(self, working_dir):
        
        self.working_dir = working_dir
        self.config_file = os.path.join(self.working_dir, 'config', 'wismon.cfg')
        if not (os.path.exists(self.config_file) and os.path.isfile(self.config_file)):
            raise WmError('Config file not exists: %s' % self.config_file)
        self.config = ConfigParser()
        self.config.optionxform = str  # preserve case
        self.config.read(os.path.join(self.working_dir, 'config', 'wismon.cfg'))

        self.data_dir = os.path.join(self.working_dir, 'data')
        if not os.path.exists(self.data_dir):
            os.mkdir(self.data_dir)
        self.log_dir = os.path.join(self.working_dir, 'logs')
        if not os.path.exists(self.log_dir):
            os.mkdir(self.log_dir)
        self.json_dir = os.path.join(self.data_dir, 'JSON')
        if not os.path.exists(self.json_dir):
            os.mkdir(self.json_dir)
        
        self.wismon_db_file = os.path.join(self.data_dir, 'wismon.sqlite3')

        # Set up the logging file
        log_handler = logging.handlers.RotatingFileHandler(
            os.path.join(self.log_dir, 'wismon.log'),
            maxBytes=1048576,
            backupCount=5)

        log_handler.setFormatter(logging.Formatter(
            '%(asctime)s %(levelname)s [%(funcName)s] - %(message)s'))
        logger.addHandler(log_handler)

        level = self.config.get('system', 'logging_level')
        try:
            level = logging._levelNames[level.upper()]
            logger.setLevel(level)
        except NameError:
            logger.warning('invalid logging level: %s' % level)

    def json_gen(self, force_regen=False):

        gisc_name = self.config.get('monitor', 'centre')
        now = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
        date_now = now[:10]

        centre_patterns = {}
        try:
            for item in self.config.options('centre'):
                centre_patterns[item] = self.config.get('centre', item, raw=True).split()
        except NoSectionError:
            pass

        with connect_wismondb(self.wismon_db_file) as (conn, cursor):

            # Do nothing if JSON messages of the day already exist in database
            qrs = cursor.execute(sql_json_get, (date_now, ))
            if qrs.fetchall():
                if force_regen:
                    logger.info('Re-generate JSON files for date %s' % date_now)
                    cursor.execute(sql_json_del, (date_now, ))
                    cursor.execute('DELETE FROM wismon_md_breakdown WHERE date = ?', (date_now,))
                    cursor.executescript('DROP TABLE IF EXISTS wismon_metadata;\n' +
                                         'ALTER TABLE old_wismon_metadata RENAME TO wismon_metadata;\n')
                else:
                    raise WmError('JSON messages for day %s already exist' % date_now)
            else:
                logger.info('Creating JSON messages for date %s' % date_now)

            with connect_openwisdb(
                    host=self.config.get('system', 'openwis_db_host'),
                    port=self.config.getint('system', 'openwis_db_port'),
                    database=self.config.get('system', 'openwis_db_name'),
                    user=self.config.get('system', 'openwis_db_user'),
                    password=self.config.get('system', 'openwis_db_pass')) as (ow_conn, ow_cursor):
                logger.info('Sending query to openwis db ...')
                ow_cursor.execute(sql_query_to_openwis)
                rows = ow_cursor.fetchall()
                logger.info('Done')

            # Save data from the previous day
            # TODO: Somehow the alter and create table statements have to be executed as a single
            #       script. Otherwise, the table will not be created after the alter statement.
            logger.info("Saving data from previous day")
            cursor.executescript("""
            DROP TABLE IF EXISTS old_wismon_metadata;
            ALTER TABLE wismon_metadata RENAME TO old_wismon_metadata;
            %s
            """ % sql_schema_wismon_metadata)

            logger.info('Saving query results ...')
            cursor.executemany(sql_save_snapshot, rows)
            conn.commit()
            logger.info('Done')

            logger.info('Querying for global metadata stats')
            qrs_concrete = qrs_draft = None
            # Calculate metadata stats by grouping as draft and non-draft
            for row in cursor.execute(sql_global_stats).fetchall():
                if row[3] == 0:  # this is the non-draft (0 is false) row
                    qrs_concrete = row
                else:
                    qrs_draft = row
            # In theory, both draft and non-draft stats could be null if no such metadata available in catalogue
            # If draft exists
            if qrs_draft:
                n_uniq_products_draft, n_products_draft, size_cache_draft, _ = qrs_draft
            else:  # otherwise set draft related stats to zero
                n_uniq_products_draft = n_products_draft = size_cache_draft = 0
            # If non-draft exists
            if qrs_concrete:
                # Number of total metadata are draft plus non-draft
                n_uniq_products = n_uniq_products_draft + qrs_concrete[0]
                n_products = n_products_draft + qrs_concrete[1]
                size_cache = size_cache_draft + qrs_concrete[2]
            else:  # if no non-draft available, stats of total is the same as stats of draft
                n_uniq_products = n_products = size_cache = n_uniq_products_draft, n_products_draft, size_cache_draft

            # centres.json
            logger.info('Querying for AMDCN metadata stats')
            centres_json = CentresJSON(gisc_name, now)
            amdcn_n_uniq_products_draft = 0
            for ct_name, patterns in centre_patterns.items():
                where_expr = ' OR '.join("uuid REGEXP '%s'" % p for p in patterns)
                ct_qrs_concrete = ct_qrs_draft = None
                for row in cursor.execute(sql_amdcn_stats % where_expr).fetchall():
                    if row[3] == 0:
                        ct_qrs_concrete = row
                    else:
                        ct_qrs_draft = row
                if ct_qrs_draft:
                    ct_n_uniq_products_draft, ct_n_products_draft, ct_size_cache_draft, _ = ct_qrs_draft
                else:
                    ct_n_uniq_products_draft = ct_n_products_draft = ct_size_cache_draft = 0
                if ct_qrs_concrete:
                    ct_n_uniq_products = ct_n_uniq_products_draft + ct_qrs_concrete[0]
                    ct_n_products = ct_n_products_draft + ct_qrs_concrete[1]
                    ct_size_cache = ct_size_cache_draft + ct_qrs_concrete[2]
                else:
                    ct_n_uniq_products = ct_n_products = ct_size_cache = 0
                amdcn_n_uniq_products_draft += ct_n_uniq_products_draft

                centres_json.add_centre(ct_name, ct_n_products, ct_size_cache)

            # monitor.json
            monitor_json = MonitorJSON(gisc_name, now)
            logger.info('Checking status of oai-pmh server')
            # Check whether oai server is up
            oai_url = self.config.get('monitor', 'oai_url')
            try:
                req = urllib2.urlopen('%s?verb=Identify' % oai_url)
                oai_status = True if 200 <= req.getcode() < 300 else False
            except Exception:
                oai_status = False

            logger.info('Checking status of openwis portal')
            catalogue_url = self.config.get('monitor', 'catalogue_url')
            try:
                req = urllib2.urlopen(catalogue_url)
                catalogue_status = True if 200 <= req.getcode() < 300 else False
            except Exception:
                catalogue_status = False

            logger.info('Checking status of dissemination server')
            dissemination_url = self.config.get('monitor', 'dissemination_url')
            try:
                req = urllib2.urlopen(dissemination_url)
                dissemination_status = True if 200 <= req.getcode() < 300 else False
            except Exception:
                dissemination_status = False

            monitor_json.metric_services(oai_pmh={'status': oai_status},
                                         catalogue={'status': catalogue_status},
                                         distribution_system={'status': dissemination_status})

            monitor_json.gisc_properties(
                catalogue_url=catalogue_url,
                oai_url=oai_url,
                centres_inAoR_url=self.config.get('monitor', 'centres_inAoR_url'),
                events_url=self.config.get('monitor', 'events_url'),
                monitor_url=self.config.get('monitor', 'monitor_url'),
                backup_giscs=[x.strip() for x in self.config.get('monitor', 'backup_giscs').split(',')],
                contact_info={'voice': self.config.get('monitor', 'contact_info_voice'),
                              'email': self.config.get('monitor', 'contact_info_email')}
            )

            rmdcn_stats_url = self.config.get('monitor', 'rmdcn')
            if rmdcn_stats_url:
                monitor_json.metrics_rmdcn(rmdcn_stats_url)

            logger.info('Counting number of records from GISC-specific sets (WIS-GISC-CITYNAME + WIMMS)')
            # Try read the category name for WIMMS set
            try:
                wimms_name = self.config.get('monitor', 'WIMMS_name').strip()
            except NoOptionError:
                wimms_name = ''

            # An empty WIMMS name acts like a dummy for the query
            _sql = sql_md_total_GISC_specific % wimms_name

            qrs = cursor.execute(_sql)
            monitor_json.metrics_catalogue(number_of_records=qrs.fetchone()[0])

            # If stats from previous day exists, we can calculate the traffic.
            # These stats also only count GISC specific sets and WIMMS
            logger.info('Comparing for new and modified metadata')
            _sql = sql_md_insert_modify % wimms_name
            qrs = cursor.execute(_sql)
            n_insert_modify = qrs.fetchone()[0]
            logger.info('Comparing for deleted metadata')
            _sql = sql_md_deleted % wimms_name
            qrs = cursor.execute(_sql)
            n_delete = qrs.fetchone()[0]

            monitor_json.metrics_catalogue(number_of_changes_insert_modify=n_insert_modify,
                                           number_of_changes_delete=n_delete)

            # logger.info('Deleting data from previous day')
            # cursor.execute("DROP TABLE IF EXISTS old_wismon_metadata;")

            monitor_json.metrics_cache(
                number_of_products_all=n_products,
                number_of_products_without_metadata=n_products_draft,
                bytes_of_cache_all=size_cache,
                bytes_of_cache_without_metadata=size_cache_draft,
                number_of_unique_products_without_metadata_AMDCN=amdcn_n_uniq_products_draft,
                number_of_unique_products_without_metadata_all=n_uniq_products_draft)

            qrs = cursor.execute(sql_remarks_get)
            remarks = qrs.fetchone()
            if remarks:
                monitor_json.remarks(remarks[0])

            # events.json
            logger.info('Gathering events')
            events_json = EventsJSON(gisc_name, now)
            qrs = cursor.execute(sql_event_get, (now, ))
            for _, title, text, startdatetime, enddatetime in qrs.fetchall():
                events_json.add_event(startdatetime, enddatetime, title, text)

            logger.info('Saving JSON messages to files')
            with open(os.path.join(self.json_dir, 'monitor.json'), 'w') as outs:
                outs.write(monitor_json.serialize(indent=4))
            with open(os.path.join(self.json_dir, 'centres.json'), 'w') as outs:
                outs.write(centres_json.serialize(indent=4))
            with open(os.path.join(self.json_dir, 'events.json'), 'w') as outs:
                outs.write(events_json.serialize(indent=4))

            logger.info('Saving JSON messages to local database')
            cursor.execute(sql_save_json, (
                date_now,
                monitor_json.serialize(),
                centres_json.serialize(),
                events_json.serialize()
            ))

            # Metadata breakdown stats
            self.metadata_source_breakdown(cursor, date_now)

            conn.commit()
            return monitor_json, centres_json, events_json
            
    def json_get(self, date=None, name='monitor'):
        if date is None:
            date = datetime.utcnow().strftime('%Y-%m-%d')
        with connect_wismondb(self.wismon_db_file) as (conn, cursor):
            logger.info('Getting JSON message %s for date %s' % (name, date))
            row = cursor.execute(sql_json_get, (date, )).fetchone()
        if row:
            indices = {
                'monitor': 1,
                'centres': 2,
                'events': 3
            }
            try:
                msg = row[indices[name]]
            except NameError:
                raise WmError('No JSON message of name: %s' % name)
            return json.loads(msg)
        else:
            raise WmError('No JSON message for date: %s' % date)

    def json_del(self, date):
        with connect_wismondb(self.wismon_db_file) as (conn, cursor):
            logger.info('Deleting JSON message for date %s' % date)
            cursor.execute(sql_json_del, (date, ))
            count = cursor.rowcount
            conn.commit()
        if count == 0:
            raise WmError('No JSON messages for date: %s' % date)

    def event_add(self, startdatetime, enddatetime, title, text=''):
        try:
            datetime.strptime(startdatetime, '%Y-%m-%dT%H:%M:%SZ')
            datetime.strptime(enddatetime, '%Y-%m-%dT%H:%M:%SZ')
        except ValueError:
            raise WmError('Datetime format must conform to ISO 8601 (YYYY-MM-DDThh:mm:ssZ)')

        with connect_wismondb(self.wismon_db_file) as (conn, cursor):
            logger.info('Adding event %s' % title)
            cursor.execute(sql_event_add, (title, text, startdatetime, enddatetime))
            conn.commit()

    def event_get(self, dt=None):
        if dt is None:
            dt = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
        with connect_wismondb(self.wismon_db_file) as (_, cursor):
            logger.info('Getting event for datetime %s' % dt)
            rows = cursor.execute(sql_event_get, (dt, )).fetchall()
        if rows:
            return rows
        else:
            raise WmError('No event for datetime %s' % dt)

    def event_del(self, eid):
        with connect_wismondb(self.wismon_db_file) as (conn, cursor):
            logger.info('Deleting event of id %s' % eid)
            cursor.execute(sql_event_del, (eid, ))
            count = cursor.rowcount
            conn.commit()
        if count == 0:
            raise WmError('No event of id: %s' % eid)

    def remarks_set(self, text):
        with connect_wismondb(self.wismon_db_file) as (conn, cursor):
            logger.info('Setting new remarks')
            cursor.execute(sql_remarks_set, (text, ))
            conn.commit()

    def remarks_get(self):
        with connect_wismondb(self.wismon_db_file) as (conn, cursor):
            logger.info('Getting remarks')
            row = cursor.execute(sql_remarks_get).fetchone()
        if row is not None:
            return row[0]
        else:
            raise WmError('No remarks is found')

    def metadata_source_breakdown(self, cursor, date):
        try:
            if self.config.getboolean('analysis', 'metadata_source_breakdown'):
                logger.info('Calculating metadata source breakdown stats')
                qstr = sql_calc_md_source_breakdown.format(date)
                cursor.executescript(qstr)
        except (NoSectionError, NoOptionError):
            pass

    @staticmethod
    def init_working_directory(working_directory):

        config_dir = os.path.join(working_directory, 'config')
        if not os.path.exists(config_dir):
            os.makedirs(config_dir)
        with open(os.path.join(config_dir, 'wismon.cfg'), 'w') as outs:
            outs.write(CONFIG_TEMPLATE)

        json_dir = os.path.join(working_directory, 'data', 'JSON')
        if not os.path.exists(json_dir):
            os.makedirs(json_dir)
        log_dir = os.path.join(working_directory, 'logs')
        if not os.path.exists(log_dir):
            os.mkdir(log_dir)


def main():
    import argparse
    from . import __version__

    ap = argparse.ArgumentParser(prog=__package__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter,
                                 description='',
                                 epilog=__doc__)
    ap.add_argument('-v', '--version',
                    action='version',
                    version='%s: v%s' % (__package__, __version__))
    ap.add_argument('-d', '--working-directory',
                    required=True,
                    help='the working directory where config, data and logs are located')
    ap.add_argument('-l', '--log-to-console',
                    action='store_true',
                    help='write logs to stdout')

    subparsers = ap.add_subparsers(dest='sub_command',
                                   title='List of sub-commands',
                                   metavar='sub-command',
                                   help='"python -m %s sub-command -h" for more help on a sub-command' % __package__)

    json_gen_parser = subparsers.add_parser('json-gen',
                                            help='generate the JSON files for WIS monitoring')
    json_gen_parser.add_argument('-f', '--force-regen',
                                 action='store_true',
                                 help='force re-generation of the JSON files')
    json_gen_parser.add_argument('-v', '--verbose',
                                 action='store_true',
                                 help='be more chatty')
    json_get_parser = subparsers.add_parser('json-get',
                                            help='get JSON message of the given date and name')
    json_get_parser.add_argument('date',
                                 nargs='?',
                                 help='date of the JSON message (default today)')
    json_get_parser.add_argument('name',
                                 choices=('monitor', 'centres', 'events'),
                                 metavar='name',
                                 nargs='?',
                                 default='monitor',
                                 help='name (monitor, centres or events) of the JSON message (default monitor)')
    json_del_parser = subparsers.add_parser('json-del',
                                            help='delete JSON messages of the given date')
    json_del_parser.add_argument('date',
                                 help='date of the JSON message')

    event_add_parser = subparsers.add_parser('event-add',
                                             help='add an event')
    event_add_parser.add_argument('-t', '--title',
                                  required=True,
                                  help='title of the event')
    event_add_parser.add_argument('-d', '--description',
                                  help='description of the event')
    event_add_parser.add_argument('-s', '--startdatetime',
                                  required=True,
                                  metavar='DATETIME',
                                  help='event start datetime (YYYY-MM-HHThh:mm:ssZ)')
    event_add_parser.add_argument('-e', '--enddatetime',
                                  required=True,
                                  metavar='DATETIME',
                                  help='event end datetime (YYYY-MM-HHThh:mm:ssZ)')

    event_get_parser = subparsers.add_parser('event-get',
                                              help='list all events ending after the given datetime')
    event_get_parser.add_argument('datetime',
                                  nargs='?',
                                  help='datetime for searching events (default now)')

    event_del_parser = subparsers.add_parser('event-del',
                                             help='remove an event of given id')
    event_del_parser.add_argument('id',
                                  help='id of an event')

    remarks_set_parser = subparsers.add_parser('remarks-set',
                                               help='set remarks for monitor.json')
    remarks_set_parser.add_argument('text',
                                    nargs='?',
                                    default='',
                                    help="content of the remarks (default empty)")

    remarks_get_parser = subparsers.add_parser('remarks-get',
                                               help='show current remarks')

    init_parser = subparsers.add_parser('init',
                                        help='initialize the working directory with a TEMPLATE of config file')

    ns = ap.parse_args()

    if ns.sub_command == 'init':
        WisMon.init_working_directory(ns.working_directory)
        return 0

    if ns.log_to_console:
        logger.addHandler(logging.StreamHandler(sys.stdout))

    try:
        wismon = WisMon(ns.working_directory)

        if ns.sub_command == 'json-gen':
            monitor, centres, events = wismon.json_gen(force_regen=ns.force_regen)
            if ns.verbose:
                print json.dumps(monitor.serialize(), indent=4)
                print json.dumps(centres.serialize(), indent=4)
                print json.dumps(events.serialize(), indent=4)

        elif ns.sub_command == 'json-get':
            msg = wismon.json_get(ns.date, ns.name)
            print json.dumps(msg, indent=4)

        elif ns.sub_command == 'json-del':
            wismon.json_del(ns.date)
            print 'JSON messages deleted'

        elif ns.sub_command == 'event-add':
            wismon.event_add(ns.startdatetime, ns.enddatetime, ns.title, ns.description)
            print 'Event added'

        elif ns.sub_command == 'event-get':
            events = wismon.event_get(ns.datetime)
            for eid, title, text, sd, ed in events:
                if text is None:
                    text = ''
                print '%8d [%s] [%s - %s] %s' % (eid, title, sd, ed, text)

        elif ns.sub_command == 'event-del':
            wismon.event_del(ns.id)
            print 'Event deleted'

        elif ns.sub_command == 'remarks-set':
            wismon.remarks_set(ns.text)

        elif ns.sub_command == 'remarks-get':
            text = wismon.remarks_get()
            print text

    except WmError as e:
        sys.stderr.write("%s: %s\n" % (__package__, e.args[0]))
        logger.warning(e.args[0])
        return 1

    except Exception:
        import traceback
        traceback.print_exc()
        logger.exception('Unexpected Exception')
        return 1
