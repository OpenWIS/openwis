###############################################################################
# == Component: Install & Configure OpenDJ
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
# This function Installs & configures OpenDJ for OpenWIS, as follows:
#
# * Downloads & unpacks the OpenDJ installer
# * Configures the installer for this environment
# * Insalls OpenDJ
# * Imports the OpenWIS LDAP schema
#
# === Notes
#
#
###############################################################################

define c_openwis::securityservice::install_opendj ($opendj_root_password
)
#
{
  include c_openwis
  include c_openwis::common::tidy_downloads

  $binaries_repo        = $c_openwis::binaries_repo
  $opendj_hostname      = $::hostname
  $templates            = "c_openwis/securityservice/opendj"
  $openwis_home         = $c_openwis::home_dir
  $opendj_dir           = "${openwis_home}/opendj"

  # download & unzip the OpenDJ installation
  c_openwis::common::unzip { "OpenDJ-2.6.0.zip":
    source      => "${binaries_repo}/OpenDJ-2.6.0.zip",
    destination => "${openwis_home}",
    user        => openwis,
    creates     => "${opendj_dir}",
    before      => Class[c_openwis::common::tidy_downloads],
    require     => File["${openwis_home}"]
  } ->
  # configure opendj.properties
  file { "${opendj_dir}/opendj.properties":
    ensure  => file,
    owner   => openwis,
    content => template("${templates}/opendj.properties"),
  } ->
  # run the OpenDJ setup
  exec { install-opendj:
    command => "${opendj_dir}/setup --cli --acceptLicense --no-prompt --propertiesFilePath ${opendj_dir}/opendj.properties",
    creates => "${opendj_dir}/config",
  } ->
  #TODO run the OpenDJ config, currently this is done manually
  #exec { configure-opendj:
  #  command => "${opendj_dir}/dsconfig --cli --acceptLicense --no-prompt --propertiesFilePath ${opendj_dir}/dsconfig.properties",
  #} ->
  # import SchemaOpenWIS.ldif
  file { "${opendj_dir}/SchemaOpenWIS.ldif":
    ensure => file,
    owner  => openwis,
    source => "puppet:///modules/${templates}/SchemaOpenWIS.ldif",
  } ->
  c_openwis::common::exec_once { import-schema:
    command   => "ldapmodify -h localhost -p 4445 -D \"cn=Directory Manager\" -w ${opendj_root_password} -a -X --useSSL -f ${opendj_dir}/SchemaOpenWIS.ldif",
    user      => openwis,
    touchfile => "${opendj_dir}/.schema_imported",
    path      => ["${opendj_dir}/bin", $::path]
  }
}

