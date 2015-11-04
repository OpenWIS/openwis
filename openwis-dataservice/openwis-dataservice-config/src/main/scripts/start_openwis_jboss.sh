#!/bin/sh
# JBoss startup script for OpenWIS

#################################
##### Parameters to modify ######

# Binding address: address on which Jboss will be bound
# 0.0.0.0 means Jboss listens to all available addresses
bindingAddress="0.0.0.0"

# Standalone configuration to use
configuration=standalone-full.xml

# Multicast address: multicast address used for communication between cluster members
# All cluster members must have the same multicast address
multicastAddress="239.255.100.100"

####################################
##### Do not modify from here ######

export JBOSS_PIDFILE=$JBOSS_HOME/jboss.pid

export LAUNCH_JBOSS_IN_BACKGROUND=1

cd $JBOSS_HOME/bin

OPENWIS_CLUSTER_OPTS="-b $bindingAddress -u $multicastAddress"
JBOSS_OPENWIS_OPTS="-Duser.timezone=UTC -c $configuration $OPENWIS_CLUSTER_OPTS"

echo "JBOSS_OPENWIS_OPTS: $JBOSS_OPENWIS_OPTS"

nohup ./standalone.sh $JBOSS_OPENWIS_OPTS -Djava.awt.headless=true >/dev/null 2>&1 &
