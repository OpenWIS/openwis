###############################################################################
# == Component: Configure the Apache proxy for each OpenWIS Portal
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
# configuration for the relevant portal.
#
# === Notes
#
# The 'portal' parameter specifies the "name" of the portal, which must be
# either 'user' or 'admin'.
#
###############################################################################

define c_openwis::portals::portal_proxy (
  $portal       = $title)
#
{
  include c_openwis
  include c_openwis::apache

  $admin_portal_host = $c_openwis::admin_portal_host
  $user_portal_host  = $c_openwis::user_portal_host

  case $portal {
    "admin" : {
      $portal_type = "adminportal"
    }
    "user"  : {
      $portal_type = "userportal"
    }
  }

  # configure the apache proxy for the relevant portal
  c_openwis::apache::conf_file { "${portal_type}.conf":
    template => "c_openwis/${portal_type}/apache/${portal_type}.conf",
  }
}
