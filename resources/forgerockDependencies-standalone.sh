#!/bin/bash
# This script aims to build Forgerock resources needed for OpenWIS build on standalone environments.
# Tested with Java 7 and maven 3.2.1

function build() {
	mvn clean install
	if [ $? -ne 0 ] ; then
		exit 1;
	fi
}


mkdir commons
cd commons
git clone https://stash.forgerock.org/scm/commons/forgerock-parent-public.git
cd forgerock-parent-public
git checkout 1.0.0
build
git checkout 1.2.0
build
git checkout 1.2.1
build
git checkout 2.0.3
build
git checkout 2.0.6
build
cd ..

git clone https://stash.forgerock.org/scm/commons/forgerock-build-tools-public.git
cd forgerock-build-tools-public
git checkout 1.0.2
build
cd ..

git clone https://stash.forgerock.org/scm/legacycommons/forgerock-util-public.git
cd forgerock-util-public
git checkout  1.3.5 
build
git checkout  1.3.2
build
cd .. 

git clone https://stash.forgerock.org/scm/commons/i18n-framework-public.git
cd i18n-framework-public
git checkout 1.4.1
build
cd ..

git clone https://stash.forgerock.org/scm/legacycommons/forgerock-rest-public.git
cd forgerock-rest-public
git checkout 2.4.2
build 
cd ..
################################################################################### 

git clone https://stash.forgerock.org/scm/commons/forgerock-guice-public.git
cd forgerock-guice-public
git checkout 1.0.2
build
cd ..

##################################################################################

cd .. # exit commons folder
mkdir openam
cd  openam
git clone https://stash.forgerock.org/scm/openam/openam-public.git
cd openam-public
git checkout 12.0.0

#############################################################################
mvn clean install -pl openam-shared -am 
mvn clean install -pl openam-schema -am

cd  openam-schema
mvn clean install -pl openam-liberty-schema -am
mvn clean install -pl openam-saml2-schema -am
mvn clean install -pl openam-xacml3-schema -am
mvn clean install -pl openam-wsfederation-schema -am
cd ..
 
mvn clean install -pl openam-federation -am
cd openam-federation

mvn clean install -pl openam-federation-library -am
mvn clean install -pl openam-idpdiscovery -am
cd ..

mvn clean install -pl openam-shared -am
cd ..