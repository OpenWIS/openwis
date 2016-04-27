#!/bin/bash
#
#   The provisioning script for the data using JBoss 5
#


# Common provisioning steps and env setup
source /vagrant/resources/vagrant/provisioning/provision-common.sh

# Copy vagrant scripts
deployVagrantScripts data

# =====================================================================================
# DATA SERVICES PROVISIONING

# -------------------------------------------------------------------------------------
# Make root directory

mkdir -p "$DS_DIR"
chown openwis: "$DS_DIR"

# -------------------------------------------------------------------------------------
# Setup JBoss

openwisHome="/home/openwis"
openwisOpt="/home/openwis/opt"
jdkHome="$openwisOpt/jdk1.6.0_33"
jbossHome="$openwisHome/EnterprisePlatform-5.1.0/jboss-eap-5.1/jboss-as"

sudo -iu openwis mkdir "$openwisOpt"
sudo -iu openwis mkdir "$openwisHome/staging"

echo "Unpacking Java"
sudo -iu openwis unzip -q -d "$openwisOpt" /vagrant/dependencies/jdk1.6.0_33.zip

echo "Unpacking JBoss"
sudo -iu openwis unzip -q -d "$openwisHome" /vagrant/dependencies/EnterprisePlatform-5.1.0.zip

echo "Configuring Environment"
sudo -iu openwis cat >> "$openwisHome/.bashrc" << .
export JAVA_HOME="$jdkHome"
export JBOSS_HOME="$jbossHome"
export PATH="\$PATH:\$JAVA_HOME/bin"
.
sudo -iu openwis mkdir "$jbossHome/server/production/log"
sudo -iu openwis ln -s "$jbossHome/server/production/log" "$openwisHome/logs"

# Create directories
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

# Deploy and configure JBoss

# To enable port forwarding, the SOAP server in JBoss must not be allowed to modify the soap:address element.
# This can be disabled by commenting out the 'webServiceHost' element in the JBoss WS deployer
sudo -iu openwis sed -i -e 's|<property name="webServiceHost".*/property>|<!--&-->|' "$jbossHome/server/production/deployers/jbossws.deployer/META-INF/jboss-beans.xml"

# Deploy and configure artefacts
jndiServiceXml="$jbossHome/server/production/deploy/openwis-dataservice-jndi-service.xml"
sudo -iu openwis cp /vagrant/artefacts/dataservices/conf/openwis-dataservice-jndi-service.xml "$jndiServiceXml"

owConf "$jndiServiceXml" "dataService.baseLocation" "$DS_DIR"
owConf "$jndiServiceXml" "harnessDisseminationPublicServer" ""
owConf "$jndiServiceXml" "stagingPost.url" "http://localhost:8180/todo"
owConf "$jndiServiceXml" "dataService.mail.from" "root"
owConf "$jndiServiceXml" "dataService.mail.smtp.host" "localhost"
owConf "$jndiServiceXml" "managementServiceServer" "localhost:8180"

openwisDsXml="$jbossHome/server/production/deploy/openwis-ds.xml"
sudo -iu openwis cp /vagrant/artefacts/dataservices/conf/openwis-ds.xml "$openwisDsXml"

owConf "$openwisDsXml" "database.url" "jdbc:postgresql://${OPENWIS_DB_HOST}:5432/$OPENWIS_DB_NAME?stringtype=unspecified"
owConf "$openwisDsXml" "database.user" "$OPENWIS_DB_USER"
owConf "$openwisDsXml" "database.password" "$OPENWIS_DB_PASSWD"

destServicesXml="$jbossHome/server/production/deploy/openwis-dataservice-destinations-service.xml"
sudo -iu openwis cp /vagrant/artefacts/dataservices/conf/openwis-dataservice-destinations-service.xml "$destServicesXml"

# Deploy the PostgreSQL JDBC driver
sudo -iu openwis cp /vagrant/dependencies/libs/postgresql-8.4-702.jdbc4.jar "$jbossHome/server/production/lib/postgresql-8.4-702.jdbc4.jar"

# Deploy startup scripts
sudo -iu openwis cp /vagrant/artefacts/dataservices/scripts/run.conf "$jbossHome/bin/."
sudo -iu openwis cp /vagrant/artefacts/dataservices/scripts/start_openwis_jboss.sh "$openwisHome/."
sudo -iu openwis cp /vagrant/artefacts/dataservices/scripts/stop_openwis_jboss.sh "$openwisHome/."
sudo -iu openwis cp /vagrant/artefacts/dataservices/scripts/tail_jboss_log.sh "$openwisHome/."
sudo -iu openwis cp /vagrant/artefacts/dataservices/scripts/deploy-dataservices.sh "$openwisHome/."
sudo -iu openwis chmod a+x "$openwisHome"/*.sh

# Deploy the application
sudo -iu openwis cp /vagrant/artefacts/dataservices/openwis-dataservice.ear "$openwisHome/staging/."
sudo -iu openwis cp /vagrant/artefacts/dataservices/openwis-management-service.ear "$openwisHome/staging/."
sudo -iu openwis "$openwisHome"/deploy-dataservices.sh
