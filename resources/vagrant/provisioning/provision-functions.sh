#!/bin/bash
#

# =====================================================================================
# FUNCTIONS
#

# Configures an XML file by replacing all '@<name>@' placeholders with a value.
# This is performed as the "openwis" user
function owConf()
{
    local file="$1"
    local name="$2"
    local value="$3"

    local sudoCmd="sudo -u openwis -i"
    if [ `whoami` = "openwis" ]; then
        sudoCmd=""
    fi
    $sudoCmd sed -i -r -e 's|@'"$name"'@|'"$value"'|g' "$file"
}

# Configures a .properties file by setting the value of a property
function setProp()
{
    local file="$1"
    local propkey="$2"
    local value="$3"

    local sudoCmd="sudo -u openwis -i"
    if [ `whoami` = "openwis" ]; then
        sudoCmd=""
    fi

    $sudoCmd sed -i -r -e 's|^'"$propkey"'=.*|'"$propkey"'='"$value"'|g' "$file"
}

# Wrapper around sudo which will execute the command as OpenWIS
function openwisDo()
{
    local sudoCmd="sudo -u openwis -i"
    if [ `whoami` = "openwis" ]; then
        sudoCmd=""
    fi

    $sudoCmd $@
}

# Deploy the vagrant scripts to the home directory of vagrant
#function deployVagrantScripts()
#{
#    local group="$1"
#
#    sudo -u vagrant -i cp /vagrant/scripts/"$group"/* /home/vagrant/.
#    sudo -u vagrant -i dos2unix /home/vagrant/*.sh 2>/dev/null
#    sudo -u vagrant -i chmod u+x /home/vagrant/*.sh
#}