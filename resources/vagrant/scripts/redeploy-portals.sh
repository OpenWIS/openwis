#!/bin/bash
#
#   Redeploy the user and admin portal without tearing down the machine.
#


source /vagrant/resources/vagrant/provisioning/provision-config.sh
source /vagrant/resources/vagrant/provisioning/provision-functions.sh

openwisHome="/home/openwis"
tomcatHome="$openwisHome/`basename "$RESOURCE_TOMCAT" .tar.gz`"


# Stop the portals
sudo -iu openwis ./stop_openwis_tomcat.sh

# Remove the old portals
sudo -iu openwis rm -rf "$tomcatHome"/webapps/openwis-user-portal
sudo -iu openwis rm -rf "$tomcatHome"/webapps/openwis-admin-portal

# Deploy the new portals
sudo -iu openwis unzip -d "$tomcatHome"/webapps/openwis-user-portal "$ARTEFACT_USER_PORTAL"
sudo -iu openwis unzip -d "$tomcatHome"/webapps/openwis-admin-portal "$ARTEFACT_ADMIN_PORTAL"

# Reconfigure the portals
sudo -iu openwis /vagrant/resources/vagrant/provisioning/config-portals.sh

# Start the portals
# Using nohup as simply calling "./start_openwis_tomcat.sh" will terminate Tomcat 
# when the SSH session ends.
sudo -iu openwis bash -c "nohup ./start_openwis_tomcat.sh"