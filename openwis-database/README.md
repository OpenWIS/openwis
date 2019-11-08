#OpenWis Database 
This project builds the openwis database code 
(That is all of the tables with an **openwis_** prefix which make up part of the overall schema used within the openwis Application)

##OpenWis Schema Install

* **schema.ddl** is the script used to install the openwis schema 
* **purge.sql** is the script used to install the openwis blaclkist function and trigger 

# Additional Schema Creation 

## PostGis Schema Install
postgis is installed using three scripts in the contrib folder of the postgres install

* /freeware/pgsql/share/contrib/postgis-<version>/postgis.sql
* /freeware/pgsql/share/contrib/postgis-<version>/postgis_comments.sql
* /freeware/pgsql/share/contrib/postgis-<version>/spatial_ref_sys.sql

## Citext

Additionally Citext extension is installed from a script in the contrib folder of the postgres install

* /freeware/pgsql/share/contrib/citext.sql


## Meta Data Portal Schema Install 

### (Geonet and Openwis-Solr) Schemas

The remaining portion of the scehma is created in the metadataportal project which includes the geonetwork elements to build the openwis schema (These additional tables can be identified as they are not prefixed with **openwis_**)

For the Postgres install ....

* openwis/openwis-metadataportal/openwis-portal-solr/src/main/resources/sql/create-postgis-spatialindex.sql
* openwis/openwis-metadataportal/openwis-portal/src/main/webapp/WEB-INF/classes/setup/sql/create/create-db-postgres.sql

For Postgress the data is loaded for this schema with 
* openwis/openwis-metadataportal/openwis-portal/src/main/webapp/WEB-INF/classes/setup/sql/data/data-db-postgres.sql
