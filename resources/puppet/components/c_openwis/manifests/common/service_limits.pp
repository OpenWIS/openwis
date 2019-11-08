###############################################################################
# == Component: Configure service limits in RHEL
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
# This common function simplifies the setting of service limits for uses,
# managing the systemd limits file for the user.
#
# === Notes
#
# systemd doesn't use the limits in limits.conf
# - the service config must be updated as well
#
###############################################################################

define c_openwis::common::service_limits (
  $service,
  $nofile = undef,
  $stack  = undef)
#
{
  include c_openwis::common::systemd

  if $nofile != undef {
    augeas { "update-${service}-nofiles":
      incl    => "/usr/lib/systemd/system/${service}.service",
      lens    => "Systemd.lns",
      context => "/files/usr/lib/systemd/system/${service}.service",
      changes => [
        "defnode nofile Service/LimitNOFILE \"\"",
        "set \$nofile/value \"${nofile}\""],
      notify  => Exec[systemd-daemon-reload],
    }
  }

  if $stack != undef {
    augeas { "update-${service}-stack":
      incl    => "/usr/lib/systemd/system/${service}.service",
      lens    => "Systemd.lns",
      context => "/files/usr/lib/systemd/system/${service}.service",
      changes => [
        "defnode stack Service/LimitSTACK \"\"",
        "set \$stack/value \"${stack}\""],
      notify  => Exec[systemd-daemon-reload],
    }
  }
}

