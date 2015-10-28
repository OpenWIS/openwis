#!/bin/sh
# JBoss shutdown script for OpenWIS

#################################
##### Parameters to modify ######

# Binding address: address on which Jboss is bound
bindingAddress="localhost"

# The controller port: the port the CLI controller is bound to
bindingPort="9999"

####################################
##### Do not modify from here ######

cd $JBOSS_HOME/bin

./jboss-cli.sh --controller="$bindingAddress:$bindingPort" -c --command=:shutdown

# Ensure for JBoss stops
SLEEP=20
JBOSS_PIDFILE=$JBOSS_HOME/jboss.pid
echo "Waiting for jboss to stop..."

while [ $SLEEP -ge 0 ]; do
  if ! [ -f "$JBOSS_PIDFILE" ] ; then
    break
  fi
  
  sleep 1
  SLEEP=`expr $SLEEP - 1 `
done

if [ -f "$JBOSS_PIDFILE" ]; then
  echo "Killing: `cat $JBOSS_PIDFILE`"
  kill -9 `cat $JBOSS_PIDFILE`
  rm $JBOSS_PIDFILE
fi

exit 0
