RACINE=.
LIB=$RACINE/lib

for jarFile in $LIB/*.jar; do
	APP_LIB="$APP_LIB:$jarFile"
done

CLASSPATH="$APP_LIB:openwis-securityservice-utils-populate-ldap-develop-SNAPSHOT.jar"

userManagementServiceWsdl=http://localhost:8080/openwis-securityservice/services/UserManagementService?wsdl
groupManagementServiceWsdl=http://localhost:8080/openwis-securityservice/services/GroupManagementService?wsdl

java -classpath $CLASSPATH -DuserManagementServiceWsdl=$userManagementServiceWsdl -DgroupManagementServiceWsdl=$groupManagementServiceWsdl org.openwis.usermanagement.PopulateUser 
