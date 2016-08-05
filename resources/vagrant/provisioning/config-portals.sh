#!/bin/bash
#
#   Reconfigure provisioning routines
#

source /vagrant/resources/vagrant/provisioning/provision-config.sh
source /vagrant/resources/vagrant/provisioning/provision-functions.sh


# --------------------------------------------------------------------------------------
# Config the portals

echo "Configuring the portals"

openwisHome="/home/openwis"
tomcatHome="$openwisHome/`basename "$RESOURCE_TOMCAT" .tar.gz`"

# openwis-metadataportal.properties
userProps="$tomcatHome/webapps/openwis-user-portal/WEB-INF/classes/openwis-metadataportal.properties"
adminProps="$tomcatHome/webapps/openwis-admin-portal/WEB-INF/classes/openwis-metadataportal.properties"

for propName in "$userProps" "$adminProps" ; do
    owConf "$propName" "dataServiceServer" "owdev-data:8180"
    owConf "$propName" "managementServiceServer" "owdev-data:8180"
    owConf "$propName" "subselectionparameters.wsdl" ""
    owConf "$propName" "mssfss.wsdl" ""
    owConf "$propName" "mssfss.support" "false"
    owConf "$propName" "stagingPost.url" "http://localhost:8061/stagingPost/"   # Host forwarded port
    owConf "$propName" "cache.enable" "true"
    owConf "$propName" "solrServer" "owdev-db:8080"
    owConf "$propName" "deploymentName" "OWDEV"
    owConf "$propName" "openam.url" ""
    owConf "$propName" "userportalServer" "owdev-portals:8080"
    owConf "$propName" "reports_dir" "/data/openwis/reports/"

    setProp "$propName" "openwis.metadataportal.securityservice.usermanagement.wsdl" "http://127.0.0.1:8814/UserManagementServiceImpl?wsdl"
    setProp "$propName" "openwis.metadataportal.securityservice.groupmanagement.wsdl" "http://127.0.0.1:8812/GroupManagementServiceImpl?wsdl"
    setProp "$propName" "openwis.metadataportal.securityservice.dissemparammanagement.wsdl" "http://127.0.0.1:8811/DisseminationParametersService?wsdl"
    setProp "$propName" "openwis.metadataportal.securityservice.monitoringservice.wsdl" "http://127.0.0.1:8813/MonitoringServiceImpl?wsdl"

    # TODO: Security
done

# openwis-deployment.properties
userDeps="$tomcatHome/webapps/openwis-user-portal/WEB-INF/classes/openwis-deployments.properties"
adminDeps="$tomcatHome/webapps/openwis-admin-portal/WEB-INF/classes/openwis-deployments.properties"

for depsName in "$userDeps" "$adminDeps" ; do
    owConf "$depsName" "deploymentName" "OWDEV"
    owConf "$depsName" "userportalServer" "localhost:8080"
    owConf "$depsName" "deployment.adminMail" "root@localhost"
done

# config.xml
userConfXml="$tomcatHome/webapps/openwis-user-portal/WEB-INF/config.xml"
adminConfXml="$tomcatHome/webapps/openwis-admin-portal/WEB-INF/config.xml"

for confName in "$userConfXml" "$adminConfXml" ; do
    owConf "$confName" "database.user" "$OPENWIS_DB_USER"
    owConf "$confName" "database.password" "$OPENWIS_DB_PASSWD"
    owConf "$confName" "database.url" "jdbc:postgresql://${OPENWIS_DB_HOST}:5432/$OPENWIS_DB_NAME"
done

# enable admin login
userPortalProfiles="$tomcatHome/webapps/openwis-user-portal/WEB-INF/userPortal-profiles.xml"
adminPortalProfiles="$tomcatHome/webapps/openwis-admin-portal/WEB-INF/adminPortal-profiles.xml"

sudoCmd="sudo -u openwis -i"
if [ `whoami` = "openwis" ]; then
    sudoCmd=""
fi

$sudoCmd sed -i -e '178 d' -e '181 d' "$userPortalProfiles"
$sudoCmd sed -i -e '342 d' -e '345 d' "$adminPortalProfiles"
