###############################################################################
# == Component: Execute a psql command
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
# This function simplifies the execution of psql scripts.
#
# Scripts can be specified as Puppet templates or file system files.
#
# === Notes
#
# Either the 'template' or the 'file' parameter must be supplied.  If both are
# supplied, the 'template' parameter takes prcedence.
#
# If the 'file' parameter is supplied, it must refer to a file on the local
# file system that contains the SQL script to execute.
#
# If the 'template' parameter is supplied, it must refer to a valid Puppet
# template that contains the SQL script to execute.
#
###############################################################################

define c_openwis::database::exec_psql (
  $schema   = "OpenWIS",
  $template = undef,
  $file     = undef,
  $user     = undef,
  #
  ) {
  # set the schema
  if $schema != undef {
    $cmd_schema = "-d ${schema}"
  } else {
    $cmd_schema = ""
  }

  # set the user
  if $user != undef {
    $cmd_user = "-U ${user}"
  } else {
    $cmd_user = ""
  }

  # use a template, if applicable
  if $template != undef {
    $template_split = split($template, "/")
    $tmp_template   = $template_split[-1]

    file { "/tmp/${tmp_template}":
      ensure  => file,
      backup  => false,
      content => template("${template}"),
    } ->
    exec { "psql-${title}":
      command => "/usr/pgsql-9.4/bin/psql ${cmd_schema} -f /tmp/${tmp_template} ${cmd_user}",
      timeout => 0,
      user    => postgres,
    }
  } else {
    # set the file
    if $file != undef {
      $cmd_file = "-f ${file}"
    } else {
      $cmd_file = ""
    }

    exec { "psql-${title}":
      command => "/usr/pgsql-9.4/bin/psql ${cmd_schema} ${cmd_file} ${cmd_user}",
      timeout => 0,
      user    => postgres,
    }
  }
}

