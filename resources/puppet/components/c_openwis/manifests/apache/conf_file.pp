###############################################################################
# == Component: Manage an Apache configuration file
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
# This common function simplifies the management of Apache configuration
# files, by reducing the number of parameters that need to be passed from the
# main scripts.
#
# === Notes
#
#
###############################################################################

define c_openwis::apache::conf_file (
  $template,
  $conf_file = $title)
#
{
  include c_openwis
  include c_openwis::apache

  $httpd_conf_dir = $c_openwis::apache::httpd_conf_dir

  file { "${httpd_conf_dir}/${conf_file}":
    ensure  => file,
    content => template("${template}"),
    owner   => root,
    group   => root,
    mode    => "0444",
    notify  => Service[httpd],
  }
}
