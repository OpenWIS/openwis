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

export ARTEFACT_PORTAL_SCRIPTS=/vagrant/openwis-metadataportal/openwis-portal/src/main/scripts
export ARTEFACT_SCHEMA_DDL=/vagrant/openwis-database/schema.ddl
export ARTEFACT_SPACIAL_INDEX_DDL=/vagrant/openwis-database/spacialindex.ddl

# Resources
export RESOURCE_TOMCAT="http://repository-openwis-association.forge.cloudbees.com/private/binaries/apache-tomcat-7.0.59.tar.gz"
