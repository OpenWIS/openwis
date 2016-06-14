###############################################################################
# == Component: Tidy-up the "downloads" folder
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
# This common class simplifies the clearing down of the 'downloads' folder.
#
# === Notes
#
# The class only removes deletes the contents of the folder (recursively).
# The folder itself is left on the filesystem, else it gets created & removed
# on EVERY puppet run.
#
###############################################################################

class c_openwis::common::tidy_downloads (
) {
  include c_openwis

  $downloads_dir = $c_openwis::downloads_dir

  exec { tidy_downloads:
    command => "rm -rf ${downloads_dir}/* || :",
    timeout => 0,
    user    => root,
    onlyif  => "test -d ${downloads_dir} && test -n \"`ls ${downloads_dir}`\"",
    path    => $::path
  }
}
