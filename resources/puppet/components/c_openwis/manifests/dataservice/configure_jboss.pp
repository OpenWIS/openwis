###############################################################################
# == Component: Configure JBoss for OpenWIS Data Services
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
# This function ensures that the JBoss installation is configured correctly
# for OpenWIS, as follows:
#
# * Manages the Data Service configuration files
# * Manages the JBoss logging configuration for OpenWIS
# * Re-starts JBoss when applicable
# * Deploys the OpenWIS Configuration Module to JBoss
# * Deploys the PostgreSQL JDBC driver to JBoss
# * Runs the OpenWIS setup.cli JBoss cofiguration script, which:
#   - Updates the JBoss logging configuration
#   - Configures the JDBC data source for OpenWIS
#   - Configures the JMS queues for OpenWIS
#   - deploys the OpenWIS configuration to JBoss
#
# === Notes
#
#
###############################################################################

define c_openwis::dataservice::configure_jboss (
)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads

  $openwis_repo             = $c_openwis::openwis_repo
  $binaries_repo            = $c_openwis::binaries_repo
  $jdbc_driver_jar          = $c_openwis::jdbc_driver_jar
  $database_host            = $c_openwis::database_host
  $database_password        = $c_openwis::database_password
  $dissemination_wsdl_url   = $c_openwis::dissemination_wsdl_url
  $admin_email              = $c_openwis::admin_email
  $jboss_as_dir             = $c_openwis::jboss::jboss_as_dir
  $openwis_home             = $c_openwis::home_dir
  $downloads_dir            = $c_openwis::downloads_dir
  $templates                = "c_openwis/dataservice"
  $openwis_opt_dir          = $c_openwis::opt_dir
  $staging_post_public_addr = $c_openwis::staging_post_public_addr
  $openwis_logs_dir         = $c_openwis::logs_dir

  file { "${openwis_home}/conf/openwis-dataservice.properties":
    ensure  => file,
    content => template("${templates}/openwis-dataservice.properties"),
    notify  => Exec[restart-jboss],
    require => File["${openwis_home}"]
  }

  file { "${openwis_home}/conf/localdatasourceservice.properties":
    ensure  => file,
    content => template("${templates}/localdatasourceservice.properties"),
    notify  => Exec[restart-jboss],
    require => File["${openwis_home}"]
  }

  # install the OpenWIS logging configuration
  file { "${jboss_as_dir}/standalone/configuration/jboss-log4j.xml":
    ensure  => file,
    content => template("${templates}/jboss-log4j.xml"),
    notify  => Exec[restart-jboss],
  }

  # deploy the OpenWIS configuration module
  c_openwis::common::unzip { "openwis-dataservice-config-module.zip":
    source      => "${openwis_repo}/openwis-dataservice-config-module.zip",
    destination => "${jboss_as_dir}/modules",
    user        => openwis,
    creates     => "${jboss_as_dir}/modules/org/openwis/dataservice/config/main/module.xml",
    notify      => Exec[restart-jboss],
    before      => Class[c_openwis::common::tidy_downloads]
  }

  # re-start JBoss, when requested
  exec { restart-jboss:
    command     => "service jboss-as restart",
    user        => root,
    refreshonly => true,
    path        => $::path
  }

  # configure JBoss
  c_openwis::common::wget { "${jdbc_driver_jar}":
    source  => "${binaries_repo}/${jdbc_driver_jar}",
    creates => "${openwis_home}/.jdbc_driver_deployed",
    require => File["${openwis_home}"]
  } ->
  c_openwis::common::exec_once { "${jdbc_driver_jar}":
    command   => "jboss-cli.sh -c --command=\"deploy --force ${downloads_dir}/${jdbc_driver_jar}\"",
    user      => openwis,
    touchfile => "${openwis_home}/.jdbc_driver_deployed",
    before    => Class[c_openwis::common::tidy_downloads],
    path      => ["${jboss_as_dir}/bin", $::path],
    require   => Exec[restart-jboss]
  } ->
  file { "${openwis_home}/setup.cli":
    ensure  => file,
    backup  => false,
    content => template("${templates}/setup.cli"),
  } ->
  c_openwis::common::exec_once { "setup.cli":
    command   => "jboss-cli.sh -c --file=${openwis_home}/setup.cli",
    user      => openwis,
    touchfile => "${openwis_home}/.jboss_configured",
    path      => ["${jboss_as_dir}/bin", $::path]
  }
}
