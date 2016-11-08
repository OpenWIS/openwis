###############################################################################
# == Component: Create the PostgreSQL database
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
# This function creates & configures the base OpenWIS database, as follows:
#
# * Creates the OpenWIS database
# * Installs PostGIS 2.1
#
# === Notes
#
# This function only runs if the database hasn't already been initialised
# (i.e. - the '/var/lib/pgsql/data/global' folder doesn't exist).
#
###############################################################################

define c_openwis::database::create_database (
)
#
{
  $db_initialised = $::db_initialised
  $db_name        = "OpenWIS"

  # Install pre-requisites for OpenWIS database
  if $db_initialised == "no" {
    exec { create-database:
      command => "/usr/pgsql-9.4/bin/createdb ${db_name}",
      require => Exec[restart-postgresql-service],
    } ->
    c_openwis::database::exec_psql { "postgis.sql":
      file => "/usr/pgsql-9.4/share/contrib/postgis-2.1/postgis.sql",
    } ->
    c_openwis::database::exec_psql { "postgis_comments.sql":
      file => "/usr/pgsql-9.4/share/contrib/postgis-2.1/postgis_comments.sql",
    } ->
    c_openwis::database::exec_psql { "spatial_ref_sys.sql":
      file => "/usr/pgsql-9.4/share/contrib/postgis-2.1/spatial_ref_sys.sql",
    } ->
    c_openwis::database::exec_psql { "legacy.sql":
      file => "/usr/pgsql-9.4/share/contrib/postgis-2.1/legacy.sql",
    }
  }
}

