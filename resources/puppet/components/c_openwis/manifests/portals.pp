# ##############################################################################
# == Component: Install & Configure OpenWIS Portals
# (Admin & User)
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
# This class deploys & configures the OpenWIS portals as follows:
#
# * Generates the OpenAM integration fedlet
#    (using the 'generate_fedlet' function)
# * Deploys & configures the Portal WAR to Tomcat
#    (using the 'deploy_portal' function)
# * Customises the Portal deployment
#    (using the 'customise_portal' function)
#
# === Notes
#
# The 'portal' parameter specifies the "name" of the portal, which must be
# either 'user' or 'admin'.
#
# The 'cot_name' parameter specifies the OpenAM Circle Of Trust (COT) name,
# and must match the corresponding COT as configured in OpenAM.
#
# The 'idp_name' parameter specifies the OpenAM Identity Provider (IDP) name,
# and must match the corresponding IDP as configured in OpenAM.
#
# The 'federation_name' parameter specifies the OpenAM Service Provider (SP)
# name of this portal.
#
###############################################################################

class c_openwis::portals (
  $portal,
  $cot_name,
  $idp_name,
  $federation_name)
#
{
  require c_openwis::java7_portal

  include c_openwis
  include c_openwis::tomcat

  $openwis_home = $c_openwis::home_dir
  $tomcat_dir   = $c_openwis::tomcat::tomcat_dir

  case $portal {
    "admin" : {
      $portal_war_file    = "openwis-admin-portal.war"
      $portal_webapp_name = "openwis-admin-portal"
      $portal_type        = "adminportal"
    }
    "user"  : {
      $portal_war_file    = "openwis-user-portal.war"
      $portal_webapp_name = "openwis-user-portal"
      $portal_type        = "userportal"
    }
  }

  File {
    owner => tomcat,
    group => tomcat,
    mode  => "0660",
  }

  Exec {
    user    => tomcat,
    timeout => 0,
  }

  if $portal == "admin" {
    file { ["${openwis_home}/GISC", "${openwis_home}/GISC/reports"]:
      ensure  => directory,
      require => File["${openwis_home}"]
    }
  }

  # generate the fedlet
  c_openwis::portals::generate_fedlet { generate_fedlet:
    portal_type     => $portal_type,
    federation_name => $federation_name,
    idp_name        => $idp_name,
    cot_name        => $cot_name
  }

  # deploy the Portal WAR
  c_openwis::portals::deploy_portal { deploy_portal:
    portal_type        => $portal_type,
    portal_war_file    => $portal_war_file,
    portal_webapp_name => $portal_webapp_name,
    federation_name    => $federation_name
  } ->
  c_openwis::portals::customise_portal { customise_portal:
    portal             => $portal,
    portal_webapp_name => $portal_webapp_name,
  }
}