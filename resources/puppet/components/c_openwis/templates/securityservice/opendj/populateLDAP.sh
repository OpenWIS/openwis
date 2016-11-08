#!/bin/bash

RACINE=$(dirname $0)
LIB=$RACINE/lib

for jarFile in $RACINE/*.jar; do
	CLASSPATH="$CLASSPATH:$jarFile"
done
for jarFile in $LIB/*.jar; do
	CLASSPATH="$CLASSPATH:$jarFile"
done

userManagementServiceWsdl=http://<%= @auth_service_host %>:8080/openwis-securityservice/services/UserManagementService?wsdl
groupManagementServiceWsdl=http://<%= @auth_service_host %>:8080/openwis-securityservice/services/GroupManagementService?wsdl

java -classpath $CLASSPATH -DuserManagementServiceWsdl=$userManagementServiceWsdl -DgroupManagementServiceWsdl=$groupManagementServiceWsdl org.openwis.usermanagement.PopulateUser
