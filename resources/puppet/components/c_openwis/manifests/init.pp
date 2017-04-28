# ##############################################################################
# == Component: OpenWIS
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
# The main OpenWIS class that specifies the common parameters...
#
# As well as holding the common OpenWIS parameters, this class does the following:
#
# * Ensures that the 'openwis' group & user exist.
# * If there's a 'tomcat' group on the server, links the 'openwis' user to this
#   group.
# * Ensures that the '/home/openwis' & 'var/opt/openwis' base folders exist.
# * Ensures that the 'downloads' folder exists.
# * Ensures that the 'openwis' user's limits file is correctly configured.
#
# === Notes
#
#
###############################################################################

class c_openwis (
  # user configuration
  $gid,
  # software repositories
  $openwis_repo,
  $binaries_repo,
  # database configuration
  $database_host,
  $database_password,
  $jdbc_driver_jar        = "postgresql-9.2-1004.jdbc41.jar",
  # JBoss installer
  $jboss_installer_file   = "jboss-as-7.1.1.Final.tar.gz",
  # global deployment name
  $deployment_name,
  # public addresses
  $admin_portal_public_addr,
  $user_portal_public_addr,
  $staging_post_public_addr,
  $auth_service_public_addr,
  # internal host names
  $admin_portal_host,
  $user_portal_host,
  $staging_post_host,
  $data_service_host,
  $data_harness_host,
  $auth_service_host,
  # internal addresses
  $user_portal_heartbeat_addr,
  $admin_email,
  $wget_use_proxy         = false,
  $wget_http_proxy        = undef,
  $wget_https_proxy       = undef,
  $dissemination_wsdl_url = undef,
  $downloads              = undef,
  $use_ssl                = false,
  $web_proxy_host         = undef,
  $web_proxy_port         = undef,
  # log retention
  $log_retention_days)
#
{
  $home_dir            = "/home/openwis"
  $opt_dir             = "/var/opt/openwis"
  $logs_dir            = "/var/log/openwis"
  $log_links_dir       = "${home_dir}/logs"
  $tomcat_group_exists = $::tomcat_group_exists

  if $downloads != undef {
    $downloads_dir = $downloads
  } else {
    $downloads_dir = "${home_dir}/downloads"
  }

  # Create the OpenWIS group
  group { openwis:
    ensure => present,
    gid    => $gid,
  }

  # link the OpenWIS user to the tomcat group, if it exists
  if $tomcat_group_exists != "" {
    $groups = [tomcat]
  } else {
    $groups = undef
  }

  # Create the OpenWIS user
  user { openwis:
    ensure => present,
    gid    => $gid,
    home   => $home_dir,
    shell  => "/bin/bash",
    groups => $groups
  }

  # Create the OpenWIS directories
  file { [$home_dir, $opt_dir, $logs_dir, $log_links_dir]:
    ensure => directory,
    owner  => openwis,
    group  => openwis,
    mode   => "0664",
  }

  # create openwis logs folder link
  file { "${log_links_dir}/openwis":
    ensure  => link,
    target  => $logs_dir,
    require => File[$logs_dir, $log_links_dir]
  }

  # compress old logs after 2 days
  cron { compress-openwis-logs:
    command => 'find /var/log/openwis -type f -mtime +2 -not -name "*.gz" -exec gzip {} \;',
    user    => root,
    hour    => 00,
    minute  => 10,
  }

  # purge old logs after the log retention period has expired
  cron { purge-openwis-logs:
    command => inline_template('find /var/log/openwis -type f -mtime +<%= @log_retention_days %> -name "*.gz" -delete'),
    user    => root,
    hour    => 23,
    minute  => 15,
  }

  # Set the security limits
  file { "/etc/security/limits.d/99-openwis.conf":
    ensure  => file,
    owner   => root,
    group   => root,
    mode    => "0644",
    backup  => false,
    content => template("c_openwis/limits/openwis.conf"),
  }
}
