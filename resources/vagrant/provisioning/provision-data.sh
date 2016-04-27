#!/bin/bash
#
#   The provisioning script for the data machine using JBoss AS 7.1
#


# Common provisioning steps and env setup
source /vagrant/resources/vagrant/provisioning/provision-common.sh

# Copy vagrant scripts
#deployVagrantScripts data

# =====================================================================================
# DATA SERVICES PROVISIONING

# -------------------------------------------------------------------------------------
# Install the psql client 

sudo yum -y install postgresql.x86_64

# -------------------------------------------------------------------------------------
# Make root directory

mkdir -p "$DS_DIR"
chown openwis: "$DS_DIR"

openwisHome="/home/openwis"
jbossHome="$openwisHome"/jboss-as-7.1.1.Final

# ------------------------------------------------------------------------------------
# Environment setup

# Create data directories.  This must be explained for new installations but not for upgrade.
mkdir "$DS_DIR"
chown openwis: "$DS_DIR"

sudo -iu openwis mkdir "$DS_DIR/harness"
sudo -iu openwis mkdir "$DS_DIR/harness/incoming"
sudo -iu openwis mkdir "$DS_DIR/harness/ingesting"
sudo -iu openwis mkdir "$DS_DIR/harness/ingesting/fromReplication"
sudo -iu openwis mkdir "$DS_DIR/harness/outgoing"
sudo -iu openwis mkdir "$DS_DIR/harness/working"
sudo -iu openwis mkdir "$DS_DIR/harness/working/fromReplication"
sudo -iu openwis mkdir "$DS_DIR/cache"
sudo -iu openwis mkdir "$DS_DIR/stagingPost"
sudo -iu openwis mkdir "$DS_DIR/temp"
sudo -iu openwis mkdir "$DS_DIR/replication"
sudo -iu openwis mkdir "$DS_DIR/replication/sending"
sudo -iu openwis mkdir "$DS_DIR/replication/sending/local"
sudo -iu openwis mkdir "$DS_DIR/replication/sending/destinations"
sudo -iu openwis mkdir "$DS_DIR/replication/receiving"
sudo -iu openwis mkdir "$DS_DIR/status"


# JBoss and OpenWIS is installed and configured in setup-openwis-using-jboss7.sh
/vagrant/resources/vagrant/provisioning/setup-openwis-using-jboss7.sh

