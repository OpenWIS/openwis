#!/bin/bash
#
#   Machine configuration.
#

export DATA_DIR=/data
export DB_DIR="$DATA_DIR/datadb"
export DS_DIR="$DATA_DIR/openwis"

export OPENWIS_DB_HOST=owdev-db
export OPENWIS_DB_USER=openwis
export OPENWIS_DB_PASSWD=openwis1
export OPENWIS_DB_NAME=OpenWIS

export OPENWIS_PASSWD=openwis1

# Artefacts
export ARTEFACT_SOLR="/vagrant/openwis-metadataportal/openwis-portal-solr/target/openwis-portal-solr.war"
export ARTEFACT_MANAGEMENT_SERVICE_EAR="/vagrant/openwis-management/openwis-management-service/openwis-management-service-ear/target/openwis-management-service.ear"
export ARTEFACT_DATA_SERVICE_EAR="/vagrant/openwis-dataservice/openwis-dataservice-server/openwis-dataservice-server-ear/target/openwis-dataservice.ear"
export ARTEFACT_STAGINGPOST_WAR="/vagrant/openwis-stagingpost/target/stagingPost.war"
export ARTEFACT_USER_PORTAL="/vagrant/openwis-metadataportal/openwis-portal/openwis-user-portal/openwis-user-portal-user.war"
export ARTEFACT_ADMIN_PORTAL="/vagrant/openwis-metadataportal/openwis-portal/openwis-admin-portal/openwis-admin-portal-admin.war"

export ARTEFACT_DATASERVICE_CONFIG_MODULE="/vagrant/openwis-dataservice/openwis-dataservice-config/target/openwis-dataservice-config-module.zip"
export ARTEFACT_DATASERVICE_CONFIG_FILES="/vagrant/openwis-dataservice/openwis-dataservice-config/target/openwis-dataservice-config-files.zip"

export ARTEFACT_PORTAL_SCRIPTS=/vagrant/openwis-metadataportal/openwis-portal/src/main/scripts
export ARTEFACT_SCHEMA_DDL=/vagrant/openwis-database/schema.ddl
export ARTEFACT_SPACIAL_INDEX_DDL=/vagrant/openwis-database/spacialindex.ddl

# Resources
export RESOURCE_TOMCAT="http://repository-openwis-association.forge.cloudbees.com/private/binaries/apache-tomcat-7.0.59.tar.gz"
export RESOURCE_JBOSS="https://repository-openwis-association.forge.cloudbees.com/private/binaries/jboss-as-7.1.1.Final.tar.gz"
export RESOURCE_POSTGRESQL_JAR="https://repository-openwis-association.forge.cloudbees.com/private/binaries/postgresql-9.2-1004.jdbc41.jar"