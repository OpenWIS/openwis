###############################################################################
# == Component: Ensure the 'wget' package is installed
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
#
# === Notes
#
#
###############################################################################

class c_openwis::common::wget_package (
)
#
{
  package { wget:
    ensure => installed,
  }
}
