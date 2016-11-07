###############################################################################
# == Component: Deploy & configure the OpenWIS Data Service Application
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
# This function deploys the OpenWIS Data Service application to JBoss
#
# === Notes
#
#
###############################################################################

define c_openwis::dataservice::deploy_dataservice (
)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads

  $openwis_repo  = $c_openwis::openwis_repo
  $jboss_as_dir  = $c_openwis::jboss::jboss_as_dir
  $openwis_home  = $c_openwis::home_dir
  $downloads_dir = $c_openwis::downloads_dir

  c_openwis::common::wget { "openwis-dataservice.ear":
    source  => "${openwis_repo}/openwis-dataservice.ear",
    creates => "${openwis_home}/.data_service_deployed",
    require => File["${openwis_home}"]
  } ->
  c_openwis::common::exec_once { "openwis-dataservice.ear":
    command   => "jboss-cli.sh -c --command=\"deploy ${downloads_dir}/openwis-dataservice.ear\"",
    user      => openwis,
    touchfile => "${openwis_home}/.data_service_deployed",
    before    => Class[c_openwis::common::tidy_downloads],
    path      => ["${jboss_as_dir}/bin", $::path]
  }
}
