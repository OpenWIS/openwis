#!/usr/bin/env python
#
#   Generates a JBoss AS 7.1 CLI script
#

# Modify the following properties to your environment
PROPERTIES = {
    "openwis.config.dir": "conf",       # Directory containing localdatasourceservice.properties and
                                        # openwis-dataservice.properties.  This is resolved from the HOME directory
    
    "postgresql.driver.name": "postgresql-8.4-702.jdbc4.jar",
                                        # Name of the PostgreSQL driver deployed
    
    "openwis.db.host": "openwis-db",    # Database host
    "openwis.db.port": "5432",          # Database port
    "openwis.db.name": "OpenWIS",       # Database name
    "openwis.db.username": "openwis",   # Database username
    "openwis.db.password": "***"        # Database password
}

# -------------------------------------------------------------------------------
#

import os.path

scriptDir = os.path.dirname(__file__)

cliFile = open(os.path.join(scriptDir, "../config/setup-openwis.cli"), "r")
try:
    fileContent = cliFile.read()
    for k, v in PROPERTIES.iteritems():
        placeholder = "@" + k + "@"
        fileContent = fileContent.replace(placeholder, v)
    print fileContent
finally:
    cliFile.close()
