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
# OpenWIS portal servers, by invoking the 'java7' function with the 'is_portal'
# flag set.
#
# === Notes
#
#
###############################################################################

class c_openwis::java7_portal (
) {
  c_openwis::java::java7 { ensure_java7_portal:
    is_portal => true
  }
}

