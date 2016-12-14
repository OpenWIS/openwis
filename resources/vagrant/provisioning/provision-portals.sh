#!/bin/bash
#
#   The provisioning script for the portals machine
#


# Common provisioning steps and env setup
source /vagrant/resources/vagrant/provisioning/provision-common.sh

# Copy vagrant scripts
#deployVagrantScripts portals 

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
#jdkHome="$openwisOpt/jdk1.7.0_51"
#tomcatHome="$openwisHome/apache-tomcat-6.0.29"
tomcatHome="$openwisHome/`basename "$RESOURCE_TOMCAT" .tar.gz`"

sudo -iu openwis mkdir "$openwisOpt"
sudo -iu openwis mkdir "$openwisHome/staging"

# Install latest available JDK (seems to break installation after 1.7.0.101-2.6.6.4)
# yum install -y java-1.7.0-openjdk-devel.x86_64

# Install latest compatible JDK (still available in YUM).
#yum install -y java-1.7.0-openjdk-devel-1.7.0.101-2.6.6.4.el6_8
yum install -y java-1.7.0-openjdk-devel-1.7.0.121

#echo "Unpacking Java"
#sudo -iu openwis tar -xvz -C "$openwisOpt" -f /vagrant/dependencies/jdk-7u51-linux-x64.tar.gz

#echo "Unpacking Tomcat"
#sudo -iu openwis unzip -q -d "$openwisHome" /vagrant/dependencies/apache-tomcat-6.0.29.zip
#sudo -iu openwis chmod u+x "$tomcatHome/bin/"*.sh
#sudo -iu openwis cp /vagrant/artefacts/portals/conf/setenv.sh "$tomcatHome/bin/."

echo "Unpacking Tomcat"
sudo -iu openwis wget -q -O /tmp/apache-tomcat.tar.gz "$RESOURCE_TOMCAT"
sudo -iu openwis tar -xvz -C "$openwisHome" -f /tmp/apache-tomcat.tar.gz
sudo -iu openwis chmod u+x "$tomcatHome/bin/"*.sh

#echo "Configuring Environment"
#sudo -iu openwis cat >> "$openwisHome/.bashrc" << .
#export JAVA_HOME="$jdkHome"
#export PATH="\$PATH:\$JAVA_HOME/bin"
#.


# --------------------------------------------------------------------------------------
# Deploy the dev-container

# TODO
#sudo -iu openwis unzip /vagrant/artefacts/portals/dev-container.zip
#sudo -iu openwis nohup /home/openwis/dev-container/bin/dev-container >/home/openwis/dev-container/dev-container.log 2>&1 &

# --------------------------------------------------------------------------------------
# Deploy the portals

# Startup scripts
sudo -iu openwis cp "$ARTEFACT_PORTAL_SCRIPTS"/* "$openwisHome"
sudo -iu openwis sed -i -e 's|export CATALINA_HOME=.*|export CATALINA_HOME='"$tomcatHome"'|' "$openwisHome"/*.sh
sudo -iu openwis chmod u+x "$openwisHome"/*.sh
sudo -iu openwis dos2unix "$openwisHome"/*.sh

# Portal artefacts
#sudo -iu openwis cp /vagrant/artefacts/portals/openwis-user-portal.war "$openwisHome"/staging/openwis-user-portal.war
#sudo -iu openwis cp /vagrant/artefacts/portals/openwis-admin-portal.war "$openwisHome"/staging/.
#sudo -iu openwis "$openwisHome"/deploy-portals.sh -nobackup
sudo -iu openwis unzip -d "$tomcatHome"/webapps/openwis-user-portal "$ARTEFACT_USER_PORTAL"
sudo -iu openwis unzip -d "$tomcatHome"/webapps/openwis-admin-portal "$ARTEFACT_ADMIN_PORTAL"

#sudo -iu openwis cp /vagrant/artefacts/portals/conf/user-portal/openwis-metadataportal.properties "$tomcatHome"/webapps/openwis-user-portal/WEB-INF/classes/.
#sudo -iu openwis cp /vagrant/artefacts/portals/conf/admin-portal/openwis-metadataportal.properties "$tomcatHome"/webapps/openwis-admin-portal/WEB-INF/classes/.

# --------------------------------------------------------------------------------------
# Config the portals

sudo -iu openwis /vagrant/resources/vagrant/provisioning/config-portals.sh


# Start the portals
sudo -iu openwis ./start_openwis_tomcat.sh
