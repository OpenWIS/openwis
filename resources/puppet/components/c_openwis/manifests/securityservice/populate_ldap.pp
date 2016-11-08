# ##############################################################################
# == Component: Populate the OpenDJ LDAP database
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
# This function initialises & populates the OpenWIS OpenDJ LDAP database,
# as follows:
#
# * Downloads & unpacks the LDAP population script
# * Configures an 'expect' script for this environment
# * Executes the LDAP population script, via the expect script to initialis
#   the LDAP database
#
# === Notes
#
#
###############################################################################

define c_openwis::securityservice::populate_ldap (
  $admin_login,
  $admin_password,
  $admin_first_name,
  $admin_last_name)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads

  $openwis_repo      = $c_openwis::openwis_repo
  $auth_service_host = $c_openwis::auth_service_host
  $deployment_name   = $c_openwis::deployment_name
  $admin_email       = $c_openwis::admin_email
  $openwis_home      = $c_openwis::home_dir
  $populateldap_dir  = "${openwis_home}/PopulateLDAP"
  $templates         = "c_openwis/securityservice/opendj"
  $ssoadmintools_dir = "${openwis_home}/ssoAdminTools"

  # download & unpack the PopulateLDAP script
  c_openwis::common::unzip { "PopulateLDAP.zip":
    source      => "${openwis_repo}/PopulateLDAP.zip",
    destination => "${populateldap_dir}",
    user        => openwis,
    creates     => "${populateldap_dir}",
    before      => Class[c_openwis::common::tidy_downloads]
  } ->
  # configure the script & ensure that it is executable
  file { "${populateldap_dir}/populateLDAP.sh":
    ensure  => file,
    mode    => "0744",
    backup  => false,
    content => template("${templates}/populateLDAP.sh"),
  } ->
  # configure the associated populateLDAP.expect
  file { "${populateldap_dir}/populateLDAP.expect":
    ensure  => file,
    mode    => "0744",
    backup  => false,
    content => template("${templates}/populateLDAP.expect"),
  } ->
  # run the PopulateLDAP script via expect
  c_openwis::common::exec_once { PopulateLDAP:
    cwd       => "${populateldap_dir}",
    command   => "expect populateLDAP.expect",
    user      => openwis,
    touchfile => "${populateldap_dir}/.populateldap_complete",
    path      => $::path,
    require   => [Package[expect], File["${ssoadmintools_dir}/.ssoadm_complete"]],
  }
}
