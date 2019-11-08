#!/bin/bash
#
#   Common provisioning routines
#

source /vagrant/resources/vagrant/provisioning/provision-config.sh
source /vagrant/resources/vagrant/provisioning/provision-functions.sh

# ---------------------------------------------------------------------------
# Download some useful tools missing from the base box

yum install -y dos2unix

# ---------------------------------------------------------------------------
# Set the timezone to UTC
cat /usr/share/zoneinfo/UTC > /etc/localtime


# ---------------------------------------------------------------------------
# Configure the hosts file so that hostnames can resolve to IP addresses

sed -i -e 's/'"`hostname`"'//g' /etc/hosts
echo "192.168.54.101    owdev-db" >> /etc/hosts
echo "192.168.54.102    owdev-data" >> /etc/hosts
echo "192.168.54.103    owdev-portals" >> /etc/hosts


# ---------------------------------------------------------------------------
# Add the OpenWIS user

useradd -m -s /bin/bash openwis
echo "$OPENWIS_PASSWD" | passwd --stdin openwis


# ---------------------------------------------------------------------------
