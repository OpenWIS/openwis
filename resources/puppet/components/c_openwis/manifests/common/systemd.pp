###############################################################################
# == Component: Reload systemd configuration
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
# This common class simplifies the reloading of the systemctl deamon.
#
# === Notes
#
#
###############################################################################

class c_openwis::common::systemd (
)
#
{
  exec { systemd-daemon-reload:
    command     => "/bin/systemctl daemon-reload",
    timeout     => 0,
    user        => root,
    refreshonly => true,
  }
}
