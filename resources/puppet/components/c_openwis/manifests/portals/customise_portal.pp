###############################################################################
# == Component: Customize Help, About and Home Pages for User Portal
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
# This function applies company/installation specific customisations to the
# portals, as follows:
#
# For all portals:
# * Updates the left header logo (company logo), if applicable
# * Updates the site logo, if applicable
# * Updates the right header logo (OpenWIS logo), if applicable
#
# Additionally, for the User Portal:
# * Updates the 'about' page, if applicable
# * Updates the 'help' page, if applicable
# * Updates the home page text, if applicable
#
# === Notes
#
# By default, this function will NOT update any of the above images or pages.
#
# Each image/page can be individually updated, by supplying the location of
# the custom file to deploy in place of the default version.
#
# The following hiera properties are used:
#
# - company logo : c_openwis::portals::header_left
# - site logo    : c_openwis::portals::site_logo
# - OpenWIS logo : c_openwis::portals::header_right
# - About page   : c_openwis::userportal::custom_about
# - Help page    : c_openwis::userportal::custom_help
# - Home page    : c_openwis::userportal::custom_home
#
# Each of these properties can start with 'puppet:///' to refer to a Puppet
# supplied file, or be a standard file loaction to refer to a file that
# pre-exists on the server.
#
###############################################################################

define c_openwis::portals::customise_portal (
  $portal,
  $portal_webapp_name)
#
{
  include c_openwis::tomcat

  $tomcat_dir   = $c_openwis::tomcat::tomcat_dir
  $webapp_dir   = "${tomcat_dir}/webapps/${portal_webapp_name}"
  $header_left  = hiera("c_openwis::portals::header_left", undef)
  $site_logo    = hiera("c_openwis::portals::site_logo", undef)
  $header_right = hiera("c_openwis::portals::header_right", undef)
  $custom_about = hiera("c_openwis::userportal::custom_about", undef)
  $custom_help  = hiera("c_openwis::userportal::custom_help", undef)
  $custom_home  = hiera("c_openwis::userportal::custom_home", undef)

  #----------------------------------------------------------------------------
  # custom header images & logos
  #----------------------------------------------------------------------------
  c_openwis::common::copy_optional_file { "${webapp_dir}/images/openwis/header-left.jpg":
    source => "${header_left}",
    owner  => tomcat,
  }

  c_openwis::common::copy_optional_file { "${webapp_dir}/images/openwis/titre_site.png":
    source => "${site_logo}",
    owner  => tomcat,
  }

  c_openwis::common::copy_optional_file { "${webapp_dir}/images/openwis/header-right.jpg":
    source => "${header_right}",
    owner  => tomcat,
  }

  #----------------------------------------------------------------------------
  # additional pages for the User Portal only
  #----------------------------------------------------------------------------
  if $portal == "user" {
    c_openwis::common::copy_optional_file { "${webapp_dir}/loc/en/xml/about.html":
      source => "${custom_about}",
      owner  => tomcat,
    }

    c_openwis::common::copy_optional_file { "${webapp_dir}/loc/en/xml/help.html":
      source => "${custom_help}",
      owner  => tomcat,
    }

    c_openwis::common::copy_optional_file { "${webapp_dir}/scripts/Openwis/lib/Openwis/Lang/Locales/en.js":
      source => "${custom_home}",
      owner  => tomcat,
    }
  }
}
