# ##############################################################################
# == Component: Install & Configure OpenWIS
# Security Services
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
# # This class builds & configures (base configuration only) the
# Security/Authentication services, as follows:
#
# * Installs & configures OpenDJ
#    (using the 'install_opendj' function)
# * Deploys OpenAM
#    (using the 'deploy_openam' function)
# * Customises the OpenAM login pages for OpenAM
#    (using the 'customise_login_pages' function)
# * Deploys & configures IDP Discovery
#    (using the 'deploy_idpdiscovery' function)
# * Deploys & configures the OpenWIS Security Service
#    (using the 'deploy_securityservice_war' function)
# * Applies the base OpenAM configuration
#    (using the 'configure_openam' function)
# * Executes the SSO Admin Tools to configure SSO
#    (using the 'run_ssoadmintools' function)
# * Populates the OpenDJ LDAP database
#    (using the 'populate_ldap' function)
#
# === Notes
#
#
###############################################################################

class c_openwis::securityservice (
  $opendj_host,
  $opendj_root_password,
  $openam_admin_password,
  $openam_url_password,
  $openam_server_url,
  $openam_cookie_domain,
  $openam_locale,
  $register_users_threshold,
  $log_timer_period,
  $openwis_admin_login,
  $openwis_admin_password,
  $openwis_admin_first_name,
  $openwis_admin_last_name)
#
{
  require c_openwis::java7

  include c_openwis

  File {
    owner => openwis,
    group => openwis,
    mode  => "0664",
  }

  Exec {
    user    => openwis,
    timeout => 0,
  }

  # ensure expect is installed
  package { expect:
    ensure => present,
  }

  # ensure backup folder exists
  file { ["/var/bck", "/var/bck/openwis"]:
    ensure => directory
  }

  # install OpenDJ
  c_openwis::securityservice::install_opendj { install_opendj:
    opendj_root_password => $opendj_root_password
  } ->
  # deploy OpenAM WAR
  c_openwis::securityservice::deploy_openam { deploy_openam:
  } ->
  # customise login pages for OpenWIS
  c_openwis::securityservice::customise_login_pages { customise_login_pages:
  } ->
  # deploy IDP Discovery WAR
  c_openwis::securityservice::deploy_idpdiscovery { deploy_idpdiscovery:
  } ->
  # deploy OpenWIS Security Service WAR
  c_openwis::securityservice::deploy_securityservice_war { deploy_securityservice_war:
    opendj_root_password     => $opendj_root_password,
    log_timer_period         => $log_timer_period,
    register_users_threshold => $register_users_threshold
  } ->
  # configure OpenAM (basic configuration only)
  c_openwis::securityservice::configure_openam { configure_openam:
    openam_admin_password => $openam_admin_password,
    openam_server_url     => $openam_server_url,
    openam_cookie_domain  => $openam_cookie_domain,
    openam_locale         => $openam_locale,
    opendj_host           => $opendj_host,
    opendj_root_password  => $opendj_root_password,
    openam_url_password   => $openam_url_password
  } ->
  # run SSO Admin tools
  c_openwis::securityservice::run_ssoadmintools { run_ssoadmintools:
    openam_admin_password => $openam_admin_password,
    opendj_root_password  => $opendj_root_password
  } ->
  # initialise OpenDJ LDAP database
  c_openwis::securityservice::populate_ldap { populate_ldap:
    admin_login      => $openwis_admin_login,
    admin_password   => $openwis_admin_password,
    admin_first_name => $openwis_admin_first_name,
    admin_last_name  => $openwis_admin_last_name
  }
}
