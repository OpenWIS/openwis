###############################################################################
# == Component: Unzip a file, optionally downloading it first
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
# This common function simplifies the uzipping of a file, optionally downloading
# it first.
#
# === Notes
#
# The option 'overwrite' parameter allows the contents of the destination to be
# overwritten.  This defaults to FALSE.
#
# Both the 'source' & 'file' parameters are optional, but at least one of them
# must be supplied.  If both parameters are supplied, the 'file' parameter
# takes precedence.
#
# If the 'source' parameter is supplied, then it must refer to a file in a
# remote repository that will be downloaded.
#
# If the 'file' parameter is supplied, then it must refer to a file that
# already exists on the local file system.
#
# The 'destination' parameter is mandatory and refers to the folder into which
# the file will be unzipped.  If this folder alerady exists, then it must be
# empty (unless the 'overwrite' parameter is TRUE).  If the folder doesn't
# exist, then it's parent folder must already exist.
#
# The optional 'creates' parameter can be used to specify a file that will
# checked for non-existence before the file is unzipped. This can be used to
# ensure that the file is only unzipped once. (see the Exec function's 'creates'
# parameter details in the main Puppet documentation)
#
# The optional 'unless' parameter can be used to specify a command that will
# be executed before the file is unzipped, with the unzip only happening if
# this command has a non-zero exit code. This can be used to ensure that the
# file is only unzipped once. (see the Exec function's 'unless' parameter
# details in the main Puppet documentation)
#
###############################################################################

define c_openwis::common::unzip (
  $overwrite = false,
  $source    = undef,
  $file      = undef,
  $destination,
  $user,
  $creates   = undef,
  $unless    = undef)
#
{
  include c_openwis::common::unzip_package

  $_overwrite = $overwrite ? {
    true    => "-o",
    false   => "-n",
    default => ""
  }

  if $unless == undef {
    if $creates != undef {
      $_creates = $creates
    } else {
      $_creates = $destination
    }
  }else {
      $_creates = undef
  }

  if $file != undef {
    $_source = $file
  }
  #
   elsif $source != undef {
    $source_split  = split($source, "/")
    $filename      = $source_split[-1]

    $downloads_dir = $c_openwis::downloads_dir
    $_source       = "${downloads_dir}/${filename}"

    c_openwis::common::wget { "${filename}":
      source  => "${source}",
      before  => Exec["unzip-${title}"],
      unless  => $unless,
      creates => $_creates,
    }
    #
  } else {
    $_source = "UNDEFINED"
  }

  exec { "unzip-${title}":
    command => "unzip ${_overwrite} ${_source} -d ${destination}",
    timeout => 0,
    user    => "${user}",
    creates => $_creates,
    unless  => $unless,
    path    => $::path,
    require => Package[unzip],
  }
}
