###############################################################################
# == Component: Install & Configure OpenWIS SOLR Web App
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
# This class Deploys & configures the OpenWIS SOLR WAR io Tomcat.
#
# === Notes
#
#
###############################################################################

class c_openwis::solr (
)
#
{
  require c_openwis::java7

  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $openwis_repo      = $c_openwis::openwis_repo
  $database_host     = $c_openwis::database_host
  $database_password = $c_openwis::database_password
  $openwis_opt_dir   = $c_openwis::opt_dir
  $tomcat_dir        = $c_openwis::tomcat::tomcat_dir

  File {
    owner => tomcat,
    group => tomcat,
    mode  => "0660",
  }

  Exec {
    user => tomcat,
  }

  # create required folders
  file { ["${openwis_opt_dir}/solr", "${openwis_opt_dir}/solr/data"]:
    ensure => directory,
    owner  => openwis,
    group  => openwis,
    mode   => "0660",
  }

  # download & unzip the SOLR WAR to the Tomcat webapps folder
  c_openwis::common::unzip { "openwis-portal-solr.war":
    source      => "${openwis_repo}/openwis-portal-solr.war",
    destination => "${tomcat_dir}/webapps/openwis-portal-solr",
    user        => tomcat,
    creates     => "${tomcat_dir}/webapps/openwis-portal-solr/WEB-INF",
    overwrite   => true,
    before      => Class[c_openwis::common::tidy_downloads],
    notify      => Service[tomcat],
  } ->
  # configure openwis.properties
  file { "${tomcat_dir}/webapps/openwis-portal-solr/WEB-INF/classes/openwis.properties"
  :
    ensure  => file,
    backup  => false,
    content => template("c_openwis/solr/openwis.properties"),
    notify  => Service[tomcat],
  }
}
