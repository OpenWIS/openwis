# == Node: devsecurity
#
# Security Service (OpenAM / OpenDJ)
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
#    HNAME=$(hostname -s) && sudo puppet apply nodes/${HNAME: -5}/${HNAME}.pp --modulepath=openwis:components:modules
#    --hiera_config=hiera.yaml --strict_variables

$openwis_env = dev

node devsecurity {
  Package {
    allow_virtual => false,
  }

  # temporarily disable the puppet daemon
  service { puppet:
    ensure => stopped,
  }

 
  #============================================================================
  # Install Security Service
  #============================================================================
  class { c_openwis::securityservice:
  }
}
