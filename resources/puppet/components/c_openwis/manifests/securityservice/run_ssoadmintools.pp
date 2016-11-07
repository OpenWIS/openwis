# ##############################################################################
# == Component: Run the SSO Admin Tools for OpenAM
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
# This function runs the OpenAM SSO Admin tools to set-up the OpenWIS
# Single Sign-On (SSO) configuration for OpenAM, as follows:
#
# * Downloads & unpacks the SSO Admin Tools
# * Installs the SSO Admin Tools
# * Configures the SSO Admin Tools for this environment
#   - sets the OpenAM password
#   - gets the OpenAM config file (amUser.xml)
#   - gets the OpenAM attributes file (attrs.properties)
# * Executes the SSO Admin Tools to:
#   - remove the iPlanetAMUserService
#   - re-create the iPlanetAMUserService (from the amUser.xml configuration)
#   - update LDAP with the latest configuration
#
# === Notes
#
#
###############################################################################

define c_openwis::securityservice::run_ssoadmintools (
  $openam_admin_password,
  $opendj_root_password)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads

  $binaries_repo         = $c_openwis::binaries_repo
  $templates             = "c_openwis/securityservice/ssoAdminTools"
  $openwis_home          = $c_openwis::home_dir
  $ssoadmintools_dir     = "${openwis_home}/ssoAdminTools"
  $openam_dir            = "${openwis_home}/openam"

  # download & unpack the SSOAdminTools
  c_openwis::common::unzip { "ssoAdminTools.zip":
    source      => "${binaries_repo}/ssoAdminTools.zip",
    destination => "${ssoadmintools_dir}",
    creates     => "${ssoadmintools_dir}",
    user        => tomcat,
    before      => Class[c_openwis::common::tidy_downloads],
    require     => File["${openwis_home}"]
  } ->
  # run the ssoAdminTools/setup script
  exec { ssoAdminTools_setup:
    cwd         => "${ssoadmintools_dir}",
    command     => "setup --path ${openam_dir} --acceptLicense --debug ${openam_dir}/openam/debug --log ${openam_dir}/openam/log",
    environment => "JAVA_HOME=/usr/lib/jvm/jre",
    user        => tomcat,
    creates     => "${ssoadmintools_dir}/openam/bin",
    path        => ["${ssoadmintools_dir}", $::path],
    require     => Package[expect],
  } ->
  # configure the OpenAM password for the following steps
  file { "${ssoadmintools_dir}/openam/bin/passwd":
    ensure  => file,
    backup  => false,
    owner   => tomcat,
    mode    => "0400",
    content => template("${templates}/passwd"),
  } ->
  # copy the amUser.xml file
  file { "${ssoadmintools_dir}/openam/bin/amUser.xml":
    ensure => file,
    backup => false,
    owner  => tomcat,
    source => "puppet:///modules/${templates}/amUser.xml",
  } ->
  # configure the attrs.properties file
  file { "${ssoadmintools_dir}/openam/bin/attrs.properties":
    ensure  => file,
    backup  => false,
    owner   => tomcat,
    mode    => "0400",
    content => template("${templates}/attrs.properties"),
  } ->
  # configure the attrs.properties file
  file { "${ssoadmintools_dir}/ssoadm.sh":
    ensure  => file,
    backup  => false,
    owner   => tomcat,
    mode    => "0774",
    content => template("${templates}/ssoadm.sh"),
  } ->
  # run the ssoAdminTools/ssoadm script
  c_openwis::common::exec_once { ssoadm:
    cwd       => "${ssoadmintools_dir}",
    command   => "ssoadm.sh",
    user      => tomcat,
    touchfile => "${ssoadmintools_dir}/.ssoadm_complete",
    path      => ["${ssoadmintools_dir}", $::path],
  }
}
