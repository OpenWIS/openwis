# ##############################################################################
# == Component: Deploy the OpenWIS custom login
# pages to OpenAM
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
# This function deploys the OpenWIS custom login pages, as follows:
#
# * Downloads & unpacks the customisation script
# * Optionally, applies an organisation-specific background
# * Executes the script to deploy the custom login pages.
#
# === Notes
#
# Currently the OpenWIS custom login pages rely on the OpenAM Legacy UI,
# rather than the new XUI. Once these custom pages have been deployed, the
# OpenAM Legacy UI must be enabled through the OpenAM configuration pages.
#
# By default, this function use the page backgroup supplied in the configuration
# script.
#
# This background can be replaced with an organisation-sepcific versiom,
# by supplying the location of the custom file to deploy in place of the default
# version.
#
# The following hiera properties is used:
#
# - custom background : c_openwis::securityservice::login_page_bg_image
#
# This property can start with 'puppet:///' to refer to a Puppet
# supplied file, or be a standard file loaction to refer to a file that
# pre-exists on the server.
#
###############################################################################

define c_openwis::securityservice::customise_login_pages (
)
#
{
  include c_openwis
  include c_openwis::tomcat
  include c_openwis::common::tidy_downloads

  $binaries_repo       = $c_openwis::binaries_repo
  $tomcat_dir          = $c_openwis::tomcat::tomcat_dir
  $openwis_home        = $c_openwis::home_dir
  $templates           = "c_openwis/securityservice/CustomPageOpenAM"
  $login_page_bg_image = hiera("c_openwis::securityservice::login_page_bg_image", undef)

  c_openwis::common::unzip { "CustomPageOpenAM.zip":
    source      => "${binaries_repo}/CustomPageOpenAM.zip",
    destination => "${openwis_home}/CustomPageOpenAM",
    user        => tomcat,
    before      => Class[c_openwis::common::tidy_downloads],
  } ->
  file { "${openwis_home}/CustomPageOpenAM/customOpenAM.sh":
    owner   => tomcat,
    mode    => "0740",
    backup  => false,
    content => template("${templates}/customOpenAM.sh")
  } ->
  c_openwis::common::copy_optional_file { "${openwis_home}/CustomPageOpenAM/images/login-backimage.jpg":
    owner  => tomcat,
    source => $login_page_bg_image
  } ->
  c_openwis::common::exec_once { update-login-pages:
    command   => "customOpenAM.sh",
    cwd       => "${openwis_home}/CustomPageOpenAM",
    touchfile => "${openwis_home}/CustomPageOpenAM/.login_pages_updated",
    user      => tomcat,
    path      => ["${openwis_home}/CustomPageOpenAM", $::path],
  }
}
