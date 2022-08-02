"""
OpenWIS monitoring tool for WMO WIS Monitoring Programme.
"""
import os
import sys
import json
import logging
import logging.handlers

from .wismon import WisMon, WmError

__version__ = '0.3.0'

LOGGER = logging.getLogger('wismon')

# So testings do not complain about no handler
if sys.version_info < (2, 7):
    class NullHandler(logging.Handler):
        def emit(self, record):
            pass


    LOGGER.addHandler(NullHandler())
else:
    LOGGER.addHandler(logging.NullHandler())


def main():
    import argparse

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
                                            help='generate monitoring JSON message for the given name')
    json_gen_parser.add_argument('name',
                                 choices=('monitor', 'cache', 'centres', 'events'),
                                 metavar='name',
                                 type=str.lower,
                                 help='name of the JSON message (Monitor, Cache, Centres, Events). '
                                      'Monitor and Cache JSONs are always generated together.')
    json_gen_parser.add_argument('-f', '--force-regen',
                                 action='store_true',
                                 help='force re-generation of the JSON messages')
    json_gen_parser.add_argument('-v', '--verbose',
                                 action='store_true',
                                 help='be more chatty')

    json_get_parser = subparsers.add_parser('json-get',
                                            help='get JSON message of the given date and name')
    json_get_parser.add_argument('name',
                                 choices=('monitor', 'cache', 'centres', 'events'),
                                 metavar='name',
                                 type=str.lower,
                                 help='name of the JSON message (Monitor, Cache, Centres, Events).')
    json_get_parser.add_argument('datetime',
                                 nargs='?',
                                 help='datetime of the JSON message (default to the most recent entry)')

    json_del_parser = subparsers.add_parser('json-del',
                                            help='delete JSON messages of the given date')
    json_del_parser.add_argument('name',
                                 choices=('monitor', 'cache', 'centres', 'events'),
                                 metavar='name',
                                 type=str.lower,
                                 help='name of the JSON message (Monitor, Cache, Centres, Events). '
                                      'Monitor and Cache JSONs are always deleted together.')
    json_del_parser.add_argument('datetime',
                                 nargs='?',
                                 help='datetime of the JSON message (default to the most recent entry)')

    event_add_parser = subparsers.add_parser('event-add',
                                             help='add an event')
    event_add_parser.add_argument('-t', '--title',
                                  required=True,
                                  help='title of the event')
    event_add_parser.add_argument('-d', '--description',
                                  default='',
                                  help='description of the event')
    event_add_parser.add_argument('-s', '--start_datetime_string',
                                  required=True,
                                  metavar='DATETIME',
                                  help='event start datetime (YYYY-MM-HHThh:mm:ssZ)')
    event_add_parser.add_argument('-e', '--end_datetime_string',
                                  required=True,
                                  metavar='DATETIME',
                                  help='event end datetime (YYYY-MM-HHThh:mm:ssZ)')
    event_add_parser.add_argument('--no-events-json',
                                  action='store_true',
                                  help='Do not generate Events JSON')

    event_get_parser = subparsers.add_parser('event-get',
                                             help='list all events ending after the given datetime')
    event_get_parser.add_argument('datetime',
                                  nargs='?',
                                  help='datetime for searching events (default now)')

    event_del_parser = subparsers.add_parser('event-del',
                                             help='remove an event of given id')
    event_del_parser.add_argument('id',
                                  help='id of an event')
    event_del_parser.add_argument('--no-events-json',
                                  action='store_true',
                                  help='Do not generate Events JSON')

    remarks_set_parser = subparsers.add_parser('remarks-set',
                                               help='set remarks for monitor.json')
    remarks_set_parser.add_argument('text',
                                    help="content of the remarks")

    remarks_get_parser = subparsers.add_parser('remarks-get',
                                               help='show current remarks')

    init_parser = subparsers.add_parser('init',
                                        help='initialize the working directory with a TEMPLATE of config file')

    ns = ap.parse_args()

    if ns.sub_command == 'init':
        print 'Initialising working directory at: {0}'.format(ns.working_directory)
        WisMon.init_working_directory(ns.working_directory)
        return 0

    if ns.log_to_console:
        LOGGER.addHandler(logging.StreamHandler(sys.stdout))

    try:
        wismon = WisMon(ns.working_directory)

        if ns.sub_command == 'json-gen':
            if ns.name in ('monitor', 'cache'):
                monitor, cache = wismon.monitor_cache_json_gen(force_regen=ns.force_regen)
                if ns.verbose:
                    print json.dumps(monitor.serialize(), indent=4)
                    print json.dumps(cache.serialize(), indent=4)

            elif ns.name == 'centres':
                centres = wismon.centres_json_gen(force_regen=ns.force_regen)
                if ns.verbose:
                    print json.dumps(centres.serialize(), indent=4)

            elif ns.name == 'events':
                events = wismon.events_json_gen(force_regen=ns.force_regen)
                if ns.verbose:
                    print json.dumps(events.serialize(), indent=4)

        elif ns.sub_command == 'json-get':
            msg = wismon.json_get(ns.name, ns.datetime)
            print json.dumps(msg, indent=4)

        elif ns.sub_command == 'json-del':
            if ns.name in ('monitor', 'cache'):
                wismon.json_del('monitor', ns.datetime)
                wismon.json_del('cache', ns.datetime)
                print 'Monitor and Cache JSON messages deleted'

            else:
                wismon.json_del(ns.name, ns.datetime)
                print '{0} JSON message deleted'.format(ns.name.title())

        elif ns.sub_command == 'event-add':
            wismon.event_add(ns.start_datetime_string, ns.end_datetime_string, ns.title, ns.description)
            print 'Event added'
            if not ns.no_events_json:
                wismon.events_json_gen(force_regen=True)
                print 'Events JSON generated'

        elif ns.sub_command == 'event-get':
            events = wismon.event_get(ns.datetime)
            for event in events:
                print '{id:8d} [{title}] [{start_datetime_string} - {end_datetime_string}] {text}'.format(**event)

        elif ns.sub_command == 'event-del':
            wismon.event_del(ns.id)
            print 'Event deleted'
            if not ns.no_events_json:
                wismon.events_json_gen(force_regen=True)
                print 'Events JSON generated'

        elif ns.sub_command == 'remarks-set':
            wismon.remarks_set(ns.text)

        elif ns.sub_command == 'remarks-get':
            text = wismon.remarks_get()
            print text

    except WmError as e:
        sys.stderr.write("%s: %s\n" % (__package__, e.args[0]))
        LOGGER.warning(e.args[0])
        return 1

    except Exception:
        import traceback
        traceback.print_exc()
        LOGGER.exception('Unexpected Exception')
        return 1


