RACINE=.
LIB=$RACINE/lib

for jarFile in $LIB/*.jar; do
	APP_LIB="$APP_LIB:$jarFile"
done

# CLASSPATH may change it needs:
# 1) openwis-securityservice-utils-populate-ldap-@VERSION@.jar
# 2)  location of @....@openwis-portal-client/target/classes 
CLASSPATH=/home/openwis/openwis/openwis-securityservice/openwis-securityservice-utils/PopulateLDAP/target/openwis-securityservice-utils-populate-ldap-3.14.5.jar:/home/openwis/openwis/openwis-portal-client/target/classes

userManagementServiceWsdl=http://@OPENWIS_SECURITY_HOST@:@PORT@/openwis-securityservice/services/UserManagementService?wsdl
groupManagementServiceWsdl=http://@OPENWIS_SECURITY_HOST@:@PORT@/openwis-securityservice/services/GroupManagementService?wsdl


java -classpath $CLASSPATH -DuserManagementServiceWsdl=$userManagementServiceWsdl -DgroupManagementServiceWsdl=$groupManagementServiceWsdl org.openwis.usermanagement.PopulateUser 
