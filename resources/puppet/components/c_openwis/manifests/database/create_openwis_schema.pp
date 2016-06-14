###############################################################################
# == Component: Create the OpenWIS schema
#
# === Authors
#
# Martin Gollogly <martin.gollogly@metoffice.gov.uk>
# Andy Jacobs <andy.jacobs@metoffice.gov.uk>
#
# === Copyright
#
# Crown Copyright 2016, unless otherwise noted.
#
# === Actions
#
# This function initilalises the OpenWIS schema, as follows:
#
# * Creates the 'citext' extension
# * Initialises the 'openwis' DB role
# * Builds the OpenWIS schema
# * Purges the schema, to ensure that there is no erroneous data
# * Creates the PostGIS spatial index
# * Adds the required base configuration data
#
# === Notes
#
# This function only runs if the database hasn't already been initialised
# (i.e. - the '/var/lib/pgsql/data/global' folder doesn't exist).
#
###############################################################################

define c_openwis::database::create_openwis_schema (
)
#
{
  $db_initialised    = $::db_initialised
  $database_password = $c_openwis::database_password

  if $db_initialised == "no" {
    c_openwis::database::exec_psql { "citext.sql":
      template => "c_openwis/database/citext.sql",
    } ->
    c_openwis::database::exec_psql { "openwis-roles.sql":
      template => "c_openwis/database/openwis-roles.sql",
    } ->
    c_openwis::database::exec_psql { "schema.ddl":
      template => "c_openwis/database/schema.ddl",
      user     => openwis,
    } ->
    c_openwis::database::exec_psql { "purge.sql":
      template => "c_openwis/database/purge.sql",
      user     => openwis,
    } ->
    c_openwis::database::exec_psql { "openwis-3.14.sql":
      template => "c_openwis/database/openwis-3.14.sql",
      user     => openwis,
    } ->
    c_openwis::database::exec_psql { "create-postgis-spatialindex.sql":
      template => "c_openwis/database/create-postgis-spatialindex.sql",
      user     => openwis,
    } ->
    c_openwis::database::exec_psql { "create_db-postgres.sql":
      template => "c_openwis/database/create_db-postgres.sql",
      user     => openwis,
    } ->
    c_openwis::database::exec_psql { "data-db-postgres.sql":
      template => "c_openwis/database/data-db-postgres.sql",
      user     => openwis,
    }
  }
}
