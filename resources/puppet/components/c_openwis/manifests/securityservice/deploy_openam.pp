###############################################################################
# == Component: Deploy the OpenAM Web App
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
# This function deploys OpenAM WAR to Tomcat.
#
# === Notes
#
#
###############################################################################

define c_openwis::securityservice::deploy_openam (
)
#
{
  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $binaries_repo = $c_openwis::binaries_repo
  $tomcat_dir    = $c_openwis::tomcat::tomcat_dir

  # download & unpack the OpenAM WAR
  c_openwis::common::unzip { "openam-patched.war":
    source      => "${binaries_repo}/openam-patched.war",
    destination => "${tomcat_dir}/webapps/openam",
    creates     => "${tomcat_dir}/webapps/openam",
    user        => tomcat,
    before      => Class[c_openwis::common::tidy_downloads],
    notify      => [Service[tomcat], Exec[restart-tomcat-service]],
  }
}

