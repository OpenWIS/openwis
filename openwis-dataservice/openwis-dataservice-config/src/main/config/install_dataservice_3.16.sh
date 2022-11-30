#!/bin/bash
#

# The PostgreSQL OpenWIS DB should be available

# wildfly must be started

# JBOSS_HOME must be configured
export DS_DIR=/home/openwis
export MS_EAR_DIR=/home/openwis
export DS_EAR_DIR=/home/openwis

#DB connection parameters
username=xxxxx
password=xxxxx
hostname=xxxxxx

#SSolr install option
SOLR_DEPLOY=FALSE

echo ""
echo "---Deploy Openwis Data and Management Services via CLI Tools---"
echo ""

echo "-- JBOSS_HOME setting check";
if [ "x$JBOSS_HOME" = "x" ]; then
echo "JBOSS_HOME undefined -> exit !";
exit;
else
echo "JBOSS_HOME ok : $JBOSS_HOME";
fi

echo ""
echo "--Set the HTTP port to 8180"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/socket-binding-group="standard-sockets"/socket-binding="http":write-attribute(name="port",value=8180)"
sleep 5

# Ports settings
echo "--Set the Management Inet Listen Address"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/interface=management:write-attribute(name=inet-address,value=127.0.0.1)"

# Enable access from any server
echo "--Set the Public Inet Listen Address"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/interface=public:write-attribute(name=inet-address,value=0.0.0.0)"

echo "--Set the WSDL Inet Listen Address"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/subsystem=webservices:write-attribute(name="wsdl-host",value=127.0.0.1)"

echo "--Set the WSDL port to 8180"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/subsystem=webservices:write-attribute(name="wsdl-port",value=8180)"
sleep 5

echo "--Configure Deployment Scanner scan-interval"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/subsystem=deployment-scanner/scanner=default:write-attribute(name=scan-interval,value=500)"
echo "--Configure Deployment Scanner auto-deploy true"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command="/subsystem=deployment-scanner/scanner=default:write-attribute(name=auto-deploy-exploded,value="true")"
echo ""
echo "**Configure Networking DONE!"

echo ""
echo "--Reload WildFly..."
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command=":reload"

sleep 5

echo ""
echo "--Setup logging"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/periodic-rotating-file-handler="CollectionHandler":add(level=INFO,  file={"relative-to"=>"jboss.server.log.dir", "path"=>"collection.log"}, formatter="%d %-5p [%c] %m%n", append=true, autoflush=true, suffix="yyyy-MM-dd")'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/periodic-rotating-file-handler="RequestHandler":add(level=INFO, file={"relative-to"=>"jboss.server.log.dir", "path"=>"request.log"}, formatter="%d %-5p [%c] %m%n", append=true, autoflush=true, suffix="yyyy-MM-dd")'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/periodic-rotating-file-handler="AlertsHandler":add(level=INFO, file={"relative-to"=>"jboss.server.log.dir", "path"=>"alerts.log"}, formatter="%d %-5p [%c] %m%n", append=true, autoflush=true, suffix="yyyy-MM-dd")'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/logger=org.openwis.dataservice.util.WMOFTP:add(use-parent-handlers=true,handlers=["CollectionHandler"])'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/logger=org.openwis.dataservice.gts.collection:add(use-parent-handlers=true,handlers=["CollectionHandler"])'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/logger=org.openwis.dataservice.dissemination:add(use-parent-handlers=true,handlers=["RequestHandler"])'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/logger=org.openwis.datasource:add(use-parent-handlers=true,handlers=["RequestHandler"])'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/logger=org.openwis.management.service:add(use-parent-handlers=true,handlers=["AlertsHandler"])'
# DEBUG Level setting for server.log
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command='/subsystem=logging/periodic-rotating-file-handler=FILE:write-attribute(name="level",value="INFO")'
echo "**Configure logging DONE!"

sleep 5

echo ""
echo "--Reload WildFly..."
# Reload
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command=':reload'
sleep 5

echo ""
echo "--Deploy the postgres driver"

# Make sure postgres driver is present in /data/openwis/
# wget https://jdbc.postgresql.org/download/postgresql-42.2.6.jar

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990  --command='deploy postgresql-42.2.20.jar'
echo "** Postgres drivers deploy DONE!"

echo ""
export OPENWIS_DB_HOST=localhost
echo "--Setup the OpenDS data source"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command=' data-source add --name=OpenwisDS --jndi-name="java:/OpenwisDS" --connection-url="jdbc:postgresql://$hostname:5432/OpenWIS?stringtype=unspecified" \
	--user-name="$username" --password="$password" --driver-name="postgresql-42.2.20.jar" --driver-class="org.postgresql.Driver" \
	--min-pool-size=10 --max-pool-size=40 --idle-timeout-minutes=15 --blocking-timeout-wait-millis=15000 --background-validation-millis=50000'

#echo "Enable the OpenDS data source" <- Activation done by previous cli command
#$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990  "data-source enable --name=OpenwisDS"
echo "**Configure OpenDS data source DONE!"
sleep 10

echo ""
echo "--Setup the JMS queues"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=CollectionQueue --entries=[java:/jms/queue/CollectionQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=IncomingDataQueue --entries=[java:/jms/queue/IncomingDataQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=RequestQueue --entries=[java:/jms/queue/RequestQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=DisseminationQueue --entries=[java:/jms/queue/DisseminationQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=PackedFeedingQueue --entries=[java:/jms/queue/PackedFeedingQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=UnpackedFeedingQueue --entries=[java:/jms/queue/UnpackedFeedingQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990   \
	--command="jms-queue add --queue-address=StatisticsQueue --entries=[java:/jms/queue/StatisticsQueue]"
echo "**Configure jms-queue DONE!"

sleep 10
echo ""
# Reload
echo "--Reload WildFly..."
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990  --command=":reload"
sleep 10

echo ""
echo "--Deploying Management Service ear file"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="deploy /home/openwis/openwis-management-service.ear"
sleep 10

echo "--Deploying Data Service ear file"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="deploy /home/openwis/openwis-dataservice.ear"
echo ""
echo "**Configure ear files DONE!"
sleep 10

echo ""
if [ "$SOLR_DEPLOY" = "TRUE" ]; then
echo "--Deploying SolR war";
#$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
#	--command="deploy /home/openwis/projects/openwis/openwis-metadataportal/openwis-portal-solr/target/openwis-portal-solr.war"
#echo "**Configure solr war DONE!"
#sleep 10
else
echo "Solr is not deployed in Wildfly";
fi

echo ""
echo "--checking deployments"
$JBOSS_HOME/bin/jboss-cli.sh --controller="localhost:9990" -c  --commands=ls\ deployment
sleep 5

echo ""
echo "*** Installation COMPLETE ***"
echo ""


