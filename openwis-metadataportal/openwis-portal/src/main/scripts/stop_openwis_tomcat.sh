#!/bin/sh
#
# Stop Tomcat
#

# Settings
export CATALINA_HOME=/home/openwis/apache-tomcat-6.0.29
export CATALINA_PID=$CATALINA_HOME/openwis-tomcat.pid

# Stop Tomcat
cd $CATALINA_HOME/bin
./shutdown.sh -force

# Ensure PID file is removed
if [ -e $CATALINA_PID ]
then
  echo "Cleaning remaining PID file"
  rm $CATALINA_PID
fi

