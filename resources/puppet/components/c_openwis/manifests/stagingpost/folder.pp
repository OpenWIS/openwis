###############################################################################
# == Component: Ensure that the 'stagingPost' folder exists
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
# This class defines the location of the Staging Post data folder & ensures
# that it exists.
#
# === Notes
#
#
###############################################################################

class c_openwis::stagingpost::folder (
  $staging_post_dir = undef)
#
{
  include c_openwis

  if $staging_post_dir == undef {
    $_staging_post_dir = "${c_openwis::opt_dir}/stagingPost"
  } else {
    $_staging_post_dir = $staging_post_dir
  }

  # configure Apache
  file { "${_staging_post_dir}":
    ensure => directory,
    owner  => openwis,
    group  => openwis,
    mode   => "0660"
  }
}

