###############################################################################
# == Component: execute a command once only
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
# This common function allows a command to be executed once only, where there
# is no simple check that can be used by the Exec function's 'creates',
# 'unless' or 'onlyif' checks.
#
# The function achieves this by taking a 'touchfile' parameter, which is checked
# by the ' creates' check Exec function, causing the Exec to be skipped if the
# file exists.
#
# The touchfile is then created, if the Exec succeeds, ensuring that the Exec
# will run once only.
#
# === Notes
#
#
###############################################################################

define c_openwis::common::exec_once (
  $cwd           = undef,
  $command,
  $user,
  $path          = undef,
  $touchfile,
  $touchfilemode = "0444")
#
{
  exec { "exec-${title}":
    cwd     => $cwd,
    command => "${command}",
    user    => "${user}",
    path    => $path,
    creates => "${touchfile}",
  } ->
  file { "${touchfile}":
    ensure => file,
    owner  => "${user}",
    mode   => "${touchfilemode}",
  }

}

