###############################################################################
# == Component: Install & Configure Apache Tomcat service
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
# This class ensures the the Tomcat user 'tomcat' is configured correctly for
# OpenWIS, as follows:
#
# * Links the 'tomcat' user to the 'openwis' group
# * Ensures that the 'tomcat' user's home directly exists
# * Ensures that any other required folders & links exist
# * Ensures that the 'tomcat' user's security limits are correctly set
# * Ensures that the 'tomcat' package is installed & running
# * provides a means for re-starting the Tomcat service
# * Ensures that iptables is configured to allow remote connections
#
# Additionally, if the AJP protocol is enabled (required for OpenAM):
# * Ensures that Tomcat is listening on port 8180 for AJP connections
#    (this port is being used to tie in with existing firewall ports)
# * Ensures that the corresponding iptables rules are set
#
# === Notes
#
#
###############################################################################

class c_openwis::tomcat (
  $enable_ajp = false,
  $ajp_port   = 8009)
#
{
  include c_openwis::common::systemd
  include c_openwis

  $home_dir           = "/home/tomcat"
  $tomcat_dir         = "/usr/share/tomcat"
  $openwis_home       = $c_openwis::home_dir
  $log_links_dir      = $c_openwis::log_links_dir
  $templates          = "c_openwis/tomcat"
  $log_retention_days = $c_openwis::log_retention_days

  # Add the tomcat user to the openwis group
  user { tomcat:
    groups  => [openwis],
    require => Group[openwis],
  }

  # Create the Tomcat home directory
  file { "${home_dir}":
    ensure => directory,
    owner  => tomcat,
    group  => tomcat,
    mode   => "0660",
  }

  # change the group on the logs folder, so that the openwis user can see the logs
  file { "/var/log/tomcat":
    ensure  => directory,
    owner   => tomcat,
    group   => tomcat,
    require => Package[tomcat]
  }

  # create tomcat logs folder link
  file { "${log_links_dir}/tomcat":
    ensure  => link,
    target  => "/var/log/tomcat",
    require => File[$log_links_dir, "/var/log/tomcat"]
  }

  # Set the security limits
  file { "/etc/security/limits.d/99-tomcat.conf":
    ensure  => file,
    owner   => root,
    group   => root,
    mode    => "0644",
    backup  => false,
    content => template("c_openwis/limits/tomcat.conf"),
  }

  # Ensure that the Tomcat package is installed
  package { tomcat:
    ensure => installed,
  }

  # Re-start the Tomcat service
  exec { restart-tomcat-service:
    command     => "service tomcat restart",
    user        => root,
    refreshonly => true,
    notify      => Service[tomcat],
    path        => $::path,
    require     => [Package[tomcat], Exec[systemd-daemon-reload]],
  }

  # Ensure that the Tomcat service is running
  service { tomcat:
    ensure  => running,
    enable  => true,
    require => [Package[tomcat], Exec[systemd-daemon-reload]],
  }

  # systemd doesn't use the limits in limits.conf
  # - the service config must be updated as well
  c_openwis::common::service_limits { update-tomcat-service-config:
    service => tomcat,
    nofile  => 8192,
  }

  # Configure firewall
  firewall { "310 allow from 0.0.0.0/0 to 8080 on all":
    proto  => tcp,
    action => accept,
    source => "0.0.0.0/0",
    dport  => 8080,
  }

  # logrotate console log (catalina.out)
  file { "/etc/logrotate.d/tomcat":
    ensure  => present,
    owner   => root,
    group   => root,
    mode    => "0640",
    content => template("${templates}/logrotate/tomcat"),
  }

  # compress old logs after 2 days (exclude the catalina.out file, as this is managed by logrotate)
  cron { compress-tomcat-logs:
    command => 'find /var/log/tomcat -type f -mtime +2 -not -name "catalina.out*" -not -name "*.gz" -exec gzip {} \;',
    user    => root,
    hour    => 00,
    minute  => 10,
  }

  # purge old logs after the log retention period has expired
  cron { purge-tomcat-logs:
    command => inline_template('find /var/log/tomcat -type f -mtime +<%= @log_retention_days %> -name "*.gz" -delete'),
    user    => root,
    hour    => 23,
    minute  => 15,
  }

  if $enable_ajp {
    # set the Tomcat AJP protocol port to the configured port (if not standard)
    if $ajp_port != 8009 {
      augeas { update-ajp-port:
        incl    => "${tomcat_dir}/conf/server.xml",
        lens    => "Xml.lns",
        context => "/files${tomcat_dir}/conf/server.xml",
        changes => [
          "defnode ajp Server/Service/Connector[#attribute/protocol = \"AJP/1.3\"] \"\"",
          "set \$ajp/#attribute/port \"8180\""],
        notify  => Service[tomcat],
      }
    }

    # update the firewall
    firewall { "888 allow from 0.0.0.0/0 to ${ajp_port} (Tomcat AJP) on all":
      proto  => tcp,
      action => accept,
      source => "0.0.0.0/0",
      dport  => $ajp_port,
    }
  }
}