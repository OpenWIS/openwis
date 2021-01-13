###############################################################################
# == Component: Configure Java 7 for OpenWIS Portals
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
# OpenWIS portal servers, by invoking the 'java8' function with the 'is_portal'
# flag set.
#
# === Notes
#
#
###############################################################################

class c_openwis::java8_portal (
) {
  c_openwis::java::java8 { ensure_java8_portal:
    is_portal => true
  }
}

