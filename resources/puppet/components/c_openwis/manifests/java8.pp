###############################################################################
# == Component: Configure Java 7 for OpenWIS services (not Portals)
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
# This class ensures that the correct version of Java is installed on the
# OpenWIS servers (other than the portal servers), by invoking the 'java8'
# function with the 'is_portal' flag unset.
#
# === Notes
#
#
###############################################################################

class c_openwis::java8 (
) {
  c_openwis::java::java8 { ensure_java8:
    is_portal => false
  }
}
