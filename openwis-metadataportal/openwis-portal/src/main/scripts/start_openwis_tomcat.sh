#!/bin/sh
#
# Start OpenWIS Tomcat
#

# Settings
export CATALINA_OPTS="-Xmx512m -XX:MaxPermSize=256m"
export CATALINA_HOME=/home/openwis/apache-tomcat-6.0.29
export CATALINA_PID=$CATALINA_HOME/openwis-tomcat.pid


# Check if Tomcat did not crash (with PID file)
if [ -e $CATALINA_PID ]
then
  ps -p `cat $CATALINA_PID` &> /dev/null
  if [ $? = 0 ]
  then
    echo "Tomcat still running"
    exit 0
  else
    echo "Tomcat crashed, cleaning remaining PID file"
    rm $CATALINA_PID
  fi
fi


# Start Tomcat
cd $CATALINA_HOME/bin
./startup.sh

