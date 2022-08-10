import os
import sqlite3 as lite
import contextlib
import logging
import re
from collections import namedtuple, defaultdict

import pg8000

LOGGER = logging.getLogger('wismon')


def regexp(expr, item):
    pattern = re.compile(expr, re.IGNORECASE)
    return pattern.search(item) is not None


@contextlib.contextmanager
def connect_openwisdb(host, port, user, password, database):
    LOGGER.info('Connecting to: {0}'.format(host))
    conn = pg8000.connect(host=host,
                          port=port,
                          database=database,
                          user=user,
                          password=password)
    cursor = conn.cursor()
    yield cursor
    LOGGER.info('Disconnecting from: {0}'.format(host))
    cursor.close()
    conn.close()


def query_openwis(host, port, user, password, database='OpenWIS'):
    with connect_openwisdb(host, port, user, password, database) as cursor:
        cursor.execute("""
        SELECT
          m.id,
          m.uuid,
          m.localimportdate,
          ct.name    AS category,
          COALESCE(mc.n_mapped_files, 0),
          COALESCE(mc.cache_size_bytes, 0),
          ct.name='draft' AS is_stopgap,
          m.createdate,
          m.owner
        FROM metadata m FULL JOIN openwis_product_metadata p
        ON m.uuid = p.urn
        FULL JOIN (SELECT mm.product_metadata_id, count(*) AS n_mapped_files, CAST(sum(cf.filesize) AS BIGINT) AS cache_size_bytes
        FROM openwis_mapped_metadata mm FULL JOIN openwis_cached_file cf
        ON mm.cached_file_id = cf.cached_file_id
        GROUP BY mm.product_metadata_id) AS mc
        ON p.product_metadata_id = mc.product_metadata_id
        FULL JOIN categories ct
        ON m.category = ct.id
        WHERE m.istemplate='n'
        ORDER BY m.id;
        """)

        rows = cursor.fetchall()

    return rows


GroupedStats = namedtuple('GroupedStats', ['n_metadata', 'n_mapped_files', 'size'])


# noinspection SqlResolve
class WisMonDB(object):
    sql_schema_wismon_metadata = """
    CREATE TABLE IF NOT EXISTS wismon_metadata (
        id INTEGER NOT NULL PRIMARY KEY,
        uuid VARCHAR(255) NOT NULL UNIQUE,
        localimportdate DATETIME NOT NULL,
        category VARCHAR(32),
        n_mapped_files INTEGER NOT NULL,
        cache_size_bytes INTEGER NOT NULL,
        is_stopgap bool NOT NULL,
        createdate DATETIME NOT NULL,
        owner VARCHAR(255),
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    )"""

    sql_schema_wismon_rest = """
    -- Events
    CREATE TABLE IF NOT EXISTS wismon_events (
        id INTEGER NOT NULL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        text TEXT,
        startdatetime DATETIME NOT NULL,
        enddatetime DATETIME NOT NULL,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    );

    -- Remarks
    CREATE TABLE IF NOT EXISTS wismon_remarks (
        id INTEGER NOT NULL PRIMARY KEY,
        text TEXT NOT NULL,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    );

    -- JSON messages
    CREATE TABLE IF NOT EXISTS wismon_named_json (
        id INTEGER NOT NULL PRIMARY KEY,
        datetime DATETIME NOT NULL,
        name VARCHAR(255) NOT NULL,
        content TEXT NOT NULL,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    );

    -- metadata snapshot from previous day
    CREATE TABLE IF NOT EXISTS old_wismon_metadata (
        id INTEGER NOT NULL PRIMARY KEY,
        uuid VARCHAR(255) NOT NULL UNIQUE,
        localimportdate DATETIME NOT NULL,
        category VARCHAR(32),
        n_mapped_files INTEGER NOT NULL,
        cache_size_bytes INTEGER NOT NULL,
        is_stopgap bool NOT NULL,
        createdate DATETIME NOT NULL,
        owner VARCHAR(255),
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    );

    -- Metadata source breakdown
    CREATE TABLE IF NOT EXISTS wismon_md_breakdown (
        id INTEGER NOT NULL PRIMARY KEY,
        date NOT NULL,
        type VARCHAR(10) NOT NULL,
        key VARCHAR(10) NOT NULL,
        n_mapped_files INTEGER NOT NULL,
        cache_size_bytes INTEGER NOT NULL
    );
    """

    def __init__(self, db_file):
        self.db_file = db_file
        if not os.path.exists(db_file):
            LOGGER.info('Setting up new database file')

        LOGGER.info('Connecting to: {0}'.format(db_file))
        self.conn = lite.connect(db_file)
        self.cursor = self.conn.cursor()

        self.conn.execute(WisMonDB.sql_schema_wismon_metadata)
        self.conn.executescript(WisMonDB.sql_schema_wismon_rest)

        # Case-insensitive regex
        self.conn.create_function('REGEXP', 2, regexp)

    def __del__(self):
        LOGGER.info('Disconnecting from: {0}'.format(self.db_file))
        self.cursor.close()
        self.conn.close()

    def archive_metadata(self):
        self.cursor.executescript(
            "DROP TABLE IF EXISTS old_wismon_metadata;\n"
            "ALTER TABLE wismon_metadata RENAME TO old_wismon_metadata;\n"
            "{0};\n".format(WisMonDB.sql_schema_wismon_metadata)
        )

    def restore_metadata(self, datetime_string):
        self.cursor.execute('DELETE FROM wismon_md_breakdown WHERE date = ?', (datetime_string,))
        self.cursor.executescript(
            'DROP TABLE IF EXISTS wismon_metadata;\n'
            'ALTER TABLE old_wismon_metadata RENAME TO wismon_metadata;'
        )

    def save_metadata(self, rows):
        self.cursor.executemany(
            "INSERT INTO wismon_metadata "
            "(id, uuid, localimportdate, category, n_mapped_files, cache_size_bytes, "
            "is_stopgap, createdate, owner)"
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            rows
        )
        self.conn.commit()

    def group_by_metadata_status(self, where_expr):
        self.cursor.execute(
            "SELECT count(uuid), "
            "coalesce(sum(n_mapped_files), 0), coalesce(sum(cache_size_bytes), 0), "
            "is_stopgap "
            "FROM wismon_metadata "
            "WHERE {0} "
            "GROUP BY is_stopgap".format(where_expr)
        )
        d = {}
        for row in self.cursor.fetchall():
            d[row[3]] = GroupedStats(row[0], row[1], row[2])

        return defaultdict(
            lambda: GroupedStats(0, 0, 0), **d
        )

    def stats_inserted_modified(self, wimms_name):
        try:
            self.cursor.execute(
                "SELECT count(*) FROM wismon_metadata m "
                "LEFT OUTER JOIN old_wismon_metadata o ON m.id = o.id "
                "WHERE (o.id IS NULL OR m.localimportdate > o.localimportdate) "
                "AND (m.category LIKE 'WIS-GISC-%' OR m.category in ('{0}', '{1}'))".format('draft', wimms_name)
            )
            return self.cursor.fetchone()[0]
        except lite.OperationalError:  # in case archived table not exists
            return 0

    def stats_deleted(self, wimms_name):
        try:
            self.cursor.execute(
                "SELECT count(*) FROM old_wismon_metadata o "
                "LEFT OUTER JOIN wismon_metadata m ON o.id = m.id "
                "WHERE m.id IS NULL "
                "AND (o.category LIKE 'WIS-GISC-%' OR o.category in ('{0}', '{1}'))".format('draft', wimms_name)
            )
            return self.cursor.fetchone()[0]
        except lite.OperationalError:
            return 0

    def json_exists(self, name, datetime_string):
        return self.json_get(name, datetime_string) is not None

    def json_save(self, name, datetime_string, named_json):
        self.cursor.execute(
            "INSERT OR REPLACE INTO wismon_named_json "
            "(datetime, name, content) "
            "VALUES (?, ?, ?)",
            (datetime_string, name, named_json.serialize())
        )
        self.conn.commit()

    def json_get(self, name, datetime_string):
        if datetime_string is None:  # get the last entry
            return self.cursor.execute(
                "SELECT id, datetime, name, content "
                "FROM wismon_named_json WHERE name = ? ORDER BY id DESC LIMIT 1",
                (name,)
            ).fetchone()
        else:
            return self.cursor.execute(
                "SELECT id, datetime, name, content "
                "FROM wismon_named_json WHERE datetime = ? AND name = ?",
                (datetime_string, name)
            ).fetchone()

    def json_del(self, name, datetime_string):
        if datetime_string is None:  # delete the last entry
            self.cursor.execute(
                "DELETE FROM wismon_named_json "
                "WHERE id = (select max(id) FROM wismon_named_json WHERE name = ?) ",
                (name,)
            )
        else:
            self.cursor.execute(
                "DELETE FROM wismon_named_json "
                "WHERE datetime = ? AND name = ?",
                (datetime_string, name)
            )
        self.conn.commit()
        return self.cursor.rowcount

    def json_throttle(self, name, n_retain):
        # Check whether there are enough number of messages
        n_existing = self.cursor.execute(
            "SELECT count(*) FROM wismon_named_json WHERE name = ?",
            (name,)
        ).fetchone()[0]

        if n_existing > n_retain:
            self.cursor.execute(
                "DELETE FROM wismon_named_json "
                "WHERE name = ? and id < "
                "(SELECT min(id) FROM "
                "(SELECT id FROM wismon_named_json WHERE name = ? ORDER BY id DESC LIMIT ?))",
                (name, name, n_retain)
            )
            self.conn.commit()
            return self.cursor.rowcount
        else:
            return 0

    def event_add(self, startdate_string, enddate_string, title, text):
        self.cursor.execute(
            "INSERT INTO wismon_events (title, text, startdatetime, enddatetime)"
            "VALUES (?, ?, ?, ?)",
            (title, text, startdate_string, enddate_string)
        )
        self.conn.commit()

    def events_get(self, date_string):
        return self.cursor.execute(
            "SELECT id, title, text, startdatetime, enddatetime "
            "FROM wismon_events WHERE enddatetime >= ?",
            (date_string,)
        ).fetchall()

    def event_del(self, eid):
        self.cursor.execute(
            "DELETE FROM wismon_events WHERE id = ?",
            (eid,)
        )
        self.conn.commit()
        return self.cursor.rowcount

    def remarks_set(self, text):
        self.cursor.execute(
            "INSERT INTO wismon_remarks (text) VALUES (?);", (text,)
        )
        self.conn.commit()

    def remarks_get(self):
        qrs = self.cursor.execute(
            "SELECT text FROM wismon_remarks ORDER BY id DESC LIMIT 1"
        ).fetchone()
        if qrs:
            return qrs[0]
        else:
            return None

    def calc_metadata_breakdown(self, date_string):
        self.cursor.executescript(
            """
            -- Create the temp table for further analysis
            CREATE TEMP TABLE wismon_gts_md AS
            SELECT substr(uuid, 27, 10) AS TTAAiiCCCC, n_mapped_files, cache_size_bytes FROM wismon_metadata
            WHERE uuid REGEXP 'urn:x-wmo:md:int.wmo.wis::[A-Z]{{4}}[0-9]{{2}}[A-Z]{{4}}';

            INSERT INTO wismon_md_breakdown (date, type, key, n_mapped_files, cache_size_bytes)
            SELECT
              '{0}' as date,
              'TT' as type,
              substr(TTAAiiCCCC, 1, 2) as key,
              sum(n_mapped_files) as n_mapped_files,
              sum(cache_size_bytes) as cache_size_bytes
            FROM wismon_gts_md
            GROUP BY substr(TTAAiiCCCC, 1, 2)
            UNION
            SELECT
              '{0}' as date,
              'C4' as type,
              substr(TTAAiiCCCC, 7, 4) as key,
              sum(n_mapped_files) as n_mapped_files,
              sum(cache_size_bytes) as cache_size_bytes
            FROM wismon_gts_md
            GROUP BY substr(TTAAiiCCCC, 7, 4);
            """.format(date_string)
        )
        self.conn.commit()
