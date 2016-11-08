###############################################################################
# == Component: Install & Configure OpenWIS Staging Post Web App
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
# This class Deploys the OpenWIS Staging Post WAR to Tomcat.
#
# === Notes
#
# This class extends the 'c_openwis::stagingpost::folder' class to inherit
# the location of the Staging Post data folder.
#
###############################################################################

class c_openwis::stagingpost (
) inherits c_openwis::stagingpost::folder
#
{
  require c_openwis::java7

  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $openwis_repo = $c_openwis::openwis_repo
  $tomcat_dir   = $c_openwis::tomcat::tomcat_dir

  File {
    owner => openwis,
    group => openwis,
    mode  => "0664",
  }

  # download & unpack the Staging Post WAR
  c_openwis::common::unzip { "stagingPost.war":
    source      => "${openwis_repo}/stagingPost.war",
    destination => "${_staging_post_dir}",
    user        => openwis,
    unless      => "test -f ${_staging_post_dir}/WEB-INF/web.xml",
    overwrite   => false,
    before      => Class[c_openwis::common::tidy_downloads],
    require     => File["${_staging_post_dir}"],
    notify      => Service[tomcat],
  }

  # configure Tomcat
  file { "${tomcat_dir}/conf/Catalina/localhost/stagingPost.xml":
    ensure  => file,
    backup  => false,
    owner => tomcat,
    group => tomcat,
    mode => "0444",
    content => template("c_openwis/stagingpost/tomcat/stagingPost.xml"),
    notify  => Service[tomcat],
  }
}
