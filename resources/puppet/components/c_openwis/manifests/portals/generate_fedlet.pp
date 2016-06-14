###############################################################################
# == Component: Generate the OpenAM integration files
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
# This function generates the OpenAM integration fedlet for the OpenWIS
#  portal, as follows:
#
# * Downloads & extracts the configuration scripts
# * Ensures that the script file are executable
# * Ensures the 'configuration.properties' is set appropriately for the
#   environment
# * Generates the integration fedlet by executing the configuration scripts
#
# === Notes
#
# The base installation of OpenAM & IDP Discovery must be completed on the
# Authorisation/Security server before this can be executed.
#
# A check is perform to ensure that IDP Discovery is available before the
# fedlet is generated, else the generation will fail.
#
# The 'fedlet' folder gets removed (if it exists) before the fedlet is
# generated, else the generation will fail.
#
###############################################################################

define c_openwis::portals::generate_fedlet (
  $portal_type,
  $federation_name,
  $idp_name,
  $cot_name)
#
{
  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $binaries_repo            = $c_openwis::binaries_repo
  $auth_service_host        = $c_openwis::auth_service_host
  $admin_portal_public_addr = $c_openwis::admin_portal_public_addr
  $user_portal_public_addr  = $c_openwis::user_portal_public_addr
  $auth_service_public_addr = $c_openwis::auth_service_public_addr
  $idp_discovery_url        = "http://${auth_service_host}:8080/idpdiscovery"
  $tomcat_home              = $c_openwis::tomcat::home_dir

  file { "${tomcat_home}/GenerateSPConfFiles":
    ensure  => directory,
    require => File["${tomcat_home}"]
  } ->
  # unzip configuration scripts
  c_openwis::common::unzip { "GenerateSPConfFiles.zip":
    source      => "${binaries_repo}/GenerateSPConfFiles.zip",
    destination => "${tomcat_home}/GenerateSPConfFiles",
    user        => tomcat,
    creates     => "${tomcat_home}/GenerateSPConfFiles/Generate-SP-Conf-Files.sh",
    before      => Class[c_openwis::common::tidy_downloads]
  } ->
  # ensure script file is executable
  file { "${tomcat_home}/GenerateSPConfFiles/Generate-SP-Conf-Files.sh":
    ensure => file,
    mode   => "0744",
  } ->
  # ensure script file is executable
  file { "${tomcat_home}/GenerateSPConfFiles/apache-ant-1.8.1/bin/ant":
    ensure => file,
    mode   => "0744",
  } ->
  # set script configuration file
  file { "${tomcat_home}/GenerateSPConfFiles/conf/configuration.properties":
    ensure  => file,
    backup  => false,
    content => template("c_openwis/${portal_type}/fedlet/configuration.properties"),
  } ->
  # the IDP discovery service must be available
  exec { check-idp-discovery-available:
    command => "echo ${idp_discovery_url} && curl -s --connect-timeout 6 ${idp_discovery_url}",
    creates => "${tomcat_home}/fedlet/idp.xml",
    path    => $::path
  } ->
  # fedlet folder must be empty
  exec { remove-fedlet:
    command => "rm -rf ${tomcat_home}/fedlet",
    creates => "${tomcat_home}/fedlet/idp.xml",
    path    => $::path
  } ->
  # generate fedlets using script
  exec { generate-fedlets:
    cwd     => "${tomcat_home}/GenerateSPConfFiles",
    command => "Generate-SP-Conf-Files.sh",
    creates => "${tomcat_home}/fedlet/idp.xml",
    notify  => Service[tomcat],
    path    => ["${tomcat_home}/GenerateSPConfFiles", $::path]
  }
}
