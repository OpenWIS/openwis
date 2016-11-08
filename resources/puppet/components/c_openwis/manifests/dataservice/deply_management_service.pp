###############################################################################
# == Component: Deploy & configure the OpenWIS Management Service Application
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
# This function deploys the OpenWIS Management Service application to JBoss
#
# === Notes
#
#
###############################################################################

define c_openwis::dataservice::deply_management_service (
)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads

  $openwis_repo  = $c_openwis::openwis_repo
  $jboss_as_dir  = $c_openwis::jboss::jboss_as_dir
  $openwis_home  = $c_openwis::home_dir
  $downloads_dir = $c_openwis::downloads_dir

  c_openwis::common::wget { "openwis-management-service.ear":
    source  => "${openwis_repo}/openwis-management-service.ear",
    creates => "${openwis_home}/.management_service_deployed",
    require => File["${openwis_home}"]
  } ->
  c_openwis::common::exec_once { "openwis-management-service.ear":
    command   => "jboss-cli.sh -c --command=\"deploy ${downloads_dir}/openwis-management-service.ear\"",
    user      => openwis,
    touchfile => "${openwis_home}/.management_service_deployed",
    before    => Class[c_openwis::common::tidy_downloads],
    path      => ["${jboss_as_dir}/bin", $::path],
  }
}

