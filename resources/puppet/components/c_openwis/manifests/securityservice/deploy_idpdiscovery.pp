###############################################################################
# == Component: Deploy & configure the IDP Discovery Web App
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
# This function deploys & configures the IDP Discovery WAR in Tomcat.
#
# === Notes
#
#
###############################################################################

define c_openwis::securityservice::deploy_idpdiscovery (
)
#
{
  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $binaries_repo    = $c_openwis::binaries_repo
  $templates        = "c_openwis/securityservice/idpdiscovery"
  $tomcat_dir       = $c_openwis::tomcat::tomcat_dir
  $openwis_home     = $c_openwis::home_dir
  $idpdiscovery_dir = "${openwis_home}/idpdiscovery"
  $tomcat_home      = $c_openwis::tomcat::home_dir

  # download & unpack the OpenAM WAR
  c_openwis::common::unzip { "idpdiscovery.war":
    source      => "${binaries_repo}/idpdiscovery.war",
    destination => "${tomcat_dir}/webapps/idpdiscovery",
    creates     => "${tomcat_dir}/webapps/idpdiscovery",
    user        => tomcat,
    before      => Class[c_openwis::common::tidy_downloads],
    notify      => [Service[tomcat], Exec[restart-tomcat-service]],
  } ->
  # configure openwis-securityservice.properties
  file { "${tomcat_home}/libIDPDiscoveryConfig.properties":
    ensure  => file,
    backup  => false,
    content => template("${templates}/libIDPDiscoveryConfig.properties"),
    require => File["${tomcat_home}"],
    notify  => Service[tomcat],
  } ->
  # ensure idpDiscovery debug folder exists
  file { $idpdiscovery_dir:
    ensure => directory,
    owner  => openwis,
  }
}
