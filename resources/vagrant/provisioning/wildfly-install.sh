#!/bin/bash
#Title : wildfly-install.sh
#Description : The script to install Wildfly 8.x
#Original script: http://sukharevd.net/wildfly-8-installation.html
         
# This version is the only variable to change when running the script
WILDFLY_VERSION=8.2.0.Final
WILDFLY_FILENAME=wildfly-$WILDFLY_VERSION
WILDFLY_ARCHIVE_NAME=$WILDFLY_FILENAME.tar.gz
WILDFLY_DOWNLOAD_ADDRESS=http://download.jboss.org/wildfly/$WILDFLY_VERSION/$WILDFLY_ARCHIVE_NAME

# Specify the destination location
INSTALL_DIR=/opt
WILDFLY_FULL_DIR=$INSTALL_DIR/$WILDFLY_FILENAME
WILDFLY_DIR=$INSTALL_DIR/wildfly
         
WILDFLY_USER="openwis"
WILDFLY_SERVICE="wildfly"
         
WILDFLY_STARTUP_TIMEOUT=240
WILDFLY_SHUTDOWN_TIMEOUT=30
         
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
         
if [[ $EUID -ne 0 ]]; then
echo "This script must be run as root."
exit 1
fi
         
echo "Downloading: $WILDFLY_DOWNLOAD_ADDRESS..."
[ -e "$WILDFLY_ARCHIVE_NAME" ] && echo 'Wildfly archive already exists.'
if [ ! -e "$WILDFLY_ARCHIVE_NAME" ]; then
wget $WILDFLY_DOWNLOAD_ADDRESS
if [ $? -ne 0 ]; then
echo "Not possible to download Wildfly."
exit 1
fi
fi

echo "Cleaning up..."
rm -f "$WILDFLY_DIR"
rm -rf "$WILDFLY_FULL_DIR"
rm -rf "/var/run/$WILDFLY_SERVICE/"
rm -f "/etc/init.d/$WILDFLY_SERVICE"
         

echo "Installation..."

echo "OpenJDK installation.. "		 
yum install -y java-1.8.0-openjdk-devel


echo "Wildfly installation.. "		 
mkdir $WILDFLY_FULL_DIR
tar -xzf $WILDFLY_ARCHIVE_NAME -C $INSTALL_DIR
ln -s $WILDFLY_FULL_DIR/ $WILDFLY_DIR
useradd -s /sbin/nologin $WILDFLY_USER
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR/
         
echo "Registering Wildfly as service..."
cp $WILDFLY_DIR/bin/init.d/wildfly-init-redhat.sh /etc/init.d/$WILDFLY_SERVICE
WILDFLY_SERVICE_CONF=/etc/default/wildfly.conf

chmod 755 /etc/init.d/$WILDFLY_SERVICE

if [ ! -z "$WILDFLY_SERVICE_CONF" ]; then
echo "Configuring service..."
echo JBOSS_HOME=\"$WILDFLY_DIR\" > $WILDFLY_SERVICE_CONF
echo JBOSS_USER=$WILDFLY_USER >> $WILDFLY_SERVICE_CONF
echo JBOSS_MODE=standalone >> $WILDFLY_SERVICE_CONF
echo JBOSS_CONFIG=standalone-full.xml >> $WILDFLY_SERVICE_CONF
echo STARTUP_WAIT=$WILDFLY_STARTUP_TIMEOUT >> $WILDFLY_SERVICE_CONF
echo SHUTDOWN_WAIT=$WILDFLY_SHUTDOWN_TIMEOUT >> $WILDFLY_SERVICE_CONF
fi

echo "Configuration backup"
cp $WILDFLY_DIR/standalone/configuration/standalone.xml $WILDFLY_DIR/standalone/configuration/standalone.orig
cp $WILDFLY_DIR/standalone/configuration/standalone-full.xml $WILDFLY_DIR/standalone/configuration/standalone-full.orig
cp $WILDFLY_DIR/bin/standalone.conf $WILDFLY_DIR/bin/standalone.orig

echo "Configuring application server..."
sed -i -e 's,<deployment-scanner path="deployments" relative-to="jboss.server.base.dir" scan-interval="5000"/>,<deployment-scanner path="deployments" relative-to="jboss.server.base.dir" scan-interval="5000" deployment-timeout="'$WILDFLY_STARTUP_TIMEOUT'"/>,g' $WILDFLY_DIR/standalone/configuration/standalone-full.xml

# The below line is added to avoid warning when starting WildFly with jdk 8 SE, as the JVM memory parameter changed
sed -i -e 's/MaxPermSize/MaxMetaspaceSize/g' $WILDFLY_DIR/bin/standalone.conf


echo "Configuring Firewalld for WildFly ports"
#firewall-cmd --permanent --add-port=8080/tcp
#firewall-cmd --permanent --add-port=8443/tcp
#firewall-cmd --permanent --add-port=9990/tcp
#firewall-cmd --permanent --add-port=9993/tcp
#firewall-cmd --reload

echo "Backup management user"
cp $WILDFLY_DIR/standalone/configuration/mgmt-users.properties $WILDFLY_DIR/standalone/configuration/mgmt-users-org.properties
cp $WILDFLY_DIR/standalone/configuration/application-users.properties $WILDFLY_DIR/standalone/configuration/application-users-org.properties
cp $WILDFLY_DIR/domain/configuration/mgmt-users.properties $WILDFLY_DIR/domain/configuration/mgmt-users-org.properties
cp $WILDFLY_DIR/domain/configuration/application-users.properties $WILDFLY_DIR/domain/configuration/application-users-org.properties
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR/standalone/configuration/mgmt-users-org.properties
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR/standalone/configuration/application-users-org.properties
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR/domain/configuration/mgmt-users-org.properties
chown -R $WILDFLY_USER:$WILDFLY_USER $WILDFLY_DIR/domain/configuration/application-users-org.properties

#echo "*****************************************************"
#cat $WILDFLY_DIR/standalone/configuration/standalone-full.xml
#echo " *****************************************************"


echo "Restaring Wildfly.."
sudo service $WILDFLY_SERVICE stop

sudo service $WILDFLY_SERVICE start
chkconfig --add wildfly
chkconfig --level 2345 wildfly on

echo "Done."
