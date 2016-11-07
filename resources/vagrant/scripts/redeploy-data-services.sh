#!/bin/bash
#
#   Redeploy the data services without tearing down the machine.
#


source /vagrant/resources/vagrant/provisioning/provision-config.sh
source /vagrant/resources/vagrant/provisioning/provision-functions.sh


openwisHome="/home/openwis"
jbossHome="$openwisHome"/jboss-as-7.1.1.Final


function jbossCli()
{
    local cmd="$1"

    echo "[jboss-cli] $cmd"
    sudo -iu openwis "$jbossHome"/bin/jboss-cli.sh -c --command="$cmd"
}


jbossCli "undeploy `basename $ARTEFACT_DATA_SERVICE_EAR`"
jbossCli "undeploy `basename $ARTEFACT_MANAGEMENT_SERVICE_EAR`"

jbossCli "deploy $ARTEFACT_MANAGEMENT_SERVICE_EAR"
jbossCli "deploy $ARTEFACT_DATA_SERVICE_EAR"