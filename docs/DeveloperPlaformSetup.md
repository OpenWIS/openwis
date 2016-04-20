# Developer Platform Setup

This document will describe the process for setting up a development environment
in Windows and Linux.

## Windows

You will need the following:

- [Java SE JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html): 
    The latest of Oracle JDK is recommended due to the lack of an official Windows release of OpenJDK 1.7
    or publicly available releases of Oracle JDK prior to the current release.
- [Eclipse - Latest version for Java EE Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars2)
- [Maven 2.2.1](https://archive.apache.org/dist/maven/maven-2/2.2.1/binaries/apache-maven-2.2.1-bin.zip)
- [GitHub Desktop](https://desktop.github.com/)
- [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
- [Vagrant](https://www.vagrantup.com/downloads.html)

### Installing GitHub Desktop

1. Install GitHub Desktop

2. Login using your GitHub user account.

3. ... config

### Cloning The Repository

1. Get an account for GitHub...

2. Create a fork from OpenWIS...

3. Open a command line and create a directory to contain the OpenWIS project code:

    C:\> cd c:\
    C:\> mkdir Projects

4. Open GitHub Desktop and select "Create, clone or add a local repository"

5. Search for your local fork of "OpenWIS".

6. Locate the project directory to clone the repository into (e.g. "C:\Projects")
    The repository will be cloned into the chosen directory.

7. Switch the branch to "develop".

### Initial Build

1. Perform an initial build of OpenWIS to confirm that the project was cloned correctly:

    C:\Projects\openwis> mvn clean exec:exec
    C:\Projects\openwis> mvn clean install -P openwis -Dfile.encoding=UTF-8
    C:\Projects\openwis> mvn clean install -P user -Dfile.encoding=UTF-8
    C:\Projects\openwis> mvn clean install -P admin -Dfile.encoding=UTF-8

  Each of the maven projects should complete without error.

2. Open up Eclipse

3. Open up the Eclipse preferences and make the following changes:

    1. Set the JDK Compliance level to `1.7`.

    2. Add the internal maven...

    3. Set the User Setting...

4. Turn off "Build Automatically"...

4. Some additional plugins will need to be installed to cover Maven lifecycle goals.  Without then, the Eclipse project
   will have a number of build errors:

    - https://github.com/ryansmith4/m2e-cxf-codegen-connector: cxf-codegen-plugin
        - https://github.com/ryansmith4/m2e-cxf-codegen-connector.update/raw/master/org.eclipselabs.m2e.cxf.codegen.connector.update-site
        - m2e-cxf-codegen-connector

   When asked about unsigned code, click "Yes".

### Importing the Projects

XXX - Use multiple workspaces

4. Click File -> Import and select "Existing Maven Projects".  Click "Next".

5. Select the root directory of OpenWIS (e.g. "C:\Projects\openwis").  The list of projects will appear.

6. Click "Deselect all" and select the following projects:
    - openwis-management-client
    - openwis-management-service-common
    - openwis-management-service-ejb
    - openwis-management-service-ear
    - openwis-dataservice-common-utils
    - openwis-dataservice-common-domain
    - openwis-dataservice-common-timer
    - openwis-dataservice-cache-core
    - openwis-dataservice-cache-ejb
    - openwis-dataservice-cache-webapp
    - openwis-dataservice-server-ejb
    - openwis-dataservice-server-webapp
    - openwis-dataservice-server-ear
    - openwis-portal-client
    - cachingxslt
    - jeeves
    - oaipmh
    - sde
    - openwis-portal-solr
    - openwis-securityservice-war
    - PopulateLDAP
    - openwis-stagingpost
    - openwis-portal (openwis-metadataportal/openwis-portal)

7. Click "Next".  You will see a number of errors...

### Vagrant Test Stack

...
