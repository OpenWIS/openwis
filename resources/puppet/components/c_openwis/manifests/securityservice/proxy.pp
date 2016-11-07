###############################################################################
# == Component: Configure the Apache proxy for OpenWIS Security Service
#               (OpenAM & IDP Discovery)
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
# This function updates an existing Apache installation to include the proxy
# configuration for OpenAM & IDP Discovery .
#
# === Notes
#
#
###############################################################################

class c_openwis::securityservice::proxy (
)
#
{
  include c_openwis
  include c_openwis::apache

  $auth_service_host = $c_openwis::auth_service_host
  $templates         = "c_openwis/securityservice/openam"

  # configure Apache
  c_openwis::apache::conf_file { "openam.conf":
    template => "${templates}/apache/openam.conf",
  }
}
