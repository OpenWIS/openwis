#!/bin/bash
#
#   Install and configure OpenWIS using JBoss 7 and OpenJDK
#
#   This script is to be executed as "root".
#
#   This script also contains the instructions for upgrading the data services from
#   JBoss 5 to JBoss AS 7.1.  This assumes that an OpenWIS system has been setup but
#   it does not cover the actual migration of the existing OpenWIS system (that will be
#   done in another script).
#

openwisHome="/home/openwis"
jbossHome="$openwisHome"/jboss-as-7.1.1.Final

function jbossCli()
{
    local cmd="$1"

    echo "[jboss-cli] $cmd"
    sudo -iu openwis "$jbossHome"/bin/jboss-cli.sh -c --command="$cmd"
}

# Configures an XML file by replacing all '@<name>@' placeholders with a value.
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

# -------------------------------------------------------------------------------------
# Setup JBoss
#
# This also makes up the installation instructions
#

# 1. As root, install the OpenJDK 1.7 Devel package from the Red Hat Repositories.
#
yum install -y java-1.7.0-openjdk-devel.x86_64

# 2. As openwis, download and install JBoss AS 7.1 community edition from jboss.org.
#
sudo -iu openwis wget -q -O /tmp/jboss-as-7.1.1.Final.tar.gz "$RESOURCE_JBOSS"

# 3. Unpack it in the home directory of openwis.
#
sudo -iu openwis tar -xvzf /tmp/jboss-as-7.1.1.Final.tar.gz

# 4. Add the JBOSS_HOME environment variable to `~/.bashrc`.  If unpacking it in the home directory of
#    openwis, this should be '/home/openwis/jboss-as-7.1.1.Final'.  After modifying `~/.bashrc`, reload
#    it by typing `source ~/.bashrc`.
#
sudo -iu openwis cat >> "$openwisHome"/.bashrc << .
export JBOSS_HOME="$jbossHome"
.

# 5. Install the jndi-properties module.  This module allows for the injection of Property objects into JNDI,
#    which is used to configure the data service.
#
( cd "$jbossHome/modules" ; unzip "$ARTEFACT_DATASERVICE_CONFIG_MODULE" )

# 5. Configure standalone.conf to enable debugging
sudo -iu openwis echo 'JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"' >> "$jbossHome"/bin/standalone.conf

# 5. !!IMPORTANT!! Set the Time Zone of JBoss to UTC
sudo -iu openwis echo 'JAVA_OPTS="$JAVA_OPTS -Duser.timezone=UTC"' >> "$jbossHome"/bin/standalone.conf

# 5. Start JBoss using the `standalone-full.xml` configuration.  The rest of the setup requires JBoss to
#    be running.
#
#    TODO: Deploy the startup and shutdown scripts.
#
sudo -iu openwis nohup "$jbossHome"/bin/standalone.sh -b 0.0.0.0 -c standalone-full.xml > "$openwisHome"/jboss.log 2>&1 &


### Setting Up The Data Source

# 1. Download the PostgreSQL JDBC driver suitable for the database currently being used: either 8.4 or 9.x.  The
#    postgres drivers can be found at <https://jdbc.postgresql.org/download.html> and must be JDBC 4 compatable.
#

# 2. Using the JBoss CLI tool, deploy the PostgreSQL JDBC driver.
#
echo "Wating for JBoss to startup"
sleep 10

# Turn on debugging logs and restart JBoss (I think that's required to get the logs working)
jbossCli "/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=DEBUG)"
jbossCli "/subsystem=logging/root-logger=ROOT:write-attribute(name=level,value=DEBUG)"

# Set the HTTP socket to 8180 -- this requires a reload
jbossCli '/socket-binding-group="standard-sockets"/socket-binding="http":write-attribute(name="port",value=8180)'

# Setup logging

jbossCli "/subsystem=logging/periodic-rotating-file-handler=\"CollectionHandler\":add( \
    level=INFO, \
    file={\"relative-to\"=>\"jboss.server.log.dir\", \"path\"=>\"collection.log\"}, \
    formatter=\"%d %-5p [%c] %m%n\", \
    append=true, \
    autoflush=true, \
    suffix=\"yyyy-MM-dd\" \
)"

jbossCli "/subsystem=logging/periodic-rotating-file-handler=\"RequestHandler\":add( \
    level=INFO, \
    file={\"relative-to\"=>\"jboss.server.log.dir\", \"path\"=>\"request.log\"}, \
    formatter=\"%d %-5p [%c] %m%n\", \
    append=true, \
    autoflush=true, \
    suffix=\"yyyy-MM-dd\" \
)"

jbossCli "/subsystem=logging/periodic-rotating-file-handler=\"AlertsHandler\":add( \
    level=INFO, \
    file={\"relative-to\"=>\"jboss.server.log.dir\", \"path\"=>\"alerts.log\"}, \
    formatter=\"%d %-5p [%c] %m%n\", \
    append=true, \
    autoflush=true, \
    suffix=\"yyyy-MM-dd\" \
)"

jbossCli '/subsystem=logging/logger=org.openwis.dataservice.util.WMOFTP:add(use-parent-handlers=true,handlers=["CollectionHandler"])'
jbossCli '/subsystem=logging/logger=org.openwis.dataservice.gts.collection:add(use-parent-handlers=true,handlers=["CollectionHandler"])'
jbossCli '/subsystem=logging/logger=org.openwis.dataservice.dissemination:add(use-parent-handlers=true,handlers=["RequestHandler"])'
jbossCli '/subsystem=logging/logger=org.openwis.datasource:add(use-parent-handlers=true,handlers=["RequestHandler"])'
jbossCli '/subsystem=logging/logger=org.openwis.management.service:add(use-parent-handlers=true,handlers=["AlertsAppender"])'

jbossCli ':reload'

sleep 5

# Deploy PostgreSQL driver
jdbcDriverJar="/tmp/postgresql.jar"

sudo -iu openwis wget -q -O "$jdbcDriverJar" "$RESOURCE_POSTGRESQL_JAR"

jbossCli "deploy $jdbcDriverJar"

# 3. Setup the OpenWIS data service using the CLI tool
#
#   TODO: It might be better if all these CLI commands were in a script file
#
jbossCli "data-source add --name=OpenwisDS --jndi-name=\"java:/OpenwisDS\" \
    --connection-url=\"jdbc:postgresql://${OPENWIS_DB_HOST}:5432/$OPENWIS_DB_NAME?stringtype=unspecified\" \
    --user-name=\"$OPENWIS_DB_USER\" --password=\"$OPENWIS_DB_PASSWD\" \
    --driver-name=\"`basename $jdbcDriverJar`\" --driver-class=\"org.postgresql.Driver\" \
    --min-pool-size=10 --max-pool-size=40 \
    --idle-timeout-minutes=15 --blocking-timeout-wait-millis=15000 \
    --background-validation-millis=50000 --check-valid-connection-sql=\"select count(*) from openwis_cache_configuration\""
jbossCli "data-source enable --name=OpenwisDS"

# 4. Setup the OpenWIS JMS queues
#
jbossCli "jms-queue add --queue-address=CollectionQueue --entries=[queue/CollectionQueue]"
jbossCli "jms-queue add --queue-address=IncomingDataQueue --entries=[queue/IncomingDataQueue]"
jbossCli "jms-queue add --queue-address=RequestQueue --entries=[queue/RequestQueue]"
jbossCli "jms-queue add --queue-address=DisseminationQueue --entries=[queue/DisseminationQueue]"
jbossCli "jms-queue add --queue-address=PackedFeedingQueue --entries=[queue/PackedFeedingQueue]"
jbossCli "jms-queue add --queue-address=UnpackedFeedingQueue --entries=[queue/UnpackedFeedingQueue]"
jbossCli "jms-queue add --queue-address=StatisticsQueue --entries=[queue/StatisticsQueue]"

# 5. Install the configuration
#
#   TODO: Work out appropriate directories
#
confDir=$openwisHome/conf

sudo -iu openwis mkdir $confDir
sudo -iu openwis unzip -d /tmp/openwis-config-files "$ARTEFACT_DATASERVICE_CONFIG_FILES"
#sudo -iu openwis cp /vagrant/artefacts/dataservices/conf/localdatasourceservice.properties $confDir/.
#sudo -iu openwis cp /vagrant/artefacts/dataservices/conf/openwis-dataservice.properties $confDir/.
sudo -iu openwis cp /tmp/openwis-config-files/openwis-dataservice-config/config/*.properties $confDir/.

owConf "$confDir/openwis-dataservice.properties" "dataService.baseLocation" "$DS_DIR"
owConf "$confDir/openwis-dataservice.properties" "harnessDisseminationPublicServer" "http://localhost:8180/client-openwis-ejbs/DisseminationImplService/DisseminationServiceImpl?wsdl"
owConf "$confDir/openwis-dataservice.properties" "stagingPost.url" "http://localhost:8180/todo"
owConf "$confDir/openwis-dataservice.properties" "dataService.mail.from" "root"
owConf "$confDir/openwis-dataservice.properties" "dataService.mail.smtp.host" "localhost"
owConf "$confDir/openwis-dataservice.properties" "managementServiceServer" "localhost:8180"

# 6. Setup the OpenWIS JNDI configuration values
#
jbossCli "/system-property=conf\/openwis-dataservice:add(value=\"/home/openwis/conf/openwis-dataservice.properties\")"
jbossCli "/system-property=ws\/localdatasourceservice:add(value=\"/home/openwis/conf/localdatasourceservice.properties\")"
jbossCli "/subsystem=naming/binding=conf\/openwis-dataservice:add(binding-type=object-factory,module=\"org.openwis.dataservice.config\",class=\"org.openwis.dataservice.config.PropertiesFactory\")"
jbossCli "/subsystem=naming/binding=ws\/localdatasourceservice:add(binding-type=object-factory,module=\"org.openwis.dataservice.config\",class=\"org.openwis.dataservice.config.PropertiesFactory\")"



# 7. Deploy the application
#
jbossCli "deploy $ARTEFACT_MANAGEMENT_SERVICE_EAR"
jbossCli "deploy $ARTEFACT_DATA_SERVICE_EAR"


# 8. Deploy the staging post as an external WAR file
#
sudo -iu openwis unzip -d "$DS_DIR/stagingPost" "$ARTEFACT_STAGINGPOST_WAR"

sudo -iu openwis ln -s "$DS_DIR/stagingPost" "$jbossHome"/standalone/deployments/stagingPost.war
sudo -iu openwis touch "$jbossHome"/standalone/deployments/stagingPost.war.dodeploy

