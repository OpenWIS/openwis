###############################################################################
# == Component: copies file only if $source is defined
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
# This common function allows the optional copying of a file, handling the
# scenario where the input source file is not supplied.
#
# === Notes
#
#
###############################################################################

define c_openwis::common::copy_optional_file (
  $source      = undef,
  $destination = $title,
  $owner)
#
{
  if $source != undef {
    file { "${destination}":
      ensure => file,
      owner  => $owner,
      backup => false,
      source => "${source}",
    }
  }
}
