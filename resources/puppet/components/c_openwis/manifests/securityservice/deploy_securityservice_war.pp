###############################################################################
# == Component: Deploy & configure the OpenWIS Security Service Web App
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
# This function deploys & configures the OpenWIS Security Service WAR in Tomcat.
#
# === Notes
#
#
###############################################################################

define c_openwis::securityservice::deploy_securityservice_war (
  $opendj_root_password,
  $log_timer_period,
  $register_users_threshold)
#
{
  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $openwis_repo      = $c_openwis::openwis_repo
  $opendj_hostname   = $::hostname
  $data_service_host = $c_openwis::data_service_host
  $templates         = "c_openwis/securityservice/securityservice"
  $tomcat_dir        = $c_openwis::tomcat::tomcat_dir
  $openwis_home      = $c_openwis::home_dir
  $openam_dir        = "${openwis_home}/openam"
  $war_dir           = "${tomcat_dir}/webapps/openwis-securityservice"

  # download & unpack the OpenWIS Security Service WAR
  c_openwis::common::unzip { "openwis-securityservice.war":
    source      => "${openwis_repo}/openwis-securityservice.war",
    destination => "${war_dir}",
    creates     => "${war_dir}",
    user        => tomcat,
    before      => Class[c_openwis::common::tidy_downloads],
    notify      => [Service[tomcat], Exec[restart-tomcat-service]],
  } ->
  # configure openwis-securityservice.properties
  file { "${war_dir}/WEB-INF/classes/openwis-securityservice.properties":
    ensure  => file,
    backup  => false,
    content => template("${templates}/webapp/openwis-securityservice.properties"),
    notify  => Service[tomcat],
  }
}
