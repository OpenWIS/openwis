# == Node: devadminportal
#
# Web Tier containing OpenWIS Admin Portal
#
# === Authors
#
# Martin Gollogly <martin.gollogly@metoffice.gov.uk>
# Andy Jacobs <andy.jacobs@metoffice.gov.uk>
#
# === Copyright
#
# Crown Copyright 2015, unless otherwise noted.
#
# TO RUN:
#    HNAME=$(hostname -s) && sudo puppet apply nodes/${HNAME: -5}/${HNAME}.pp --modulepath=openwis:components:modules --hiera_config=hiera.yaml --strict_variables

$openwis_env = dev

node devadminportal {
  Package {
    allow_virtual => false,
  }

  # temporarily disable the puppet daemon
  service { puppet:
    ensure => stopped,
  }

  #============================================================================
  # Install User Portal
  #============================================================================
  class { c_openwis::portals:
    portal => "admin",
  }
  class { c_openwis::portals::proxy:
    portals => ["admin"],
  }
  class { c_openwis::securityservice::proxy:
  }

}
