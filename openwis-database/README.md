#OpenWis Database 
This project builds the openwis database code 
(That is all of the tables with an openwis_prefix which make up part of the overall schema used within the openwis Application)

OpenWis Install

* **schema.ddl** is the script used ton install the openwis schema 

## PostGis Install
postgis is installed using three scripts in the contrib folder of the postgres install

The remaining portion of the scehma is created in the metadataportal project which includes the geonetwork elements to the openwis schema (These additional tables can be identified as they are not prefixed with openwis_)

## Meta Data Portal (Geonet) Schema
See openwis/openwis-metadataportal/openwis-portal/src/main/webapp/WEB-INF/classes/setup/sql/create/create-db-postgres.sql
