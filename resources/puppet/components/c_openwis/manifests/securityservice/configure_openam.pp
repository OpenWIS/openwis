###############################################################################
# == Component: Configure OpenAM (basic configuration only)
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
# This function performs the base OpenAM configuration, which set-up the
# following:
#
# * Creates & configures the 'amAdmin' OpenAM administration user
# * Confiures the OpenAM server URL & cookie domain
# * Sets the OpenAM locale
# * Sets the OpenAM data/configuration folder
# * Sets the OpenAM listen ports (for internal connections)
# * Configures OpenAM to use the pre-configured OpenDJ installation
#
# === Notes
#
# Currently, this fuction peforms the configuration steps by using CURL to
# automate the OpenAM browser-based initial configuration process, which means
# this currently, this function can only perform the initial configuration steps
# that are performed before the OpenWIS 'amAdmin' administration user is
# created.  Indeed - this script creates this user.
#
# Once the 'amAdmin' user has been created, all browser-based interaction with
# OpenAM requires an authenticated user. The way the OpenAM manages this makes,
# difficult/complex to emulate in CURL.
#
###############################################################################

#============================================================================
# Configure OpenAM
#============================================================================
define c_openwis::securityservice::configure_openam (
  $openam_admin_password,
  $openam_server_url,
  $openam_cookie_domain,
  $openam_locale,
  $opendj_host,
  $opendj_root_password,
  $openam_url_password)
#
{
  include c_openwis

  $auth_service_host = $c_openwis::auth_service_host
  $templates         = "c_openwis/securityservice/openam"
  $openwis_home      = $c_openwis::home_dir
  $configure_dir     = "${openwis_home}/configure_openam"
  $openam_dir        = "${openwis_home}/openam"

  file { "${configure_dir}":
    ensure  => directory,
    require => File["${openwis_home}"]
  } ->
  file { "${configure_dir}/wait_for_openam.sh":
    ensure  => file,
    mode    => "0774",
    content => template("${templates}/wait_for_openam.sh")
  } ->
  file { "${configure_dir}/configure_openam.sh":
    ensure  => file,
    mode    => "0774",
    content => template("${templates}/configure_openam.sh")
  } ->
  exec { wait-for-openam:
    cwd     => "${configure_dir}",
    command => "wait_for_openam.sh",
    creates => "${openam_dir}/bootstrap",
    path    => ["${configure_dir}", $::path],
  } ->
  exec { "configure_openam.sh":
    cwd     => "${configure_dir}",
    command => "configure_openam.sh",
    creates => "${openam_dir}/bootstrap",
    path    => ["${configure_dir}", $::path],
  }
}
