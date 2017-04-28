# ##############################################################################
# == Component: Install & Configure OpenWIS JBoss
# Application Server
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
# This class ensures that the correct JBoss AS version is installed
# & configured, as follows:
#
# * Ensures that the relevant folders & links exist
# * Downloads & unpacks the JBoss AS installation file
# * Ensures that the JBoss folders & files have the correct ownership
# * Ensures that iptables is configured to allow remote connections to JBoss
# * Installs & configures the 'jboss-as' service
# * Sets some base JBoss configuration values:
#   - Binds the JBoss listener to all interfaces
#   - Ensures that JBoss is using the UTC timezone
#   - Ensures that JBoss listens on port 8180
#      (default of 8080 clashes with Tomcat)
#
# === Notes
#
#
###############################################################################

class c_openwis::jboss (
)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads
  include c_openwis::common::systemd

  $binaries_repo        = $c_openwis::binaries_repo
  $jboss_installer_file = $c_openwis::jboss_installer_file
  $jboss_as_dir         = "/usr/share/jboss-as"
  $openwis_home         = $c_openwis::home_dir
  $downloads_dir        = $c_openwis::downloads_dir
  $templates            = "c_openwis/jboss"
  $log_links_dir        = $c_openwis::log_links_dir
  $log_retention_days   = $c_openwis::log_retention_days

  Exec {
    user    => openwis,
    timeout => 0,
  }

  # create required folders
  file { ["${jboss_as_dir}", "/etc/jboss-as", "/var/log/jboss-as", "/var/run/jboss-as"]:
    ensure => directory,
    owner  => openwis,
    group  => openwis,
    mode   => "0660",
  }

  # logrotate console log (console.log)
  file { "/etc/logrotate.d/jboss-as":
    ensure  => present,
    owner   => root,
    group   => root,
    mode    => "0640",
    content => template("${templates}/logrotate/jboss-as"),
  }

  # compress old logs after 2 days (exclude the console.log file, as this is managed by logrotate)
  cron { compress-jboss-logs:
    command => 'find /var/log/jboss-as -type f -mtime +2 -not -name "console.log*" -not -name "*.gz" -exec gzip {} \;',
    user    => root,
    hour    => 00,
    minute  => 10,
  }

  # purge old logs after the log retention period has expired
  cron { purge-jboss-logs:
    command => inline_template('find /var/log/jboss-as -type f -mtime +<%= @log_retention_days %> -name "*.gz" -delete'),
    user    => root,
    hour    => 23,
    minute  => 15,
  }

  # Download the JBoss installer
  c_openwis::common::wget { "${jboss_installer_file}":
    source  => "${binaries_repo}/${jboss_installer_file}",
    creates => "${jboss_as_dir}/LICENSE.txt",
  } ->
  # unpack the JBoss installation
  exec { unpack-jboss-installation:
    command => "tar -xvzf ${downloads_dir}/${jboss_installer_file} -C ${jboss_as_dir} --strip-components 1",
    creates => "${jboss_as_dir}/LICENSE.txt",
    before  => Class[c_openwis::common::tidy_downloads],
    path    => $::path,
    require => File["${jboss_as_dir}"],
    notify  => Exec[set-jboss-installation-ownership],
  } ->
  file { "${jboss_as_dir}/standalone/log":
    ensure  => link,
    target  => "/var/log/jboss-as",
    require => File["/var/log/jboss-as"]
  } ->
  file { "${log_links_dir}/jboss-as":
    ensure  => link,
    target  => "/var/log/jboss-as",
    require => File[$log_links_dir]
  }

  # ensure that the ownership is correct
  exec { set-jboss-installation-ownership:
    command     => "chown -R openwis: ${jboss_as_dir}",
    path        => $::path,
    refreshonly => true
  }

  # Configure firewall
  firewall { "888 allow from 0.0.0.0/0 to 8180 (JBoss) on all":
    proto  => tcp,
    action => accept,
    source => "0.0.0.0/0",
    dport  => 8180,
  }

  # Configure JBoss service
  file { "/etc/systemd/system/jboss-as.service":
    ensure  => file,
    backup  => false,
    content => template("${templates}/service/jboss-as.service"),
    notify  => Exec[systemd-daemon-reload],
  }

  file { "/etc/jboss-as/jboss-as.conf":
    ensure  => file,
    backup  => false,
    content => template("${templates}/service/jboss-as.conf"),
  }

  # Set the JBoss startup options
  file_line { set-jboss-startup-options:
    path    => "${jboss_as_dir}/bin/standalone.conf",
    line    => "JAVA_OPTS=\"\$JAVA_OPTS -Duser.timezone=UTC -Djboss.bind.address=0.0.0.0\"",
    require => Exec[unpack-jboss-installation],
  } ->
  # configure the JBoss settings
  augeas { configure-jboss-settings:
    incl    => "${jboss_as_dir}/standalone/configuration/standalone-full.xml",
    lens    => "Xml.lns",
    context => "/files${jboss_as_dir}/standalone/configuration/standalone-full.xml",
    changes => [
      "defnode binding server/socket-binding-group/socket-binding[#attribute/name = \"http\"] \"\"",
      "set \$binding/#attribute/port \"8180\""],
  }
}

