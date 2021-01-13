###############################################################################
# == Component: Deploy & configure the OpenWIS Portal Web Apps
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
# This function deploys & configures the portal as follows:
#
# * Downloads & unzips the WAR file into Tomcat
# * Ensures the portal configuration files are correct:
#   - config.xml
#   - openwis-deployments.properties
#   - openwis-metadataportal.properties
# * Applies a fix to the web.xml, to ensure that the JSPs get correctly loaded
#    (ACJ - this is a bug that should really be fixed in the OpenWIS code)
#
# === Notes
#
#
###############################################################################

define c_openwis::portals::deploy_portal (
  $portal_type,
  $portal_war_file,
  $portal_webapp_name,
  $federation_name)
#
{
  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $openwis_repo               = $c_openwis::openwis_repo
  $auth_service_host          = $c_openwis::auth_service_host
  $database_host              = $c_openwis::database_host
  $database_password          = $c_openwis::database_password
  $deployment_name            = $c_openwis::deployment_name
  $admin_portal_public_addr   = $c_openwis::admin_portal_public_addr
  $user_portal_public_addr    = $c_openwis::user_portal_public_addr
  $user_portal_host           = $c_openwis::user_portal_host
  $admin_email                = $c_openwis::admin_email
  $data_service_host          = $c_openwis::data_service_host
  $staging_post_public_addr   = $c_openwis::staging_post_public_addr
  $user_portal_heartbeat_addr = $c_openwis::user_portal_heartbeat_addr
  $openwis_home               = $c_openwis::home_dir
  $tomcat_dir                 = $c_openwis::tomcat::tomcat_dir
  $auth_service_public_addr   = $c_openwis::auth_service_public_addr
  $webapp_dir                 = "${tomcat_dir}/webapps/${portal_webapp_name}"
  $openwis_logs_dir           = $c_openwis::logs_dir

  # download & unzip the WAR into the Tomcat webapps folder
  c_openwis::common::unzip { "${portal_war_file}":
    source      => "${openwis_repo}/${portal_war_file}",
    destination => "${webapp_dir}",
    user        => tomcat,
    creates     => "${webapp_dir}/WEB-INF",
    overwrite   => true,
    before      => Class[c_openwis::common::tidy_downloads]
  } ->
  # set the portal configuration
  file { "${webapp_dir}/WEB-INF/config.xml":
    ensure  => file,
    backup  => false,
    content => template("c_openwis/${portal_type}/webapp/config.xml"),
    notify  => Service[tomcat],
  } ->
  file { "${webapp_dir}/WEB-INF/classes/openwis-deployments.properties":
    ensure  => file,
    backup  => false,
    content => template("c_openwis/${portal_type}/webapp/openwis-deployments.properties"),
    notify  => Service[tomcat],
  } ->
  file { "${webapp_dir}/WEB-INF/classes/openwis-metadataportal.properties":
    ensure  => file,
    backup  => false,
    content => template("c_openwis/${portal_type}/webapp/openwis-metadataportal.properties"),
    notify  => Service[tomcat],
  } ->
  file { "${webapp_dir}/WEB-INF/log4j.cfg":
    ensure  => file,
    backup  => false,
    content => template("c_openwis/${portal_type}/webapp/log4j.cfg"),
    notify  => Service[tomcat],
  } ->
  # fix the portal web.xml (this should really be fixed in the code)
  augeas { fixup-web-xml:
    incl    => "${webapp_dir}/WEB-INF/web.xml",
    lens    => "Xml.lns",
    context => "/files${webapp_dir}/WEB-INF/web.xml",
    changes => ["set web-app/servlet[5]/jsp-file/#text \"/jsp/openWisChooseDomain.jsp\""],
    notify  => Service[tomcat],
  }
}
