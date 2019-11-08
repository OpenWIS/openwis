# ##############################################################################
# == Component: Configure Java 7 for OpenWIS
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
# This function ensures that the correct version of Java is running on each
# of the OpenWIS servers.
#
# The function ensures that appropriate version of java 7 is installed - both
# the main & 'devel' packages.
#
# Additionally, if installing v1.7.0.19, also ensures that the 'headless'
# package is not installed as this is not applicable for this version.
#
# If Java 8 is also found on the server, ensures that Java 7 is used in
# preference to Java 8.
#
# If the server needs to support SSL, ensures that the SSL certificate & key
# are imported into the Java certificate store.
#
# === Notes
#
# The portal servers (User Portal & Admin Portal) must run a specific version
# of Java, which is currently v1.7.0.19.
#
# All other servers must run Java 7, but this can an currently support point
# version.
#
###############################################################################

define c_openwis::java::java7 (
  $is_portal,
  #
  ) {
  include c_openwis
  include c_openwis::common::tidy_downloads

  $binaries_repo = $c_openwis::binaries_repo
  $downloads_dir = $c_openwis::downloads_dir
  $use_ssl       = $c_openwis::use_ssl

  #============================================================================
  # For Portals install specific version of Java7 (1.7.0.19)
  #============================================================================
  if $is_portal {
    $version = "1.7.0.19-2.3.9.1.el6_4"

    # install the Java 7 packages at the specified version
    c_openwis::common::wget { "java-1.7.0-openjdk":
      source => "${binaries_repo}/java-1.7.0-openjdk-${version}.x86_64.rpm",
      unless => "yum list installed java-1.7.0-openjdk | grep \"${version}\"",
    } ->
    package { "java-1.7.0-openjdk":
      ensure          => "${version}",
      provider        => rpm,
      source          => "${downloads_dir}/java-1.7.0-openjdk-${version}.x86_64.rpm",
      install_options => ["--nodeps"],
      before          => [Class[c_openwis::common::tidy_downloads], Exec[ensure-java7]]
    } ->
    c_openwis::common::wget { "java-1.7.0-openjdk-devel":
      source => "${binaries_repo}/java-1.7.0-openjdk-devel-${version}.x86_64.rpm",
      unless => "yum list installed java-1.7.0-openjdk-devel | grep \"${version}\"",
    } ->
    package { "java-1.7.0-openjdk-devel":
      ensure          => "${version}",
      provider        => rpm,
      source          => "${downloads_dir}/java-1.7.0-openjdk-devel-${version}.x86_64.rpm",
      install_options => ["--nodeps"],
      before          => [Class[c_openwis::common::tidy_downloads], Exec[ensure-java7]]
    } ->
    # Ensure that "headless" version of Java 7 is NOT installed
    package { "java-1.7.0-openjdk-headless":
      ensure => absent,
      before => Exec[ensure-java7]
    }
  }
  #============================================================================
  # Otherwise, install current version of Java7
  #============================================================================
   else {
    # install the Java 7 packages
    package { "java-1.7.0-openjdk":
      ensure => installed,
      before => Exec[ensure-java7]
    } ->
    package { "java-1.7.0-openjdk-devel":
      ensure => installed,
      before => Exec[ensure-java7]
    }
  }

  # ============================================================================
  # Ensure the system is using Java 7,
  # if Java 8 is also installed
  #============================================================================
  exec { ensure-java7:
    command => 'alternatives --set java `alternatives --display java | grep priority | grep 1.7.0 | cut -d" " -f 1`',
    onlyif  => 'alternatives --display java | grep auto',
    user    => root,
    path    => $::path,
  }

  #============================================================================
  # Import OpenWIS SSL cert, if required
  #============================================================================
  if $use_ssl {
    $ssl_cert_file    = hiera("c_openwis::ssl_cert_file")
    $jvm_security_dir = "/usr/lib/jvm/jre/lib/security"

    file { "${jvm_security_dir}/openwis.crt":
      ensure  => file,
      owner   => root,
      source  => "${ssl_cert_file}",
      require => Exec[ensure-java7]
    } ->
    exec { backup-cacerts:
      cwd     => "${jvm_security_dir}",
      command => "cp cacerts cacerts.bak",
      user    => root,
      creates => "${jvm_security_dir}/cacerts.bak",
      path    => $::path,
    } ->
    exec { import-openwis-cert:
      cwd     => "${jvm_security_dir}",
      command => "keytool -import -alias openwis -file openwis.crt -keystore cacerts -storepass changeit -noprompt",
      unless  => "keytool -list -keystore cacerts -storepass changeit -noprompt | grep openwis",
      user    => root,
      path    => $::path,
    }
  }
}
