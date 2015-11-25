#!/usr/bin/env python
#
# This script provides a quick way to configure the data services.
#

import os
import os.path
import getpass

DESCRIPTION = """
OpenWIS Data Services - Setup Script
------------------------------------

This script can be used to setup the initial configuration of the data and
management services and configure the JBoss environment.
"""

# ---------------------------------------------------------------------------
#

# A parameter that is exposed to the user for configuration.  The following
# attributes are used:
#
#    key:        the key for the property.  Must be set.
#    descr:      human readable description of the property.
#    default:    default value of the property.
#    password:   if True, entering values for this property will not be echoed
#    valids:     a list of validation tests.  These are hash which have a test
#                lambda which returns true, and a validation error message.
#
class Parameter(object):
    """A parameter description"""
    
    def __init__(self, key=None, descr=None, default=None, password=False,
                 valids=[], extraKeys=None):
        self.key = key
        self.descr = descr
        self.default = default
        self.password = password
        self.valids = valids
        self.extraKeys = extraKeys
    
    def get_value(self):
        """Get the value for this parameter.
        This will show a prompt to the user and validate the result."""
        while True:
            value = self.__prompt_for_value()
            try:
                self.check_valid(value)
                return value
            except Exception as e:
                print("Invalid value: " + value + " - " + str(e))
    
    def get_extra_values(self, value):
        """Get any values derived from the originally entered value."""
        if self.extraKeys:
            extraValues = {}
            for key, transform in self.extraKeys.iteritems():
                extraValues[key] = transform(value)
            return extraValues
        else:
            return None
            
    def check_valid(self, value):
        """Check that the passed in value is valid.
        If the value is invalid, an exception is raised."""
        for rule in self.valids:
            if (not rule["test"](value)):
                raise Exception(rule["message"])
        
    def __prompt_for_value(self):
        """Shows a prompt to the user for a value of this parameter."""
        
        if (self.password):
            return getpass.getpass("Enter " + self.descr + ": ")
        if (self.default):
            res = raw_input("Enter " + self.descr + " [" + self.default + "]: ")
            if res != "":
                return res
            else:
                return self.default
        else:
            return raw_input("Enter " + self.descr + ": ")
        


# ---------------------------------------------------------------------------
#

# Parameters that the user can config.  Each parameter has the following keys:
PARAMETERS = [

    # data base location
    Parameter(
        key="dataService.baseLocation",
        descr="OpenWIS data location",
        default="/var/opt/openwis"
    ),

    # config directory
    Parameter(
        key="openwis.config.dir",
        descr="Configuration directory", 
        default=os.path.join(os.getenv("HOME"), "conf"),
        valids=[
            { "test": os.path.isdir, "message": "Must be a directory" }
        ]
    ),

    # postgresql driver name
    Parameter(
        key="postgresql.driver.path",
        descr="PostgreSQL JDBC driver", 
        default=os.path.join(os.getenv("HOME"), "conf"),
        valids=[
            { "test": os.path.isfile, "message": "Must be a file" }
        ],
        extraKeys={
            "postgresql.driver.name": (lambda value: os.path.basename(value)) 
        }
    ),
    
    # database config
    Parameter(key="openwis.db.host", descr="OpenWIS database host"),
    Parameter(key="openwis.db.port", descr="OpenWIS database port", default="5432"),
    Parameter(key="openwis.db.name", descr="OpenWIS database name", default="OpenWIS"),
    Parameter(key="openwis.db.username", descr="OpenWIS database username", default="openwis"),
    Parameter(key="openwis.db.password", descr="OpenWIS database password", password=True)
]

# Files to transform and the destinations
FILES = [
    { "src": "setup-openwis.cli", "targetDir": os.path.join(os.getenv("HOME"), "conf") },
    { "src": "localdatasourceservice.properties", "targetDir": (lambda paramValues: paramValues["openwis.config.dir"]) },
    { "src": "openwis-dataservice.properties", "targetDir": (lambda paramValues: paramValues["openwis.config.dir"]) }
]

# ---------------------------------------------------------------------------
#

# Display a quick intro screen 
def display_intro():
    print(DESCRIPTION)
    raw_input("Press ENTER to continue: ")


# Prompt for all parameters.  Returns a hash containing the entered values.
def prompt_for_params():
    paramValues = {}
    for param in PARAMETERS:
        print("")
        enteredValue = param.get_value()
        paramValues[param.key] = enteredValue
        
        extraValues = param.get_extra_values(enteredValue)
        if extraValues != None:
            for k, v in extraValues.iteritems():
                paramValues[k] = v
    return paramValues

# Replace placeholders from a source file and write the results to a target
# file
def process_file(srcFilename, targetFilename, parameterValues):
    srcFile = open(srcFilename, "r")
    targetFile = open(targetFilename, "w")
    
    try:
        fileContent = srcFile.read()
        
        # Replace comments marked with 'setup:' with the empty string,
        # enabling the commented out line
        fileContent = fileContent.replace("#setup: ", "")
        
        for k, v in parameterValues.iteritems():
            placeholder = "@" + k + "@"
            fileContent = fileContent.replace(placeholder, v)
        
        targetFile.write(fileContent)
    finally:
        targetFile.close()
        srcFile.close()
        
    

# Process each of the source configuration files by replacing the placeholders
# and copying them to the target directory
def process_files(parameterValues):
    for file in FILES:
        srcFilename = os.path.join(os.path.dirname(__file__), "config", file["src"])
        if type(file["targetDir"]) is str:
            targetFilename = os.path.join(file["targetDir"], os.path.basename(srcFilename))
        else:
            targetFilename = os.path.join(file["targetDir"](parameterValues), os.path.basename(srcFilename))
        
        print("Writing " + targetFilename)
        process_file(srcFilename, targetFilename, parameterValues)

# ---------------------------------------------------------------------------
#

display_intro()

parameterValues = prompt_for_params()
process_files(parameterValues)