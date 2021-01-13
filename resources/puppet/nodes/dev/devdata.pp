# == Node: devdata
#
# Data Tier - DataService containing Database, Solr and Jboss, Staging Post
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
#    HNAME=$(hostname -s) && sudo puppet apply nodes/${HNAME: -5}/${HNAME}.pp
#    --modulepath=openwis:components:modules --hiera_config=hiera.yaml
#    --strict_variables

$openwis_env = dev

node devdata {
  Package {
    allow_virtual => false,
  }

  # temporarily disable the puppet daemon
  service { puppet:
    ensure => stopped,
  }

  #============================================================================
  # Database
  #============================================================================
  class { c_openwis::database:
  } ->
 
  #============================================================================
  # Data Service
  #============================================================================
  class { c_openwis::dataservice:
    require => Class[c_openwis::database],
  }

  #============================================================================
  # Solr
  #============================================================================
  class { c_openwis::solr:
    require => Class[c_openwis::database],
  }

  #============================================================================
  # Staging Post
  #============================================================================
  class { c_openwis::stagingpost:
  }

  class { c_openwis::stagingpost::proxy:
    require => Class[c_openwis::stagingpost],
  }



}
