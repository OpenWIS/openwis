#!/bin/bash
#
# Be sure to edit this file before launching:
# openwis: The OpenWIS User
# ********: The OpenWIS User Password
# 192.168.91.43: IP address of the installation target host.
# 192.168.91.52: IP address of primary database host.
# openwis: The OpenWIS Database Name
# openwis: The OpenWIS Database User
# $$$$$$$$: Database User Password

# Install iNotify Tools
#echo "Installing EPEL Repository"
#yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-6.noarch.rpm


# Configures an XML file by replacing all '@<name>@' placeholders with a value.


#echo "Installing iNotify Tools For OpenWIS Harness"
yum -y install inotify-tools.x86_64



export JBOSS_HOME=/opt/wildfly
export DS_DIR=/home/openwis
export confDir=/home/openwis/conf


#echo "Installing OpenWIS Dispatch Scripts"
#cp *_openwis_dispatch_openwis_dispatch.sh /home/openwis
#chmod a+x /home/openwis/*_openwis_dispatch.sh
#chown openwis:openwis /home/openwis/*_openwis_dispatch.sh
#cp openwis-dispatch /etc/init.d/
#/sbin/chkconfig --add openwis-dispatch
#/sbin/chkconfig openwis-dispatch on

echo "Cron Based Purge"
sudo -u openwis crontab -e <<CRONTABEOF

A

# Remove all cache files older than 1 day
*/15 * * * * /usr/bin/find /var/opt/openwis/cache -mmin +1440 -exec /bin/rm -rf {} \;

# Remove empty cache folders
*/20 * * * * /usr/bin/find /var/opt/openwis/cache -mindepth 1 -depth -type d -empty -exec rmdir {} \;

## Purge Data Service logs: remove all log files from logs folder older than 7 days; executed every day at 23:15
15 23 * * * /usr/bin/find /home/openwis/logs/*.log* -mtime +6 -exec rm {} \;

:wq
CRONTABEOF

echo "Creating filesystem directories for OpenWIS use"
mkdir /var/opt/openwis
mkdir /var/opt/openwis/harness
mkdir /var/opt/openwis/harness/incoming
mkdir /var/opt/openwis/harness/ingesting
mkdir /var/opt/openwis/harness/ingesting/fromReplication
mkdir /var/opt/openwis/harness/outgoing
mkdir /var/opt/openwis/harness/working
mkdir /var/opt/openwis/harness/working/fromReplication
mkdir /var/opt/openwis/cache
mkdir /var/opt/openwis/stagingPost
mkdir /var/opt/openwis/temp
mkdir /var/opt/openwis/replication
mkdir /var/opt/openwis/replication/sending
mkdir /var/opt/openwis/replication/sending/local
mkdir /var/opt/openwis/replication/sending/destinations
mkdir /var/opt/openwis/replication/receiving
mkdir /var/opt/openwis/reports
mkdir /var/opt/openwis/solr
mkdir /var/opt/openwis/status

chown -R openwis:openwis /var/opt/openwis
chmod -R 775 /var/opt/openwis


#mkdir /home/openwis/openwis-dataservice-config
echo "Setting Conf directory"
mkdir /home/openwis/conf/
cp /vagrant/openwis-management/openwis-management-service/openwis-management-service-common/target/classes/conf/openwis-dataservice.properties  /home/openwis/conf
chown -R openwis:openwis /home/openwis/conf

mkdir /home/openwis/status
chown -R openwis:openwis /home/openwis/status

sed -i -e 's/${sys:openwis.dataService.baseLocation}/\/home\/openwis/'  /$confDir/openwis-dataservice.properties
sed -i -e 's/localhost/owdev-data/'  /$confDir/openwis-dataservice.properties


# owConf "$confDir/openwis-dataservice.properties" "dataService.baseLocation" "$DS_DIR"
# owConf "$confDir/openwis-dataservice.properties" "harnessDisseminationPublicServer" "http://owdev-data:8180/openwis-management-service-ejb/IngestedDataStatistics/IngestedDataStatistics?wsdl" #"http://localhost:8180/client-openwis-ejbs/DisseminationImplService/DisseminationServiceImpl?wsdl"
# owConf "$confDir/openwis-dataservice.properties" "stagingPost.url" "http://localhost:8180/todo"
# owConf "$confDir/openwis-dataservice.properties" "dataService.mail.from" "root"
#"localhost"
# owConf "$confDir/openwis-dataservice.properties" "managementServiceServer" "owdev-data:8180"
 #"localhost:8180"

# function owConf()
# {
#     local file="$1"
#     local name="$2"
#     local value="$3"

#     local sudoCmd="sudo -u openwis -i"
#     if [ `whoami` = "openwis" ]; then
#         sudoCmd=""
#     fi

#     $sudoCmd sed -i -r -e 's|@'"$name"'@|'"$value"'|g' "$file"
# }

# sed -i -e 's/<inet-address value="${jboss.bind.address:127.0.0.1}"/<inet-address value="${jboss.bind.address:192.168.91.43}"/'  "$jbossHome"/bin/standalone.conf

# owConf "$confDir/openwis-dataservice.properties" "dataService.mail.smtp.host" "owdev-data"

# mail.smtp.host
# sed -i -r -e 's/@'"$dataService.mail.smtp.host"'@/'"owdev-data"'/g' /home/openwis/conf/openwis-dataservice.properties

#cp localdatasourceservice.properties /home/openwis/openwis-dataservice-config
#cp /home/openwis/openwis/openwis-management/openwis-management-service/openwis-management-service-common/target/classes/conf/localdatasourceservice.properties  /home/openwis/openwis-dataservice-config
#cp openwis-dataservice.properties /home/openwis/openwis-dataservice-config
#cp /home/openwis/openwis/openwis-management/openwis-management-service/openwis-management-service-common/target/classes/conf/openwis-dataservice.properties  /home/openwis/conf

#chown -R openwis:openwis /home/openwis/openwis-dataservice-config
#unzip -d $JBOSS_HOME/modules 	openwis-dataservice-config-module.zip # zip is missing.. 
#unzip -d $JBOSS_HOME/modules 	openwis-dataservice-config-module.zip
chown -R openwis:openwis $JBOSS_HOME/modules


echo "WildFly status:"
service wildfly status

sleep 5


echo "Deploy JDBC JARs and EARs via CLI Tools"

echo "Copying pre-set JBoss logging configuration"
sudo -u openwis cp jboss-log4j.xml  $JBOSS_HOME/standalone/configuration

echo "Set the HTTP port to 8180"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="/socket-binding-group="standard-sockets"/socket-binding="http":write-attribute(name="port",value=8180)"
sleep 5

# Enable access from any server
#echo "Enable access from any server .."


echo "Set the Management Inet Listen Address"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
        --command="/interface=management:write-attribute(name=inet-address,value=owdev-data)"

echo "Set the Public Inet Listen Address"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
        --command="/interface=public:write-attribute(name=inet-address,value=owdev-data)"

echo "Set the WSDL Inet Listen Address"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="/subsystem=webservices:write-attribute(name="wsdl-host",value=owdev-data)"

echo "Set the WSDL port to 8180"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="/subsystem=webservices:write-attribute(name="wsdl-port",value=8180)"
sleep 5

echo "Configure Deployment Scanner"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="/subsystem=deployment-scanner/scanner=default:write-attribute(name=scan-interval,value=500)"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
	--command="/subsystem=deployment-scanner/scanner=default:write-attribute(name=auto-deploy-exploded,value="true")"


$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 --command=":reload"
echo "Configure Networking DONE!"
sleep 5



echo "Setup logging"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  \
--command='/subsystem=logging/periodic-rotating-file-handler="CollectionHandler":add(level=INFO,  file={"relative-to"=>"jboss.server.log.dir", "path"=>"collection.log"},  append=true, autoflush=true, suffix="yyyy-MM-dd")'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  \
	--command='/subsystem=logging/periodic-rotating-file-handler="RequestHandler":add(level=INFO, file={"relative-to"=>"jboss.server.log.dir", "path"=>"request.log"}, append=true, autoflush=true, suffix="yyyy-MM-dd")'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command='/subsystem=logging/periodic-rotating-file-handler="AlertsHandler":add(level=INFO, file={"relative-to"=>"jboss.server.log.dir", "path"=>"alerts.log"}, append=true, autoflush=true, suffix="yyyy-MM-dd")'

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command='/subsystem=logging/logger=org.openwis.dataservice.util.WMOFTP:add(use-parent-handlers=true,handlers=["CollectionHandler"])'
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command='/subsystem=logging/logger=org.openwis.dataservice.gts.collection:add(use-parent-handlers=true,handlers=["CollectionHandler"])'
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command='/subsystem=logging/logger=org.openwis.dataservice.dissemination:add(use-parent-handlers=true,handlers=["RequestHandler"])'
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command='/subsystem=logging/logger=org.openwis.datasource:add(use-parent-handlers=true,handlers=["RequestHandler"])'
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command='/subsystem=logging/logger=org.openwis.management.service:add(use-parent-handlers=true,handlers=["AlertsHandler"])'
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command=':reload'
sleep 5


echo "Deploy the postgres drivers"

#export RESOURCE_POSTGRESQL_JAR="https://repository-openwis-association.forge.cloudbees.com/private/binaries/postgresql-9.2-1004.jdbc41.jar"
wget https://repository-openwis-association.forge.cloudbees.com/private/binaries/postgresql-9.2-1004.jdbc41.jar

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command='deploy postgresql-9.2-1004.jdbc41.jar'


export OPENWIS_DB_HOST=owdev-db
									
echo "Setup the OpenDS data source"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command=' data-source add --name=OpenwisDS --jndi-name="java:/OpenwisDS" --connection-url="jdbc:postgresql://${OPENWIS_DB_HOST}:5432/OpenWIS?stringtype=unspecified" \
	--user-name="openwis" --password="openwis" --driver-name="postgresql-9.2-1004.jdbc41.jar" --driver-class="org.postgresql.Driver" \
	--min-pool-size=10 --max-pool-size=40 --idle-timeout-minutes=15 --blocking-timeout-wait-millis=15000 --background-validation-millis=50000'

#echo "Enable the OpenDS data source" 
#$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  "data-source enable --name=OpenwisDS"
sleep 5


echo "Setup the JMS queues"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=CollectionQueue --entries=[java:/jms/queue/CollectionQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=IncomingDataQueue --entries=[java:/jms/queue/IncomingDataQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=RequestQueue --entries=[java:/jms/queue/RequestQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=DisseminationQueue --entries=[java:/jms/queue/DisseminationQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=PackedFeedingQueue --entries=[java:/jms/queue/PackedFeedingQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=UnpackedFeedingQueue --entries=[java:/jms/queue/UnpackedFeedingQueue]"
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990   \
	--command="jms-queue add --queue-address=StatisticsQueue --entries=[java:/jms/queue/StatisticsQueue]"
sleep 10



$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command=":reload"
sleep 10


echo "Restarting WildFly...." 
sudo service wildfly stop
sleep 10

sudo service wildfly start
sleep 10

echo "Deploying ear files.."
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command="deploy /vagrant/openwis-management/openwis-management-service/openwis-management-service-ear/target/openwis-management-service.ear"
sleep 20

$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command="deploy /vagrant/openwis-dataservice/openwis-dataservice-server/openwis-dataservice-server-ear/target/openwis-dataservice.ear"
sleep 10

echo "Deploy Hack for StagingPost"



#sudo -iu openwis mkdir  $DS_DIR/stagingPost
#sudo -iu openwis cp /vagrant/openwis-stagingpost/target/stagingPost.war $DS_DIR/stagingPost
#su -c "(cd $DS_DIR/stagingPost/; jar -xvf stagingPost.war)" openwis
#sudo -iu openwis ln -s "$DS_DIR/stagingPost" /opt/wildfly/standalone/deployments/stagingPost.war
# sudo -iu openwis touch /opt/wildfly/standalone/deployments/stagingPost.war.dodeploy


# su -c "cp  /var/opt/openwis/stagingPost" openwis
# su -c "cp /home/openwis/openwis/openwis-stagingpost/target/stagingPost.war /var/opt/openwis/stagingPost" openwis

#$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command="deploy /vagrant/openwis-stagingpost/target/stagingPost.war "

#su -c "(cd /var/opt/openwis/stagingPost/; jar -xvf stagingPost.war)" openwis
#su -c "ln -s /var/opt/openwis/stagingPost $JBOSS_HOME/standalone/deployments/stagingPost.war" openwis
#su -c "touch $JBOSS_HOME/standalone/deployments/stagingPost.war.dodeploy" openwis 
sleep 5
echo "Deploy SolR"
#su -c "cp openwis-portal-solr.war $JBOSS_HOME/standalone/deployments/" openwis
#su -c "cp /home/openwis/openwis/openwis-metadataportal/openwis-portal-solr/target/openwis-portal-solr.war $JBOSS_HOME/standalone/deployments/" openwis
$JBOSS_HOME/bin/jboss-cli.sh --connect --controller=owdev-data:9990  --command="deploy /vagrant/openwis-metadataportal/openwis-portal-solr/target/openwis-portal-solr.war"

echo "Installation complete"