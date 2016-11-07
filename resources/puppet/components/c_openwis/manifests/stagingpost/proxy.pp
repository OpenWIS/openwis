###############################################################################
# == Component: Configure the Apache proxy for OpenWIS Staging Post
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
# configuration for the Staging Post.
#
# === Notes
#
#
###############################################################################

class c_openwis::stagingpost::proxy (
)
#
{
  include c_openwis
  include c_openwis::apache

  $staging_post_host = $c_openwis::staging_post_host

  # configure Apache
  c_openwis::apache::conf_file { "stagingpost.conf":
    template => "c_openwis/stagingpost/apache/stagingpost.conf",
  }
}
