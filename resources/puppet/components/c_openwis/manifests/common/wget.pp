###############################################################################
# == Component: Use WGET to download a file to the downloads folder
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
# This common function simplifies the downloading of a file from a remote
# repository.
#
# === Notes
#
# If the 'source' parameter is mandatory and must refer to a file in a remote
# repository that will be downloaded.
#
# The optional 'creates' parameter can be used to specify a file that will
# checked for non-existence before the file is downloaded. This can be used to
# ensure that the file is only downloaded once. (see the Exec function's
# 'creates' parameter details in the main Puppet documentation)
#
# The optional 'unless' parameter can be used to specify a command that will
# be executed before the file is downloaded, with the download only happening if
# this command has a non-zero exit code. This can be used to ensure that the
# file is only downloaded once. (see the Exec function's 'unless' parameter
# details in the main Puppet documentation)
#
###############################################################################

define c_openwis::common::wget (
  $source,
  $exec_user = openwis,
  $unless    = undef,
  $creates   = undef)
#
{
  include c_openwis::common::wget_package
  include c_openwis

  $use_proxy     = $c_openwis::wget_use_proxy
  $http_proxy    = $c_openwis::wget_http_proxy
  $https_proxy   = $c_openwis::wget_https_proxy
  $openwis_home  = $c_openwis::home_dir
  $downloads_dir = $c_openwis::downloads_dir

  if $use_proxy {
    $env = ["http_proxy=${http_proxy}", "https_proxy=${https_proxy}"]
  } else {
    $env = []
  }

  $source_split = split($source, "/")
  $filename     = $source_split[-1]

  $command      = "wget \"${source}\""

  if $unless != undef {
    $_unless = $unless
  } else {
    $_unless = "test -f ${downloads_dir}/${filename}"
  }

  ensure_resource(file, "${downloads_dir}", {
    ensure => directory,
    owner  => root,
    mode   => "0777"
  }
  )

  exec { "wget-${filename}":
    command     => "${command}",
    cwd         => "${downloads_dir}",
    timeout     => 0,
    unless      => $_unless,
    creates     => $creates,
    environment => $env,
    user        => "${exec_user}",
    path        => [$::path, "."],
    require     => [Package[wget], File["${downloads_dir}"]],
  }
}
