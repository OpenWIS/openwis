#!/bin/bash
#
#   The provisioning script for the portals machine
#


# Common provisioning steps and env setup
source /vagrant/resources/vagrant/provisioning/provision-common.sh

# Copy vagrant scripts
deployVagrantScripts portals 

# =====================================================================================
# PORTAL PROVISIONING


# --------------------------------------------------------------------------------------
# Create data directory

mkdir -p /data/openwis
chown -R openwis: /data/openwis

sudo -iu openwis mkdir /data/openwis/reports

# Solr directories
sudo -iu openwis mkdir /data/openwis/solr
sudo -iu openwis mkdir /data/openwis/solr/data
sudo -iu openwis mkdir /data/openwis/solr/spatial

# --------------------------------------------------------------------------------------
# Install Java and Tomcat

openwisHome="/home/openwis"
openwisOpt="/home/openwis/opt"
jdkHome="$openwisOpt/jdk1.7.0_51"
tomcatHome="$openwisHome/apache-tomcat-6.0.29"

sudo -iu openwis mkdir "$openwisOpt"
sudo -iu openwis mkdir "$openwisHome/staging"

#yum install -y java-1.7.0-openjdk-devel.x86_64

echo "Unpacking Java"
sudo -iu openwis tar -xvz -C "$openwisOpt" -f /vagrant/dependencies/jdk-7u51-linux-x64.tar.gz

echo "Unpacking Tomcat"
sudo -iu openwis unzip -q -d "$openwisHome" /vagrant/dependencies/apache-tomcat-6.0.29.zip
sudo -iu openwis chmod u+x "$tomcatHome/bin/"*.sh
sudo -iu openwis cp /vagrant/artefacts/portals/conf/setenv.sh "$tomcatHome/bin/."

echo "Configuring Environment"
sudo -iu openwis cat >> "$openwisHome/.bashrc" << .
export JAVA_HOME="$jdkHome"
export PATH="\$PATH:\$JAVA_HOME/bin"
.


# --------------------------------------------------------------------------------------
# Deploy the dev-container

sudo -iu openwis unzip /vagrant/artefacts/portals/dev-container.zip
sudo -iu openwis nohup /home/openwis/dev-container/bin/dev-container >/home/openwis/dev-container/dev-container.log 2>&1 &

# --------------------------------------------------------------------------------------
# Deploy the portals

sudo -iu openwis cp /vagrant/artefacts/portals/scripts/* "$openwisHome"
sudo -iu openwis chmod u+x "$openwisHome"/*.sh
sudo -iu openwis dos2unix "$openwisHome"/*.sh

sudo -iu openwis cp /vagrant/artefacts/portals/openwis-user-portal.war "$openwisHome"/staging/.
sudo -iu openwis cp /vagrant/artefacts/portals/openwis-admin-portal.war "$openwisHome"/staging/.
sudo -iu openwis "$openwisHome"/deploy-portals.sh -nobackup

sudo -iu openwis cp /vagrant/artefacts/portals/conf/user-portal/openwis-metadataportal.properties "$tomcatHome"/webapps/openwis-user-portal/WEB-INF/classes/.
sudo -iu openwis cp /vagrant/artefacts/portals/conf/admin-portal/openwis-metadataportal.properties "$tomcatHome"/webapps/openwis-admin-portal/WEB-INF/classes/.

# --------------------------------------------------------------------------------------
# Config the portals

sudo -iu openwis /vagrant/provisioning/config-portals.sh
