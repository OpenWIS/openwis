###############################################################################
# == Component: Configure the Apache proxy for the OpenWIS Portals
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
# This class wraps the 'portal_proxy' function to allow multiple portal proxy
# configurations to be deployed to the same Apache installation.
#
# === Notes
#
# The 'portals' parameter must be a list of portal names.
#
###############################################################################

class c_openwis::portals::proxy (
  $portals = [])
#
{
  c_openwis::portals::portal_proxy { $portals:
  }
}
