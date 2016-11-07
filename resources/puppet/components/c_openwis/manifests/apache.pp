###############################################################################
# == Component: Install & Configure Apache HTTPD service
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
# * Ensure that the 'httpd' package is installed
# * Ensure that the 'httpd' service is running
# * Ensure that Apache cache control is correctly configured
# * Where SSL is enabled:
#   - Ensure that port 443 is allowed in iptables
#   - Ensure that 'mod_ssl' package is installed
#   - Ensure that the appropriate SSL certificate & key are installed
#     & configured
#   - Enable the appropriate OpenWIS SSL configuration
# * Where SSL is disabled:
#   - Ensure that port 443 is blocked in iptables
#   - Ensure that 'mod_ssl' is not installed
#
# === Notes
#
# Currently this module expects the SSL certificate & key to be available as
# local files on the box.  This can be achieved by providing the certificate &
# key as files within a puppet module & using the 'puppet://' file protocol,
# or by ensuring that the certificate & key files exist somewhere on the local
# file system before running this script.
#
###############################################################################

class c_openwis::apache (
  $httpd_conf_dir = "/etc/httpd/conf.d",
  $cache_period   = "1 hours")
#
{
  include c_openwis

  $use_ssl       = $c_openwis::use_ssl
  $ssl_certs_dir = "${httpd_conf_dir}/ssl"
  $templates     = "c_openwis/apache"

  # install 'httpd' package
  package { httpd:
    ensure => installed,
  }

  # ensure 'httpd' service is running
  service { httpd:
    ensure  => running,
    enable  => true,
    require => Package[httpd],
  }

  # configure cache control
  c_openwis::apache::conf_file { "openwis_cache_control.conf":
    template => "c_openwis/apache/openwis_cache_control.conf",
  }

  if $use_ssl {
    #==========================================================================
    # SSL enabled - configure SSL
    #==========================================================================

    $public_domain_name = hiera("c_openwis::public_domain_name")
    $ssl_cert_file      = hiera("c_openwis::ssl_cert_file")
    $ssl_cert_key_file  = hiera("c_openwis::ssl_cert_key_file")

    # allow HTTPS through the firewall
    firewall { "888 allow from 0.0.0.0/0 to 443 (HTTPS) on all":
      proto  => tcp,
      action => accept,
      source => "0.0.0.0/0",
      dport  => 443,
    }

    # install 'mod_ssl'
    package { mod_ssl:
      ensure => installed,
    } ->
    # set the ServerName in ssl.conf
    file_line { ssl_ServerName:
      ensure => present,
      path   => "${httpd_conf_dir}/ssl.conf",
      line   => "ServerName ${public_domain_name}",
      match  => "^.?ServerName",
      notify => Service[httpd]
    } ->
    # set the SSLCertificateFile location in ssl.conf
    file_line { ssl_SSLCertificateFile:
      ensure => present,
      path   => "${httpd_conf_dir}/ssl.conf",
      line   => "SSLCertificateFile ${ssl_certs_dir}/openwis.crt",
      match  => "^.?SSLCertificateFile",
      notify => Service[httpd]
    } ->
    file_line { ssl_SSLCertificateKeyFile:
      ensure => present,
      path   => "${httpd_conf_dir}/ssl.conf",
      line   => "SSLCertificateKeyFile ${ssl_certs_dir}/openwis.key",
      match  => "^.?SSLCertificateKeyFile",
      notify => Service[httpd]
    } ->
    c_openwis::apache::conf_file { "openwis_ssl.conf":
      template => "${templates}/openwis_ssl.conf",
    } ->
    file { "${ssl_certs_dir}":
      ensure => directory,
      owner  => root,
    } ->
    file { "${ssl_certs_dir}/openwis.crt":
      ensure => file,
      owner  => root,
      source => "${ssl_cert_file}",
      notify => Service[httpd]
    } ->
    file { "${ssl_certs_dir}/openwis.key":
      ensure => file,
      owner  => root,
      source => "${ssl_cert_key_file}",
      notify => Service[httpd]
    }
  } else {
    #==========================================================================
    # SSL disabled - configure 'NO SSL'
    #==========================================================================

    package { mod_ssl:
      ensure => absent,
    } ->
    file { ["${ssl_certs_dir}", "${httpd_conf_dir}/openwis_ssl.conf"]:
      ensure => absent,
      force  => true,
      notify => Service[httpd]
    }
  }
}

