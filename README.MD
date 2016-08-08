#OpenWis

From [about OpenWis](http://wis.bom.gov.au/openwis-user-portal/srv/en/about.home). 

OpenWIS is an implementation of WMO Information System (WIS) and aims to perform the three functions required by the WIS, i.e. GISC, DCPC and NC. It is originally developed by AKKA Information System (France) on behalf of OpenWIS Association (with UKMO, MF, BoM, KMA, and MFI as the founding members)

##1. Requirements

* Oracle JDK 1.7
* Maven 3.1.1

###Environment variables:

The JAVA_HOME  should be set with the valid JDK path.

The MAVEN_OPTS should be set with "-Xms256m -Xmx512m -XX:MaxPermSize=256m" to avoid OutOfMemory issue during the build.

The "mvn" command should be available in the terminal, maybe you should add the <maven home>/bin directory to the PATH

##2. Sources Contents

**Repository Contents :**

      .
      +-- pom.xml
      |+--openwis-dataservice
      |+--openwis-dataservice
          |+-- openwis-dataservice-cache-core
          |+-- openwis-dataservice-cache-ejb
          |+-- openwis-dataservice-cache-ftpreplication (not used)
          |+-- openwis-dataservice-cache-test (not used)
          |+-- openwis-dataservice-cache-webapp
          |+-- openwis-dataservice-common-domain
          |+-- openwis-dataservice-common-timer
          |+-- openwis-dataservice-common-utils
          |+-- openwis-dataservice-server-ear
          |+-- openwis-dataservice-server-ejb
          |+-- openwis-dataservice-server-test (not used)
          |+-- openwis-dataservice-server-webapp
      |+--openwis-dependencies
      |+--openwis-factorytests
      |+--openwis-harness 
          |+--openwis-harness-client
          |+--openwis-harness-dissemination
          |+--openwis-harness-localdatasource
          |+--openwis-harness-mssfss
          |+--openwis-harness-subselectionparameters
      |+--openwis-management
          |+--openwis-management-client
          |+--openwis-management-service-common
          |+--openwis-management-service-ear
          |+--openwis-management-service-ejb		  
      |+--openwis-metadataportal
          |+--cachingxslt
          |+--jeeves
          |+--oaipmh
          |+--openwis-portal  
          |+--openwis-portal-solr
          |+--schematrons (not used)
          |+--sde
	  |+--openwis-portal-client
      |+--openwis-securityservice
          |+--openwis-securityservice-war
          |+--openwis-securityservice-utils
			|+--GenerateSPConfFiles
			|+--PopulateLDAP
      |+--openwis-stagingpost
      |+--openwis-tools
      |+--resources

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

	[INFO] ------------------------------------------------------------------------
	[INFO] Building OpenWIS
	[INFO]    task-segment: [install:install-file] (aggregator-style)
	[INFO] ------------------------------------------------------------------------
	[INFO] [install:install-file {execution: default-cli}]
	[INFO] Installing C:\openwis\resources\javaee-5.0.jar to c:\Repository\com\sun\javaee\javaee\5.0\javaee-5.0.jar
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESSFUL
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 28 seconds
	[INFO] Finished at: Mon Aug 08 10:06:46 BST 2016
	[INFO] Final Memory: 8M/247M
	[INFO] ------------------------------------------------------------------------
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 47.077s
	[INFO] Finished at: Mon Aug 08 10:06:47 BST 2016
	[INFO] Final Memory: 12M/247M
	[INFO] ------------------------------------------------------------------------

**#2.Launch the Maven build**

 In a terminal, go into the root directory and launch the command: `mvn clean install -P openwis -DskipTests -Dfile.encoding=UTF-8`

After few minutes the build should finished successfully like this:

	[INFO] OpenWIS ........................................... SUCCESS [0.312s]
	[INFO] openwis-harness ................................... SUCCESS [0.015s]
	[INFO] openwis-harness-subselectionparameters ............ SUCCESS [6.020s]
	[INFO] openwis-harness-localdatasource ................... SUCCESS [1.302s]
	[INFO] openwis-harness-client ............................ SUCCESS [0.722s]
	[INFO] openwis-harness-mssfss ............................ SUCCESS [2.292s]
	[INFO] openwis-harness-dissemination ..................... SUCCESS [1.576s]
	[INFO] openwis-management ................................ SUCCESS [0.016s]
	[INFO] openwis-management-client ......................... SUCCESS [8.083s]
	[INFO] openwis-management-service-common ................. SUCCESS [1.774s]
	[INFO] openwis-management-service-ejb .................... SUCCESS [5.070s]
	[INFO] openwis-management-service-ear .................... SUCCESS [1.179s]
	[INFO] openwis-dataservice ............................... SUCCESS [0.012s]
	[INFO] openwis-dataservice-common-utils .................. SUCCESS [1.344s]
	[INFO] openwis-dataservice-common-domain ................. SUCCESS [6.183s]
	[INFO] openwis-dataservice-common-timer .................. SUCCESS [1.762s]
	[INFO] openwis-dataservice-cache-core .................... SUCCESS [1.514s]
	[INFO] openwis-dataservice-cache-ejb ..................... SUCCESS [5.995s]
	[INFO] openwis-dataservice-cache-webapp .................. SUCCESS [0.827s]
	[INFO] openwis-dataservice-server-ejb .................... SUCCESS [5.413s]
	[INFO] openwis-dataservice-server-webapp ................. SUCCESS [0.452s]
	[INFO] openwis-dataservice-server-ear .................... SUCCESS [0.734s]
	[INFO] openwis-dataservice-config ........................ SUCCESS [1.201s]
	[INFO] openwis-portal-client ............................. SUCCESS [26.001s]
	[INFO] GeoNetwork opensource ............................. SUCCESS [0.000s]
	[INFO] Caching xslt module ............................... SUCCESS [0.421s]
	[INFO] Jeeves modules .................................... SUCCESS [6.735s]
	[INFO] Oaipmh modules .................................... SUCCESS [7.238s]
	[INFO] ArcSDE module (dummy-api) ......................... SUCCESS [3.646s]
	[INFO] openwis-portal-solr ............................... SUCCESS [10.071s]
	[INFO] openwis-securityservice ........................... SUCCESS [0.013s]
	[INFO] openwis-securityservice-war ....................... SUCCESS [4.812s]
	[INFO] openwis-securityservice-utils ..................... SUCCESS [0.009s]
	[INFO] openwis-securityservice-utils-populate-ldap ....... SUCCESS [15.238s]
	[INFO] openwis-stagingpost ............................... SUCCESS [1.654s]
	[INFO] OpenWIS module .................................... SUCCESS [5:25.570s]
	[INFO] openwis-dependencies .............................. SUCCESS [3.557s]
	[INFO] openwis-tools ..................................... SUCCESS [0.265s]
	[INFO] load-test-data .................................... SUCCESS [14.825s]
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 7:54.691s
	[INFO] Finished at: Mon Aug 08 10:52:18 BST 2016
	[INFO] Final Memory: 94M/268M
	[INFO] ------------------------------------------------------------------------
	
**#3. Generate the admin portal*	
	
In a terminal, go to the root directory and launch the command: `mvn clean install -P admin -DskipTests -Dfile.encoding=UTF-8`

	[INFO] ------------------------------------------------------------------------
	[INFO] Reactor Summary:
	[INFO]
	[INFO] OpenWIS module .................................... SUCCESS [2:38.097s]
	[INFO] OpenWIS ........................................... SUCCESS [0.011s]
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 2:38.345s
	[INFO] Finished at: Mon Aug 08 10:55:58 BST 2016
	[INFO] Final Memory: 49M/247M
	[INFO] ------------------------------------------------------------------------

**#4  Getting built artifacts**

* **dataservice EAR:** <rootdir>/openwis-dataservice/openwis-dataservice-server-ear/target/openwis-dataservice.ear
* **dataservice configs:** <rootdir>/openwis-dataservice/openwis-dataservice-config/target/openwis-dataservice-config-files.zip
* **dataservice config module:** <rootdir>/openwis-dataservice/openwis-dataservice-config/target/openwis-dataservice-config-module.zip
* **harness client:** <sources>/openwis-harness/openwis-harness-client/target/openwis-harness-client.jar
* **security service:** <rootdir>/openwis-securityservice/openwis-securityservice-war/target/openwis-securityservice.war
* **user portal WAR:** <rootdir>/openwis-metadataportal/openwis-portal/openwis-user-portal/openwis-portal-user.war
* **admin portal WAR:** <rootdir>/openwis-metadataportal/openwis-portal/openwis-admin-portal/openwis-portal-admin.war
* **SolR search WAR:** <rootdir>/openwis-metadataportal/openwis-portal-solr/target/openwis-portal-solr.war
* **staging post WAR:** <rootdir>/openwis-stagingpost/target/stagingPost.war 
* **Management Service:** <rootdir>/openwis-management/openwis-management-service-ear/target/openwis-management-service.ear

**#5 Changing Verison Number**

In a terminal, go into the root directory and launch the command: `mvn versions:set -DnewVersion=0.0.1-SNAPSHOT -P openwis` or whatever version you wish to change the project to.
In some cases, you may find that OpenWIS complains about missing dependencies, should this happen try running 'mvn versions:set -DnewVersion=0.0.1-SNAPSHOT -P openwis' followed by 'mvn versions:set -DnewVersion=0.0.1-SNAPSHOT -P admin'

**#6 Maven Releasing **

Maven releasing for OpenWIS is two step process as the portal requires the build to be run twice (once for admin portal and once for the user portal).

In a terminal, go into the root directory and launch the command: `mvn -Dresume=false release:prepare -Popenwis release:perform -Dgoals="install"' this needs to be followed by a build of the admin portal as desribed in #3

This will not deploy to the CloudBees repo as there are currentl permissions issues

 
**#7 Running SonarQube**
 
In a terminal, go into the root directory and launch the command: `mvn sonar:sonar`. Because there is currently no central SonarQube via CloudBees, you need to have your local SonarQube settings
configured in you ~/.m2/settings.xml. 

This does take a while given the number of modules that need to run.
 