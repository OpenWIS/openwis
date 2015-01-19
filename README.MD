#OpenWis

From [about OpenWis](http://wis.bom.gov.au/openwis-user-portal/srv/en/about.home). 

OpenWIS is an implementation of WMO Information System (WIS) and aims to perform the three functions required by the WIS, i.e. GISC, DCPC and NC. It is originally developed by AKKA Information System (France) on behalf of OpenWIS Association (with UKMO, MF, BoM, KMA, and MFI as the founding members)

##1. Requirements

* Java JDK 1.6
* Maven 2.2.1

###Environment variables:

The JAVA_HOME  should be set with the valid JDK path.

The MAVEN_OPTS should be set with "-Xms256m -Xmx512m -XX:MaxPermSize=256m" to avoid OutOfMemory issue during the build.

The "mvn" command should be available in the terminal, maybe you should add the <maven home>/bin directory to the PATH

##2. Sources Contents

**Repository Contents :**

      .
      +-- pom.xml
      |+--openwis-dataservice
          |+--openwis-dataservice-cache
             |+-- openwis-dataservice-cache-core
             |+-- openwis-dataservice-cache-ejb
             |+-- openwis-dataservice-cache-ftpreplication (not used)
             |+-- openwis-dataservice-cache-test (not used)
             |+-- openwis-dataservice-cache-webapp
          |+--openwis-dataservice-common
             |+-- openwis-dataservice-common-domain
             |+-- openwis-dataservice-common-timer
             |+-- openwis-dataservice-common-utils
          |+--openwis-dataservice-server
             |+-- openwis-dataservice-server-ear
             |+-- openwis-dataservice-server-ejb
             |+-- openwis-dataservice-server-test (not used)
             |+-- openwis-dataservice-server-webapp
      |+--openwis-factorytests (not used)
      |+--openwis-harness 
          |+--openwis-harness-client
          |+--openwis-harness-dissemination
          |+--openwis-harness-localdatasource
          |+--openwis-harness-mssfss
          |+--openwis-harness-samples (not used)
          |+--openwis-harness-subselectionparameters
      |+--openwis-management
          |+--openwis-management-client
          |+--openwis-management-server
      |+--openwis-openwis-metadataportal
          |+--cachingxslt
          |+--jeeves
          |+--oaipmh
          |+--openwis-portal
          |+--openwis-portal-client
          |+--openwis-portal-solr (not used)
          |+--schematrons (not used)
          |+--sde
      |+--openwis-securityservice
          |+--openwis-securityservice-usermanagement-common
          |+--openwis-securityservice-usermanagement-server
             |+-- openwis-securityservice-usermanagement-server-ear
             |+-- openwis-securityservice-usermanagement-server-ejb
             |+-- openwis-securityservice-usermanagement-server-webapp
          |+--openwis-securityservice-usermanagement-utils
      |+--openwis-stagingpost (not used)
      |+--openwis-resources

* **openwis-dataservice:** contains the dataservice module (including the cache system)
* **openwis-factorytests:** contains selenium functional tests
* **openwis-harness:** contains harness WSDL (dissemination, localdatasource) and samples harness implementations
* **openwis-management:** contains management module (monitoring and control)
* **openwis-metadataportal:** contains portals module
* **openwis-release:** contains some binary (JBoss, PostgreSQL, ...)
* **openwis-securityservice:** contains the security module
* **openwis-stagingpost:** contains the staging post web application
* **resources:** contains jars required for build but not available on Public Maven repositories

**Harness WSDL**
 
* **The harness dissemination WSDL and WSD:** openwis-harness/openwis-harness-dissemination/src/wsdl/
* **The harness localdatasource WSDL and WSD:** openwis-harness/openwis-harness-localdatasource/src/wsdl/
* **The harness MSSFSS WSDL and WSD:** openwis-harness/openwis-harness-mssfss/src/wsdl/
* **The harness subselectionparameters WSDL and WSD:** openwis-harness/openwis-harness-subselectionparameters/src/wsdl/



##3. Building OpenWIS

**#1. Installing JavaEE**

Javee is a library that is shipped with installations of GlassFish but is not available in any pulic Maven repository. In a terminal launch the command `mvn clean exec:exec`

**#2.Launch the Maven build**

 In a terminal, go into the root directory and launch the command: `mvn clean install -P all -DskipTests -Dfile.encoding=UTF-8`

After few minutes the build should finished successfully like this:

    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] ------------------------------------------------------------------------
    [INFO] OpenWis ............................................... SUCCESS [3.147s]
    [INFO] OpenWIS Harness Parent Project ........................ SUCCESS [0.159s]
    [INFO] Harness for Sub Selection Parameters .................. SUCCESS [8.140s]
    [INFO] openwis-harness-localdatasource ....................... SUCCESS [1.199s]
    [INFO] Openwis Harness client ................................ SUCCESS [0.755s]
    [INFO] openwis-harness-mssfss ................................ SUCCESS [1.456s]
    [INFO] openwis-harness-dissemination ......................... SUCCESS [1.085s]
    [INFO] OpenWIS Monitoring And Control ........................ SUCCESS [0.132s]
    [INFO] OpenWIS Management Client ............................. SUCCESS [2.993s]
    [INFO] OpenWIS Management Service ............................ SUCCESS [0.072s]
    [INFO] openwis-management-service-common ..................... SUCCESS [0.826s]
    [INFO] openwis-management-service-ejb ........................ SUCCESS [7.762s]
    [INFO] openwis-management-service-ear ........................ SUCCESS [1.478s]
    [INFO] OpenWIS DataService project ........................... SUCCESS [0.070s]
    [INFO] openwis-dataservice-common ............................ SUCCESS [0.135s]
    [INFO] openwis-dataservice-common-utils ...................... SUCCESS [0.894s]
    [INFO] openwis-dataservice-common-domain ..................... SUCCESS [6.116s]
    [INFO] openwis-dataservice-common-timer ...................... SUCCESS [0.654s]
    [INFO] openwis-dataservice-cache ............................. SUCCESS [0.055s]
    [INFO] openwis-dataservice-cache-core ........................ SUCCESS [0.787s]
    [INFO] openwis-dataservice-cache-ejb ......................... SUCCESS [2.714s]
    [INFO] openwis-dataservice-cache-webapp ...................... SUCCESS [1.265s]
    [INFO] openwis-dataservice-server ............................ SUCCESS [0.052s]
    [INFO] openwis-dataservice-server-ejb ........................ SUCCESS [6.536s]
    [INFO] openwis-dataservice-server-webapp ..................... SUCCESS [0.546s]
    [INFO] openwis-dataservice-server-ear ........................ SUCCESS [0.868s]
    [INFO] GeoNetwork opensource ................................. SUCCESS [0.055s]
    [INFO] Caching xslt module ................................... SUCCESS [0.554s]
    [INFO] Jeeves modules ........................................ SUCCESS [3.264s]
    [INFO] Oaipmh modules ........................................ SUCCESS [0.947s]
    [INFO] ArcSDE module (dummy-api) ............................. SUCCESS [0.559s]
    [INFO] Openwis Portal client ................................. SUCCESS [10.950s]
    [INFO] Openwis Portal SolR ................................... SUCCESS [4.508s]
    [INFO] OpenWIS Security Service project ...................... SUCCESS [0.049s]
    [INFO] openwis-securityservice-usermanagement-common ......... SUCCESS [0.536s]
    [INFO] openwis-securityservice-usermanagement-server ......... SUCCESS [0.054s]
    [INFO] openwis-securityservice-usermanagement-server-ejb ..... SUCCESS [1.055s]
    [INFO] openwis-securityservice-usermanagement-server-webapp .. SUCCESS [0.381s]
    [INFO] openwis-securityservice-usermanagement-server-ear ..... SUCCESS [0.474s]
    [INFO] openwis-securityservice-utils ......................... SUCCESS [0.053s]
    [INFO] openwis-securityservice-utils-populate-ldap ........... SUCCESS [3.217s]
    [INFO] ------------------------------------------------------------------------
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESSFUL
    [INFO] ------------------------------------------------------------------------
	
**#3. Generate the admin and user portals*

**User** In a terminal, go to the root directory and launch the command: `mvn clean install -P user -DskipTests -Dfile.encoding=UTF-8`
**Admin:** In a terminal, go to the root directory and launch the command: `mvn install -P admin -DskipTests -Dfile.encoding=UTF-8`

**#4  Getting built artifacts**

* **dataservice EAR:** <rootdir>/openwis-dataservice/openwis-dataservice-server/openwis-dataservice-server-ear/target/openwis-dataservice.ear
* **harness client:** <sources>/openwis-harness/openwis-harness-client/target/openwis-harness-client.jar
* **security service EAR:** <rootdir>/openwis-securityservice/openwis-securityservice-usermanagement-server/openwis-securityservice-usermanagement-server-ear/target/openwis-securityservice.ear
* **user portal WAR:** <rootdir>/openwis-metadataportal/openwis-portal/target/openwis-user-portal.war
* **admin portal WAR:** <rootdir>/openwis-metadataportal/openwis-portal/target/openwis-admin-portal.war
* **SolR search WAR:** <rootdir>/openwis-metadataportal/openwis-portal-solr/target/openwis-portal-solr.war
* **staging post WAR:** <rootdir>/openwis-stagingpost/target/stagingPost.war 

**#5 Changing Verison Number**

In a terminal, go into the root directory and launch the command: `mvn versions:set -DnewVersion=0.0.1-SNAPSHOT -P all` or whatever version you wish to change the project to
 
 **#6 Running SonarQube**
 
In a terminal, go into the root directory and launch the command: `mvn -P all sonar:sonar`. Because there is currently no central SonarQube via CloudBees, you need to have your local SonarQube settings
configured in you ~/.m2/settings.xml. 

This does take a while given the number of modules that need to run.
 