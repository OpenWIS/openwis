###############################################################################
# == Component: Configure OpenWIS PostgreSQL service
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
# This script sets the system limits for the 'postgers' user
#
# === Notes
#
#
###############################################################################

class c_openwis::postgres (
)
#
{
  # Set the security limits
  file { "/etc/security/limits.d/99-postgres.conf":
    ensure  => file,
    owner   => root,
    group   => root,
    mode    => "0644",
    backup  => false,
    content => template("c_openwis/limits/postgres.conf"),
  }
}
