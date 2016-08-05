#!/bin/bash
#
#   The provisioning script for the data machine
#


# Common provisioning steps and env setup
source /vagrant/resources/vagrant/provisioning/provision-common.sh

# Copy vagrant scripts
#deployVagrantScripts db

# =====================================================================================
# DATABASE PROVISIONING

#SCHEMA_DDL=/vagrant/openwis-database/schema.ddl

#SPACIAL_INDEX_DDL=/vagrant/openwis-database/spacialindex.ddl

# --------------------------------------------------------------------------------------
# Database Setup

# Setup and install the database
rpm -ivh https://download.postgresql.org/pub/repos/yum/9.2/redhat/rhel-6-x86_64/pgdg-centos92-9.2-7.noarch.rpm
yum -y install postgresql92 postgresql92-server postgresql92-contrib postgis2_92-2.1.5

# Configure the database
cat > /etc/sysconfig/pgsql/postgresql-9.2 << .
PGDATA=$DB_DIR
PGLOG=$DB_DIR/pgstartup.log
.

# Initialize and start the database
mkdir -p /data/datadb
chown -R postgres: /data/datadb

sudo -u postgres /usr/pgsql-9.2/bin/initdb -D "$DB_DIR"

/sbin/service postgresql-9.2 start
chkconfig --add postgresql-9.2
chkconfig postgresql-9.2 on

# -------------------------------------------------------------------------------------
# Initialize the database

sudo -iu postgres createuser -SDR "$OPENWIS_DB_USER"
sudo -iu postgres createdb -O "$OPENWIS_DB_USER" "$OPENWIS_DB_NAME"
sudo -iu postgres createdb -O "$OPENWIS_DB_USER" harnesses

# Install extensions
sudo -iu postgres createlang -d "$OPENWIS_DB_NAME" plpgsql
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -f /usr/pgsql-9.2/share/contrib/postgis-2.1/postgis.sql  >/dev/null
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -f /usr/pgsql-9.2/share/contrib/postgis-2.1/postgis_comments.sql  >/dev/null
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -f /usr/pgsql-9.2/share/contrib/postgis-2.1/spatial_ref_sys.sql  >/dev/null
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -f /usr/pgsql-9.2/share/contrib/postgis-2.1/legacy.sql  >/dev/null
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -c "CREATE EXTENSION citext" >/dev/null

sudo -iu postgres psql -c "alter user $OPENWIS_DB_USER with login password '$OPENWIS_DB_PASSWD'"

# Configure DB
newStackDepthMB="$((`ulimit -s` / 5 * 4 / 1024))"
sudo -iu postgres sed -i -e 's/^#listen_addresses = '\''localhost'\''/listen_addresses = '\''*'\''/' /data/datadb/postgresql.conf
sudo -iu postgres sed -i -e 's/^#max_stack_depth =.*/max_stack_depth = '"$newStackDepthMB"'MB/' /data/datadb/postgresql.conf
sudo -iu postgres echo 'host    all         all         0.0.0.0/0           password' >> /data/datadb/pg_hba.conf
/sbin/service postgresql-9.2 restart


# Install the schema
sudo -iu openwis psql -d "$OPENWIS_DB_NAME" -f $ARTEFACT_SCHEMA_DDL >/dev/null

echo "Adding spacialindex table:"
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -f $ARTEFACT_SPACIAL_INDEX_DDL >/dev/null

# HACK - This is not exactly like the official installation guide but seems to be the only way
# to avoid internal errors in Solr.
sudo -iu postgres psql -d "$OPENWIS_DB_NAME" -c "grant all on geography_columns, geometry_columns, spatial_ref_sys, spatialindex to $OPENWIS_DB_USER;"

# Check everything.  If OpenWIS cannot log into the database, fail the script
if ! sudo -iu openwis psql -d "$OPENWIS_DB_NAME" -c 'select 1;' ; then
    echo "ERROR: Database Setup FAILED!!!!" >&2
    exit 1
fi


# ====================================================================================
# SOLR PROVISIONING

# ------------------------------------------------------------------------------------
# Create Directories


mkdir -p "$DS_DIR"
chown openwis: "$DS_DIR"

# Solr directories
sudo -iu openwis mkdir /data/openwis/solr
sudo -iu openwis mkdir /data/openwis/solr/data
sudo -iu openwis mkdir /data/openwis/solr/spatial

# --------------------------------------------------------------------------------------
# Install Java and Tomcat

openwisHome="/home/openwis"
openwisOpt="/home/openwis/opt"
tomcatHome="$openwisHome/`basename "$RESOURCE_TOMCAT" .tar.gz`"

sudo -iu openwis mkdir "$openwisOpt"
sudo -iu openwis mkdir "$openwisHome/staging"

#echo "Unpacking Java"
yum install -y java-1.7.0-openjdk-devel.x86_64

echo "Unpacking Tomcat"
sudo -iu openwis wget -q -O /tmp/apache-tomcat.tar.gz "$RESOURCE_TOMCAT"
sudo -iu openwis tar -xvz -C "$openwisHome" -f /tmp/apache-tomcat.tar.gz
sudo -iu openwis chmod u+x "$tomcatHome/bin/"*.sh

# --------------------------------------------------------------------------------------
# Deploy Solr

sudo -iu openwis unzip -q -d "$tomcatHome"/webapps/openwis-portal-solr "$ARTEFACT_SOLR"

# --------------------------------------------------------------------------------------
# Config Solr

echo "Configuring solr"
solrProps="$tomcatHome/webapps/openwis-portal-solr/WEB-INF/classes/openwis.properties"
owConf "$solrProps" "solr.home" "/data/openwis/solr"
owConf "$solrProps" "database.url" "jdbc:postgresql://${OPENWIS_DB_HOST}:5432/$OPENWIS_DB_NAME"
owConf "$solrProps" "database.user" "$OPENWIS_DB_USER"
owConf "$solrProps" "database.password" "$OPENWIS_DB_PASSWD"

# --------------------------------------------------------------------------------------
# Deploy And Configure Startup Scripts

sudo -iu openwis cp "$ARTEFACT_PORTAL_SCRIPTS"/* "$openwisHome"
sudo -iu openwis sed -i -e 's|export CATALINA_HOME=.*|export CATALINA_HOME='"$tomcatHome"'|' "$openwisHome"/*.sh
sudo -iu openwis chmod u+x "$openwisHome"/*.sh
sudo -iu openwis dos2unix "$openwisHome"/*.sh


# --------------------------------------------------------------------------------------
# Start Solr

sudo -iu openwis ./start_openwis_tomcat.sh