###############################################################################
# # == Component: Build & Configure OpenWIS Database schema
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
# This class initialises & manages the OpenWIS PostgreSQL database server,
# as follows:
#
# * Ensures that the 'postgres' user's service limits are correctly set
# * Initialises the database, if required
# * Enables remote access to the database by:
#   - Updating the PostgreSQL configuration to allow remote connections
#   - Updating iptables to allow remote connections
# * Configures the PostgreSQL database defaults:
#   - Increases the 'max_stack_depth' to 10MB
#   - Sets the 'log_line_prefix' to include the date
# * Creates the OpenWIS database, if required
# * Initialises the OpenWIS schema, if required.
#
# === Notes
#
#
###############################################################################

class c_openwis::database (
)
#
{
  require c_openwis::postgres

  include c_openwis::common::systemd

  $db_initialised = $::db_initialised

  Exec {
    user    => postgres,
    timeout => 0,
  }

  # systemd doesn"t use the limits in limits.conf
  # - the service config must be updated as well
  c_openwis::common::service_limits { update-postgresql-service-config:
    service => "postgresql-9.4",
    nofile  => "8192",
    stack   => "12582912",
  }

  # initialise the database, if required
  exec { initialise-database:
    command => "/usr/pgsql-9.4/bin/initdb --locale en_US.UTF-8 -D /var/lib/pgsql/data",
    creates => "/var/lib/pgsql/data/postgresql.conf",
  }

  # ensure remote connections are allowed
  file_line { "pg_hba.conf: enable remote":
    path    => "/var/lib/pgsql/data/pg_hba.conf",
    line    => "host    all     all        0.0.0.0/0            password",
    require => Exec[initialise-database],
    notify  => Exec[restart-postgresql-service],
  }

  firewall { "888 allow from all to 5432 (PostgreSQL) on all":
    proto  => tcp,
    action => accept,
    source => "0.0.0.0/0",
    dport  => 5432,
  }

  # set PostgreSQL to list on all interfaces
  file_line { "postgresql.conf: listen_addresses":
    ensure  => present,
    path    => "/var/lib/pgsql/data/postgresql.conf",
    line    => "listen_addresses = '*'",
    match   => "^.?listen_addresses",
    require => Exec[initialise-database],
    notify  => Exec[restart-postgresql-service],
  }

  # set the Max Stack Depth
  file_line { "postgresql.conf: max_stack_depth":
    ensure  => present,
    path    => "/var/lib/pgsql/data/postgresql.conf",
    line    => "max_stack_depth = 10MB",
    match   => "^.?max_stack_depth",
    require => Exec[initialise-database],
    notify  => Exec[restart-postgresql-service],
  }

  # configure logging to include the timestamp
  file_line { "postgresql.conf: log_line_prefix":
    ensure  => present,
    path    => "/var/lib/pgsql/data/postgresql.conf",
    line    => "log_line_prefix = '%t'",
    match   => "^.?log_line_prefix",
    require => Exec[initialise-database],
    notify  => Exec[restart-postgresql-service],
  }

  # restart the PostgeSQL service, when triggered
  exec { restart-postgresql-service:
    command     => "/usr/sbin/service postgresql-9.4 restart",
    user        => root,
    refreshonly => true,
    require     => Exec[systemd-daemon-reload],
  }

  # install the Database
  if $db_initialised == "no" {
    # Install pre-requisites for OpenWIS database
    c_openwis::database::create_database { create_database:
    } ->
    # Install OpenWIS Schema
    c_openwis::database::create_openwis_schema { create_openwis_schema:
    }
  }
}
