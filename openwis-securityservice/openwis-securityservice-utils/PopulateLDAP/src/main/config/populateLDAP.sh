RACINE=.
LIB=$RACINE/lib

for jarFile in $LIB/*.jar; do
	APP_LIB="$APP_LIB:$jarFile"
done

CLASSPATH="$APP_LIB:openwis-securityservice-utils-populate-ldap-1.0-SNAPSHOT.jar"

userManagementServiceWsdl=http://localhost:8180/openwis-securityservice-openwis-securityservice-usermanagement-server-ejb-1.0-SNAPSHOT/UserManagementService?wsdl
groupManagementServiceWsdl=http://localhost:8180/openwis-securityservice-openwis-securityservice-usermanagement-server-ejb-1.0-SNAPSHOT/GroupManagementService?wsdl

java -classpath $CLASSPATH -DuserManagementServiceWsdl=$userManagementServiceWsdl -DgroupManagementServiceWsdl=$groupManagementServiceWsdl org.openwis.usermanagement.PopulateUser 