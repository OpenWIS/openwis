###############################################################################
# == Component: Install & Configure OpenWIS Data Services
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
# This class manages the OpenWIS Data Services configuration, as follows:
#
# * Ensures that all of the required folders & links are present
# * Ensures that the 'vsftpd' service is running
#    (ACJ - is this still required?)
# * Configures JBoss for OpenWIS
#    (using the 'configure_jboss' function)
# * Deploys the Data Service Application to JBoss
#    (using the 'deploy_dataservice' function)
# * Deploys the Management Service Application to JBoss
#    (using the 'deploy_managementservice' function)
#
# === Notes
#
#
###############################################################################

class c_openwis::dataservice (
)
#
{
  require c_openwis::java7
  require c_openwis::jboss

  include c_openwis

  $openwis_home = $c_openwis::home_dir
  $openwis_opt  = $c_openwis::opt_dir

  File {
    owner => openwis,
    group => openwis,
    mode  => "0660",
  }

  Exec {
    user    => openwis,
    timeout => 0,
  }

  # Create required folders, files & links
  file { [
    "${openwis_home}/conf",
    "${openwis_home}/openwis-data-service",
    "${openwis_opt}/harness",
    "${openwis_opt}/harness/incoming",
    "${openwis_opt}/harness/ingesting",
    "${openwis_opt}/harness/ingesting/fromReplication",
    "${openwis_opt}/harness/outgoing",
    "${openwis_opt}/harness/working",
    "${openwis_opt}/harness/working/fromReplication",
    "${openwis_opt}/cache",
    "${openwis_opt}/temp",
    "${openwis_opt}/replication",
    "${openwis_opt}/replication/sending",
    "${openwis_opt}/replication/sending/local",
    "${openwis_opt}/replication/sending/destinations",
    "${openwis_opt}/status"]:
    ensure  => directory,
    require => File[["${openwis_home}", "${openwis_opt}"]]
  }

  file { "${openwis_opt}/replication/receiving":
    ensure  => link,
    target  => "${openwis_home}",
    require => File["${openwis_home}"]
  }


  # Ensure vsftpd service is running
  service { vsftpd:
    ensure => running,
    enable => true,
  }

  # Do the deployments
  c_openwis::dataservice::configure_jboss { configure_jboss:
  } ->
  c_openwis::dataservice::deply_management_service { deply_management_service:
  } ->
  c_openwis::dataservice::deploy_dataservice { deploy_dataservice:
  }
}
